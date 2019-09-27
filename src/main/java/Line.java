import java.util.ArrayList;
import java.util.List;

public class Line implements Comparable<Line> {
    private String number;
    private String name;
    //private List<Station> stations;

    public Line(String number, String name) {
        this.number = number;
        this.name = name;
        //stations = new ArrayList<>();
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

//    public void addStation(Station station) {
//        stations.add(station);
//    }
//
//    public List<Station> getStations() {
//        return stations;
//    }

    @Override
    public int compareTo(Line line) {
        if (toInt(number).equals(toInt(line.getNumber()))) {
            if (hasEnding(number) && hasEnding(line.getNumber()))
                return 0;
            if (!hasEnding(number) && hasEnding(line.getNumber()))
                return 1;
            if (hasEnding(number) && !hasEnding(line.getNumber()))
                return -1;
        }
        return Integer.compare(toInt(number), toInt(line.getNumber()));

    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Line) obj) == 0;
    }

    private Integer toInt(String number) {
        return Integer.parseInt(number.replaceAll("[^0-9]", ""));
    }

    private boolean hasEnding(String number) {
        return number.replaceAll("[0-9]", "").matches("[^0-9]");
    }

}