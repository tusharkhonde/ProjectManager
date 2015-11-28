package com.sjsu.project.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Tasks")
public class Task implements Serializable{
	
	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="taskid")
    private long taskId;

	@Column(name="title")
    private String title;

    @Column(name="description")
    private String desc;

    @Column(name="state")
    private String state;
	
    // one to one userid
    @OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userid")
    private User assignee;

	// Many tasks to one project
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="projectid")
	private Project project;
    
    @Column(name="estimate")
    private long estimate;
    
    @Column(name="actual")
    private long actual;

	public Task() {
	}

	public Task(String title, String desc, String state, long estimate) {
		this.title = title;
		this.desc = desc;
        this.state = state;
		this.estimate = estimate;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
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

	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	public long getEstimate() {
		return estimate;
	}

	public void setEstimate(long estimate) {
		this.estimate = estimate;
	}

	public long getActual() {
		return actual;
	}

	public void setActual(long actual) {
		this.actual = actual;
	}
    
    

}
