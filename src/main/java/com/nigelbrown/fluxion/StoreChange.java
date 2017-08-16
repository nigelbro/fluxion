package com.nigelbrown.fluxion;

// TODO: 8/16/2017 Is this still needed?
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
