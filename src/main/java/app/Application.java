package app;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.CSVDataParser;
import dao.ExchangeRateDAO;
import dao.PortDAO;
import dao.PriceDAO;
import dao.RegionDAO;
import models.Port;
import models.Price;
import models.Region;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired private JdbcTemplate jdbcTemplate;

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public PortDAO portDAO() {
        return new PortDAO();
    }

    @Bean
    public RegionDAO getRegionDAO() {
        return new RegionDAO();
    }

    @Bean
    public PriceDAO getPriceDAO() {
        return new PriceDAO();
    }
    
    @Bean
    public ExchangeRateDAO getExchangeRateDAO() {
        return new ExchangeRateDAO();
    }

    @Override
    public void run(String... strings) throws Exception {

        jdbcTemplate.execute("DROP TABLE port IF EXISTS");
        jdbcTemplate.execute("DROP TABLE region IF EXISTS");
        jdbcTemplate.execute("DROP TABLE price IF EXISTS");

        log.info("Creating port, region and price table");

        jdbcTemplate.execute(
                "CREATE TABLE port(code VARCHAR(5) NOT NULL PRIMARY KEY, name VARCHAR(100) NOT NULL, slug VARCHAR(100) NOT NULL)");
        jdbcTemplate.execute(
                "CREATE TABLE region(slug VARCHAR(100) NOT NULL PRIMARY KEY, name VARCHAR(100) NOT NULL, parent VARCHAR(100) )");
        jdbcTemplate.execute(
                "CREATE TABLE price(ID int NOT NULL AUTO_INCREMENT PRIMARY KEY, origin VARCHAR(5) NOT NULL, destination VARCHAR(5) NOT NULL, date VARCHAR(20) NOT NULL, price INT NOT NULL)");

        jdbcTemplate.execute(
                "ALTER TABLE port ADD CONSTRAINT FK_ports_slug FOREIGN KEY (slug) REFERENCES region(slug)");
        
        jdbcTemplate.execute(
                "ALTER TABLE price ADD CONSTRAINT FK_price_dest FOREIGN KEY (destination) REFERENCES port(code)");
        
        jdbcTemplate.execute(
                "ALTER TABLE price ADD CONSTRAINT FK_price_orig FOREIGN KEY (origin) REFERENCES port(code)");
        
        jdbcTemplate.execute(
                "ALTER TABLE region ADD CONSTRAINT FK_region_parent_slug FOREIGN KEY (parent) REFERENCES region(slug)");

        bootstrapRegionsData(CSVDataParser.parseRegionsdata());
        bootstrapPortsData(CSVDataParser.parsePortsData());
        bootstrapPriceData(CSVDataParser.parsePricedata());

//        log.info("query port table for the data");
//
//        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList("SELECT * from port");
//
//        for (Map<String, Object> q : queryResult) {
//            log.info(String.format("[code = %s, name = %s slug = %s]", q.get("code"), q.get("name"), q.get("slug")));
//        }
//
//        queryResult = jdbcTemplate.queryForList("SELECT * from region");
//
//        for (Map<String, Object> q : queryResult) {
//            log.info(
//                    String.format("[slug = %s, name = %s parent = %s]", q.get("slug"), q.get("name"), q.get("parent")));
//        }
//
//        queryResult = jdbcTemplate.queryForList("SELECT * from price");
//
//        for (Map<String, Object> q : queryResult) {
//            log.info(String.format("[origin = %s, destination = %s date = %s, price = %d]",
//                    q.get("origin"),
//                    q.get("destination"),
//                    q.get("date"),
//                    q.get("price")));
//        }

    }
    
    private void bootstrapPriceData(List<Price> prices) {
        jdbcTemplate.batchUpdate("INSERT INTO price(origin, destination, date, price) values(?,?,?,?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, prices.get(i).getOrigin());
                        ps.setString(2, prices.get(i).getDestination());
                        ps.setString(3, prices.get(i).getDate());
                        ps.setInt(4, prices.get(i).getPrice());
                    }

                    @Override
                    public int getBatchSize() {
                        return prices.size();
                    }
                });
        
    }

    private void bootstrapRegionsData(List<Region> regions) {
        jdbcTemplate.batchUpdate("INSERT INTO region(slug, name, parent) values(?,?,?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, regions.get(i).getSlug());
                        ps.setString(2, regions.get(i).getName());
                        ps.setString(3, regions.get(i).getParent());
                    }

                    @Override
                    public int getBatchSize() {
                        return regions.size();
                    }
                });
        
    }

    private void bootstrapPortsData(List<Port> ports) {
        log.info("inserting one port data into port table");
        jdbcTemplate.batchUpdate("INSERT INTO port(code, name, slug) values(?,?,?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, ports.get(i).getCode());
                        ps.setString(2, ports.get(i).getName());
                        ps.setString(3, ports.get(i).getSlug());
                    }

                    @Override
                    public int getBatchSize() {
                        return ports.size();
                    }
                });

    }
}
