package com.nigelbrown.fluxion;

/**
 * A wrapper for an exception that occurs in the process of posting an action.
 */
public class FluxionActionError {
    private Throwable mThrowable;

    public FluxionActionError(Throwable throwable) {
        this.mThrowable = throwable;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
