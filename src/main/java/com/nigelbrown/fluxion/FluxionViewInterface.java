package com.nigelbrown.fluxion;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public interface FluxionViewInterface extends BaseFluxionViewInterface {
	/**
	 * Fluxion method to let the view create the stores that need for this activity, this method is
	 * called every time the activity is created. Normally you will instantiate the store with the
	 * singleton instance. Should not use this for fragments since their parent should be already registering the stores necessary for the activity
	 */
	void onRegisterStores();
}
