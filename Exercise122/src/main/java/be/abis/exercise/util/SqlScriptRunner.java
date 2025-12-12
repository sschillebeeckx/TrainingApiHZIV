package be.abis.exercise.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class SqlScriptRunner {

    private final JdbcTemplate jdbcTemplate;

    public SqlScriptRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void runSqlScript(String classpathScript) {
        try (InputStream is = getClass().getResourceAsStream(classpathScript)) {
            if (is == null) {
                throw new RuntimeException("SQL script not found: " + classpathScript);
            }
            String sql = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
            // Split by semicolon to handle multiple statements
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    jdbcTemplate.execute(trimmed);
                }
            }
            System.out.println("SQL script executed successfully: " + classpathScript);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL script", e);
        }
    }
}

