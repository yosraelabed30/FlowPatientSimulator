package scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import medical.Center;
import events.ActivityEvent;
import events.ArrivalConsultation;
import events.PreConsultation;
import events.Idle;
import tools.Time;
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
	 * start, in minutes between 0 and 24*60-1.
	 */
	private int start;
	/**
	 * end, in minutes between 0 and 24*60-1.
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
		this.end = start+duration-1;
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
	 * @return true if this in which we insert has been completely replaced by the activity
	 */
	public boolean insert(Activity activity) {

		Block block = this.getBlock();
		boolean replacement = false;
		boolean cancellation = false;
		if(block!=null){
			if(activity.getStart() == this.getStart() && activity.getEnd() < this.getEnd()){
				this.setStart(activity.getEnd()+1);
				if(this.isCurrent() && this.getType()==ActivityType.Free){
					this.getSchedule().setCurrentActivityBeingDone(null);
					Idle idle = ((Idle)this.getActivityEvent());
					idle.getEnd().cancel();
				}
				else{
					cancellation = this.getActivityEvent().cancel(); //the cancel method cancels this event before it occurs. Returns true if cancellation succeeds(this event was found in the list), false otherwise
				}
			}
			else if(activity.getEnd() == this.getEnd() && activity.getStart() > this.getStart()){
				this.setEnd(activity.getStart()-1);
				if(this.isCurrent() && this.getType()==ActivityType.Free){
					Idle idle = ((Idle)this.getActivityEvent());
					idle.generateDelay();
					idle.getEnd().reschedule(idle.getDelay());
				}
			}
			else if(activity.getStart() == this.getStart() && activity.getEnd() == this.getEnd()){
				replacement=true;
				this.getActivityEvent().cancel();
				block.getActivities().remove(this);
				if(this.isCurrent() && this.getType()==ActivityType.Free){
					this.getSchedule().setCurrentActivityBeingDone(null);
					Idle idle = ((Idle)this.getActivityEvent());
					idle.getEnd().cancel();
				}
				else{
					cancellation = this.getActivityEvent().cancel(); //the cancel method cancels this event before it occurs. Returns true if cancellation succeeds(this event was found in the list), false otherwise
				}
				this.setBlock(null);
			}
			else{
				Activity free2 = this.clone(); // TODO problem there, the activity cloned has a Done status, so when it's time to do the event associated to that activity, it is ignored. 
				/*
				 * Now the simple way to correct it is to set free2.status to NotDone.
				 * Another way is to only set the status of an activity to done when the events associated to it are done. 
				 * The latter is the most logical, it requires however to change the Activity.
				 * Activity right now (06/08/16) has a start event, but no end event... this should be changed => see the idea of a list of activityEvent
				 */
				//TODO see with Yosra, we should schedule only the necessary time in the schedule of the doctor. Or else if he finishes in advance we should know what he should do
				free2.setStatus(ActivityStatus.NotDone);
				free2.setStart(activity.getEnd()+1);
				this.setEnd(activity.getStart()-1);
				block.getActivities().add(free2);
				if(this.isCurrent() && this.getType()==ActivityType.Free){
					Idle idle = ((Idle)this.getActivityEvent());
					idle.generateDelay();
					idle.getEnd().reschedule(idle.getDelay());
				}
			}
			activity.setBlock(block);
			block.getActivities().add(activity);
			Collections.sort(block.getActivities());
		}
		else{
			System.out.println("Activity insert ; the block of the activity in which we want to insert is null");
		}
		return replacement;
	}
	
	public boolean isCurrent() {
		return this.getSchedule().getCurrentActivityBeingDone()==this;
	}
	
	/**
	 * Only an activity which does not have a status free can be deleted
	 */
	public void delete() {

		Block block = this.getBlock();
		

		if (block != null) {
			LinkedList<Activity> activities = block.getActivities();
			this.setType(ActivityType.Free);
			this.setActivityEvent(new Idle());
			int index = activities.indexOf(this);
			
			if (index == 0 && activities.size() >= 2) {
				if (activities.get(1).getType() == ActivityType.Free) {
					this.merge(activities.get(1));
				}

			} else if (activities.size() >= 2 && index == activities.size() - 1) {
				if (activities.get(index - 1).getType() == ActivityType.Free) {
					activities.get(index - 1).merge(this);
				}
			}

			else if (activities.size() >= 3 && index != activities.size() - 1 && index != 0) {
				if (activities.get(index - 1).getType() == ActivityType.Free
						&& activities.get(index + 1).getType() != ActivityType.Free) {
					activities.get(index - 1).merge(this);
				} else if (activities.get(index - 1).getType() != ActivityType.Free
						&& activities.get(index + 1).getType() == ActivityType.Free) {
					this.merge(activities.get(index + 1));
				} else if (activities.get(index - 1).getType() == ActivityType.Free
						&& activities.get(index + 1).getType() == ActivityType.Free) {
					activities.get(index - 1).merge(this);
					activities.get(index - 1).merge(activities.get(index));
				}
			}

		}
	}
	
	/*
	 * Getters and setters
	 */

	/**
	 * this must always be before activity
	 * both this and activity must be free activity
	 * @param activity
	 */
	private void merge(Activity activity) {
		
		if (this.compareTo(activity)<=0) {
			this.setEnd(activity.getEnd());
		} else {
			System.out.println("this should always be before activity");
		}
		this.getBlock().getActivities().remove(activity);
		activity.setBlock(null);
		if(this.isCurrent()){
//			System.out.println("Fusion ; this activity id : "+this.activityId+", activity id : "+activity.getActivityId());
			Idle idle = ((Idle)this.getActivityEvent());
			idle.generateDelay();
			idle.getEnd().reschedule(idle.getDelay());
		}
	}
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
		return Time.duration(start, end);
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

	public ISchedule getiSchedule() {
		ISchedule res = null;
		if(this.getSchedule()!=null){
			res = this.getSchedule().getiSchedule();
		}
		return res;
	}

	public Date getDate() {
		// TODO exception si l'activité n'est pas liée à une semaine ou un jour
		
		Date date= new Date (this.getWeek().getWeekId(),this.getDay().getDayId(),this.getStart());
		return date;
	
	}
	
	public Date getEndDate(){
		// TODO exception si l'activité n'est pas liée à une semaine ou un jour
		Date date = new Date(this.getWeek().getWeekId(), this.getDay()
				.getDayId(), this.getEnd());
		return date;
	}
	
	/**
	 * 
	 * @param activity
	 * @return arrayList of intervals corresponding to interval1 with interval2 excluded
	 */
	private static ArrayList<ArrayList<Integer>> exclude(
			ArrayList<Integer> interval1, ArrayList<Integer> interval2) {
		ArrayList<ArrayList<Integer>> res = new ArrayList<>();
		ArrayList<Integer> remaining1 = new ArrayList<>();
		
		if(Time.duration(interval1.get(0), interval1.get(1))==0 && Time.duration(interval2.get(0), interval2.get(1))!=0){
			if(interval1.get(0)<interval2.get(0) || interval1.get(0)>interval2.get(1)){
				remaining1.add(interval1.get(0));
				remaining1.add(interval1.get(1));
				res.add(remaining1);
			}
		}
		else if (Time.duration(interval2.get(0), interval2.get(1))==0 && Time.duration(interval1.get(0), interval1.get(1))!=0){
			if(interval2.get(0)==interval1.get(0)){
				remaining1.add(interval1.get(0));
				remaining1.add(interval1.get(1));
				res.add(remaining1);
			}
			else if(interval2.get(0)>interval1.get(0) && interval2.get(0)<=interval1.get(1)){
				remaining1.add(interval1.get(0));
				remaining1.add(interval1.get(1)-1);
				res.add(remaining1);
				ArrayList<Integer> remaining2 = new ArrayList<>();
				remaining2.add(interval2.get(0));
				remaining2.add(interval1.get(1));
				res.add(remaining2);
			}
		}
		else if (Time.duration(interval2.get(0), interval2.get(1))==0 && Time.duration(interval1.get(0), interval1.get(1))==0){
			if(interval2.get(0)!=interval1.get(0)){
				remaining1.add(interval1.get(0));
				remaining1.add(interval1.get(1));
				res.add(remaining1);
			}
		}
		else{
			if(interval2.get(0) <= interval1.get(0) && interval2.get(1) >= interval1.get(1)){
				// res stays empty
			} else if (interval2.get(0) <= interval1.get(0)
					&& interval2.get(1) >= interval1.get(0)
					&& interval2.get(1) < interval1.get(1)) {
				remaining1.add(interval2.get(1)+1);
				remaining1.add(interval1.get(1));
				res.add(remaining1);
			} else if (interval2.get(1) >= interval1.get(1)
					&& interval2.get(0) > interval1.get(0)
					&& interval2.get(0) <= interval1.get(1)) {
				remaining1.add(interval1.get(0));
				remaining1.add(interval2.get(0)-1);
				res.add(remaining1);
			} else if (interval2.get(0) > interval1.get(0)
					&& interval2.get(1) < interval1.get(1)) {
				remaining1.add(interval1.get(0));
				remaining1.add(interval2.get(0)-1);
				res.add(remaining1);
				ArrayList<Integer> remaining2 = new ArrayList<>();
				remaining2.add(interval2.get(1)+1);
				remaining2.add(interval1.get(1));
				res.add(remaining2);
			} else if (interval2.get(1) < interval1.get(0)
					|| interval2.get(0) > interval1.get(1)) {
				remaining1.add(interval1.get(0));
				remaining1.add(interval1.get(1));
				res.add(remaining1);
			}
		}
		return res;
	}

	/**
	 * The activities must not overlap
	 * This method sort the activities before
	 * @param activities
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> exclude(ArrayList<Activity> activities){
		ArrayList<ArrayList<Integer>> res = new ArrayList<>();
		ArrayList<Integer> thisInterval = new ArrayList<>();
		thisInterval.add(this.getStart());
		thisInterval.add(this.getEnd());
		res.add(thisInterval);
		if(activities!=null){
			ArrayList<Activity> noNulls = new ArrayList<>();
			for (Activity activity : activities) {
				if(activity!=null){
					noNulls.add(activity);
				}
			}
			Collections.sort(noNulls);
			for (Activity activity : noNulls) {
				if(activity!=null && this.getDate().checkSameWeekAndDayAs(activity.getDate())){
					ArrayList<Integer> activityInterval = new ArrayList<>();
					activityInterval.add(activity.getStart());
					activityInterval.add(activity.getEnd());
					int indexLast = res.size()-1;
					ArrayList<Integer> last = res.get(indexLast);
					res.remove(indexLast);
					res.addAll(Activity.exclude(last, activityInterval));
				}
			}
		}
		return res;
	}

}
