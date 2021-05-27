package com.apress.todo.jdbc.domain;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ToDo {

    @NotNull
    private String id;

    @NotNull
    @NotBlank
    private String description;

    //@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    //@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modified;


    private Boolean completed;

    public ToDo () {
        LocalDateTime date = LocalDateTime.now();
        this.id = UUID.randomUUID().toString();
        this.created = date;
        this.modified = date;
        this.completed = false;
    }

    public ToDo (String description) {
        this();
        this.description = description;
    }
}
