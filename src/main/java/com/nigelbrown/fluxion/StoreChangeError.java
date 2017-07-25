package com.nigelbrown.fluxion;

/**
 * Created by Nigel.Brown on 5/17/2017.
 */
public class StoreChangeError {
	private Throwable mThrowable;

	private StoreChangeError(Throwable throwable) {
		this.mThrowable = throwable;
	}

	public Throwable getThrowable() {
		return mThrowable;
	}
}
