package com.nigelbrown.fluxion;

import android.support.v4.util.ArrayMap;

/**
 * Represents operation to be performed by one or more {@link FluxionStore stores}.
 * The action must include a type and may include data to be used by stores when the action is being performed.
 */
public class FluxionAction {
    private final String mType;
    private final ArrayMap<String, Object> mData;

    FluxionAction(String type, ArrayMap<String, Object> data) {
        this.mType = type;
        this.mData = data;
    }

    /**
     * Set the action's type.
     *
     * @param type An identifier for the action. Action type strings are used throughout the Flux architectural flow
     *             and should be publicly accessible.
     * @return A builder object to allow for chaining of calls to set methods
     */
    public static Builder type(String type) {
        return new Builder().with(type);
    }

    /**
     * @return The action's type
     */
    public String getType() {
        return mType;
    }

    /**
     * @return A map of the action's data
     */
    public ArrayMap<String, Object> getData() {
        return mData;
    }

    /**
     * Retrieves a specific data object from the action's data map.
     *
     * @param key The key of the data being requested
     * @return The data object corresponding to the entered key
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) mData.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof FluxionAction)) { return false; }
        FluxionAction fluxionAction = (FluxionAction) o;
        if (!mType.equals(fluxionAction.mType)) { return false; }
        return !(mData != null ? !mData.equals(fluxionAction.mData) : fluxionAction.mData != null);
    }

    @Override
    public int hashCode() {
        int result = mType.hashCode();
        result = 31 * result + (mData != null ? mData.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FluxionAction{" + "mType='" + mType + '\'' + ", mData=" + mData + '}';
    }

    static class Builder {
        private String type;
        private ArrayMap<String, Object> data;

        private Builder with(String type) {
            if (type == null) {
                throw new IllegalArgumentException("Type may not be null.");
            }
            this.type = type;
            this.data = new ArrayMap<>();
            return this;
        }

        Builder bundle(String key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("Key may not be null.");
            }
            if (value == null) {
                throw new IllegalArgumentException("Value may not be null.");
            }
            data.put(key, value);
            return this;
        }

        FluxionAction build() {
            if (type == null || type.isEmpty()) {
                throw new IllegalArgumentException("At least one key is required.");
            }
            return new FluxionAction(type, data);
        }
    }
}
