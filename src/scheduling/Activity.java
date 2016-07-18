package scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import medical.Center;
import medical.Resource;
import events.ActivityEvent;
import events.Arrival;
import events.CheckAndPreConsultation;
import events.Idle;
import umontreal.iro.lecuyer.simevents.Event;
import umontreal.iro.lecuyer.simevents.Sim;

/**
 * Activity in a Block within a Schedule
 * @author Joffrey
 *
 */
public class Activity implements Comparable<Activity> {
	/**
	 * Associated block
	 */
	private Block block;
	/**
	 * id of the activity, beware, it is here an absolute number of the activity. Not the number of the activity in the associated block.
	 * All the activities in the program have an id which represents the order of creation.
	 * Hence the activityId is between 0 and +infinity.
	 */
	private int activityId;
	/**
	 * start, in minutes between 0 and 24*60.
	 */
	private int start;
	/**
	 * end, in minutes between 0 and 24*60.
	 */
	private int end;
	/**
	 * type of the activity, see the ActivityType class
	 */
	private ActivityType type;
	/**
	 * status of the activity, see the ActivityStatus class
	 */
	private ActivityStatus status;
	/**
	 * Event linked to this activity, when the time comes to do that activity, the event is called
	 */
	private ActivityEvent event;
	/**
	 * static counter used to set an activity's id
	 */
	private static int staticActivityId=0;

	public Activity(Block block, int activityId, int start, int end,
			ActivityType type, ActivityStatus status, ActivityEvent event) {
		super();
		this.block=block;
		this.activityId = activityId;
		this.start = start;
		this.end = end;
		this.type = type;
		this.status = status;
		this.event = event;
		if(event!=null){
			this.event.setActivity(this);
		}
	}
	
	public Activity(int start, int end){
		this(null, start, end);
	}

	public Activity(Block block, int start, int end) {
		this(block, staticActivityId++, start, end, ActivityType.Free,
				ActivityStatus.NotDone, new Idle());
	}
	
	public Activity(Block block, int start, int end, ActivityEvent event) {
		this(block, staticActivityId++, start, end, ActivityType.Free,
				ActivityStatus.NotDone, event);
	}

	public Activity(Block block, int start, int end, ActivityType type,
			ActivityEvent event) {
		this(block, staticActivityId++, start, end, type,
				ActivityStatus.NotDone, event);
	}
	
	public Activity(int start, int duration, ActivityType type, ActivityEvent event){
		this.start=start;
		this.end = start+duration;
		this.type = type;
		this.event = event;
		if(event!=null){
			this.event.setActivity(this);
		}
		this.activityId = staticActivityId++;
		this.status = ActivityStatus.NotDone;
		this.block=null;
	}
	
	/**
	 * deep copy of this activity, the layer of schedule under the activity (ActivityEvent) is also clones linked to the newly created clone activity.
	 * The clone activity is however linked to the same block as the original activity.
	 */
	public Activity clone() {
		Activity clone = new Activity(this.getBlock(), staticActivityId++, this.start, this.end, this.type,
				this.status, null);
		ActivityEvent actEvent = this.getActivityEvent();
		actEvent.setActivity(clone);
		clone.setActivityEvent(actEvent.clone());
		actEvent.setActivity(this);
		return clone;
	}
	
	/**
	 * Should normally be used if this is a free activity
	 * @param activity
	 */
	public void insert(Activity activity) {
		Block block = this.getBlock();
		if(block!=null){
			if(activity.getStart() == this.getStart() && activity.getEnd() < this.getEnd()){
				this.setStart(activity.getEnd());
			}
			else if(activity.getEnd() == this.getEnd() && activity.getStart() > this.getStart()){
				this.setEnd(activity.getStart());
			}
			else if(activity.getStart() == this.getStart() && activity.getEnd() == this.getEnd()){
				block.getActivities().remove(this);
			}
			else{
				Activity free2 = this.clone();
				free2.setStart(activity.getEnd());
				this.setEnd(activity.getStart());
				block.getActivities().add(free2);
			}
			activity.setBlock(block);
			block.getActivities().add(activity);
			Collections.sort(block.getActivities());
		}
	}
	
	public void deleteActivityAssociated() {
		
		Block block = this.getBlock();	
		
		if(block!=null){
			LinkedList <Activity> activitiesAssociated = block.getActivities();
			activitiesAssociated.remove(this);
	
		}
	}
	
	/*
	 * Getters and setters
	 */

	public ActivityType getType() {
		return type;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int duration() {
		return end - start;
	}

	public boolean startsEarlierThan(Activity earliest) {
		return this.start <= earliest.start;
	}

	/**
	 *TODO take into account the week and day, even though comparison are made within the same day
	 * Compares two activities, so that Activity implements Comparable<Activity> correctly
	 * if this starts earliers than the act input then it returns -1
	 * else if this starts on the same minute as act then it returns 0
	 * else if this starts after act input then it return 1
	 */
	@Override
	public int compareTo(Activity act) {
		int res;
		if (this.start < act.start) {
			res = -1;
		} else if (this.start == act.start) {
			res = 0;
		} else {
			res = 1;
		}
		return res;
	}

	public ActivityEvent getActivityEvent() {
		return event;
	}

	public void setActivityEvent(ActivityEvent event) {
		event.setActivity(this);
		this.event = event;
	}

	public boolean canBeDone() {
		// TODO Auto-generated method stub
		return true;
	}

	public ActivityStatus getStatus() {
		return status;
	}

	public void setStatus(ActivityStatus status) {
		this.status = status;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int id) {
		this.activityId = id;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public Schedule getSchedule() {
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

	public Resource getResource() {
		Resource res = null;
		if(this.getSchedule()!=null){
			res = this.getSchedule().getResource();
		}
		return res;
	}
	
	public Center getCenter(){
		Center res = null;
		if(this.getResource()!=null){
			res = this.getResource().getCenter();
		}
		return res;
	}

	public Date getDate() {
		// TODO exception si l'activité n'est pas liée à une semaine ou un jour
		
		Date date= new Date (this.getWeek().getWeekId(),this.getDay().getDayId(),this.getStart());
		return date;
		
	}
}
