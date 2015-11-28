package com.sjsu.project.controller;


import com.sjsu.project.dao.UserDao;
import com.sjsu.project.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Created by TUSHAR_SK on 11/25/15.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-module.xml");
    UserDao userDao = (UserDao) ctx.getBean("userDao");

    // CREATE user
    @RequestMapping(method = RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public ResponseEntity<User> createUser(
            @RequestParam(value="name", required = true) String name,
            @RequestParam(value="email", required = true) String email,
            @RequestParam(value="password", required = true) String password) {

        if (name == null || "".equalsIgnoreCase(name) || email == null || "".equalsIgnoreCase(email)) {
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }

        User user = new User(name, email, password);
        userDao.createUser(user);

        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    // GET user by id
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = { "application/json" })
    public ResponseEntity<User> getUser(@PathVariable(value = "userId") long userId) {

        User user = userDao.getUser(userId);
        if(user == null){
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    // Update user name and password (no email change as email is unique)
    @RequestMapping(value = "/{userId}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<User> updateUser(@PathVariable(value = "userId") long userId,
                                             @RequestParam(value="name", required = true) String name,
                                             @RequestParam(value="password", required = true) String password){

        User user = userDao.getUser(userId);
        if (user == null) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }

        if (name != null || !"".equalsIgnoreCase(name))
            user.setName(name);
        if (password != null || !"".equalsIgnoreCase(password))
            user.setPassword(password);

        userDao.updateUser(userId,user);
        User user1 = userDao.getUser(userId);
        return new ResponseEntity<User>(user1, HttpStatus.OK);
    }

    // Delete a user
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "userId") long userId){

        User user = userDao.getUser(userId);
        if (user == null) {
            return new ResponseEntity<String>("User Not found",HttpStatus.NOT_FOUND);
        }

        userDao.deleteUser(userId);
        return new ResponseEntity<String>("User Deleted", HttpStatus.OK);
    }
}
