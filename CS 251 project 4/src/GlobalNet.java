import java.util.ArrayList;

public class GlobalNet
{
    //creates a global network 
    //O : the original graph
    //regions: the regional graphs
    public static Graph run(Graph O, Graph[] regions)
    {
        Graph network = new Graph(O.V());
        network.setCodes(O.getCodes());
        int[] distances = new int[O.V()];
        int[] previous = new int[O.V()];
        ArrayList<Edge> q = new ArrayList<>();
        DistQueue queue = new DistQueue(O.V());
        //adds all regions to a new graph
        for (int j = 0; j < regions.length; j++)
        {
            Graph region = regions[j];
            ArrayList<Edge> r1 = region.edges();
            for (int i = 0; i < r1.size(); i++) {
                network.addEdge(r1.get(i));
            }
        }
        int r = 0;
        //while r < regions.length
        while (r < regions.length) {
            int r2 = r + 1;
            while (r2 < regions.length) {
                //while r2 < regions.length
                Graph region = regions[r];
                Graph region2 = regions[r2];
                distances = new int[O.V()];
                previous = new int[O.V()];
                //makes all distances infinity
                for (int i = 1; i < distances.length; i++) {
                    distances[i] = 9999999;
                }
                //sets distances of region1 nodes to 0  and their previous nodes to -1
                for (int i = 0; i < region.V(); i++) {
                    distances[O.index(region.getCode(i))] = 0;
                    previous[O.index(region.getCode(i))] = -1;
                    //System.out.println(region.toString());
                    ArrayList adj = O.adj(region.getCode(i));
                    //System.out.println(adj);
                    for (int j = 0; j < adj.size(); j++) {
                        Edge e = new Edge(region.getCode(i), O.getCode((Integer) adj.get(j)), O.getEdgeWeight(O.index(region.getCode(i)), O.index(O.getCode((Integer) adj.get(j)))));
                        q.add(e);
                        //System.out.println("edge: " + e);
                    }
                }
                //while the arraylist is not empty performs dijkstra
                //System.out.println("here");
                while (q.size() != 0) {
                    boolean one = false;
                    int weight = q.get(0).weight();
                    int index = 0;
                    for (int i = 1; i < q.size(); i++) {
                        Edge e = q.get(i);
                        //System.out.println("edge: " + e);
                        int weight2 = e.weight();
                        if (weight > weight2) {
                            weight = weight2;
                            index = i;
                        }
                    }
                    Edge e = q.remove(index);
                    ArrayList<Edge> l = O.edges();
                    ArrayList<Edge> ll = new ArrayList<>();
                    if (l.contains(e)) {
                        //System.out.println("Edge to be added: " + e);
                        if (!ll.contains(e)) {
                            ll.add(e);
                            int prev = O.index(e.u);
                            int dest = O.index(e.v);
                            int weight2 = e.weight();
                            //System.out.println(O.getCode(O.index(e.u)));
                            //System.out.println("weightdest: " + distances[dest]);
                            //System.out.println("weightprev: " + (distances[prev] + weight2));
                            if (distances[dest] >= (distances[prev] + weight2)) {
                                //System.out.println("Added: " + e);
                                distances[dest] = distances[prev] + weight2;
                                previous[dest] = prev;
                                ArrayList adj = O.adj(dest);
                                for (int j = 0; j < adj.size(); j++) {
                                    Edge e2 = new Edge(O.getCode(dest), O.getCode((Integer) adj.get(j)), O.getEdgeWeight(dest, (Integer) adj.get(j)));
                                    q.add(e2);
                                }
                            }
                        }

                    }
                }
                //System.out.println("Here");
                int toadd = 0;
                int distance = 999999999;
                // finds which node of second region has shortest path to first
                for (int i = 0; i < region2.V(); i++) {
                    int index = O.index(region2.getCode(i));
                    //System.out.println(region2.getCode(i));
                    //System.out.println(distances[index]);
                    //System.out.println(distance);
                    if (distances[index] < distance) {
                        distance = distances[index];
                        toadd = index;
                    }
                    else if (distances[index] == distance)
                    {
                        int stops1 = 0;
                        int stops2 = 0;
                        int index2 = index;
                        int toadd2 = toadd;
                        while (previous[toadd2] != -1) {
                            int before = previous[toadd2];
                            //System.out.println(toadd);
                            //System.out.println(previous[toadd]);
                            //System.out.println(O.getEdge(before, toadd));
                            toadd2 = before;
                            stops1++;
                        }
                        while (previous[index2] != -1) {
                            int before = previous[index2];
                            //System.out.println(toadd);
                            //System.out.println(previous[toadd]);
                            //System.out.println(O.getEdge(before, toadd));
                            index2 = before;
                            stops2++;
                        }
                        if (stops2 < stops1)
                        {
                            distance = distances[index];
                            toadd = index;
                        }
                    }
                }
                //System.out.println("here3");
                // adds edges of that path to the network graph
                while (previous[toadd] != -1) {
                    int before = previous[toadd];
                     //System.out.println(toadd);
                    //System.out.println(previous[toadd]);
                    //System.out.println(O.getEdge(before, toadd));
                    network.addEdge(O.getEdge(before, toadd));
                    toadd = before;
                }
                //System.out.println(network.toString());
                r2++;
            }
            r++;
        }
        return network;
    }
    public static void main(String[] args)
    {
        Graph O = new Graph("src/graph5.txt");
        Graph r[]= new Graph[3];
        r[0] = new Graph("src/region1.txt");
        r[1] = new Graph("src/region2.txt");
        r[2] = new Graph("src/region3.txt");
        System.out.println(run(O, r).toString());
    }
}
    
    
    