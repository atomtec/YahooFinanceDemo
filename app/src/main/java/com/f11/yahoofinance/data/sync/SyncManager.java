package com.f11.yahoofinance.data.sync;

import android.content.Context;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Sync stratgey is like this , when the app is opened
 * we will poll after every 10 seconds and when the app is closed
 * all sync will be closed
 */
public class SyncManager {

    private Context mContext;
    private static SyncManager INSTANCE;
    private Timer mLiveTimer;
    private static final int SYNC_INTERVAL= 15000;
    private static final String TAG = SyncManager.class.getSimpleName();

    private SyncManager(Context context) {
        //Singleton
        this.mContext = context;
    }

    public static synchronized SyncManager getInstance(Context context) {
          if (INSTANCE == null) {
                INSTANCE = new SyncManager(context);
            }
        return INSTANCE;
    }
    private Constraints getNetworkContraints(){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        return constraints;
    }


    //Unique fetchnow to avid processing dupliacte request
    public void fetchNow(){
        Log.i(TAG, "syncNow() started");
        WorkRequest oneShotRequest =
                new OneTimeWorkRequest.Builder(StockSyncWorker.class)
                        .setConstraints(getNetworkContraints())
                        .addTag("one_shot_tag")
                        .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                            TimeUnit.MILLISECONDS)
                        .build();
        WorkManager
                .getInstance(mContext)
                .enqueueUniqueWork("one_shot_unique",ExistingWorkPolicy.KEEP,
                        (OneTimeWorkRequest) oneShotRequest);

    }

    public void startLiveSync(){
        fetchNow();
        schedule();
    }

    /**
     * PeriodicWorkRequest have minimum time of 15 minutes
     * so here I am doing it manually till the app is open in foreground
     * once the app is stopped sync stops
     * Syncing every 15 seconds
     */
    private void schedule(){
        Log.i(TAG, "startLiveSync() started");
        if(mLiveTimer == null)
            mLiveTimer = new Timer();
        mLiveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fetchNow();
            }
        },1000,SYNC_INTERVAL);//every 15 seconds
    }

    public void stopLiveSync(){
        Log.i(TAG, "LiveSync() Stop");
        if(mLiveTimer != null) {
            mLiveTimer.cancel();
            mLiveTimer = null;
        }
        WorkManager.getInstance(mContext).cancelAllWork();
    }
}
