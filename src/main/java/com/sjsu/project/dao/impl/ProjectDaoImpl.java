package com.sjsu.project.dao.impl;

import com.sjsu.project.dao.ProjectDao;
import com.sjsu.project.model.Project;
import com.sjsu.project.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Set;

/**
 * Created by TUSHAR_SK on 11/25/15.
 */
public class ProjectDaoImpl implements ProjectDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    public void createProject(Project project) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(project);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public Project getProject(long projectId) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            Project project = (Project) session.get(Project.class, projectId);
            tx.commit();
            return project;
        }catch (HibernateException e){
            tx.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    public void updateProject(long projectId, Project project) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            Project project1 = (Project) session.get(Project.class, projectId);
            if (project.getTitle() != null) {
                project1.setTitle(project.getTitle());
            }
            if (project.getDesc() != null) {
                project1.setDesc(project.getDesc());
            }
            if (project.getState() != null) {
                project1.setState(project.getState());
            }
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void deleteProject(long projectId) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            Project project = (Project) session.get(Project.class, projectId);
            session.delete(project);
            tx.commit();
        }catch (HibernateException e){
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }
    }

    public void addUsers(long projectId, long userId) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            Project project = (Project) session.get(Project.class, projectId);
            User user = (User) session.get(User.class, userId);
            if (project!= null && user!= null) {
                Set<User> userSet = project.getUsers();
                userSet.add(user);
                project.setUsers(userSet);
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
