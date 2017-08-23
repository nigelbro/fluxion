package com.nigelbrown.fluxion;

/**
 * Created by Nigel.Brown on 5/12/2017.
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
