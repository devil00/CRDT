package org.crdt;

import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.crdt.models.LWWBias;
import org.crdt.models.LWWElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Last-Write-Win set or LWWElementSet is the implementation of one of the CRDT.
 * @see <a href="https://www.wikiwand.com/en/Conflict-free_replicated_data_type>for more details on CRDT</a>
 *
 * <p>
 *     Two set addset and removeset  are being used in this CRDT implementation.
 *     Both set maintains the state of element as per the last write timestamp.
 *     Whenever an element is added to the CRDT, it will be added in addset and
 *     if it is removed from the CRDT that entry will be added in the removeSet.
 *     Internal set of elements will maintain the current elements available in the CRDT with the biasing strategy
 *     we choose {@link LWWBias}. Default biasing strategy is Add which means ,
 *     if an element is added or removed at the same time, then CRDT will add that element.
 * </p>
 * @param <E> any datatype(string,int etc)
 */
@Getter
public class LWWElementSet<E> implements CRDT<LWWElementSet<E>> {
    private Map<E, LWWElement<E>> addSet;
    private Map<E, LWWElement<E>> removeSet;
    private Set<E> elements;
    private LWWBias bias;

    /**
     * Constructor to create {@code LWWElement} with default addset and removeset.
     */
    public LWWElementSet() {
        this.addSet = new HashMap<>();
        this.removeSet = new HashMap<>();
        this.bias = LWWBias.ADD;
        this.elements = new HashSet<>();
    }

    /**
     * Constructor to create {@code LWWElement} with provided addset and removeset.
     * @param addSet set of elements added in this CRDT.
     * @param removeSet set of elements removed from this CRDT
     */
    public LWWElementSet(Set<LWWElement<E>> addSet, Set<LWWElement<E>> removeSet) {
        this();
        for (LWWElement el : addSet) {
            this.addSet.put((E)el.getElement(), el);
        }
        for (LWWElement el : removeSet) {
            this.removeSet.put((E)el.getElement(), el);
        }
    }

    /**
     * Add an element to CRDT.
     * @param element Element to be added
     */
    public void add(E element) {
        LWWElement el = new LWWElement<>(element);
        addSet.put(element, new LWWElement<>(element));
        updateElements(element);
    }

    /**
     * Removed element from CRDT.
     * @param element element to bre removed
     */
    public void remove(E element) {
        removeSet.put(element, new LWWElement<>(element));
        updateElements(element);
    }

    /**
     * Check if the element exists in the CRDT.
     * @param element
     * @return
     */
    public boolean contains(E element) {
        return elements.contains(element);
    }

    /**
     * Check if the all the elements exist in the CRDT.
     * @param elements
     * @return
     */
    public boolean containsAll(Collection<E> elements) {
        return elements.containsAll(elements);
    }

    /**
     * Check if the CRDT is empty or not.
     * @return
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Size of crdt {@code LWWElementSet}
     * @return
     */
    public int size() {
        return elements.size();
    }

    /**
     * Iterator to all the available elements in the set.
     * @return
     */
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    /**
     * Merge two {@link LWWElementSet}
     * @param other {@code CRDT}
     * @return
     */
    @Override
    public LWWElementSet<E> merge(LWWElementSet<E> other) {
        other.getAddSet().forEach((el, lwwEl) -> this.addSet.merge(el,
                lwwEl, (v1, v2) -> new LWWElement<>(el, Math.max(v2.getTimestamp(), v1.getTimestamp()))));

        other.getRemoveSet().forEach((el, lwwEl) -> this.removeSet.merge(el,
                lwwEl, (v1, v2) -> new LWWElement<>(el, Math.max(v2.getTimestamp(), v1.getTimestamp()))));

        elements.retainAll(other.getElements());

        return this;
    }

    private void updateElements(E element) {
        Long removeTime = removeSet.get(element) != null ? removeSet.get(element).getTimestamp() : null;
        Long addTime = addSet.get(element) != null ? addSet.get(element).getTimestamp() : null;
        if (removeTime != null && addTime != null) {
            int addCompWithRemove = addTime.compareTo(removeTime);
            if (addCompWithRemove > 0 || (addCompWithRemove == 0 && LWWBias.ADD == this.bias)) {
                elements.add(element);
            } else {
                elements.remove(element);
            }
        } else if (removeTime != null) {
            elements.remove(element);
        } else  {
            elements.add(element);
        }

    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        LWWElementSet otherSet = (LWWElementSet) other;

        return new EqualsBuilder().append(this.elements,otherSet.getElements() ).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.elements).hashCode();
    }

}
