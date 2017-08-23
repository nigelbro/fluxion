package com.nigelbrown.fluxion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public abstract class FluxActionCreator {

	private Flux msFluxInstance;
	public FluxActionCreator(Flux flux) {
		msFluxInstance = flux;
	}

	/**
	 * This is the prefered method for posting actions to the subscription manager
	 * @param actionId
	 * @param data
	 */
	protected void emitAction(String actionId, Object... data)  throws IllegalAccessException,InvocationTargetException {
		if(actionId.isEmpty()) {
			throw new IllegalArgumentException("Type must not be empty");
		}
		if(data.length % 2 != 0) {
			throw new IllegalArgumentException("Data must be a valid list of key,value pairs");
		}
		FluxAction.Builder actionBuilder = FluxAction.type(actionId);
		int i = 0;
		while(i < data.length) {
			String key = (String)data[i++];
			Object value = data[i++];
			actionBuilder.bundle(key, value);
		}
		final FluxAction fluxAction = actionBuilder.build();
		msFluxInstance.emitAction(fluxAction)
		    .subscribeOn(Schedulers.computation())
			.observeOn(AndroidSchedulers.mainThread())
		.subscribe(getActionObserver());
	}

	Observer getActionObserver(){
		return new Observer() {
			@Override
			public void onSubscribe(@NonNull Disposable d) {
			}

			@Override
			public void onNext(@NonNull Object o) {
				HashMap<String,Object> map = (HashMap<String, Object>)o;
				Method method = (Method)map.get("METHOD");
				FluxAction action = (FluxAction)map.get("ACTION");
				try {
					method.invoke(map.get("CLASS"),action);
				}catch(Exception e){

				}
			}

			@Override
			public void onError(@NonNull Throwable e) {
			}

			@Override
			public void onComplete() {
			}
		};

	}
}
