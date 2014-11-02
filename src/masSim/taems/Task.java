
package masSim.taems;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.concurrent.locks.Lock;

import masSim.world.WorldState;
import raven.Main;

public class Task extends Node {

	private boolean debugFlag = false;
	private QAF qaf;
	public Date earliest_start_time;
	public Date deadline;
	public boolean isComplete = false;
	private Lock lock;
	
	public int GetUtility()
	{
		return 0;
	}
	
	public boolean IsTask(){return true;}
	
	public boolean hasChildren()
	{
		return this.children.size()>0;
	}
	
	public boolean IsFullyAssigned()
	{
		if (this.agent==null) return false;
		for(Node n: children)
		{
			if (n.agent==null)
				return false;
		}
		return true;
	}
		
	// Constructor
	public Task(String label, QAF qaf, Date earliest_start, Date deadline, IAgent agent, Node[] m){
		this.label = label;
		children = new ArrayList<Node>();
		this.qaf = qaf;
		this.earliest_start_time = earliest_start;
		this.deadline = deadline;
		if (m!=null)
		{
			for(Node mm : m){
				this.children.add(mm);
			}
		}
		this.agent = agent;
	}
	
	public Task(String label, QAF qaf, Date earliest_start, Date deadline, IAgent agent ,Node m){
		this(label, qaf, earliest_start, deadline, agent, new Node[]{m});
	}
	
	public Task(String name, QAF qaf, IAgent agent){
		this(name, qaf, new Date(), new Date(2015,1,1), agent, new Method[]{});
	}
	
	public Task(String name, QAF qaf, IAgent agent, Node m){
		this(name, qaf, new Date(), new Date(2015,1,1), agent, m);
	}
	
	public Task(String name, QAF qaf, IAgent agent, Node[] m){
		this(name, qaf, new Date(), new Date(2015,1,1), agent, m);
	}
	
	
	public void addTask(Node task){
		this.children.add(task);
	}
	
	public QAF getQAF(){
		return qaf;
	}
	
	
	@Override
	public void MarkCompleted()
	{
		super.MarkCompleted();
		Main.Message(debugFlag, "[Task 63] Task " + label + " completed.");
		WorldState.CompletedTasks.add(this);
		this.NotifyAll();
	}
	
	@Override
	public synchronized void Cleanup()
	{
		if (this.hasChildren())
			for(Node n : children)
			{
				if (n!=null)
				{
					if (n.IsComplete())
					{
						children.remove(n);
					}
					else
					{
						if (n.IsTask())
						{
							n.Cleanup();
							if (n.IsComplete())//Recheck after cleanup
							{
								children.remove(n);
							}
						}
					}
				}
			}
		}
	
	public static Task CreateDefaultTask(int counter, double x, double y)
	{
		return new Task("Station " + counter,new SumAllQAF(), null, new Method("Visit Station " + counter,1,x,y));
	}
}
