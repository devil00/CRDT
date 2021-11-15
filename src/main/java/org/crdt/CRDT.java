package org.crdt;

/**
 * All CRDT commom behaviour will be provided by this interface.
 * @param <T>
 */
public interface CRDT <T> {
    /**
     * Merge two instances of CRDt of same type based on their state.
     * @param other {@code CRDT}
     * @return merged CRDT
     */
    T merge(T other);
}
