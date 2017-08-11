package com.nigelbrown.fluxion;

/**
 * Created by Nigel.Brown on 5/17/2017.
 */
public interface BaseFluxionViewInterface {
	/**
	 * Deprecated method
	 */
	@Deprecated
	void onStoreChanged(StoreChange change);

	/**
	 * All the stores will call this method so the view can react and request the needed data
	 */
	void onReact(Reaction reaction);
	/**
	 * All action creators will call this if they have to post an error with the emitting observable.
	 */
	void onActionError(FluxionActionError error);
	/**
	 * All stores will be able able to post errors and activities or fragments will be able to react to those errors.
	 */
	void onStoreChangedError(StoreChangeError error);
}
