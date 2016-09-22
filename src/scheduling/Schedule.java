package scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import events.ActivityEvent;
import medical.Center;
import medical.Patient;
import tools.Time;
import umontreal.iro.lecuyer.simevents.Sim;

/**
 * Schedule of a resource, if a resource has a schedule then it is an attribute
 * of the child resource class
 * 
 * @author Joffrey
 *
 */
public class Schedule {
	/**
	 * Static id used to increase the scheduleId
	 */
	private static int staticScheduleId = 0;
	/**
	 * each schedule has an id, the first schedule being id 0, the 100th
	 * schedule being id 99
	 */
	private int scheduleId;
	/**
	 * Resource to which this schedule is linked
	 */
	private ISchedule iSchedule;
	/**
	 * list of weeks
	 */
	protected ArrayList<Week> weeks;
	/**
	 * by default the id of the default week is -1 and it should not be changed
	 */
	protected Week defaultWeek;
	/**
	 * the events associated to that activity are being done, is used in the
	 * insert method in the class activity
	 */
	private Activity currentActivityBeingDone;

	public Schedule(ISchedule iSchedule) {
		super();
		this.scheduleId = staticScheduleId++;
		this.setiSchedule(iSchedule);
		this.weeks = new ArrayList<>();
		this.defaultWeek = new Week(this, -1);
	}

	public ArrayList<Week> getWeeks() {
		return weeks;
	}

	public void setWeeks(ArrayList<Week> weeks) {
		this.weeks = weeks;
	}

	/**
	 * Throws an IndexOutOfBoundsException if there is no week of id weekId in
	 * the this schedule
	 * 
	 * @param weekId
	 * @return the week in the schedule with an id equal to weekId
	 */
	public Week getWeek(int weekId) {
		Week res = null;
		if (!this.weeks.isEmpty()) {
			int weekIndex = weekId - this.weeks.get(0).getWeekId();
			res = this.weeks.get(weekIndex);
		} else {
			throw new IndexOutOfBoundsException();
		}
		return res;
	}

	public void setWeek(int weekId, Week week) {
		this.weeks.set(weekId, week);
	}

	/**
	 * Throws an IndexOutOfBoundsException if there is day corresponding to the
	 * input It might be because the weekId and/or the dayId do not exist
	 * 
	 * @param weekId
	 * @param dayId
	 * @return the day id'ed dayId of the week id'ed weekId
	 */
	public Day getDay(int weekId, int dayId) {
		Week w = null;
		try {
			w = this.getWeek(weekId);
		} catch (Exception e) {
			throw new IndexOutOfBoundsException();
		}
		return w.getDay(dayId);
	}

	public Block getBlock(int weekId, int dayId, int blockId) {
		Day d = null;
		try {
			d = this.getDay(weekId, dayId);
		} catch (Exception e) {
			throw new IndexOutOfBoundsException();
		}
		return d.getBlock(blockId);
	}

	public Activity getActivity(int weekId, int dayId, int blockId,
			int activityId) {
		Block b = null;
		try {
			b = this.getBlock(weekId, dayId, blockId);
		} catch (Exception e) {
			throw new IndexOutOfBoundsException();
		}
		return b.getActivity(activityId);
	}

