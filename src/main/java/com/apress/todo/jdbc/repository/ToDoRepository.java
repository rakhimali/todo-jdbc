package com.apress.todo.jdbc.repository;

import com.apress.todo.jdbc.domain.ToDo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ToDoRepository implements CommonRepository<ToDo> {
    private static final String SQL_INSERT = "insert into todo (id, description, created, modified, completed) values " +
                                             "(:id, :description, :created, :modified, :completed)";
    private final static String SQL_QUERY_FIND_ALL = "select * from todo ";
    private final static String SQL_QUERY_FIND_BY_ID = SQL_QUERY_FIND_ALL + "where id = :id";
    private final static String SQL_UPDATE = "update todo set description = :description, modified = :modified, completed = :completed where id = :id";
    private final static String SQL_DELETE = "delete from todo where id = :id";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ToDoRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<ToDo> toDoRowMapper = (ResultSet rs, int rowNum) -> {
        ToDo toDo = new ToDo();
        toDo.setId(rs.getString("id"));
        toDo.setDescription(rs.getString("description"));
        toDo.setModified(rs.getTimestamp("modified").toLocalDateTime());
        toDo.setCreated(rs.getTimestamp("created").toLocalDateTime());
        toDo.setCompleted(rs.getBoolean("completed"));
        return toDo;
    };

    @Override
    public ToDo save(final ToDo td) {
        ToDo res = findById(td.getId());
        if(res!=null) {
            res.setDescription(td.getDescription());
            res.setModified(LocalDateTime.now());
            res.setCompleted(td.getCompleted());
            return upsert(res, SQL_UPDATE);
        }
        return upsert(td, SQL_INSERT);
    }

    public ToDo upsert (final ToDo toDo,
                        final String sql) {
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("id", toDo.getId());
        namedParameters.put("description", toDo.getDescription());
        namedParameters.put("created", Timestamp.valueOf(toDo.getCreated()));
        namedParameters.put("modified", Timestamp.valueOf(toDo.getModified()));
        namedParameters.put("completed", toDo.getCompleted());
        this.jdbcTemplate.update(sql, namedParameters);
        return findById(toDo.getId());
    }

    @Override
    public Iterable<ToDo> save(Collection<ToDo> tds) {
        tds.forEach(this::save);
        return findAll();
    }

    @Override
    public void delete(final ToDo td) {
        Map<String, Object> namedParams = Collections.singletonMap("id", td.getId());
        this.jdbcTemplate.update(SQL_DELETE, namedParams);
    }

    @Override
    public ToDo findById(String id) {
        try {
            Map<String, String> namedParams = Collections.singletonMap("id", id);
            return this.jdbcTemplate.queryForObject(SQL_QUERY_FIND_BY_ID, namedParams, toDoRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public Iterable<ToDo> findAll() {
        return this.jdbcTemplate.query(SQL_QUERY_FIND_ALL, toDoRowMapper);
    }
}
