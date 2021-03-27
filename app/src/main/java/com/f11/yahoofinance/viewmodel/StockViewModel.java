package com.f11.yahoofinance.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import com.f11.yahoofinance.data.model.AppStock;
import com.f11.yahoofinance.data.model.FetchStatus;
import com.f11.yahoofinance.data.repository.StockRepository;
import com.f11.yahoofinance.view.ui.StockDisplayFragment;


import java.util.List;

public class StockViewModel extends ViewModel implements LifecycleObserver {

    private StockRepository mRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<AppStock>> mAllStocks;
    private MutableLiveData<FetchStatus> mFetchData;
    private static final String TAG = StockViewModel.class.getSimpleName();

    public StockViewModel (StockRepository repo) {
        super();
        mRepository = repo;
        mAllStocks = mRepository.getAllStocks();
        mFetchData = mRepository.getFetchLiveData();
    }

    public LiveData<List<AppStock>> getAllStocks() { return mAllStocks; }
    public MutableLiveData<FetchStatus> getFetchData(){ return mFetchData;}

    public void searchAndStock(String symbol){
        mRepository.searchAndAddStock(symbol);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onResume() {
        Log.d(TAG, "resumed observing lifecycle.");
        mRepository.startLiveSync();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        mRepository.stopLiveSync();
        Log.d(TAG, "paused observing lifecycle.");
    }


}