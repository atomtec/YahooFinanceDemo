package com.f11.yahoofinance.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f11.yahoofinance.data.model.AppStock;
import com.f11.yahoofinance.data.model.FetchStatus;
import com.f11.yahoofinance.data.sync.RemoteProviderManager;
import com.f11.yahoofinance.data.sync.SyncManager;

import java.io.IOException;
import java.util.List;

public class StockRepository {

    private StockDao mStockDao;
    private LiveData<List<AppStock>> mAllStocks;
    private SyncManager mSyncManager;
    private MutableLiveData<FetchStatus> fetchLiveData = new MutableLiveData<FetchStatus>();



    public StockRepository (Application application) {
        StockDataBase db = StockDataBase.getDatabase(application);
        mStockDao = db.mStockDao();
        mAllStocks = mStockDao.getAllStocks();
        mSyncManager = SyncManager.getInstance(application);
    }

    public List<AppStock> getAllStocksSync(){
        return mStockDao.getAllStocksSync();
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


    private  void insert (AppStock stock) {
        new insertAsyncTask(mStockDao).execute(stock);
    }

    public void stopLiveSync(){
        mSyncManager.stopLiveSync();
    }

    public void startLiveSync(){
        new startSyncAndSchedule(mStockDao,mSyncManager).execute();
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


    private static class startSyncAndSchedule extends AsyncTask<Void, Void, Void> {

        private StockDao mAsyncTaskDao;
        private SyncManager mAsyncSyncManager;

        startSyncAndSchedule(StockDao dao , SyncManager syncManager) {
            mAsyncTaskDao = dao;
            mAsyncSyncManager = syncManager;
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<AppStock> dbStocks = mAsyncTaskDao.getAllStocksSync();
            if(dbStocks != null && dbStocks.size() > 0 ){
                mAsyncSyncManager.syncAndSchedule();
            }
            return null;
        }
    }
    //TODO add RX maybe

    private static class searchAndAddStock extends AsyncTask<String, Void, Void> {

        private StockDao mAsyncTaskDao;
        private MutableLiveData<FetchStatus> mFetchStatusLiveData;
        AppStock mAppStock ;
        FetchStatus mFetchStatus;
        SyncManager mAsyncSyncManager;

        searchAndAddStock(StockDao dao, MutableLiveData<FetchStatus> status,
                          SyncManager syncManager) {
            mAsyncTaskDao = dao;
            mFetchStatusLiveData = status;
            mFetchStatus = new FetchStatus();
            mAsyncSyncManager = syncManager;
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
                mAsyncSyncManager.syncAndSchedule();//For the first time case
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
