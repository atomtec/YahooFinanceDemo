package com.f11.yahoofinance.view.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f11.yahoofinance.R;
import com.f11.yahoofinance.data.model.AppStock;
import com.f11.yahoofinance.data.model.FetchStatus;
import com.f11.yahoofinance.data.sync.SyncManager;
import com.f11.yahoofinance.view.adapter.StockListAdapter;
import com.f11.yahoofinance.viewmodel.StockViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;
import static com.f11.yahoofinance.view.ui.MainActivity.SHOW_PERCENTAGE_KEY;

public class StockDisplayFragment extends Fragment {

    private StockViewModel mStockViewModel;
    RecyclerView mStockRecyclerView;
    StockListAdapter mAdapter;
    List<AppStock> mStock = new ArrayList<AppStock>();
    View mProgressBar ;
    SharedPreferences mPrefs;
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
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean showPercentage = mPrefs.getBoolean(SHOW_PERCENTAGE_KEY,false);
        mStockRecyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new StockListAdapter(getContext(), showPercentage);
        mStockRecyclerView.setAdapter(mAdapter);
        mStockRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Get a new or existing ViewModel from the ViewModelProvider.
        mStockViewModel = new ViewModelProvider(this).get(StockViewModel.class);
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

        //Not using Live Data for this
        mPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void addStock(String symbol){
        mProgressBar.setVisibility(View.VISIBLE);
        mStockViewModel.searchAndStock(symbol);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy() called");
        mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            // listener implementation
            if (key.equals(SHOW_PERCENTAGE_KEY)) {
                boolean showPercentage = prefs.getBoolean(SHOW_PERCENTAGE_KEY, false);
                mAdapter.toggleMode(showPercentage);
            }

        }
    };
}