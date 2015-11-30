package com.sjsu.project.controller;

import com.sjsu.project.dao.ProjectDao;
import com.sjsu.project.dao.TaskDao;
import com.sjsu.project.dao.UserDao;
import com.sjsu.project.mail.agent.GoogleMailAgent;
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
    TaskDao taskDao = (TaskDao) ctx.getBean("taskDao");


    /*

    Project has four states  1. Planning, 2. Ongoing, 3. Cancelled, and 4. Completed.

    */

    // CREATE project and assign to owner
    @RequestMapping(value = "/{userId}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity createProject(
            @PathVariable(value = "userId") long userId,
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "description", required = true) String description,
            @RequestParam(value = "state", required = false) String state) {

        if (title == null || "".equalsIgnoreCase(title) || description == null || "".equalsIgnoreCase(description) ||
                state == null || "".equalsIgnoreCase(state)) {
            return new ResponseEntity<Set<Project>>(HttpStatus.BAD_REQUEST);
        }


        Project project = new Project(title, description, "Planning");
        User user1 = userDao.getUser(userId);
        project.setOwner(user1);
        
        userDao.addProjects(userId, project);
        
        Set<Project> projectSet1 = user1.getProjects();
        return new ResponseEntity<Set<Project>>(projectSet1, HttpStatus.OK);
    }

    // GET project
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity getProject(
            @PathVariable(value = "projectId") long projectId) {

        Project project = projectDao.getProject(projectId);

        if (project == null) {
            return new ResponseEntity<Project>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }

    // Assign projects to user.
    @RequestMapping(value = "/{projectId}/{userId}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addUsersToProject(@PathVariable(value = "projectId") long projectId,
                                            @PathVariable(value = "userId") long userId) {

        User user = userDao.getUser(userId);
        Set<User> users = null;
        if( null != user ){
        	projectDao.addUsers(projectId, userId);
        	
        	// TODO - check for successful addition
        	GoogleMailAgent agent = new GoogleMailAgent();
        	agent.sendInvitationMail(user.getName(), user.getEmail(), null, null);
        	
            Project project = projectDao.getProject(projectId);
            if( null != project )
            	users = project.getUsers();
        }
    	return new ResponseEntity<Set<User>>(users, HttpStatus.OK);
    }

    // Start project (change state to ongoing) or change state of project
    @RequestMapping(value = "/{userId}/{projectId}/{state}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public ResponseEntity startProject(@PathVariable(value = "userId") long userId,
                                       @PathVariable(value = "projectId") long projectId,
                                       @PathVariable(value = "state") String state) {
        Project project = projectDao.getProject(projectId);
        User user = userDao.getUser(userId);

        if (project.getState().compareToIgnoreCase("completed") == 0 ||
                project.getState().compareToIgnoreCase("cancelled") == 0) {

            return new ResponseEntity<String>("Project state cannot be changed after project is in completed or cancelled state",
                    HttpStatus.BAD_REQUEST);

        }else {

            if (state.compareToIgnoreCase("ongoing") == 0) {

                // Check if userId is of owner to start project
                for (Project project1 : user.getProjects()) {
                    if (project1.getProjectId() == projectId) {

                        // Check if every task is in assigned state
                        Set<Task> taskSet = project.getListTasks();
                        for (Task task : taskSet) {
                            // If any of task is not in assigned state
                            if (task.getState().compareToIgnoreCase("assigned") != 0) {
                                return new ResponseEntity<String>("Task " + task.getTaskId() + " not in assigned state", HttpStatus.BAD_REQUEST);
                            }
                            // Check if task has estimated amount of work
                            if (task.getEstimate() != 0L) {
                                return new ResponseEntity<String>("Task " + task.getTaskId() + " has not estimated time", HttpStatus.BAD_REQUEST);
                            }
                        }

                        // If above satisfy then only change state of project to ongoing
                        Project p = new Project();
                        p.setState(state);

                        projectDao.updateProject(projectId, p);
                        return new ResponseEntity<Project>(projectDao.getProject(projectId), HttpStatus.OK);
                    }
                }

            } else if (state.compareToIgnoreCase("cancelled") == 0) {

                // Check if userId is of owner to cancel project
                for (Project project1 : user.getProjects()) {
                    if (project1.getProjectId() == projectId) {

                        // If above satisfy then only change state of project to cancelled
                        Project p = new Project();
                        p.setState(state);
                        projectDao.updateProject(projectId, p);

                        return new ResponseEntity<Project>(projectDao.getProject(projectId), HttpStatus.OK);
                    }
                }
            } else if (state.compareToIgnoreCase("completed") == 0) {

                if(project.getState().compareToIgnoreCase("planning") == 0){
                    return new ResponseEntity<String>("Project has not started yet", HttpStatus.BAD_REQUEST);
                }else {
                    Set<Task> tasks = project.getListTasks();
                    int size = tasks.size();
                    int countFinished = 0;
                    int countCancelled = 0;
                    for (Task task : tasks) {
                        //check if each task is either finished or cancelled

                        //atleast one finished state task
                        if (task.getState().compareToIgnoreCase("finished") == 0) {
                            countFinished++;
                        }
                        if (task.getState().compareToIgnoreCase("cancelled") == 0) {
                            countCancelled++;
                        }
                    }

                    if (countFinished + countCancelled < size) {
                        return new ResponseEntity<String>("Project status cannot to changed to completed, check all task status", HttpStatus.BAD_REQUEST);
                    }

                    Project p = new Project();
                    p.setState(state);
                    projectDao.updateProject(projectId, p);

                    return new ResponseEntity<Project>(projectDao.getProject(projectId), HttpStatus.OK);
                }
            } else if (state.compareToIgnoreCase("planning") == 0) {

                if (project.getState().compareToIgnoreCase("ongoing") == 0) {

                    return new ResponseEntity<String>("Project state cannot be changed to planning", HttpStatus.BAD_REQUEST);
                }
            }

            return new ResponseEntity<String>("Bad Request", HttpStatus.BAD_REQUEST);
        }

    }

}
