package com.nigelbrown.fluxion;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public abstract class FluxStore {

	public FluxStore() {}

	private Reaction newReaction(String reactionId,Object... data) {
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

	protected void emitReaction(String reactionId,Object... data) throws IllegalAccessException,InvocationTargetException {
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
		AnnotationHelper.callMethodsWithReactAnnotation(reactionBuilder.build());
	}
}
