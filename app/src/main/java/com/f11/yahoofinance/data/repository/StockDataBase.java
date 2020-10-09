package com.f11.yahoofinance.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.f11.yahoofinance.data.model.AppStock;

@Database(entities = {AppStock.class}, version = 1,  exportSchema = false)
public abstract class StockDataBase extends RoomDatabase {

    public abstract StockDao mStockDao();

    private static StockDataBase INSTANCE;

    public static StockDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StockDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StockDataBase.class, "stock_database")
                            .fallbackToDestructiveMigration()//not defining migration for demo
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
        }
    };


}