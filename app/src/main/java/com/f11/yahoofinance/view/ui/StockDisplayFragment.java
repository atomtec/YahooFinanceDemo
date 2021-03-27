package com.f11.yahoofinance.view.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f11.yahoofinance.R;
import com.f11.yahoofinance.data.model.AppStock;
import com.f11.yahoofinance.data.model.FetchStatus;
import com.f11.yahoofinance.data.repository.StockRepository;
import com.f11.yahoofinance.data.sync.SyncManager;
import com.f11.yahoofinance.view.adapter.StockListAdapter;
import com.f11.yahoofinance.viewmodel.StockViewModel;
import com.f11.yahoofinance.viewmodel.StockViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;


public class StockDisplayFragment extends Fragment {

    private StockViewModel mStockViewModel;
    RecyclerView mStockRecyclerView;
    StockListAdapter mAdapter;
    View mProgressBar ;
    private static final String TAG = StockDisplayFragment.class.getSimpleName();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStockRecyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new StockListAdapter(getContext());
        mStockRecyclerView.setAdapter(mAdapter);
        mStockRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Get a new or existing ViewModel from the ViewModelProvider.

        mStockViewModel = new ViewModelProvider(getActivity(), new StockViewModelFactory(StockRepository.getInstance(getContext().getApplicationContext())))
                .get(StockViewModel.class);
        mProgressBar = view.findViewById(R.id.llProgressBar);
        getLifecycle().addObserver(mStockViewModel);//stopsyncing when fragment is onstop

        // Add an observer on the LiveData
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mStockViewModel.getAllStocks().observe(getViewLifecycleOwner(), new Observer<List<AppStock>>() {
            @Override
            public void onChanged(@Nullable final List<AppStock> stocks) {
                mAdapter.setStocks(stocks);
            }
        });

        mStockViewModel.getFetchData().observe(getViewLifecycleOwner(), new Observer<FetchStatus>() {
            @Override
            public void onChanged(FetchStatus status) {
                if(status.isFetchOpComplete()){
                    mProgressBar.setVisibility(View.GONE);
                }
               switch (status.getFetchStatus()){
                   case FetchStatus.FETCH_ERROR:
                       makeText(StockDisplayFragment.this.getContext(),
                               R.string.fetch_error,
                               Toast.LENGTH_LONG).show();
                       break;
                   case FetchStatus.STOCK_FOUND:
                       makeText(StockDisplayFragment.this.getContext(),
                               R.string.stock_found,Toast.LENGTH_LONG).show();
                       break;
                   case FetchStatus.STOCK_NOT_FOUND:
                       makeText(StockDisplayFragment.this.getContext(),
                               R.string.stock_not_found,Toast.LENGTH_LONG).show();
                       break;
                   default:
                       makeText(StockDisplayFragment.this.getContext(),
                               R.string.generic_error,Toast.LENGTH_LONG).show();
                       break;

               }
            }
        });


        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
        AppCompatToggleButton toggleMenu = menu.findItem(R.id.switchId)
                .getActionView().findViewById(R.id.displaymodeswitch);
        toggleMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAdapter.toggleMode(isChecked);
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }



    public void addStock(String symbol){
        mProgressBar.setVisibility(View.VISIBLE);
        mStockViewModel.searchAndStock(symbol);
    }


}