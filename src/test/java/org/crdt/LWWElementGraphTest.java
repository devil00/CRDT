package org.crdt;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class LWWElementGraphTest {
    private LWWElementGraph<String> lwwElementGraph;

    @Before
    public void setUp() {
        lwwElementGraph = new LWWElementGraph<>();
    }

    @Test
    public void testAddVertex() {
        lwwElementGraph.addVertex("a");
        Assert.assertTrue(lwwElementGraph.containsVertex("a"));
    }

    @Test
    public void testAddVertex_HavingEdge() {
        lwwElementGraph.addVertex("a");
        lwwElementGraph.addVertex("b");
        lwwElementGraph.addEdge("a", "b");
        lwwElementGraph.removeVertex("a");
        Assert.assertFalse(lwwElementGraph.containsVertex("a"));
        Assert.assertEquals(Collections.emptyList(), lwwElementGraph.findAdjacentVertices("a"));
        Assert.assertEquals(Collections.emptyList(), lwwElementGraph.findAdjacentVertices("b"));
    }

    @Test
    public void testRemoveVertex() {
        lwwElementGraph.addVertex("a");
        Assert.assertTrue(lwwElementGraph.containsVertex("a"));
        lwwElementGraph.removeVertex("a");
        Assert.assertFalse(lwwElementGraph.containsVertex("a"));
    }

    @Test
    public void testAddEdge() {
        lwwElementGraph.addVertex("a");
        lwwElementGraph.addVertex("b");
        lwwElementGraph.addEdge("a", "b");
        Assert.assertEquals(ImmutableList.of("b"), lwwElementGraph.findAdjacentVertices("a"));
        Assert.assertEquals(ImmutableList.of("a"), lwwElementGraph.findAdjacentVertices("b"));
    }

    @Test
    public void testAddEdge_WithNoVertices() {
        lwwElementGraph.addVertex("a");
        lwwElementGraph.addEdge("a", "b");
        Assert.assertEquals(Collections.emptyList(), lwwElementGraph.findAdjacentVertices("a"));
    }

    @Test
    public void testRemoveEdge() {
        lwwElementGraph.addVertex("a");
        lwwElementGraph.addVertex("b");
        lwwElementGraph.addEdge("a", "b");
        Assert.assertEquals(ImmutableList.of("b"), lwwElementGraph.findAdjacentVertices("a"));
        lwwElementGraph.removeEdge("a", "b");
        Assert.assertEquals(Collections.emptyList(), lwwElementGraph.findAdjacentVertices("a"));
    }

    @Test
    public void testFindAdjacentVertices() {
        lwwElementGraph.addVertex("a");
        lwwElementGraph.addVertex("b");
        lwwElementGraph.addEdge("a", "b");
        List<String> neighbours = lwwElementGraph.findAdjacentVertices("a");
        Assert.assertTrue(neighbours.size() == 1);
        Assert.assertEquals(ImmutableList.of("b"), neighbours);
        Assert.assertEquals(ImmutableList.of("a"), lwwElementGraph.findAdjacentVertices("b"));
    }

    @Test
    public void testRemoveEdge_WithReverseVertexOrder() {
        lwwElementGraph.addVertex("a");
        lwwElementGraph.addVertex("b");
        lwwElementGraph.addEdge("a", "b");
        Assert.assertEquals(ImmutableList.of("b"), lwwElementGraph.findAdjacentVertices("a"));
        lwwElementGraph.removeEdge("b", "a");
        Assert.assertEquals(Collections.emptyList(), lwwElementGraph.findAdjacentVertices("b"));
    }

    @Test
    public void testFindPath() {
        lwwElementGraph.addVertex("a");
        lwwElementGraph.addVertex("b");
        lwwElementGraph.addVertex("c");
        lwwElementGraph.addEdge("a", "b");
        System.out.println(lwwElementGraph.findPath("a", "b"));
        Assert.assertEquals(ImmutableList.of("a", "b"), lwwElementGraph.findPath("a", "b"));
        Assert.assertEquals(ImmutableList.of("b", "a"), lwwElementGraph.findPath("b", "a"));
        Assert.assertEquals(Collections.emptyList(), lwwElementGraph.findPath("a", "c"));

        lwwElementGraph.addEdge("b", "c");
        Assert.assertEquals(ImmutableList.of("a", "b", "c"), lwwElementGraph.findPath("a", "c"));

        lwwElementGraph.addVertex("d");
        lwwElementGraph.addEdge("a", "d");
        Assert.assertEquals(ImmutableList.of("c", "b", "a", "d"), lwwElementGraph.findPath("c", "d"));

        lwwElementGraph.addEdge("b", "d");
        Assert.assertEquals(ImmutableList.of("a", "d"), lwwElementGraph.findPath("a", "d"));
    }

    @Test
    public void testMerge() {
        lwwElementGraph.addVertex("a");
        lwwElementGraph.addVertex("b");

        LWWElementGraph<String> lwwElementGraph2 = new LWWElementGraph<>();
        lwwElementGraph2.addVertex("a");
        lwwElementGraph2.addVertex("b");

        lwwElementGraph.addEdge("a", "b");
        lwwElementGraph2.removeVertex("a");

        lwwElementGraph = lwwElementGraph.merge(lwwElementGraph2);

        Assert.assertTrue(lwwElementGraph.containsVertex("b"));
        Assert.assertFalse(lwwElementGraph.containsVertex("a"));
        Assert.assertEquals(Collections.emptyList(), lwwElementGraph.findAdjacentVertices("a"));
        Assert.assertEquals(Collections.emptyList(), lwwElementGraph.findPath("a", "b"));
        Assert.assertEquals(Collections.emptyList(), lwwElementGraph.findPath("b", "a"));
    }
}
