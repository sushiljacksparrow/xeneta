package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import models.Port;

public class PortDAO {

    private static final Logger log = LoggerFactory.getLogger(PortDAO.class);
    
    private static final String QUERY_STATEMENT = "SELECT * FROM port WHERE slug = ?";
    @Autowired private JdbcTemplate jdbcTemplate;

    public void setPort(String code, String name, String slug) {
        jdbcTemplate.update("INSERT INTO port(code, name, slug) values(?,?,?)", new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, code);
                ps.setString(2, name);
                ps.setString(3, slug);

            }
        });
    }

    public Port getPort(String code) {
        Port port = jdbcTemplate.queryForObject("SELECT * from port where code = ?",
                new Object[] { code },
                new BeanPropertyRowMapper<>(Port.class));
        return port;
    }

    public boolean isPort(String code) {
        List<Port> result = jdbcTemplate.query("SELECT * from port where code = ?",
                new Object[] { code },
                new BeanPropertyRowMapper<>(Port.class));

        if (result.isEmpty()) {
            return false;
        }
        return true;
    }

    public List<String> getAllPortsForSlug(String slug) {
        List<Port> result = jdbcTemplate.query(QUERY_STATEMENT,
                new Object[] { slug },
                new BeanPropertyRowMapper<>(Port.class));
        List<String> ports = new ArrayList<>();
        for (Port port : result) {
            log.info(String.format("Fetched ports %s for slug %s", port.getCode(), slug));
            ports.add(port.getCode());
        }
        return ports;
    }

    public String getSlugForPort(String code) {
        Port port = jdbcTemplate.queryForObject("SELECT * FROM port WHERE code = ?",
                new Object[] { code },
                new BeanPropertyRowMapper<>(Port.class));
        return port.getSlug();
    }
}
