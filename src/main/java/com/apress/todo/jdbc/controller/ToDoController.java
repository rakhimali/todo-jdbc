package com.apress.todo.jdbc.controller;

import com.apress.todo.jdbc.domain.ToDo;
import com.apress.todo.jdbc.domain.ToDoBuilder;
import com.apress.todo.jdbc.repository.ToDoRepository;
import com.apress.todo.jdbc.validation.ToDoValidationError;
import com.apress.todo.jdbc.validation.ToDoValidationErrorBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api")
public class ToDoController {

    private final ToDoRepository repository;

    public ToDoController(ToDoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("todo")
    public ResponseEntity<Iterable<ToDo>> getToDos() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("todo/{id}")
    public ResponseEntity<ToDo> getToDoById (@PathVariable String id) {
        return ResponseEntity.ok(repository.findById(id));
    }

    @PostMapping("todo/{id}")
    public ResponseEntity<ToDo> setCompleted (@PathVariable String id) {
        ToDo result = repository.findById(id);
        result.setCompleted(true);
        repository.save(result);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(result.getId()).toUri();
        return ResponseEntity.ok().header("Location", location.toString()).build();
    }

    @RequestMapping(value = "/todo", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<?> createToDo (@Validated @RequestBody ToDo todo,
                                         Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ToDoValidationErrorBuilder.fromBindingErrors(errors));
        }

        ToDo result = repository.save(todo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + result.getId()).buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("todo/{id}")
    public ResponseEntity<ToDo> deleteToDo (@PathVariable String id) {
        repository.delete(ToDoBuilder.create().withId(id).build());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("todo")
    public ResponseEntity<ToDo> deleteToDo (@RequestBody ToDo toDo) {
        repository.delete(toDo);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ToDoValidationError handleException(Exception exception) {
        return new ToDoValidationError(exception.getMessage());
    }

}
