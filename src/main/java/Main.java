import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static JsonClass jsonClass = new JsonClass();
    private static TreeSet<Connection> connections;
    private static String dataFile = "src/main/resources/MSKmetro.json";

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();


        try (PrintWriter writer = new PrintWriter(dataFile)) {
            Document doc = Jsoup.connect("https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена").maxBodySize(0).get();
            Elements metroElements = doc.select("tr > td[data-sort-value][style]");
            for (Element element : metroElements) {

                List<String> linesNumbers = element.select("span[title]").prev().eachText();
                String stationName = element.nextElementSibling().select("a[title~=^[А-ЯЁ]{1,}[а-яё]{0,}.{0,}]").text();
                List<String> lineName = element.select("span[title]").eachAttr("title");

                for (int i = 0; i < linesNumbers.size(); i++) {
                    List<String> urlsOfConnections = element.nextElementSibling().nextElementSibling().nextElementSibling().select("a[href]").eachAttr("href");
                    List<String> connectionStationsLinesNumbers = element.nextElementSibling().nextElementSibling().nextElementSibling().select("span[style][class]").eachText();
                    connections = new TreeSet<>();
                    boolean stationNumberLessThanConnectionNumber = true;

                    for (int j = 0; j < urlsOfConnections.size(); j++) {
                        String urlPrefix = "https://ru.wikipedia.org";
                        urlPrefix += urlsOfConnections.get(j);
                        Document connection = Jsoup.connect(urlPrefix).maxBodySize(0).get();
                        Elements connectionStationsNames = connection.select("#mw-content-text > div table.infobox th[class=infobox-above]");
//                        System.out.println("Линия перехода: " + zeroCut(connectionStationsLinesNumbers.get(j)));
//                        System.out.println("Станция перехода: " + removeQuotes(connectionStationsNames.eachText().get(0)));
                        if(toInt(zeroCut(linesNumbers.get(i))) < toInt(zeroCut(connectionStationsLinesNumbers.get(j))) && stationNumberLessThanConnectionNumber)
                        {
                            connections.add(new Connection(zeroCut(connectionStationsLinesNumbers.get(j)), removeQuotes(connectionStationsNames.eachText().get(0))));
                        }
                        else stationNumberLessThanConnectionNumber = false;
                    }
//                    System.out.println("Основная часть:");
//                    System.out.println(zeroCut(linesNumbers.get(i)));
//                    System.out.println(stationName);
//                    System.out.println(lineName.get(i));
//                    System.out.println("================");
                    logger.info("Прочитана станция: {}, Линия: {}, {}", stationName, lineName.get(i), zeroCut(linesNumbers.get(i)));
                    jsonClass.addLine(new Line(zeroCut(linesNumbers.get(i)), lineName.get(i)));
                    if (!connections.isEmpty() && stationNumberLessThanConnectionNumber) {
                        connections.add(new Connection(zeroCut(linesNumbers.get(i)), stationName));
                        jsonClass.addConnections(connections);
                    }
                    jsonClass.addStations(zeroCut(linesNumbers.get(i)), stationName);
                }
            }
            Elements monorailElements = doc.select("td:has(span[title=Московский монорельс])").next();
            for (Element element : monorailElements) {
                String lineNumber = "13";
                String lineName = "Московский монорельс";
                String stationName = element.text();
                logger.info("Прочитана станция: {}, Линия: {}, {}", stationName, lineName, lineNumber);
                jsonClass.addLine(new Line(lineNumber, lineName));
                jsonClass.addStations(lineNumber, stationName);
            }
            Elements centralRingElements = doc.select("td:has(span[title=Московское центральное кольцо])").next();
            for (Element element : centralRingElements) {
                String lineNumber = "14";
                String lineName = "Московское центральное кольцо";
                String stationName = element.text();
                logger.info("Прочитана станция: {}, Линия: {}, {}", stationName, lineName, lineNumber);
                jsonClass.addLine(new Line(lineNumber, lineName));
                jsonClass.addStations(lineNumber, stationName);
            }

            String json = gson.toJson(jsonClass);
            writer.write(json);
            writer.close();

            // From JSON
            JsonClass metro = new Gson().fromJson(getJsonFile(), JsonClass.class);
            getStationNumbersPerLine(metro.getStations());

        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    static String zeroCut(String lineNumber) {
        if (lineNumber.substring(0, 1).equals("0"))
            return lineNumber.substring(1);
        else return lineNumber;
    }

    static String removeQuotes(String text) {
        if (text.substring(0, 1).equals("«") && text.substring(text.length() - 1).equals("»"))
            return text.substring(1, text.length() - 1);
        else return text;
    }

    private static Integer toInt(String number) {
        return Integer.parseInt(number.replaceAll("[^0-9]", ""));
    }

    private static String getJsonFile() {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(dataFile));
            lines.forEach(line -> builder.append(line));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    private static void getStationNumbersPerLine(TreeMap<String, List<String>> collection) {
        collection.forEach((k, v) -> System.out.println("Линия: " + k + " " + "Количество станций: " + v.size()));
    }
}
