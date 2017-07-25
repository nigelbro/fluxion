package com.nigelbrown.fluxion;

import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;

import rx.Subscription;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public class SubscriptionManager {
	private static SubscriptionManager sInstance;
	private ArrayMap<String, Pair<Integer, Subscription>> mMap;

	private SubscriptionManager() {
		mMap = new ArrayMap<>();
	}

	public static synchronized SubscriptionManager getInstance() {
		if(sInstance == null) { sInstance = new SubscriptionManager(); }
		return sInstance;
	}

	/**
	 * Given an action and a subscription, add the new subscription and unsubscribe if there
	 * was an existing one.
	 */
	public void add(FluxionAction action, Subscription subscription) {
		Pair<Integer, Subscription> old = mMap.put(action.getType(), getPair(action, subscription));
		if(old != null && ! old.second.isUnsubscribed()) { old.second.unsubscribe(); }
	}

	private Pair<Integer, Subscription> getPair(FluxionAction action, Subscription subscription) {
		return new Pair<>(action.hashCode(), subscription);
	}

	/**
	 * Remove an rxAction and unsubscribe from it
	 */
	public void remove(FluxionAction action) {
		Pair<Integer, Subscription> old = mMap.remove(action.getType());
		if(old != null && ! old.second.isUnsubscribed()) { old.second.unsubscribe(); }
	}

	/**
	 * Checks if the action (with the same params) is already running a subscription
	 *
	 * @return true if the exact action is inside the map and running
	 */
	public boolean contains(FluxionAction action) {
		Pair<Integer, Subscription> old = mMap.get(action.getType());
		return (old != null && old.first == action.hashCode() && ! old.second.isUnsubscribed());
	}

	/**
	 * Clear all the subscriptions
	 */
	public synchronized void clear() {
		if(mMap.isEmpty()) { return; }
		for(Pair<Integer, Subscription> pair : mMap.values()) {
			if(pair != null) {
				if(! pair.second.isUnsubscribed()) { pair.second.unsubscribe(); }
			}
		}
	}
}
