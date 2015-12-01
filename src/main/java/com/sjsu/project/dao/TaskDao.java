package com.sjsu.project.dao;

import com.sjsu.project.model.Task;
import com.sjsu.project.model.User;

/**
 * Created by TUSHAR_SK on 11/25/15.
 */
public interface TaskDao {

    void createTask(Task task);
    Task getTask(long taskId);
    void updateTask(long taskId, Task task);
    void deleteTask(long taskId);
    
}
