package org.crdt.models;

/**
 * In case of conflict when add and remove are having same timestamp, then CRDT will bias on these strategies.
 */
public enum LWWBias {
    ADD,
    REMOVE;
}
