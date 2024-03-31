import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ResizingArrayBag;

import java.util.Collections;
import java.util.HashMap;

public class SAP {
    private final Digraph G;

    public SAP(Digraph G) {
        nonNull(G, "Digraph G is null");
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return length(Collections.singletonList(v), Collections.singletonList(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return ancestor(Collections.singletonList(v), Collections.singletonList(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return bfs(v, w).length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return bfs(v, w).ancestor;
    }

    private SAPResult bfs(Iterable<Integer> v, Iterable<Integer> w) {
        nonNull(v, "Vertex set is null");
        nonNull(w, "Vertex set is null");
        boolean[] markedV = new boolean[G.V()];
        boolean[] markedW = new boolean[G.V()];
        HashMap<Integer, Integer> distToV = new HashMap<>();
        HashMap<Integer, Integer> distToW = new HashMap<>();
        Queue<Integer> queueV = new Queue<>();
        Queue<Integer> queueW = new Queue<>();

        for (Integer i : v) {
            nonNull(i, "The vertex id is null");
            vertexInRange(i);

            queueV.enqueue(i);
            markedV[i] = true;
            distToV.put(i, 0);
        }
        for (Integer i : w) {
            nonNull(i, "The vertex id is null");
            vertexInRange(i);

            if (markedV[i]) {
                return new SAPResult(i, 0);
            }
            queueW.enqueue(i);
            markedW[i] = true;
            distToW.put(i, 0);
        }

        int steps = 0;
        int length = Integer.MAX_VALUE;
        int ancestor = -1;
        while (!queueV.isEmpty() || !queueW.isEmpty()) {
            Iterable<Integer> vertices = bfsStep(queueV, markedV, distToV, markedW);
            for (int vertex : vertices) {
                int distance = distToV.get(vertex) + distToW.get(vertex);
                if (distance < length) {
                    length = distance;
                    ancestor = vertex;
                }
            }

            vertices = bfsStep(queueW, markedW, distToW, markedV);
            for (int vertex : vertices) {
                int distance = distToV.get(vertex) + distToW.get(vertex);
                if (distance < length) {
                    length = distance;
                    ancestor = vertex;
                }
            }

            steps++;
            if (length <= steps) {
                return new SAPResult(ancestor, length);
            }
        }

        return (ancestor != -1)
               ? new SAPResult(ancestor, length)
               : new SAPResult(-1, -1);
    }


    private Iterable<Integer> bfsStep(Queue<Integer> queue, boolean[] marked,
                                      HashMap<Integer, Integer> distTo, boolean[] otherMarked) {
        ResizingArrayBag<Integer> result = new ResizingArrayBag<>();
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            int vertex = queue.dequeue();
            for (int adj : G.adj(vertex)) {
                if (!marked[adj]) {
                    queue.enqueue(adj);
                    marked[adj] = true;
                    distTo.put(adj, distTo.get(vertex) + 1);
                }

                if (otherMarked[adj]) {
                    result.add(adj);
                }
            }
        }

        return result;
    }

    private void nonNull(Object notNull, String msg) {
        if (notNull == null) throw new IllegalArgumentException(msg);
    }

    private void vertexInRange(int v) {
        if (v >= G.V() || v < 0)
            throw new IllegalArgumentException("The vertex id is out of the range");
    }

    private static class SAPResult {
        final int ancestor;
        final int length;

        SAPResult(int ancestor, int length) {
            this.ancestor = ancestor;
            this.length = length;
        }
    }

}
