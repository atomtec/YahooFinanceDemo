package com.f11.yahoofinance.data.model;

public class FetchStatus {
    public static final int STOCK_FOUND = 1;
    public static final int STOCK_NOT_FOUND =2 ;
    public static final int FETCH_ERROR = 3;
    public static final int FETCHING = 4;

    private boolean isFetchOpComplete = false;
    private int fetchStatus = FETCHING;

    public FetchStatus(){
        this.isFetchOpComplete = false;
        this.fetchStatus = FETCHING;
    }

    public void setFetchOpComplete(boolean isfetchOpComplete) {
        this.isFetchOpComplete = isfetchOpComplete;
    }

    public void setFetchStatus(int fetchStatus) {
        if(fetchStatus >= STOCK_FOUND && fetchStatus <= FETCHING) {
            this.fetchStatus = fetchStatus;
        }
    }

    public int getFetchStatus() {
        return fetchStatus;
    }

    public boolean isFetchOpComplete() {
        return isFetchOpComplete;
    }
}
