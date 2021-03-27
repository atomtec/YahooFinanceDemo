package com.f11.yahoofinance.viewmodel;

import com.f11.yahoofinance.data.repository.StockRepository;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class StockViewModelFactory implements ViewModelProvider.Factory {
    private StockRepository mRepo;



    public StockViewModelFactory(StockRepository repo) {
       mRepo = repo;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new StockViewModel(mRepo);
    }
}

