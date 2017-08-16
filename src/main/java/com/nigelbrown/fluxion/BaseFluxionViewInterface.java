package com.nigelbrown.fluxion;

/**
 * Interface definition for callbacks invoked by the {@link FluxionDispatcher dispatcher} following
 * {@link FluxionStore store} changes or errors.
 * This interface should be implemented by any non-activity class to be considered a "view" in the flux flow.
 * Possible implementors include fragments and services. The application's parent activity should implement {@link FluxionViewInterface}.
 */
public interface BaseFluxionViewInterface {
    /**
     * Called when a change is posted by a store. When called, the view can react to the change
     *
     * @param reaction Contains the type of action to be reacted upon and any data provided
     *                 by the store to be used when reacting to the store change.
     */
    void onReact(Reaction reaction);
    /**
     * Called if an error occurs during the creation of an action.
     *
     * @param error Contains the error ({@link Throwable}) that occurred.
     */
    void onActionError(FluxionActionError error);
    /**
     * Called whenever a {@link FluxionStore store} posts an error using {@link FluxionStore#postChangeError(StoreChangeError) postChangeError}
     *
     * @param error Contains the error ({@link Throwable}) posted.
     */
    void onStoreChangedError(StoreChangeError error);
}
