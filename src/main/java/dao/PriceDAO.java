package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import models.Price;

public class PriceDAO {

    private static final String INSERT_STATEMENT = "INSERT INTO price(origin, destination, date, price) values(?,?,?,?)";
    private static final String QUERY_STATEMENT = "SELECT * FROM price WHERE origin = ? AND destination = ? AND date = ?";

    @Autowired private JdbcTemplate jdbcTemplate;

    public void setPrice(String origin, String destination, String date, int price) {
        jdbcTemplate.update(INSERT_STATEMENT, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, origin);
                ps.setString(2, destination);
                ps.setString(3, date);
                ps.setInt(4, price);
            }
        });
    }

    public List<Price> getPrices(String origin, String destination, String date) {
        List<Price> result = jdbcTemplate.query(QUERY_STATEMENT,
                new Object[] { origin, destination, date },
                new BeanPropertyRowMapper<>(Price.class));
        return result;
    }
}
