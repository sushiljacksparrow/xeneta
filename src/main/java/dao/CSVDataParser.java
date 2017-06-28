package dao;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.tools.Csv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import models.Port;
import models.Price;
import models.Region;

public class CSVDataParser {

    private static final Logger log = LoggerFactory.getLogger(CSVDataParser.class);

    public static List<Port> parsePortsData() {

        List<Port> ports = new ArrayList<>();

        Csv csv = new Csv();
        String[] colNames = new String[3];
        colNames[0] = "code";
        colNames[1] = "name";
        colNames[2] = "slug";
        try {
            Reader reader = new FileReader(new File("resources/port.csv"));
            ResultSet resulSet = csv.read(reader, colNames);

            while (resulSet.next()) {

                String code = resulSet.getString("code");
                String name = resulSet.getString("name");
                String slug = resulSet.getString("slug");
                Port port = new Port(code, name, slug);
                ports.add(port);
            }
        } catch (IOException | SQLException e) {
            log.error("Error parsing price.csv", e);
        }

        return ports;
    }

    public static List<Region> parseRegionsdata() {

        Csv csv = new Csv();
        List<Region> regions = new ArrayList<>();
        String[] colNames = new String[3];
        colNames[0] = "slug";
        colNames[1] = "name";
        colNames[2] = "parent";

        try {
            Reader reader = new FileReader(new File("resources/region.csv"));
            ResultSet resulSet = csv.read(reader, colNames);

            while (resulSet.next()) {

                String slug = resulSet.getString("slug");
                String name = resulSet.getString("name");
                String parent = resulSet.getString("parent");
                Region region = new Region(slug, name, parent);
                regions.add(region);
            }
        } catch (IOException | SQLException e) {
            log.error("Error parsing region.csv", e);
        }

        return regions;
    }
    
    public static List<Price> parsePricedata() {

        Csv csv = new Csv();
        List<Price> prices = new ArrayList<>();
        String[] colNames = new String[4];
        colNames[0] = "origin";
        colNames[1] = "destination";
        colNames[2] = "date";
        colNames[3] = "price";

        try {
            Reader reader = new FileReader(new File("resources/price.csv"));
            ResultSet resulSet = csv.read(reader, colNames);

            while (resulSet.next()) {

                String origin = resulSet.getString("origin");
                String destination = resulSet.getString("destination");
                String date = resulSet.getString("date");
                int value = resulSet.getInt("price");
                Price price = new Price(origin, destination, date, value);
                prices.add(price);
            }
        } catch (IOException | SQLException e) {
            log.error("Error parsing price.csv", e);
        }

        return prices;
    }
}
