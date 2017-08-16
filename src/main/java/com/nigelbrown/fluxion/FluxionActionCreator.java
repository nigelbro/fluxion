package com.nigelbrown.fluxion;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * The Action Creator class, which should be used as a parent for all action creators.
 * <p><i></i>Action Creators</i> contain helper methods that create an {@link FluxionAction action} from method parameters,
 * assign it to a type, and provide the action to the {@link FluxionDispatcher dispatcher}. Once an action is received, the
 * dispatcher passes the action to the {@link FluxionStore#onFluxionAction(FluxionAction) onFluxionAction}
 * method of all {@link FluxionStore stores}.</p>
 */
public abstract class FluxionActionCreator {
    private final FluxionDispatcher mDispatcher;
    private final SubscriptionManager mManager;

    public FluxionActionCreator(FluxionDispatcher dispatcher, SubscriptionManager manager) {
        this.mDispatcher = dispatcher;
        this.mManager = manager;
    }

    private FluxionAction newFluxionAction(String actionId, Object... data) {
        if (actionId.isEmpty()) {
            throw new IllegalArgumentException("Type must not be empty");
        }
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("Data must be a valid list of key,value pairs");
        }
        FluxionAction.Builder actionBuilder = FluxionAction.type(actionId);
        int i = 0;
        while (i < data.length) {
            String key = (String) data[i++];
            Object value = data[i++];
            actionBuilder.bundle(key, value);
        }
        return actionBuilder.build();
    }

    /**
     * Creates a new action and posts the action to the dispatcher.
     *
     * @param actionId The ID of the action requested. Storing IDs in a public interface is recommended,
     *                 since they are used by Action Creators, Stores, and Views.
     * @param data     (Optional) Data provided by the view to be used in the store when handling the requested action.
     *                 Data must be provided in Key/Value pairs. Keys should be strings. A value can be any type of object
     *                 <p>
     *                 <p>Example - This is what an actions creator method using postAction might look like:</p>
     *                 <pre> {@code
     *                                 public void attemptLogin(String username, String password) {
     *                                      postAction(Actions.ATTEMPT_LOGIN, Keys.USERNAME, username, Keys.PASSWORD, password);
     *                                 }
     *                                 } </pre>
     * @throws IllegalArgumentException - If the actionId is empty or if the data provided is not a valid set of key value pairs
     */
    protected void postAction(String actionId, Object... data) {
        final FluxionAction action = newFluxionAction(actionId, data);
        Subscription subscription = Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object s) {/**/}
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {/**/}
                }, new Action0() {
                    @Override
                    public void call() {mDispatcher.postFluxionAction(action);}
                });
        mManager.add(action, subscription);
    }

    private void postError(Throwable throwable) {
        mDispatcher.postFluxionActionError(new FluxionActionError(throwable));
    }
}
