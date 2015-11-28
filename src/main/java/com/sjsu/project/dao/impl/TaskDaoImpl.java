package com.sjsu.project.dao.impl;

import com.sjsu.project.dao.TaskDao;
import com.sjsu.project.model.Task;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Created by TUSHAR_SK on 11/25/15.
 */
public class TaskDaoImpl implements TaskDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    public void createTask(Task task) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(task);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public Task getTask(long taskId) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            Task task = (Task) session.get(Task.class, taskId);
            tx.commit();
            return task;
        }catch (HibernateException e){
            tx.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    public void updateTask(long taskId, Task task) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            Task task1 = (Task) session.get(Task.class, taskId);
            if (task.getTitle() != null) {
                task1.setTitle(task.getTitle());
            }
            if (task.getDesc() != null) {
                task1.setDesc(task.getDesc());
            }
            if (task.getState() != null) {
                task1.setState(task.getState());
            }
            if (task.getActual()!= 0L){
                task1.setActual(task.getActual());
            }
            if (task.getEstimate()!= 0L){
                task1.setEstimate(task.getEstimate());
            }
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void deleteTask(long taskId) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            Task task = (Task) session.get(Task.class, taskId);
            session.delete(task);
            tx.commit();
        }catch (HibernateException e){
            tx.rollback();
            throw e;
        }finally {
            session.close();
        }
    }
}
