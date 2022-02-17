import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class RegNet
{
    //creates a regional network
    //G: the original graph
    //max: the budget
    public static Graph run(Graph G, int max) 
    {
	    //TODO
        //sort edges
        ArrayList<Edge> edges = G.sortedEdges();
        //System.out.println(edges);
        // initialize graph
        Graph MST = new Graph(G.V());
        MST.setCodes(G.getCodes());
        //add edges and union find, check if vertices are already connected
        UnionFind list = new UnionFind(G.V());
        for (int i = 0; i < edges.size(); i++ )
        {
            String s = edges.get(i).toString();
            //System.out.println(edges.get(i).ui());
            //System.out.println(edges.get(i).vi());
            //System.out.println(s.substring(s.lastIndexOf(" ") + 1, s.indexOf(")")));
            if (!list.connected(edges.get(i).ui(), edges.get(i).vi()))
            {
                list.union(edges.get(i).ui(), edges.get(i).vi());
                MST.addEdge(edges.get(i));
            }
        }
        // once all edges are gone through you have mst
        //System.out.println(G.toString());
        System.out.println(MST.toString());
        // check mst against budget
        // if over remove until its under but only ones that dont disconnect graphs
        ArrayList<Edge> edges2 = MST.sortedEdges();
        int x = edges2.size() - 1;
        while ((MST.totalWeight() > max))
        {
            //System.out.println("Beginx: " + x);
            edges2 = MST.sortedEdges();
            Edge temp = edges2.get(x);
            MST.removeEdge(temp);
            //System.out.println("temp: " + temp);
            //System.out.println("edges: " + edges2.toString());
            //System.out.println("stray count: " + MST.getStrayCount());
            if (MST.getStrayCount() != 1)
            {
                //System.out.println("temp2: " + temp);
                MST.addEdge(temp);
                x--;
            }
            else
            {
                MST = MST.connGraph();
                x = edges2.size() - 1;
            }
            //System.out.println("Endx: " + x);
        }
        System.out.println(MST.toString());
        // then check stops and add edges correspondingly
        ArrayList stopList = new ArrayList<String>();
        for (int i = 0; i < MST.V(); i++)
        {
            for (int j = i+1; j < MST.V(); j++)
            {
                stopList.add(i + "," + j + "," + stops(i, j, MST));
            }
        }
        //System.out.println(stopList.toString());
        ArrayList stopList2 = new ArrayList<String>();
        while (stopList.size() != 0)
        {
            String temp = (String) stopList.get(0);
            int toadd = 0;
            int val = Integer.parseInt(temp.substring(temp.lastIndexOf(",") + 1));
            //System.out.println("val1: " + val);
            for (int i = 1; i < stopList.size(); i++)
            {
                String temp2 = (String) stopList.get(i);
                int val2 = Integer.parseInt(temp2.substring(temp2.lastIndexOf(",") + 1));
                //System.out.println("val2: " + val2);
                if (val2 > val) {
                    toadd = i;
                    val = val2;
                    temp = temp2;
                }
                else if (val2 == val)
                {
                    int val2weight = G.getEdgeWeight(Integer.parseInt(temp2.substring(0, temp2.indexOf(","))), Integer.parseInt(temp2.substring(temp2.indexOf(",") + 1, temp2.lastIndexOf(","))));
                    int valweight = G.getEdgeWeight(Integer.parseInt(temp.substring(0, temp.indexOf(","))), Integer.parseInt(temp.substring(temp.indexOf(",") + 1, temp.lastIndexOf(","))));
                    if (val2weight < valweight)
                    {
                        toadd = i;
                        val = val2;
                        temp = temp2;
                    }
                }
            }
            stopList2.add(stopList.get(toadd));
            stopList.remove(toadd);
        }
        System.out.println(stopList2.toString());
        for (int i = 0; i < stopList2.size(); i++)
        {
            String temp2 = (String) stopList2.get(i);
            //System.out.println("stop: " + G.getEdge(G.index(MST.getCode(Integer.parseInt(temp2.substring(0, temp2.indexOf(","))))), G.index(MST.getCode(Integer.parseInt(temp2.substring(temp2.indexOf(",") + 1, temp2.lastIndexOf(",")))))));
            int val2weight = G.getEdgeWeight(G.index(MST.getCode(Integer.parseInt(temp2.substring(0, temp2.indexOf(","))))), G.index(MST.getCode(Integer.parseInt(temp2.substring(temp2.indexOf(",") + 1, temp2.lastIndexOf(","))))));
            //System.out.println("weight: " + val2weight);
            if ( (val2weight + MST.totalWeight()) <= max)
            {
                Edge e = G.getEdge(G.index(MST.getCode(Integer.parseInt(temp2.substring(0, temp2.indexOf(","))))), G.index(MST.getCode(Integer.parseInt(temp2.substring(temp2.indexOf(",") + 1, temp2.lastIndexOf(","))))));
                MST.addEdge(e);
                System.out.println("added: " + e);
            }
            Edge e = G.getEdge(G.index(MST.getCode(Integer.parseInt(temp2.substring(0, temp2.indexOf(","))))), G.index(MST.getCode(Integer.parseInt(temp2.substring(temp2.indexOf(",") + 1, temp2.lastIndexOf(","))))));
            System.out.println( e);
        }
        //System.out.println(MST.toString());
        return MST;
    }
    public static void main(String[] args)
    {
        Graph O = new Graph("src/graph3.txt");
        run(O, 10);
    }
    public static int stops(int vertex1, int vertex2, Graph graph)
    {
        DistQueue queue = new DistQueue(graph.V());
        int x = graph.V();
        int[] parent = new int[x];
        boolean[] list = new boolean[x];
        int y = 0;
        queue.insert(vertex1, y);
        while (!queue.isEmpty())
        {
            int v = queue.delMin();
            for (int i = 0; i < graph.adj(v).size(); i++)
            {
                int node = graph.adj(v).get(i);
                if (!list[node])
                {
                    list[node] = true;
                    parent[node] = v;
                    y++;
                    queue.insert(node, y);
                }
            }
        }
        int stops = 0;
        int index = parent[vertex2];
        while (index != vertex1)
        {
            stops++;
            index = parent[index];
        }
        return stops;
    }
}