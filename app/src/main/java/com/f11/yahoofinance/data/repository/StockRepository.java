package com.f11.yahoofinance.data.repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.f11.yahoofinance.data.model.AppStock;
import com.f11.yahoofinance.data.model.FetchStatus;
import com.f11.yahoofinance.data.sync.RemoteProviderManager;
import com.f11.yahoofinance.data.sync.SyncManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockRepository {

    private StockDao mStockDao;
    private LiveData<List<AppStock>> mAllStocks;
    private SyncManager mSyncManager;
    private MutableLiveData<FetchStatus> fetchLiveData = new MutableLiveData<FetchStatus>();

    Runnable refreshStockRunaable = new Runnable(){

        @Override
        public void run() {
            try {
                refreshStocks();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private static StockRepository INSTANCE = null;



    public static StockRepository getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (StockDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE =new StockRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }



    private  StockRepository (Context application) {
        mStockDao = StockDataBase.getDatabase(application).mStockDao();
        mAllStocks = mStockDao.getAllStocks();
        mSyncManager = SyncManager.getInstance(application);
        ExecutorService mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.submit(refreshStockRunaable);
    }



    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<AppStock>> getAllStocks() {
        return mAllStocks;
    }

    public MutableLiveData<FetchStatus> getFetchLiveData(){
        return fetchLiveData;
    }

    public void searchAndAddStock(String symbol){
        new searchAndAddStock(mStockDao,fetchLiveData,mSyncManager).execute(symbol);
    }




    public void stopLiveSync(){
        mSyncManager.stopLiveSync();
    }

    public void startLiveSync(){
        mSyncManager.startLiveSync();
    }

    private static class insertAsyncTask extends AsyncTask<AppStock, Void, Void> {

        private StockDao mAsyncTaskDao;

        insertAsyncTask(StockDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AppStock... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    //This should be called from Background thread onlu
    public void refreshStocks() throws IOException {
        List<AppStock> dbStocks = mStockDao.getAllStocksSync();
        List<AppStock> networkStocks = null;
        if(dbStocks.size() > 0){
             networkStocks = RemoteProviderManager.getInstance().getSDKProvider().getRemoteStocks(dbStocks);
             mStockDao.insertAll(networkStocks);
        }
    }


  
    //TODO add RX maybe

    private static class searchAndAddStock extends AsyncTask<String, Void, Void> {

        private StockDao mAsyncTaskDao;
        private MutableLiveData<FetchStatus> mFetchStatusLiveData;
        AppStock mAppStock ;
        FetchStatus mFetchStatus;


        searchAndAddStock(StockDao dao, MutableLiveData<FetchStatus> status,
                          SyncManager syncManager) {
            mAsyncTaskDao = dao;
            mFetchStatusLiveData = status;
            mFetchStatus = new FetchStatus();

        }

        @Override
        protected Void doInBackground(final String ... params) {
           try {
               mAppStock = RemoteProviderManager.getInstance().
                        getSDKProvider().getStockBySymbol(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
               mFetchStatus.setFetchOpComplete(true);
               mFetchStatus.setFetchStatus(FetchStatus.FETCH_ERROR);
               mFetchStatusLiveData.postValue(mFetchStatus);
            }
           return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(mAppStock != null){
                new insertAsyncTask(mAsyncTaskDao).execute(mAppStock);
                mFetchStatus.setFetchOpComplete(true);
                mFetchStatus.setFetchStatus(FetchStatus.STOCK_FOUND);
                mFetchStatusLiveData.postValue(mFetchStatus);

            }
            else if (mFetchStatus.getFetchStatus() == FetchStatus.FETCHING){
                // Stock not found
                mFetchStatus.setFetchStatus(FetchStatus.STOCK_NOT_FOUND);
                mFetchStatus.setFetchOpComplete(true);
                mFetchStatusLiveData.postValue(mFetchStatus);
            }
        }
    }
}
