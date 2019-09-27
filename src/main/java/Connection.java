public class Connection implements Comparable<Connection> {
    private String line;
    private String station;

    public Connection(String line, String station) {
        this.line = line;
        this.station = station;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    @Override
    public int compareTo(Connection connection) {

        if (toInt(line).equals(toInt(connection.getLine()))){
            if(hasEnding(line) && hasEnding(connection.getLine()))
                return station.compareTo(connection.getStation());
            if (!hasEnding(line) && hasEnding(connection.getLine()))
                return 1;
            if (hasEnding(line) && !hasEnding(connection.getLine()))
                return -1;
            if(!hasEnding(line) && !hasEnding(connection.getLine()))
                return station.compareTo(connection.getStation());
        }
        return Integer.compare(toInt(line), toInt(connection.getLine()));
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Connection) obj) == 0;
    }

    private Integer toInt(String number) {
        return Integer.parseInt(number.replaceAll("[^0-9]", ""));
    }

    private boolean hasEnding(String number) {
        return number.replaceAll("[0-9]","").matches("[^0-9]");
    }
}
