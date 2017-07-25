package com.nigelbrown.fluxion;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public abstract class FluxionStore implements FluxionActionInterface {
	private final FluxionDispatcher mDispatcher;

	public FluxionStore(FluxionDispatcher dispatcher) {
		this.mDispatcher = dispatcher;
	}

	public void register() {
		mDispatcher.registerFluxionAction(this);
	}

	protected void postChange(StoreChange change) {
		mDispatcher.postFluxionStoreChange(change);
	}

	protected void postChangeError(StoreChangeError change) {
		mDispatcher.postFluxionStoreChangeError(change);
	}
}
