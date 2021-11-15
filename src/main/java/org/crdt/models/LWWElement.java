package org.crdt.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represent CRDT element with timestamp.
 * <p>
 *     For every element created, default current system timestamp with nano seconds precision will be used.
 * </p>
 * @param <T> any datatype
 */
@Data
@AllArgsConstructor
public final class LWWElement<T> {
    private T element;
    private Long timestamp;

    public LWWElement(T element) {
        this.element = element;
        this.timestamp = System.nanoTime();
    }

}
