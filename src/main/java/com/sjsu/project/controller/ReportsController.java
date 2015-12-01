package com.sjsu.project.controller;


import java.util.Set;

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


@Controller
@RequestMapping("/reports")
public class ReportsController {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-module.xml");
    UserDao userDao = (UserDao) ctx.getBean("userDao");
    ProjectDao projectDao = (ProjectDao) ctx.getBean("projectDao");

    

    // GET user by id
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET, produces = { "application/json" })
    public ResponseEntity getReports(@PathVariable(value = "projectId") long projectId,
    		@RequestParam(value="taskReport", required=true) String taskReport, 
    		@RequestParam(value="teamReport", required=true) String teamReport ) {

        Project project = projectDao.getProject(projectId);
        if(project == null){
            return new ResponseEntity<String>("Project not found.",HttpStatus.NOT_FOUND);
        }
        
        if( null != taskReport && taskReport.length() != 0 && taskReport.equalsIgnoreCase("yes") ){
        	// get report based on tasks
        }
        if( null != teamReport && teamReport.length() != 0 && teamReport.equalsIgnoreCase("yes") ){
        	// get report based on team members
        }
        
        return new ResponseEntity<String>("In reports", HttpStatus.OK);
    }

}
