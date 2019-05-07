package org.clay.task.mapper;

import org.clay.task.entity.Task;

import java.util.List;

public interface TaskMapper {
    void insert(Task task);
    void delete(Task task);
    void modify(Task task);
    List<Task> findAll(Task task);
}
