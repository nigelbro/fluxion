package com.nigelbrown.fluxion;

/**
 * A wrapper to be used when communicating store errors to registered views.
 */
public class StoreChangeError {
    private Throwable mThrowable;

    public StoreChangeError(Throwable throwable) {
        this.mThrowable = throwable;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
