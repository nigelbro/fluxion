package com.nigelbrown.fluxion;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Nigel.Brown on 5/12/2017.
 */
public abstract class FluxActionCreator {

	public FluxActionCreator() {}

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
		AnnotationHelper.callMethodsWithActionAnnotation(fluxAction);
	}
}
