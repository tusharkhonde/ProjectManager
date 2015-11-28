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

    /*
    Task has five states 1. New, 2. Assigned, 3. Started, 4. Finished, and 5. cancelled
     */


    // CREATE task and assign to user and project
    @RequestMapping(value = "/{userId}/{projectId}",method = RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public ResponseEntity createTask(
            @PathVariable(value = "userId") long userId,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value="title", required = true) String title,
            @RequestParam(value="description", required = true) String description,
            @RequestParam(value="state", required = false) String state,
            @RequestParam(value="estimate", required = true) String estimate,
            @RequestParam(value="actual", required = false) String actual) {

        // Task cannot be added after Project Planning state
        Project myProject = projectDao.getProject(projectId);
        if (myProject.getState().compareToIgnoreCase("Planning") != 0){
            return new ResponseEntity<String>("Task cannot be deleted",HttpStatus.BAD_REQUEST);
        }

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
                                     @RequestBody Task taskState) {

        if (taskState == null) {
            return new ResponseEntity<String>("Task state Required", HttpStatus.BAD_REQUEST);
        }
        Task task = taskDao.getTask(taskId);

        // Check if project is in terminal state completed or cancelled
        if (task.getProject().getState().compareToIgnoreCase("completed") == 0 ||
                task.getProject().getState().compareToIgnoreCase("cancelled") == 0) {
            return new ResponseEntity<String>("Task state cannot be changed, project is in terminal state", HttpStatus.BAD_REQUEST);
        }

        else {

            User user = userDao.getUser(userId);
            if (user.getUserid() != task.getAssignee().getUserid()) {
                return new ResponseEntity<String>("User not found", HttpStatus.BAD_REQUEST);
            }

            if (taskState.getState().compareToIgnoreCase("cancelled") == 0) {
                long id = task.getProject().getProjectId();
                Set<Project> projectSet = user.getProjects();
                Iterator<Project> iterator = projectSet.iterator();
                while (iterator.hasNext()) {
                    if (id == iterator.next().getProjectId()) {
                        task.setState(taskState.getState());
                        taskDao.updateTask(taskId, task);
                        return new ResponseEntity<Task>(taskDao.getTask(taskId), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<String>("User not authorized", HttpStatus.BAD_REQUEST);
                    }
                }
            }

            task.setState(taskState.getState());
            taskDao.updateTask(taskId, task);

            return new ResponseEntity<Task>(taskDao.getTask(taskId), HttpStatus.OK);
        }

    }

    // Delete a task from project
    @RequestMapping(value = "/{taskId}",method = RequestMethod.DELETE,produces = "application/json")
    @ResponseBody
    public ResponseEntity deleteTask(@PathVariable(value = "userId") long userId,
                                     @PathVariable(value = "taskId") long taskId){
        Task task = taskDao.getTask(taskId);
        long id = task.getProject().getProjectId();
        Project myProject = projectDao.getProject(id);

        // Task cannot be deleted after Project planning state
        if (myProject.getState().compareToIgnoreCase("Planning") != 0){
            return new ResponseEntity<String>("Task cannot be deleted",HttpStatus.BAD_REQUEST);
        }

        taskDao.deleteTask(taskId);
        return new ResponseEntity<String>("Task Deleted",HttpStatus.OK);

    }
}
