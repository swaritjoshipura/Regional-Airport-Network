
import java.util.ArrayList;
import java.util.Comparator;
public class RegNet {
    private static int end_all = -1;
    private static Graph graph;
    private static int distance( boolean[] arr, int index, int count, int recurse) {
        if (arr[index] == false) {
            arr[index] = true;
            for (int i = 0; i < graph.adj(index).size(); i++) {
                if (graph.adj(index).get(i) != count) {
                    int val = distance(arr, graph.adj(index).get(i), count, recurse + 1);
                    if (val != end_all) {
                        return val;
                    }
                }
                else {
                    return recurse;
                }
            }
        }
        return end_all;
    }
    public static Graph run(Graph G, int max) {
        graph = kruskal(G);
        int cost = graph.totalWeight();

        if(cost > max) {
            loop(cost,max);
        }
        ArrayList<ArrayList<TempEdge>> val_of_nums = new ArrayList<ArrayList<TempEdge>>();
        int at_point = graph.V();
        for (int i = 0; i < at_point; i++) {
            for (int j = i + 1; j < at_point; j++) {
                boolean[] visited_array = new boolean[at_point];
                int dist = distance(visited_array, i, j, 0);
                while (val_of_nums.size() <= dist) {
                    val_of_nums.add(new ArrayList<TempEdge>());
                }
                int edge_weight = G.getEdgeWeight(G.index(graph.getCode(i)),
                        G.index(graph.getCode(j)));
                val_of_nums.get(dist).add(new TempEdge(i, j, edge_weight));
            }
        }
        int l = val_of_nums.size() - 1;
        for (int i = l; i > 0; i--) {
            if (val_of_nums.get(i) == null) {
                continue;
            }
            else {
                Graph min_span_tree = graph;
//                    val_of_nums.get(i).sort(Comparator.comparingInt(o -> G.getEdgeWeight(G.index(min_span_tree.getCode(o.u)),
//                            G.index(min_span_tree.getCode(o.v)))));
                val_of_nums.get(i).sort(new Comparator<TempEdge>() {
                    @Override
                    public int compare(TempEdge o1, TempEdge o2) {
                        int weight1 =  G.getEdgeWeight(G.index(min_span_tree.getCode(o1.u)), G.index(min_span_tree.getCode(o1.v)));
                        int weight2 =  G.getEdgeWeight(G.index(min_span_tree.getCode(o2.u)), G.index(min_span_tree.getCode(o2.v)));

                        return (weight1-weight2);
                    }
                });
                for (int j = 0; j < val_of_nums.get(i).size(); j++) {
                    int edge_weight = val_of_nums.get(i).get(j).w;
                    int burden = edge_weight + cost;
                    if (burden > max) {
                        break;
                    }
                    graph.addEdge(val_of_nums.get(i).get(j).u, val_of_nums.get(i).get(j).v, edge_weight);
                    cost += edge_weight;
                }
            }
        }
        return graph;
    }
    private static Graph kruskal(Graph G){
        Graph graph = new Graph(G.V());
        graph.setCodes(G.getCodes());
        UnionFind search = new UnionFind(G.V());
        int edge = 0;
        for (int i = 0; edge < G.V() + end_all; i++) {
            if (search.connected(G.sortedEdges().get(i).ui(), G.sortedEdges().get(i).vi())) {
                continue;
            }
            else {
                graph.addEdge(G.sortedEdges().get(i).u, G.sortedEdges().get(i).v, G.sortedEdges().get(i).w);
                search.union(G.sortedEdges().get(i).ui(), G.sortedEdges().get(i).vi());
                edge++;
            }
        }
        return graph;
    }
    private static void loop(int cost, int max){
        ArrayList<Edge> edges = graph.sortedEdges();

        while (cost > max) {
            int start = edges.size() + end_all;
            for (int i = start; i > 0; i--) {
                Edge e = edges.get(i);
                graph.removeEdge(edges.get(i));
                int val = graph.V() + end_all;
                int to_connect = graph.connGraph().V();
                if (to_connect == val) {
                    cost = cost - e.w;
                    graph = graph.connGraph();
                    break;
                }
                graph.addEdge(e);
            }
        }
        return;
    }

}

class TempEdge {
    int u;
    int v;
    int w;

    public TempEdge(int u, int v, int w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }
}

