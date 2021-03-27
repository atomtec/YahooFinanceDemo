package com.f11.yahoofinance.data.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.f11.yahoofinance.data.model.AppStock;
import com.f11.yahoofinance.data.repository.StockDao;
import com.f11.yahoofinance.data.repository.StockDataBase;
import com.f11.yahoofinance.data.repository.StockRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StockSyncWorker extends Worker {


    private StockRepository mStockRepo;
    private static final String TAG = StockSyncWorker.class.getSimpleName();

    public StockSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mStockRepo = StockRepository.getInstance(context.getApplicationContext());
        Log.i(TAG, "StockSyncWorker created");
    }




    @NonNull
    @Override //Runs in background thead
    public Result doWork() {

        try {
           mStockRepo.refreshStocks();
        } catch (IOException e) {
            e.printStackTrace();
            Result.retry();
        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.i(TAG, "OnStopped called for this worker");
    }
}
