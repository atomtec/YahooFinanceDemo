package com.f11.yahoofinance.data.sync;

import com.f11.yahoofinance.data.sync.sdk.RemoteStockProviderSDK;

public class RemoteProviderManager {

    private static RemoteProviderManager INSTANCE;

    private RemoteProviderManager(){};

    public static synchronized RemoteProviderManager getInstance (){
        if (INSTANCE == null){
            INSTANCE = new RemoteProviderManager();
        }
        return INSTANCE;
    }

    public RemoteStockProvider getSDKProvider(){
        return new RemoteStockProviderSDK();
    }

    public RemoteStockProvider getAPIProvider(){
        throw new UnsupportedOperationException("DirectAPI Not Implemented");
    }

}
