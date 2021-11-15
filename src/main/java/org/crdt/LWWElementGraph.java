package org.crdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * LastWriteWin Element Graph is one of the CRDT type.
 * @see <a href="https://www.wikiwand.com/en/Conflict-free_replicated_data_type>for more details on LWWGraph based CRDT</a>
 *
 * A graph has vertices and edges and the state of these information is stored in graph map.
 * Also, to maintain the state of all the vertices, A {@link LWWElementSet} CRDT is being used,
 * and since edge is the made up of vertices, edge state can be maintained by the same set as well.
 * @param <E>
 */
public class LWWElementGraph<E> implements CRDT<LWWElementGraph<E>>{
    private Map<E, List<E>> graph;
    private LWWElementSet<E> vertices;

    public LWWElementGraph() {
        this.graph= new HashMap<>();
        this.vertices = new LWWElementSet<>();
    }

    /**
     * Add edge to the graph.
     * @param u source vertex vertex
     * @param v destination vertex
     */
    public void addEdge(E u, E v) {
        if (vertices.contains(u) && vertices.contains(v)) {
            graph.computeIfPresent(u, (key, val) -> {
                val.add(v);
                return val;
            });

            graph.computeIfPresent(v, (key, val) -> {
                val.add(u);
                return val;
            });
        }
    }

    /**
     * Remove vertex from the graph.
     * @param u vertex to be removed
     */
    public void removeVertex(E u) {
        if (vertices.contains(u)) {
            vertices.remove(u);
            // remove adjacent vertices
            graph.values().forEach(e -> e.remove(u));
            graph.remove(u);
        }
    }

    /**
     * Remove edge from the graph.
     * @param u source vertex
     * @param v destination vertex
     */
    public void removeEdge(E u, E v) {
        if (vertices.contains(u) && vertices.contains(v)) {
            graph.computeIfPresent(u, (key, val) -> {
                val.remove(v);
                return val;
            });

            graph.computeIfPresent(v, (key, val) -> {
                val.remove(u);
                return val;
            });
        }
    }

    /**
     * Check of the vertex exists in the graph.
     * @param u Vertex to be verified.
     * @return true if vertex exist, else false.
     */
    public boolean containsVertex(E u) {
        return vertices.contains(u);
    }

    /**
     * Add a verted to the graph.
     * @param element
     */
    public void addVertex(E element) {
        vertices.add(element);
        graph.computeIfAbsent(element, (k) -> new ArrayList<>());
    }

    /**
     * Find all the adjacent vertices to the vertex u
     * @param u vertex to be queried
     * @return list of all adjacent vertices
     */
    public List<E> findAdjacentVertices(E u) {
        return graph.getOrDefault(u, Collections.emptyList());
    }

    /**
     * Find the path between vertex u and v
     * @param src source vertex
     * @param dest destination vertex
     * @return list of vertices connecting vertex u and v.
     */
    public List<E> findPath(E src, E dest) {
        Set<E> visited = new HashSet<>();
        List<E> path = new ArrayList<>();
        Stack<E> st = new Stack<>();
        st.push(src);
        while(!st.isEmpty()) {
            E curr = st.pop();
            visited.add(curr);
            path.add(curr);

            if (curr.equals(dest)) {
                return path;
            }

            for (E v : findAdjacentVertices(curr)) {
                if (!visited.contains(v)) {
                    st.push(v);
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Merge current graph with another {@link LWWElementGraph}.
     * @param other {@code CRDT} instance of {@code LWWElementGraph}.
     * @return merged graph
     */
    @Override
    public LWWElementGraph<E> merge(LWWElementGraph<E> other) {
        this.vertices.merge(other.vertices);

        Set<E> mergedVertices = this.vertices.getElements();
        this.graph.keySet().retainAll(mergedVertices);

        for (E key : mergedVertices) {
            this.graph.computeIfPresent(key, (k, v) -> other.graph.getOrDefault(key, v));
        }
        return this;
    }
}
