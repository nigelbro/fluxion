package com.nigelbrown.fluxion;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public class FluxionBus {
	private static FluxionBus sInstance;
	private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());

	private FluxionBus() {
	}

	public synchronized static FluxionBus getInstance() {
		if(sInstance == null) {
			sInstance = new FluxionBus();
		}
		return sInstance;
	}

	public void send(Object o) {
		mBus.onNext(o);
	}

	public Observable<Object> get() {
		return mBus;
	}

	public boolean hasObservers() {
		return mBus.hasObservers();
	}
}
