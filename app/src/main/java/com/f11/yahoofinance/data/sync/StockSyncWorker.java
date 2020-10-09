package com.f11.yahoofinance.data.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.f11.yahoofinance.data.model.AppStock;
import com.f11.yahoofinance.data.repository.StockDao;
import com.f11.yahoofinance.data.repository.StockDataBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StockSyncWorker extends Worker {


    private StockDao mStockDao;
    private static final String TAG = StockSyncWorker.class.getSimpleName();

    public StockSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        StockDataBase db = StockDataBase.getDatabase(context);
        mStockDao = db.mStockDao();
        Log.i(TAG, "StockSyncWorker created");
    }




    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "doWork called for this worker");
        List<AppStock> stocks = mStockDao.getAllStocksSync();
        List<AppStock> appStocks = new ArrayList<>();
        try {
            appStocks = RemoteProviderManager.getInstance().
                    getSDKProvider().getRemoteStocks(stocks);
        } catch (IOException e) {
            e.printStackTrace();
            Result.retry();
        }

        for (AppStock stock : appStocks){
            mStockDao.insert(stock);
        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.i(TAG, "OnStopped called for this worker");
    }
}
