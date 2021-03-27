package com.f11.yahoofinance.data.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.f11.yahoofinance.data.model.AppStock;

import java.util.List;

@Dao
public interface StockDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from stock_table ORDER BY symbol ASC")
    LiveData<List<AppStock>> getAllStocks();

    //When querying directly without observer
    @Query("SELECT * from stock_table ORDER BY symbol ASC")
    List<AppStock> getAllStocksSync();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppStock stock);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AppStock> stocks);



    @Query("DELETE FROM stock_table")
    void deleteAll();

    //TODO add delete by symbol
}
