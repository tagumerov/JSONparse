import java.util.*;

public class JsonClass {
    TreeMap<String, List<String>> stations;
    TreeSet<Line> lines;
    Set<TreeSet<Connection>> connections;

    public JsonClass() {
        stations = new TreeMap<>();
        lines = new TreeSet<>();
        connections = new TreeSet<>(new Comparator<TreeSet<Connection>>() {
            @Override
            public int compare(TreeSet<Connection> o1, TreeSet<Connection> o2) {
                if (o1.containsAll(o2) && o2.containsAll(o1))
                    return 0;
                if (o1.containsAll(o2) && !o2.containsAll(o1))
                    return 1;
                return -1;
            }
        });
    }

    public Set<TreeSet<Connection>> getConnections() {
        return connections;
    }

    public TreeMap<String, List<String>> getStations() {
        return stations;
    }

    public Set<Line> getLines() {
        return lines;
    }

    public void addStations(String lineNumber, String stationName) {
        if (!stations.containsKey(lineNumber)) {
            stations.put(lineNumber, new ArrayList<>());
        }
        stations.get(lineNumber).add(stationName);
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public void addConnections(TreeSet<Connection> connection) {
        connections.add(connection);
    }

}
