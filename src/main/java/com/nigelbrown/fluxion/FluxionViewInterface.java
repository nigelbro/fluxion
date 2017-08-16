package com.nigelbrown.fluxion;

/**
 * Implement this on Activity classes to leverage the Fluxion framework
 */
public interface FluxionViewInterface extends BaseFluxionViewInterface {
	/**
	 * Fluxion method to let the view create the stores that need for this activity, this method is
	 * called every time the activity is created. Normally you will instantiate the store with the
	 * singleton instance. Do not use this for fragments, since their parent activity should be responsible for registering all necessary stores.
	 */
	void onRegisterStores();
}
