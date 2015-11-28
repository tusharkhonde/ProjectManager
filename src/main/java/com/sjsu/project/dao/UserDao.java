package com.sjsu.project.dao;

import com.sjsu.project.model.Project;
import com.sjsu.project.model.User;

/**
 * Created by TUSHAR_SK on 11/25/15.
 */
public interface UserDao {

    void createUser(User user);
    User getUser(long userId);
    void updateUser(long userId, User user);
    void deleteUser(long userId);
    void addProjects(long userId, Project project);
}
