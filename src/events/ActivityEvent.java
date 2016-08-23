package events;

import medical.Center;
import scheduling.Activity;
import scheduling.ActivityStatus;
import scheduling.Block;
import scheduling.Day;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;
import umontreal.iro.lecuyer.simevents.Event;
import umontreal.iro.lecuyer.simprocs.Condition;

/**
 *	Events to happen associated to an instance of Activity 
 * @author Joffrey
 *
 */
public abstract class ActivityEvent extends Event{
	/**
	 * Activity to which this is linked.
	 * Is null iff the ActivityEvent is not linked to an Activity
	 */
	protected Activity activity;
	/**
	 * True if the activityEvent method action is called for the first time, false the second time (no third time)
	 * The point is to have the method action called twice with different behaviours, whether it is the start or the end the activityEvent
	 */
	private boolean inProgress;
	/**
	 * minimal number of minutes after which another ActivityEvent can be executed.
	 * Is used with the .schedule(int delay) 
	 */
	protected int delay;
	/**
	 * Model the gap, in minutes, between the activity programmed in a schedule and this.
	 */
	private int lateness;
	
	/**
	 * The activity is set to null, the lateness to 0 and the delay to -1 (suggesting that this cannot happen as long as {@link events.ActivityEvent#generateDelay()} is not called) , inProgress to false. See {@link events.ActivityEvent}
	 */
	public ActivityEvent(){
		this.setLateness(0);
		this.activity=null;
		this.setDelay(-1);
		this.setInProgress(false);
	}
	
	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public ISchedule getiSchedule() {
		ISchedule res = null;
		if(this.getSchedule()!=null){
			res = this.getSchedule().getiSchedule();
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

	public int getLateness() {
		return lateness;
	}

	public void setLateness(int lateness) {
		this.lateness = lateness;
	}
	
	/**
	 * Actions always performed when the this activityEvent happens.
	 */
	@Override
	public void actions() {
		if(!this.isInProgress()){
			if(this.conditions()){
				if(this.getSchedule()!=null){ // TODO change the activityEvent subclasses instances so that every one of them is linked to an activity
					this.getSchedule().setCurrentActivityBeingDone(this.getActivity());
				}
				this.setInProgress(true);
//				System.out.println("activityEvent starts");
				this.startActions();
				if(this.getDelay()==-1){
					this.generateDelay();
				}
				if(this.getDelay()<0){
					System.out.println("hey");
				}
				this.schedule(delay);
			}
		}
		else{
			if(this.getActivity()!=null){ 
				this.getActivity().setStatus(ActivityStatus.Done);
			}
			if(this.getSchedule()!=null){ // TODO change the activityEvent subclasses instances so that every one of them is linked to an activity
				this.getSchedule().setCurrentActivityBeingDone(null);
			}
//			System.out.println("activityEvent ends");
			endActions();
		}
	}
	
	/**
	 * actions needed to be performed at the beginning of the corresponding activity, if the conditions are respected,
	 * see {@link events.ActivityEvent#conditions()}. For actions that are done during the activity, and can be considered to be done 
	 * only at the end of the activity, see {@link events.ActivityEvent#endActions()}.
	 */
	public abstract void startActions();
	
	/**
	 * if this method returns true, the actions linked to that activity will be done, see {@link events.ActivityEvent#startActions()} and {@link events.ActivityEvent#endActions()}
	 * @return true if the conditions necessary to do the activity are respected.
	 */
	public abstract boolean conditions();

	/**
	 * actions supposed to happen during the event, the actions will all occur at the end of the activityEvent.
	 * Warning : these actions can only be done if the conditions for the activity are respected, see {@link events.ActivityEvent#conditions()}.
	 */
	public abstract void endActions();
	
	/**
	 * Each ActivityEvent child class has its own impletementation of that method.
	 */
	public abstract ActivityEvent clone();

	public abstract void generateDelay();

	/**
	 * 
	 * @return true if the activityEvent is currently being done, false otherwise.
	 */
	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}
	

}
