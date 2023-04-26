import java.security.InvalidAlgorithmParameterException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 *
 */
public class GraphProcessor {
    private HashMap<Point, HashSet<Point>> myMap = new HashMap<>();
    private HashMap<Point, List<Point>> connect = new HashMap<>();
    private Point[] myArray;
    private int vertices = 0;
    private int edges = 0;

    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */
    public void initialize(FileInputStream file) throws Exception {
        // TODO: Implement initialize
        if (file == null){
            throw new FileNotFoundException("File does not exist");
        }

        Scanner s = new Scanner(file);
        vertices = s.nextInt();
        edges = s.nextInt();
        myArray = new Point[vertices];

        for (int j=0; j<vertices; j++){
            String x = s.next();
            myArray[j] = new Point(s.nextDouble(), s.nextDouble());
        }

        for (int k=0; k<edges; k++){
            int y = s.nextInt();
            int z = s.nextInt();
            s.next();

            if (!myMap.containsKey(myArray[y])){
                myMap.put(myArray[y], new HashSet<>());
            }
            if (!myMap.containsKey(myArray[z])){
                myMap.put(myArray[z], new HashSet<>());
            }
            myMap.get(myArray[y]).add(myArray[z]);
            myMap.get(myArray[z]).add(myArray[y]);
        }
        s.close();
    }


    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        // TODO: Implement nearestPoint
        double min = p.distance(myArray[0]);
        double count = 0;
        Point minPoint = myArray[0];
        for (int i=1; i<vertices; i++){
            count = p.distance(myArray[i]);
            if (count < min){
                min = count;
                minPoint = myArray[i];
            }
        }
        return minPoint;
    }


    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        // TODO Implement routeDistance
        double total = 0.0;
        for (int i=1; i<route.size(); i++){
            total += route.get(i-1).distance(route.get(i));
        }
        return total;
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        // TODO: Implement connected
        if (!myMap.containsKey(p2) || !myMap.containsKey(p1)){
            return false;
        }

        if (p1.equals(p2)){
            return false;
        }
        Stack<Point> toExplore = new Stack<>();
        Set<Point> visited = new HashSet<>();
        HashMap<Point, Point> previous = new HashMap<>();
        Point current = p1;
        toExplore.add(current);
        visited.add(current);

        while(!toExplore.isEmpty()){
            current = toExplore.pop();
            for (Point adj : myMap.get(current)){
                if (adj.equals(p2)){
                    return true;
                }
                if(!visited.contains(adj)){
                    previous.put(adj,current);
                    visited.add(adj);
                    toExplore.push(adj);
                }
            }
        }
        return false;
    }


    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
        // TODO: Implement route
        if (start == end){
            throw new InvalidAlgorithmParameterException("No path between start and end");
        }
        if (!myMap.containsKey(start) || !myMap.containsKey(end)){
            throw new InvalidAlgorithmParameterException("No path between start and end");
        }

        Map<Point, Double> distance = new HashMap<>();
        distance.put(start, 0.0);
        Comparator<Point> comp = (a, b) -> distance.get(a).compareTo(distance.get(b));
        PriorityQueue<Point> toExplore = new PriorityQueue<>(comp);
        toExplore.add(start);
        HashMap<Point, Point> prev = new HashMap<>();
        List<Point> myList = new ArrayList<>();

        while (toExplore.size() > 0) {
            Point current = toExplore.remove();
            for (Point neighbour : myMap.get(current)) {
                double newDist = distance.get(current) + current.distance(neighbour);
                if (!distance.containsKey(neighbour) || newDist < distance.get(neighbour)) {
                    distance.put(neighbour, newDist);
                    toExplore.add(neighbour);
                    prev.put(neighbour, current);
                }
            }
        }

        if (prev.containsKey(end)) {
            myList.add(end);
            Point node = end;
            while(node != start) {
                node = prev.get(node);
                myList.add(node);
            }
        }
        else { 
            throw new InvalidAlgorithmParameterException("No path between start and end");
        }
        Collections.reverse(myList);
        return myList;
    }
}
