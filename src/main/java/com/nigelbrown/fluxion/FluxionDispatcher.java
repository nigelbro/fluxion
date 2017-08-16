package com.nigelbrown.fluxion;

import android.support.v4.util.ArrayMap;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * The dispatcher sends {@link FluxionAction actions} to all {@link FluxionStore stores}
 * via registered callbacks. Stores must be registered with the dispatcher to receive actions.
 */
public class FluxionDispatcher {
    private static FluxionDispatcher sInstance;
    private final FluxionBus mBus;
    private ArrayMap<String, Subscription> mFluxionActionMap;
    private ArrayMap<String, Subscription> mFluxionStoreMap;

    private FluxionDispatcher(FluxionBus bus) {
        this.mBus = bus;
        this.mFluxionActionMap = new ArrayMap<>();
        this.mFluxionStoreMap = new ArrayMap<>();
    }

    static synchronized FluxionDispatcher getInstance(FluxionBus fluxionBus) {
        if (sInstance == null) { sInstance = new FluxionDispatcher(fluxionBus); }
        return sInstance;
    }

    public <T extends FluxionActionInterface> void registerFluxionAction(final T object) {
        final String tag = object.getClass().getSimpleName();
        Subscription subscription = mFluxionActionMap.get(tag);
        if (subscription == null || subscription.isUnsubscribed()) {
            mFluxionActionMap.put(tag, mBus.get().filter(new Func1<Object, Boolean>() {
                @Override
                public Boolean call(Object o) {
                    return o instanceof FluxionAction;
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    object.onFluxionAction((FluxionAction) o);
                }
            }));
        }
    }

    public <T extends BaseFluxionViewInterface> void registerReaction(final T object) {
        final String tag = object.getClass().getSimpleName();
        Subscription subscription = mFluxionStoreMap.get(tag);
        if (subscription == null || subscription.isUnsubscribed()) {
            mFluxionStoreMap.put(tag, mBus.get().filter(new Func1<Object, Boolean>() {
                @Override
                public Boolean call(Object o) {
                    return o instanceof Reaction;
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    object.onReact((Reaction) o);
                }
            }));
        }
        registerFluxionStoreError(object);
    }

    public <T extends BaseFluxionViewInterface> void registerFluxionStoreError(final T object) {
        final String tag = object.getClass().getSimpleName() + "_error";
        Subscription subscription = mFluxionStoreMap.get(tag);
        if (subscription == null || subscription.isUnsubscribed()) {
            mFluxionStoreMap.put(tag, mBus.get().filter(new Func1<Object, Boolean>() {
                @Override
                public Boolean call(Object o) {
                    return o instanceof StoreChangeError;
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    object.onStoreChangedError((StoreChangeError) o);
                }
            }));
        }
        registerFluxionError(object);
    }

    public <T extends BaseFluxionViewInterface> void registerFluxionError(final T object) {
        final String tag = object.getClass().getSimpleName() + "_error";
        Subscription subscription = mFluxionActionMap.get(tag);
        if (subscription == null || subscription.isUnsubscribed()) {
            mFluxionActionMap.put(tag, mBus.get().filter(new Func1<Object, Boolean>() {
                @Override
                public Boolean call(Object o) {
                    return o instanceof FluxionActionError;
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    object.onActionError((FluxionActionError) o);
                }
            }));
        }
    }

    public <T extends FluxionActionInterface> void unregisterFluxionAction(final T object) {
        String tag = object.getClass().getSimpleName();
        Subscription subscription = mFluxionActionMap.get(tag);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            mFluxionActionMap.remove(tag);
        }
    }

    private <T extends BaseFluxionViewInterface> void unregisterFluxionStore(final T object) {
        String tag = object.getClass().getSimpleName();
        Subscription subscription = mFluxionStoreMap.get(tag);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            mFluxionStoreMap.remove(tag);
        }
        unregisterFluxionError(object);
        unregisterFluxionStoreError(object);
    }

    private <T extends BaseFluxionViewInterface> void unregisterFluxionError(final T object) {
        String tag = object.getClass().getSimpleName() + "_error";
        Subscription subscription = mFluxionActionMap.get(tag);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            mFluxionActionMap.remove(tag);
        }
    }

    private <T extends BaseFluxionViewInterface> void unregisterFluxionStoreError(final T object) {
        String tag = object.getClass().getSimpleName() + "_error";
        Subscription subscription = mFluxionStoreMap.get(tag);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            mFluxionStoreMap.remove(tag);
        }
    }

    /**
     * Unregisters all views from the dispatcher.
     */
    public synchronized void unregisterAll() {
        for (Subscription subscription : mFluxionActionMap.values()) {
            subscription.unsubscribe();
        }
        for (Subscription subscription : mFluxionStoreMap.values()) {
            subscription.unsubscribe();
        }
        mFluxionActionMap.clear();
        mFluxionStoreMap.clear();
    }

    /**
     * Unregister a {@link BaseFluxionViewInterface} view from the dispatcher
     *
     * @param object the view to be unregistered.
     */
    public synchronized <T extends BaseFluxionViewInterface> void unregister(final T object) {
        unregisterFluxionError(object);
        unregisterFluxionStore(object);
        unregisterFluxionStoreError(object);
    }

    void postFluxionAction(final FluxionAction action) {
        mBus.send(action);
    }

    void postFluxionActionError(final FluxionActionError actionError) {
        mBus.send(actionError);
    }

    void postReaction(final Reaction reaction) {
        mBus.send(reaction);
    }

    void postFluxionStoreChangeError(final StoreChangeError storeChange) {
        mBus.send(storeChange);
    }
}
