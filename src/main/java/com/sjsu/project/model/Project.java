package com.sjsu.project.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Projects")
public class Project implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="projectid")
    private long projectId;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String desc;

    @Column(name="state")
    private String state;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "PROJECT_USER", joinColumns = { @JoinColumn(name = "projectid") }, inverseJoinColumns = { @JoinColumn(name = "userid") })
	private Set<User> usersAssigned = new HashSet<User>();

	public Set<User> getUsers() {
		return this.usersAssigned;
	}

	public void setUsers(Set<User> users) {
		this.usersAssigned = users;
	}

    // one project many tasks
    @OneToMany(cascade=CascadeType.ALL,mappedBy = "project",fetch = FetchType.EAGER)
	private Set<Task> listTasks = new HashSet<Task>();

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}


	public Set<Task> getListTasks() {
		return listTasks;
	}

	public void setListTasks(Set<Task> listTasks) {
		this.listTasks = listTasks;
	}

	public Project() {
	}

	public Project(String title, String desc, String state) {
		this.title = title;
		this.desc = desc;
		this.state = state;
	}
}
