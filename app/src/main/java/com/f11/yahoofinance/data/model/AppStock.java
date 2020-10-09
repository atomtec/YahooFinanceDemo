package com.f11.yahoofinance.data.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * For simple use cases we are using
 * using one model and entity
 *
 */
@Entity(tableName = "stock_table")
public class AppStock {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "symbol")
    private String mSymbol;
    @ColumnInfo(name = "price")
    private float mPrice;
    @ColumnInfo(name = "change")
    private float mChange;
    @ColumnInfo(name = "absolutechange")
    private float mAbsolutechange;
    @ColumnInfo(name = "stockname")
    private String mStockname;



    public AppStock(@NonNull String symbol, @NonNull float price ,
                    @NonNull float change , @NonNull float absolutechange,
                    @NonNull String stockname) {
        this.mSymbol = symbol;
        this.mPrice = price;
        this.mChange = change;
        this.mAbsolutechange = absolutechange;
        this.mStockname = stockname;
    }

    @NonNull
    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(@NonNull String symbol) {
        this.mSymbol = symbol;
    }

    @NonNull
    public float getPrice() {
        return mPrice;
    }

    public void setPrice(@NonNull float price) {
        this.mPrice = price;
    }

    @NonNull
    public float getChange() {
        return mChange;
    }

    public void setChange(@NonNull  float change) {
        this.mChange = change;
    }

    @NonNull
    public float getAbsolutechange() {
        return mAbsolutechange;
    }

    public void setAbsolutechange(@NonNull float absolutechange) {
        this.mAbsolutechange = absolutechange;
    }

    @NonNull
    public String getStockname() {
        return mStockname;
    }

    public void setStockname(@NonNull String stockname) {
        this.mStockname = stockname;
    }

}
