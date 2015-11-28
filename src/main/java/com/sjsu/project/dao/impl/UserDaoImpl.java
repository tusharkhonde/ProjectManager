package com.sjsu.project.dao.impl;

import com.sjsu.project.dao.UserDao;
import com.sjsu.project.model.Project;
import com.sjsu.project.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by TUSHAR_SK on 11/25/15.
 */
@Repository
public class UserDaoImpl implements UserDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    public void createUser(User user) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(user);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public User getUser(long userId) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            User user = (User) session.get(User.class, userId);
            tx.commit();
            return user;
        }catch (HibernateException e){
            tx.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    public void updateUser(long userId, User user) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            User user1 = (User) session.get(User.class, userId);
            if (user.getName() != null) {
                user1.setName(user.getName());
            }
            if (user.getPassword() != null) {
                user1.setPassword(user.getPassword());
            }
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void deleteUser(long userId) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            User user = (User) session.get(User.class, userId);
            session.delete(user);
            tx.commit();
        }catch (HibernateException e){
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }
    }

    public void addProjects(long userId, Project project) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            User user1 = (User) session.get(User.class, userId);
            if (user1.getProjects() != null) {
                Set<Project> projectSet = user1.getProjects();
                projectSet.add(project);
                user1.setProjects(projectSet);
            }
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

}
