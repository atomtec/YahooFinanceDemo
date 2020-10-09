package com.f11.yahoofinance.view.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.f11.yahoofinance.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    public final static String SHOW_PERCENTAGE_KEY = "show_percentage";
    SharedPreferences mPrefs;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFragment = getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkUp()) {
                    new AddStockDialogFragment().show(getSupportFragmentManager(),
                            "StockDialogFragment");
                }
                //TODO add cache and retry
                else{
                  Snackbar.make(view, R.string.internet_down_message, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        AppCompatToggleButton toggleMenu = menu.findItem(R.id.switchId)
                .getActionView().findViewById(R.id.displaymodeswitch);
        toggleMenu.setChecked(mPrefs.getBoolean(SHOW_PERCENTAGE_KEY,false));
        toggleMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean(SHOW_PERCENTAGE_KEY,isChecked);
                editor.commit();
            }
        });
        return true;
    }

    public void addStock(String symbol){
        if (mFragment != null){
            StockDisplayFragment.class.cast(mFragment).addStock(symbol);
        }


    }
    private boolean isNetworkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}