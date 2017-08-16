package com.nigelbrown.fluxion;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * The bus communicates store change events ({@link Reaction reactions} or
 * {@link StoreChangeError errors}) to all views implementing {@link BaseFluxionViewInterface}.
 */
public class FluxionBus {
    private static FluxionBus sInstance;
    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());

    private FluxionBus() {}

    public synchronized static FluxionBus getInstance() {
        if (sInstance == null) {
            sInstance = new FluxionBus();
        }
        return sInstance;
    }

    void send(Object o) {mBus.onNext(o);}

    /**
     * @return the FluxionBus instance.
     */
    public Observable<Object> get() {return mBus;}

    /**
     * @return true if the bus has observers.
     */
    public boolean hasObservers() {return mBus.hasObservers();}
}
