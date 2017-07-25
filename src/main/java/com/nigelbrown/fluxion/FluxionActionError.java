package com.nigelbrown.fluxion;

import android.support.v4.util.ArrayMap;

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
