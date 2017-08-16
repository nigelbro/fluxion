package com.nigelbrown.fluxion;

import android.support.v4.util.ArrayMap;

/**
 * Represents a store change to be reacted upon by one or more flux views.
 * The reaction must include a type and may include data to be handed to the views
 * when the reaction is posted.
 */
public class Reaction {
    private final String mType;
    private final ArrayMap<String, Object> mData;

    Reaction(String type, ArrayMap<String, Object> data) {
        this.mType = type;
        this.mData = data;
    }

    /**
     * Set the reaction's type.
     *
     * @param type An identifier for the reaction. Action/Reaction type strings are used throughout the Flux architectural flow
     *             and should be publicly accessible.
     * @return A builder object to allow for chaining of calls to set methods
     */
    public static Builder type(String type) {
        return new Builder().with(type);
    }

    public String getType() {
        return mType;
    }

    /**
     * @return A map of the reaction's data
     */
    public ArrayMap<String, Object> getData() {
        return mData;
    }

    /**
     * Retrieves a specific data object from the reaction's data map.
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
        if (!(o instanceof Reaction)) { return false; }
        Reaction reaction = (Reaction) o;
        if (!mType.equals(reaction.mType)) { return false; }
        return !(mData != null ? !mData.equals(reaction.mData) : reaction.mData != null);
    }

    @Override
    public int hashCode() {
        int result = mType.hashCode();
        result = 31 * result + (mData != null ? mData.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FluxionReaction{" + "mType='" + mType + '\'' + ", mData=" + mData + '}';
    }

    public static class Builder {
        private String type;
        private ArrayMap<String, Object> data;

        Builder with(String type) {
            if (type == null) {
                throw new IllegalArgumentException("Type may not be null.");
            }
            this.type = type;
            this.data = new ArrayMap<>();
            return this;
        }

        public Builder bundle(String key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("Key may not be null.");
            }
            if (value == null) {
                throw new IllegalArgumentException("Value may not be null.");
            }
            data.put(key, value);
            return this;
        }

        public Reaction build() {
            if (type == null || type.isEmpty()) {
                throw new IllegalArgumentException("At least one key is required.");
            }
            return new Reaction(type, data);
        }
    }
}
