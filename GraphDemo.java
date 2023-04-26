import java.io.FileInputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Add your name(s) as authors
 */
public class GraphDemo {
    public static void main(String[] args) throws Exception{
        GraphProcessor g = new GraphProcessor();
        FileInputStream file = new FileInputStream("data/usa.graph");
        g.initialize(file);

        System.out.println("Enter start city with its two letter state abbreviation: ");
        Scanner s = new Scanner(System.in);
        String start = s.next();
        System.out.println("Enter end city with its two letter state abbreviation: ");
        String end = s.next();

        Point startPoint = helper(new FileInputStream("data/uscities.csv"), start);
        Point endPoint = helper(new FileInputStream("data/uscities.csv"), end);

        Point closestPointStart = g.nearestPoint(startPoint);
        Point closestPointEnd = g.nearestPoint(endPoint);

        List<Point> shortestRoute = g.route(closestPointStart, closestPointEnd);
        double shortesDistance = g.routeDistance(shortestRoute);

        long startTime = System.nanoTime();
        Visualize vis = new Visualize("data/usa.vis", "images/usa.png");
        vis.drawPoint(closestPointStart);
        vis.drawPoint(closestPointEnd);
        vis.drawRoute(shortestRoute);
        long endTime = System.nanoTime();

        System.out.println("Closest Vertex to start point: " + closestPointStart);
        System.out.println("Closest Vertex to end point: " + closestPointEnd);
        System.out.println("Distance between their closest points on the map: "+shortesDistance+"miles");
        System.out.println("Total time to calculate the closest points, shortest path, and distance along the path: " + (endTime-startTime)/1000000 + "ms");

        s.close();
    }

    public static Point helper(FileInputStream file, String cityName){
       Scanner sc = new Scanner(file);
       while (sc.hasNextLine()){
        String line = sc.nextLine();
        String[] values = line.split(",");
        String name = values[0]+","+values[1];
        if (name.equals(cityName)){
            double lat = Double.parseDouble(values[2]);
            double lon = Double.parseDouble(values[3]);
            return new Point(lat, lon);
        }
        }
        sc.close();
        return null;
    }
}