package com.sjsu.project.controller;

import com.sjsu.project.dao.ProjectDao;
import com.sjsu.project.dao.UserDao;
import com.sjsu.project.model.Project;
import com.sjsu.project.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

/**
 * Created by TUSHAR_SK on 11/25/15.
 */

@Controller
@RequestMapping("/project")
public class ProjectController {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-module.xml");
    ProjectDao projectDao = (ProjectDao) ctx.getBean("projectDao");
    UserDao userDao = (UserDao) ctx.getBean("userDao");


    // CREATE project and assign to owner
    @RequestMapping(value = "/{userId}",method = RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public ResponseEntity<Set<Project>> createProject(
            @PathVariable(value = "userId") long userId,
            @RequestParam(value="title", required = true) String title,
            @RequestParam(value="description", required = true) String description,
            @RequestParam(value="state", required = false) String state) {

        if (title == null || "".equalsIgnoreCase(title) || description == null || "".equalsIgnoreCase(description) ||
                state == null || "".equalsIgnoreCase(state)) {
            return new ResponseEntity<Set<Project>>(HttpStatus.BAD_REQUEST);
        }


        Project project = new Project(title, description, state);
        userDao.addProjects(userId,project);
        User user1 = userDao.getUser(userId);
        Set<Project> projectSet1 = user1.getProjects();
        return new ResponseEntity<Set<Project>>(projectSet1, HttpStatus.OK);
    }

    // GET project
    @RequestMapping(value = "/{projectId}",method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public ResponseEntity<Project> getProject(
            @PathVariable(value = "projectId") long projectId) {

        Project project = projectDao.getProject(projectId);

        if (project == null) {
            return new ResponseEntity<Project>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }

    // Assign projects to user.
    @RequestMapping(value = "/{projectId}/{userId}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Set<User>> addUserstoProject(@PathVariable(value = "projectId") long projectId,
                                                     @PathVariable(value = "userId") long userId){

        projectDao.addUsers(projectId,userId);
        Project project = projectDao.getProject(projectId);
        Set<User> users = project.getUsers();
        return new ResponseEntity<Set<User>>(users,HttpStatus.OK);
    }

}
