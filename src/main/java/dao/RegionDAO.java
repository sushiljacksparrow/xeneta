package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import models.Region;

public class RegionDAO {

    private static final String INSERT_STATEMENT = "INSERT INTO port(slug, name, parent) values(?,?,?)";
    private static final String QUERY_STATEMENT = "SELECT * FROM port WHERE slug = ? ";

    @Autowired private JdbcTemplate jdbcTemplate;

    public void setRegion(String slug, String name, String parent) {
        jdbcTemplate.update(INSERT_STATEMENT, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, slug);
                ps.setString(2, name);
                ps.setString(3, parent);
            }
        });
    }

    public Region getRegion(String slug) {
        Region region = jdbcTemplate.queryForObject(QUERY_STATEMENT,
                new Object[] { slug },
                new BeanPropertyRowMapper<>(Region.class));
        return region;
    }

    public boolean isSlug(String slug) {
        List<Region> result = jdbcTemplate.query("SELECT * FROM region where slug = ?",
                new Object[] { slug },
                new BeanPropertyRowMapper<>(Region.class));
        if (result.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public String getParentForSlug(String origin) {
        Region region = jdbcTemplate.queryForObject("SELECT * FROM region WHERE slug = ?",
                new Object[] { origin },
                new BeanPropertyRowMapper<>(Region.class));
        return region.getParent();
    }

    public List<Region> getChildrenForSlug(String parent) {
        return jdbcTemplate.query("SELECT * FROM region WHERE parent = ?",
                new Object[] { parent },
                new BeanPropertyRowMapper<>(Region.class));
    }
}
