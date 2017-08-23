package com.nigelbrown.fluxion;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.nigelbrown.fluxion.Annotation.Action;
import com.nigelbrown.fluxion.Annotation.React;
import com.nigelbrown.fluxion.Annotation.Store;

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

/**
 * Created by Nigel.Brown on 8/23/2017.
 */
public class Flux implements Application.ActivityLifecycleCallbacks {
	/**
	 * Cache react methods for each class.
	 */
	private static final ConcurrentMap<Class<?>, Set<Method>> REACT_CACHE = new ConcurrentHashMap<Class<?>, Set<Method>>();
	/**
	 * Cache action methods for each store class.
	 */
	private static final ConcurrentMap<Class<?>, Set<Method>> ACTIONS_CACHE = new ConcurrentHashMap<Class<?>, Set<Method>>();
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
		final ExecutorService classExecutor = Executors.newFixedThreadPool(2);
		classExecutor.execute(new MethodsWithReactAnnotationHelperRunnable(activity.getClass()));
		((AppCompatActivity)activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
			@Override
			public void onFragmentAttached(FragmentManager fragmentManager, Fragment fragment, Context context) {
				super.onFragmentAttached(fragmentManager, fragment, context);
				classExecutor.execute(new MethodsWithReactAnnotationHelperRunnable(fragment.getClass()));
			}

			@Override
			public void onFragmentDetached(FragmentManager fragmentManager, Fragment fragment) {
				super.onFragmentDetached(fragmentManager, fragment);
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
	}

	void registerActionSubscriber(Class<?> storeClass) {
		ExecutorService storeExecutor = Executors.newFixedThreadPool(1);
		if(storeClass.isAnnotationPresent(Store.class)) {
			storeExecutor.execute(new MethodsWithActionAnnotationHelperRunnable(storeClass));
		}
	}

	public void registerReactionSubscriber(Class<?> viewClass) {
		ExecutorService reactionExecuter = Executors.newFixedThreadPool(1);
		reactionExecuter.execute(new MethodsWithReactAnnotationHelperRunnable(viewClass));
	}

	Observable emitAction(final FluxAction action) {
		return Observable.just(action).create(new ObservableOnSubscribe() {
			@Override
			public void subscribe(@NonNull ObservableEmitter e) throws Exception {
				for(Class<?> parentClass : ACTIONS_CACHE.keySet()) {
					for(Method method : ACTIONS_CACHE.get(parentClass)) {
						if(action.getType().equals(method.getAnnotation(Action.class).actionType())) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("METHOD", method);
							map.put("CLASS", parentClass);
							map.put("ACTION", action);
							e.onNext(map);
						}
					}
				}
				e.onComplete();
			}
		});
	}

	Observable emitReaction(final Reaction reaction) {
		return Observable.just(reaction).create(new ObservableOnSubscribe() {
			@Override
			public void subscribe(@NonNull ObservableEmitter e) throws Exception {
				for(Class<?> parentClass : ACTIONS_CACHE.keySet()) {
					for(Method method : ACTIONS_CACHE.get(parentClass)) {
						if(reaction.getType().equals(method.getAnnotation(React.class).reactionType())) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("METHOD", method);
							map.put("CLASS", parentClass);
							map.put("REACTION", reaction);
							e.onNext(map);
						}
					}
				}
				e.onComplete();
			}
		});
	}

	private class MethodsWithReactAnnotationHelperRunnable implements Runnable {
		private Class<?> parentClass;

		public MethodsWithReactAnnotationHelperRunnable(Class<?> parentClass) {
			this.parentClass = parentClass;
		}

		public void run() {
			if(! REACT_CACHE.containsKey(parentClass)) {
				Set<Method> classMethods = new HashSet<>();
				for(Method method : parentClass.getDeclaredMethods()) {
					Class[] paramTypes = method.getParameterTypes();
					if(method.isAnnotationPresent(React.class) && paramTypes.length == 1 && paramTypes[0].equals(Reaction.class)) {
						classMethods.add(method);
					}
				}
				REACT_CACHE.put(parentClass, classMethods);
			}
		}
	}

	private class MethodsWithActionAnnotationHelperRunnable implements Runnable {
		private Class<?> parentClass;

		public MethodsWithActionAnnotationHelperRunnable(Class<?> parentClass) {
			this.parentClass = parentClass;
		}

		public void run() {
			if(! ACTIONS_CACHE.containsKey(parentClass)) {
				Set<Method> classMethods = new HashSet<>();
				for(Method method : parentClass.getDeclaredMethods()) {
					Class[] paramTypes = method.getParameterTypes();
					if(method.isAnnotationPresent(Action.class) && paramTypes.length == 1 && paramTypes[0].equals(FluxAction.class)) {
						classMethods.add(method);
					}
				}
				ACTIONS_CACHE.put(parentClass, classMethods);
			}
		}
	}
}