	/**
	 * do the next task of the day
	 */
	public void doNextTask() {
		if(this.getiSchedule().getClass()!=Patient.class || !((Patient)this.getiSchedule()).isOut()){
			int time = (int) Sim.time();
			int weekId = Time.weekCorrespondingToTime(time);
			int dayId = Time.weekDayCorrespondingToTime(time);
			int min = Time.minIntoTheDay(time);
			
			ArrayList<Block> blocks = null;
			try {
				blocks = this.getDay(weekId, dayId).getBlocks();
			} catch (Exception e) {
				this.addWeek(weekId);
				blocks = this.getDay(weekId, dayId).getBlocks();
			}

			int delay = Integer.MAX_VALUE;
			searchNext: for (Block block : blocks) {
				for (Activity activity : block.getActivities()) {
					if (activity.getStatus() == ActivityStatus.NotDone
							&& activity.getActivityEvent().time() == -10) {
						if (activity.getActivityEvent().getLateness() != Integer.MAX_VALUE) {
							delay = Math.max(0, activity.getStart()+ activity.getActivityEvent().getLateness() - min);
							activity.getActivityEvent().schedule(delay);
							break searchNext;
						} else {
							activity.setStatus(ActivityStatus.ToPostpone);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param weekId
	 * @return the week of id weekId, which has been created and added if it was
	 *         not in the schedule
	 */
	public Week addWeek(int weekId) {
		Week res = null;
		try {
			res = this.getWeek(weekId);
		} catch (Exception e) {
			this.defaultWeek.setWeekId(weekId);
			res = defaultWeek.clone();
			this.defaultWeek.setWeekId(-1);
			this.weeks.add(res);
		}
		return res;
	}

	/**
	 * Used to be called findFreeActivityToInsertOtherActivity
	 * 
	 * @param date
	 * @param duration
	 * @return
	 */
	public Availability findAvailability(Date date, int duration) {
		Availability free = null;
		int start = date.getMinute();
		int end = Time.end(start, duration);
		int weekId = date.getWeekId();
		try {
			this.getWeek(weekId);
		} catch (Exception e) {
			this.addWeeks(weekId);
		}
		int dayId = date.getDayId();
		ArrayList<Block> blocks = this.getDay(weekId, dayId).getBlocks();
		for (Block block : blocks) {
			if (block.getStart() <= start && block.getEnd() >= end) {
				for (Activity activity : block.getActivities()) {
					if (activity.getType() == ActivityType.Free
							&& activity.getStart() <= start
							&& activity.getEnd() >= end) {
						free = new Availability(activity, start, end);
						break;
					}
				}
			}
		}
		return free;
	}

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public Week getDefaultWeek() {
		return defaultWeek;
	}

	public void setDefaultWeek(Week defaultWeek) {
		this.defaultWeek = defaultWeek;
	}

	/**
	 * Throws an IndexOutOfBoundException if the list weeks is empty
	 * 
	 * @return
	 */
	public Week getLastWeek() {
		return this.getWeeks().get(this.getWeeks().size() - 1);
	}

	public int getLastWeekId() {
		return this.getWeeks().get(this.getWeeks().size() - 1).getWeekId();
	}

	/**
	 * Add the missing weeks from the last week of the schedule to week id'ed
	 * weekId If the weekId is inferior to the first weekId of the weeks, this
	 * method will throw an exception IndexOutOfBound
	 * 
	 * @param weekId
	 * @return week id'ed weekId
	 */
	public Week addWeeks(int weekId) {
		Week res = null;
		if (weekId >= 0) {
			if (this.weeks.isEmpty()) {
				for (int newWeekId = 0; newWeekId <= weekId; newWeekId++) {
					this.addWeek(newWeekId);
				}
				res = this.getLastWeek();
			} else {
				int lastWeekId = this.getLastWeekId();
				if (weekId > lastWeekId) {
					for (int newWeekId = lastWeekId + 1; newWeekId <= weekId; newWeekId++) {
						this.addWeek(newWeekId);
					}
					res = this.getLastWeek();
				} else {
					res = this.getWeek(weekId);
				}
			}
		}
		if (res == null) {
			throw new IndexOutOfBoundsException();
		}
		return res;
	}

	public Activity getActivityAssociated(Date date) {
		int weekId = date.getWeekId();
		int dayId = date.getDayId();

		Day day = null;
		Activity activityAssociated = null;
		day = this.getDay(weekId, dayId);
		for (Block blocks : day.getBlocks()) {
			for (Activity activity : blocks.getActivities()) {
				if (activity.getStart() <= date.getMinute()
						&& date.getMinute() <= activity.getEnd()) {
					activityAssociated = activity;
				}
			}

		}
		return activityAssociated;
	}

	public void deleteFirstTreatmentAssociated(Date date) {

		getActivityAssociated(date).delete();

	}

	public ISchedule getiSchedule() {
		return iSchedule;
	}

	public void setiSchedule(ISchedule iSchedule) {
		this.iSchedule = iSchedule;
	}

	public Availability findFirstAvailabitlity(ActivityType activityType,
			int duration, ArrayList<BlockType> blockTypes,
			ArrayList<Integer> daysForbidden) {
		return this.findFirstAvailability(activityType, duration, blockTypes,
				daysForbidden, Date.now(), Date.infinity);
	}

	public Availability findFirstAvailability(ActivityType activityType,
			int duration, ArrayList<BlockType> blockTypes,
			ArrayList<Integer> daysForbidden, Date dateLowerBound) {
		return this.findFirstAvailability(activityType, duration, blockTypes,
				daysForbidden, dateLowerBound, Date.infinity);
	}

	public Availability findFirstAvailability(ActivityType activityType,
			int duration, ArrayList<BlockType> blockTypes,
			ArrayList<Integer> daysForbidden, Date dateLowerBound,
			Date dateUpperBound) {
		return this.findFirstAvailability(activityType, duration, blockTypes,
				daysForbidden, new ArrayList<Activity>(), dateLowerBound,
				dateUpperBound);
	}

	/**
	 * @param activityType for which you wish to find an availability
	 * @param duration of the availability at minimal
	 * @param blockTypes the availability must be in a block of one of those types
	 * @param daysForbidden list of integer between 0 and 6, of forbidden days for the availability
	 * @param activitiesToAvoid list of activities to avoid, the availability returned should not overlap any of those activities
	 * @param dateLowerBound date at which we start to find the availability, included
	 * @param dateUpperBound last possible date of the availability, included
	 * @return the first availability in this schedule, respecting the constraints linked to the inputs
	 */
	public Availability findFirstAvailability(ActivityType activityType,
			int duration, ArrayList<BlockType> blockTypes,
			ArrayList<Integer> daysForbidden,
			ArrayList<Activity> activitiesToAvoid, Date dateLowerBound,
			Date dateUpperBound) {

		Availability avail = null;

		while (avail == null && dateLowerBound.compareTo(dateUpperBound) <= 0) {
			Week week = addWeeks(dateLowerBound.getWeekId());
			if (activityType != ActivityType.Consultation
					|| week.getQuotas() > 0) {
				Day day = this.getDay(dateLowerBound.getWeekId(),
						dateLowerBound.getDayId());
				if (!daysForbidden.contains(day.getDayId())) {
					if (!dateLowerBound.checkSameWeekAndDayAs(dateUpperBound)) {
						for (Block block : day.getBlocks()) {
							if (blockTypes.contains(block.getType())
									&& block.getEnd() >= dateLowerBound
											.getMinute()) {
								for (Activity activity : block.getActivities()) {
									if (activity.getType() == ActivityType.Free
											&& Time.duration(Math.max(
													dateLowerBound.getMinute(),
													activity.getStart()),
													activity.getEnd()) >= duration) {
										ArrayList<ArrayList<Integer>> remaining = activity
												.exclude(activitiesToAvoid);
										for (ArrayList<Integer> interval : remaining) {
											int end = Math.min(interval.get(1),
													dateUpperBound.getMinute());
											int start = Math.max(
													interval.get(0),
													dateLowerBound.getMinute());
											if (Time.duration(start, end) >= duration) {
												avail = new Availability(
														activity, start, end);
												return avail;
											}
										}
									}
								}
							}
						}
					} else {
						for (Block block : day.getBlocks()) {
							if (blockTypes.contains(block.getType())
									&& block.getEnd() >= dateLowerBound
											.getMinute()
									&& block.getStart() <= dateUpperBound
											.getMinute()) {
								for (Activity activity : block.getActivities()) {
									if (activity.getType() == ActivityType.Free
											&& Time.duration(Math.max(
													dateLowerBound.getMinute(),
													activity.getStart()), Math
													.min(dateUpperBound
															.getMinute(),
															activity.getEnd())) >= duration) {
										ArrayList<ArrayList<Integer>> remaining = activity
												.exclude(activitiesToAvoid);
										for (ArrayList<Integer> interval : remaining) {
											int end = Math.min(interval.get(1),
													dateUpperBound.getMinute());
											int start = Math.max(
													interval.get(0),
													dateLowerBound.getMinute());
											if (Time.duration(start, end) >= duration) {
												avail = new Availability(
														activity, start, end);
												return avail;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			dateLowerBound = dateLowerBound.increase();
			dateLowerBound.setMinute(0);
		}
		return avail;
	}

	public Activity getCurrentActivityBeingDone() {
		return currentActivityBeingDone;
	}

	public void setCurrentActivityBeingDone(Activity currentActivityBeingDone) {
		this.currentActivityBeingDone = currentActivityBeingDone;
	}

}
