package scheduling;

import medical.Center;
import medical.Resource;
/**
 * Week in a schedule
 * @author Joffrey
 *
 */
public class Week implements Comparable<Week>{
	/**
	 * schedule to which this week is linked
	 */
	private Schedule schedule;
	/**
	 * weekId is the identifier of the week in the schedule, it is not the position of the week in the list of weeks of the schedule.
	 * It is the week number ex : the 49th week since beginning is id 48. 
	 * From a Schedule with the weeks {47,48,49,50}, you can get the 49th week of the timeline by calling getWeek(49).
	 * If this week number is in the Schedule, it will be returned. Else an exception is raised. 
	 * See {@link Schedule#getWeek(int)}
	 */
	private int weekId;
	/**
	 * Array of the 7 days of the week
	 */
	private Day[] days;
	
	public Week(int weekId, Day[] days){
		this(null, weekId, days);
	}
	
	public Week(Schedule schedule, int weekId, Day[] days) {
		this.schedule=schedule;
		this.weekId = weekId;
		this.days = days;
		if(days!=null){
			for (Day day : days) {
				if(day!=null){
					day.setWeek(this);
				}
			}
		}
	}
	
	public Week(Schedule schedule, int weekId) {
		this(schedule, weekId, new Day[7]);
		for (int dayId = 0; dayId < days.length; dayId++) {
			days[dayId] = new Day(this, dayId);
		}
	}

	/**
	 * deep copy of this week, the layer of schedule under the week (days) is also clones linked to the newly created clone week.
	 * The clone week is however linked to the same schedule as the original week.
	 */
	public Week clone(){
		Week clone = new Week(this.schedule, this.weekId);
		for(int i=0;i<7;i++){
			Day day = this.getDay(i);
			day.setWeek(clone);
			clone.days[i] = day.clone();
			day.setWeek(this);
		}
		return clone;
	}

	public Day[] getDays() {
		return days;
	}

	public void setDays(Day[] days) {
		this.days = days;
	}

	public int getWeekId() {
		return weekId;
	}

	public void setWeekId(int id) {
		this.weekId = id;
	}
	
	public Day getDay(int dayId){
		return this.days[dayId];
	}
	
	public void setDay(int dayId, Day day){
		this.days[dayId]=day;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
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

	@Override
	public int compareTo(Week w) {
		int res;
		if(this.weekId < w.weekId){
			res = -1;
		}
		else if(this.weekId == w.weekId){
			res = 0;
		}
		else{
			res = 1;
		}
		return res;
	}
}
