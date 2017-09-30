package com.nigelbrown.fluxion;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.nigelbrown.fluxion.Annotation.Action;
import com.nigelbrown.fluxion.Annotation.FluxRecyclerView;
import com.nigelbrown.fluxion.Annotation.React;
import com.nigelbrown.fluxion.Annotation.Store;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

import static javax.xml.transform.OutputKeys.METHOD;

/**
 * Created by Nigel.Brown on 8/23/2017.
 */
public class Flux implements Application.ActivityLifecycleCallbacks {
	/**
	 * Cache react methods for each class.
	 */
	private static final ConcurrentMap<Object, Set<Method>> REACT_CACHE = new ConcurrentHashMap<Object, Set<Method>>();
	/**
	 * Cache action methods for each store class.
	 */
	private static final ConcurrentMap<Object, Set<Method>> ACTIONS_CACHE = new ConcurrentHashMap<Object, Set<Method>>();
	public static final String CLASS = "class";
	public static final String ACTION = "action";
	public static final String REACTION = "reaction";
	public static String TAG = "RxFlux";
	private static Flux sInstance;

	private Flux(Application application) {
		application.registerActivityLifecycleCallbacks(this);
	}

	public static Flux getsInstance() {
		return sInstance;
	}

	public static Flux init(Application application) {
		if(sInstance != null) { return sInstance; }

		return sInstance = new Flux(application);
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle bundle) {
		registerReactionSubscriber(activity);
		final ExecutorService activityExecutor = Executors.newFixedThreadPool(1);
		activityExecutor.execute(new RecyclerViewInstance(activity));
		((AppCompatActivity)activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
			@Override
			public void onFragmentAttached(FragmentManager fragmentManager, final Fragment fragment, Context context) {
				super.onFragmentAttached(fragmentManager, fragment, context);
				registerComponentAndAllMembers(fragment);
			}

			@Override
			public void onFragmentDetached(FragmentManager fragmentManager,final Fragment fragment) {
				unregisterFragmentAndAllMembers(fragment);
				super.onFragmentDetached(fragmentManager, fragment);
			}

			@Override
			public void onFragmentResumed(FragmentManager fragmentManager,final Fragment fragment) {
				super.onFragmentResumed(fragmentManager, fragment);
				registerComponentAndAllMembers(fragment);
			}

			@Override
			public void onFragmentPaused(FragmentManager fragmentManager, Fragment fragment) {
				unregisterFragmentAndAllMembers(fragment);
				super.onFragmentPaused(fragmentManager, fragment);
			}
		}, true);
	}

	@Override
	public void onActivityStarted(Activity activity) {
	}

	@Override
	public void onActivityResumed(Activity activity) {
	}

	@Override
	public void onActivityPaused(Activity activity) {
	}

	@Override
	public void onActivityStopped(Activity activity) {
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		REACT_CACHE.remove(activity);
	}

	void registerActionSubscriber(Object storeClass) {
		ExecutorService storeExecutor = Executors.newFixedThreadPool(1);
		if(storeClass.getClass().isAnnotationPresent(Store.class)) {
			storeExecutor.execute(new MethodsWithActionAnnotationHelperRunnable(storeClass));
		}
	}

	public void registerReactionSubscriber(Object viewClass) {
		if(! REACT_CACHE.containsKey(viewClass)) {
			Set<Method> classMethods = new HashSet<>();
			for(Method method : viewClass.getClass().getDeclaredMethods()) {
				Class[] paramTypes = method.getParameterTypes();
				if(method.isAnnotationPresent(React.class) && paramTypes.length == 1 && paramTypes[0].equals(Reaction.class)) {
					classMethods.add(method);
				}
			}
			REACT_CACHE.put(viewClass, classMethods);
		}
	}

	Observable emitAction(final FluxAction action) {
		return Observable.just(action).create(new ObservableOnSubscribe() {
			@Override
			public void subscribe(@NonNull ObservableEmitter e) throws Exception {
				for(Object parentClass : ACTIONS_CACHE.keySet()) {
					for(Method method : ACTIONS_CACHE.get(parentClass)) {
						if(action.getType().equals(method.getAnnotation(Action.class).actionType())) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put(METHOD, method);
							map.put(CLASS, parentClass);
							map.put(ACTION, action);
							e.onNext(map);
						}
					}
				}
				e.onComplete();
			}
		});
	}

	private void unregisterFragmentAndAllMembers(final Object parentClass){
		REACT_CACHE.remove(parentClass);
		final ExecutorService classExecutor = Executors.newFixedThreadPool(1);
		classExecutor.execute(new Thread(new Runnable() {
			@Override
			public void run() {
				Field[] classMemberFields = parentClass.getClass().getDeclaredFields();
				for (Field field: classMemberFields) {
					if(field.getAnnotation(FluxRecyclerView.class) != null ) {
						REACT_CACHE.remove(field);
					}
				}
			}
		}));
	}

	private void registerComponentAndAllMembers(final Object parentClass){
		final ExecutorService classExecutor = Executors.newFixedThreadPool(1);
		classExecutor.execute(new Thread(new Runnable() {
			@Override
			public void run() {
				registerReactionSubscriber(parentClass);
				Field[] classMemberFields = parentClass.getClass().getDeclaredFields();
				for (Object field: classMemberFields) {
					if(((Field)field).getAnnotation(FluxRecyclerView.class) != null ) {
						Object instance = field.getClass().getClass();
						if(RecyclerView.Adapter.class.isAssignableFrom(field.getClass().getClass())){
							registerReactionSubscriber(field);
						}
					}
				}
			}
		}));
	}

	Observable emitReaction(final Reaction reaction) {
		return Observable.just(reaction).create(new ObservableOnSubscribe() {
			@Override
			public void subscribe(@NonNull ObservableEmitter e) throws Exception {
				for(Object parentClass : REACT_CACHE.keySet()) {
					for(Method method : REACT_CACHE.get(parentClass)) {
						if(reaction.getType().equals(method.getAnnotation(React.class).reactionType())) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put(METHOD, method);
							map.put(CLASS, parentClass);
							map.put(REACTION, reaction);
							e.onNext(map);
						}
					}
				}
				e.onComplete();
			}
		});
	}

	public void unregesterReactionSubscriber(Object view){
		if(REACT_CACHE.containsKey(view)){
			REACT_CACHE.remove(view);
		}
	}

	private class MethodsWithActionAnnotationHelperRunnable implements Runnable {
		private Object parentClass;

		public MethodsWithActionAnnotationHelperRunnable(Object parentClass) {
			this.parentClass = parentClass;
		}

		public void run() {
			if(! ACTIONS_CACHE.containsKey(parentClass)) {
				Set<Method> classMethods = new HashSet<>();
				for(Method method : parentClass.getClass().getDeclaredMethods()) {
					Class[] paramTypes = method.getParameterTypes();
					if(method.isAnnotationPresent(Action.class) && paramTypes.length == 1 && paramTypes[0].equals(FluxAction.class)) {
						classMethods.add(method);
					}
				}
				ACTIONS_CACHE.put(parentClass, classMethods);
			}
		}
	}

	private class RecyclerViewInstance implements Runnable {
		private Object mParentClass;

		public RecyclerViewInstance(Object viewClass) {
			this.mParentClass = viewClass;
		}

		@Override
		public void run() {
			Field[] classMemberFields = mParentClass.getClass().getDeclaredFields();
			for (Field field: classMemberFields) {
				if(field.getAnnotation(FluxRecyclerView.class) != null ) {
					Class<?> classType = field.getType();
					if(RecyclerView.Adapter.class.isAssignableFrom(classType)){
						registerReactionSubscriber(field);
					}
				}
			}
		}
	}
}
