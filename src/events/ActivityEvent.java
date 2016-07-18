package events;

import medical.Center;
import medical.Resource;
import scheduling.Activity;
import scheduling.ActivityStatus;
import scheduling.Block;
import scheduling.Day;
import scheduling.Schedule;
import scheduling.Week;
import umontreal.iro.lecuyer.simevents.Event;

public abstract class ActivityEvent extends Event{
	/**
	 * Activity to which this is linked.
	 * Is null iff the ActivityEvent is not linked to an Activity
	 */
	protected Activity activity;
	/**
	 * minimal number of minutes after which another ActivityEvent can be executed.
	 * Is used with the .schedule(int delay) 
	 */
	protected int delay;
	
	public ActivityEvent(){
		this.activity=null;
		this.setDelay(-1);
	}
	
	@Override
	public void actions() {
		if(this.getDelay()==-1){
			this.generateDelay();
		}
		if(activity!=null){ 
			activity.setStatus(ActivityStatus.Done);
		}
		childActions();
	}
	
	public abstract void childActions();

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	/**
	 * Each ActivityEvent child class has its own impletementation of that method.
	 */
	public abstract ActivityEvent clone();

	public abstract void generateDelay();

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public Center getCenter(){
		Center res = null;
		if(this.getResource()!=null){
			res = this.getResource().getCenter();
		}
		return res;
	}
	
	public Resource getResource() {
		Resource res = null;
		if(this.getSchedule()!=null){
			res = this.getSchedule().getResource();
		}
		return res;
	}
	
	public Schedule getSchedule(){
		Schedule res = null;
		if(this.getWeek()!=null){
			res = this.getWeek().getSchedule();
		}
		return res;
	}
	
	public Week getWeek(){
		Week res = null;
		if(this.getDay()!=null){
			res = this.getDay().getWeek();
		}
		return res;
	}
	
	public Day getDay(){
		Day res = null;
		if(this.getBlock()!=null){
			res = this.getBlock().getDay();
		}
		return res;
	}
	
	public Block getBlock(){
		Block res = null;
		if(this.getActivity()!=null){
			res = this.getActivity().getBlock();
		}
		return res;
	}
}
