package com.sjsu.project.controller;

import com.sjsu.project.dao.ProjectDao;
import com.sjsu.project.dao.TaskDao;
import com.sjsu.project.dao.UserDao;
import com.sjsu.project.model.Project;
import com.sjsu.project.model.Task;
import com.sjsu.project.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by TUSHAR_SK on 11/25/15.
 */

@Controller
@RequestMapping("/task")
public class TaskController {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-module.xml");
    UserDao userDao = (UserDao) ctx.getBean("userDao");
    ProjectDao projectDao = (ProjectDao) ctx.getBean("projectDao");
    TaskDao taskDao = (TaskDao) ctx.getBean("taskDao");

    // CREATE task and assign to user and project
    @RequestMapping(value = "/{userId}/{projectId}",method = RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public ResponseEntity<Set<Task>> createTask(
            @PathVariable(value = "userId") long userId,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value="title", required = true) String title,
            @RequestParam(value="description", required = true) String description,
            @RequestParam(value="state", required = false) String state,
            @RequestParam(value="estimate", required = true) String estimate,
            @RequestParam(value="actual", required = false) String actual) {

        if (title == null || "".equalsIgnoreCase(title) || description == null || "".equalsIgnoreCase(description)
                || estimate == null || "".equalsIgnoreCase(estimate)) {
            return new ResponseEntity<Set<Task>>(HttpStatus.BAD_REQUEST);
        }

        Task task = new Task(title, description, "new",Long.valueOf(estimate));
        User user = userDao.getUser(userId);
        task.setAssignee(user);
        Project project = projectDao.getProject(projectId);
        task.setProject(project);
        taskDao.createTask(task);

        Project project1 = projectDao.getProject(projectId);
        Set<Task> taskSet = project1.getListTasks();

        return new ResponseEntity<Set<Task>>(taskSet,HttpStatus.OK);
    }

    // Get Task
    @RequestMapping(value = "/{taskId}",method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public ResponseEntity<Task> getTask(@PathVariable(value = "taskId") long taskId){

        Task task = taskDao.getTask(taskId);
        if (task == null) {
            return new ResponseEntity<Task>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }


    // Update task state
    @RequestMapping(value = "/{userId}/{taskId}",method = RequestMethod.PUT,produces = "application/json")
    @ResponseBody
    public ResponseEntity updateTask(@PathVariable(value = "userId") long userId,
                                     @PathVariable(value = "taskId") long taskId,
                                     @RequestBody Task taskState){

        if (taskState == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        User user = userDao.getUser(userId);
        Task task = taskDao.getTask(taskId);

        if(user.getUserid() != task.getAssignee().getUserid()){
            return new ResponseEntity("User not found",HttpStatus.BAD_REQUEST);
        }

        if(taskState.getState().compareTo("cancelled") == 0) {
            long id = task.getProject().getProjectId();
            Set<Project> projectSet = user.getProjects();
            Iterator<Project> iterator = projectSet.iterator();
            while (iterator.hasNext()) {
                if (id == iterator.next().getProjectId()) {
                    task.setState(taskState.getState());
                    taskDao.updateTask(taskId,task);
                    return new ResponseEntity(taskDao.getTask(taskId),HttpStatus.OK);
                }
                else {
                    return new ResponseEntity("User not authorized",HttpStatus.BAD_REQUEST);
                }
            }
        }

        task.setState(taskState.getState());
        taskDao.updateTask(taskId,task);

        return new ResponseEntity(taskDao.getTask(taskId),HttpStatus.OK);
    }
}
