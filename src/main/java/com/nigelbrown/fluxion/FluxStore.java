package com.nigelbrown.fluxion;

import java.lang.reflect.Method;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static javax.xml.transform.OutputKeys.METHOD;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public abstract class FluxStore {
	private Flux msFluxInstance;
	public FluxStore(Flux flux) {
		msFluxInstance = flux;
	}

	public void registerActionSubscriber(Object storeClass) {
		msFluxInstance.registerActionSubscriber(storeClass);
	}

	private Reaction newReaction(String reactionId, Object... data) {
		if(reactionId.isEmpty()) {
			throw new IllegalArgumentException("Type must not be empty");
		}
		if(data.length % 2 != 0) {
			throw new IllegalArgumentException("Data must be a valid list of key,value pairs");
		}
		Reaction.Builder reactionBuilder = Reaction.type(reactionId);
		int i = 0;
		while(i < data.length) {
			String key = (String)data[i++];
			Object value = data[i++];
			reactionBuilder.bundle(key, value);
		}
		return reactionBuilder.build();
	}

	protected void emitReaction(String reactionId, Object... data) {
		if(reactionId.isEmpty()) {
			throw new IllegalArgumentException("Type must not be empty");
		}
		if(data.length % 2 != 0) {
			throw new IllegalArgumentException("Data must be a valid list of key,value pairs");
		}
		Reaction.Builder reactionBuilder = Reaction.type(reactionId);
		int i = 0;
		while(i < data.length) {
			String key = (String)data[i++];
			Object value = data[i++];
			reactionBuilder.bundle(key, value);
		}
		final Reaction reaction = reactionBuilder.build();
		msFluxInstance.emitReaction(reaction).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(getReactionObserver());
	}

	Observer getReactionObserver() {
		return new Observer() {
			@Override
			public void onSubscribe(@NonNull Disposable d) {
			}

			@Override
			public void onNext(@NonNull Object o) {
				HashMap<String, Object> map = (HashMap<String, Object>)o;
				Method method = (Method)map.get(METHOD);
				Reaction reaction = (Reaction)map.get(Flux.REACTION);
				method.setAccessible(true);
				try {
					method.invoke(map.get(Flux.CLASS), reaction);
				}catch(Exception e) {
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
