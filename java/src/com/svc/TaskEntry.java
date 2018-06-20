package com.svc;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class TaskEntry
{
	private String topic;
	private String taskName;
	private ITask instance;
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public ITask getInstance() {
		return instance;
	}
	public void setInstance(ITask instance) {
		this.instance = instance;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return topic + "-" + taskName;
	}
}
