package com.f11.yahoofinance.data.sync;

import com.f11.yahoofinance.data.model.AppStock;

import java.io.IOException;
import java.util.List;

public interface RemoteStockProvider {
    //TODO possible to have another model here but leaving for the demo
    List<AppStock> getRemoteStocks(List<AppStock> stocks) throws IOException;
    AppStock getStockBySymbol(String symbol) throws IOException;
}
