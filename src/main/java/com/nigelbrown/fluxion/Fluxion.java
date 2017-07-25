package com.nigelbrown.fluxion;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public class Fluxion implements Application.ActivityLifecycleCallbacks {

	public static String TAG = "Fluxion";
	private static Fluxion sInstance;
	private final FluxionBus mFluxionBus;
	private final FluxionDispatcher mDispatcher;
	private final SubscriptionManager mSubscriptionManager;

	private Fluxion(Application application) {
		this.mFluxionBus = FluxionBus.getInstance();
		this.mDispatcher = FluxionDispatcher.getInstance(mFluxionBus);
		this.mSubscriptionManager = SubscriptionManager.getInstance();
		application.registerActivityLifecycleCallbacks(this);
	}

	public static Fluxion init(Application application) {
		if (sInstance != null)
			return sInstance;
		return sInstance = new Fluxion(application);
	}

	public static void shutdown() {
		if (sInstance == null) return;
		sInstance.mSubscriptionManager.clear();
		sInstance.mDispatcher.unregisterAll();
	}

	/**
	 * @return the sInstance of the FluxionBus in case you want to reused for something else
	 */
	public FluxionBus getFluxionBus() {
		return mFluxionBus;
	}

	/**
	 * @return the sInstance of the mDispatcher
	 */
	public FluxionDispatcher getDispatcher() {
		return mDispatcher;
	}

	/**
	 * @return the sInstance of the subscription manager in case you want to reuse for something else
	 */
	public SubscriptionManager getSubscriptionManager() {
		return mSubscriptionManager;
	}

	@Override public void onActivityCreated(Activity activity, Bundle bundle) {
		if (activity instanceof FluxionViewInterface) {
			((FluxionViewInterface) activity).onRegisterStores();
			mDispatcher.registerFluxionStore((FluxionViewInterface) activity);
			((AppCompatActivity)activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
				@Override
				public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
					if(f instanceof BaseFluxionViewInterface){
						mDispatcher.registerFluxionStore((BaseFluxionViewInterface) f);
					}
				}

				@Override
				public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
					if(f instanceof BaseFluxionViewInterface){
						mDispatcher.unregister((BaseFluxionViewInterface) f);
					}
				}
			},true);

		}
	}

	@Override public void onActivityStarted(Activity activity) {

	}

	@Override public void onActivityResumed(Activity activity) {
	}

	@Override public void onActivityPaused(Activity activity) {
	}

	@Override public void onActivityStopped(Activity activity) {

	}

	@Override public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

	}

	@Override public void onActivityDestroyed(Activity activity) {
	}
}
