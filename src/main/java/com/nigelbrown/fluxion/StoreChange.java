package com.nigelbrown.fluxion;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public class StoreChange {
	private String mStoreId;
	private FluxionAction mFluxionAction;

	public StoreChange(String storeId, FluxionAction fluxionAction) {
		this.mStoreId = storeId;
		this.mFluxionAction = fluxionAction;
	}

	public FluxionAction getFluxionAction() {
		return mFluxionAction;
	}

	public String getStoreId() {
		return mStoreId;
	}
}
