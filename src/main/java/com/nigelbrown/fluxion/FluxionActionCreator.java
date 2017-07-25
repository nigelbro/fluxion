package com.nigelbrown.fluxion;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public abstract class FluxionActionCreator {
	private final FluxionDispatcher mDispatcher;
	private final SubscriptionManager mManager;

	public FluxionActionCreator(FluxionDispatcher dispatcher, SubscriptionManager manager) {
		this.mDispatcher = dispatcher;
		this.mManager = manager;
	}

	public FluxionAction newFluxionAction(String actionId,Object... data) {
		if(actionId.isEmpty()) {
			throw new IllegalArgumentException("Type must not be empty");
		}
		if(data.length % 2 != 0) {
			throw new IllegalArgumentException("Data must be a valid list of key,value pairs");
		}
		FluxionAction.Builder actionBuilder = FluxionAction.type(actionId);
		int i = 0;
		while(i < data.length) {
			String key = (String)data[i++];
			Object value = data[i++];
			actionBuilder.bundle(key, value);
		}
		return actionBuilder.build();
	}

	/**
	 * This is the prefered method for posting actions to the subscription manager
	 * @param actionId
	 * @param data
	 */
	public void postAction(String actionId, Object... data) {
		final FluxionAction action = newFluxionAction(actionId,data);
		Subscription subscription = Observable.empty()
		                                      .observeOn(AndroidSchedulers.mainThread())
		                                      .subscribe(new Action1<Object>() {
			                                      @Override
			                                      public void call(Object s) {/**/}
		                                      }, new Action1<Throwable>() {
			                                      @Override
			                                      public void call(Throwable throwable) {/**/}
		                                      }, new Action0() {
			                                      @Override
			                                      public void call() {mDispatcher.postFluxionAction(action);}
		                                      });
		mManager.add(action, subscription);
	}

	public void postError(Throwable throwable) {
		mDispatcher.postFluxionActionError(new FluxionActionError(throwable));
	}
}
