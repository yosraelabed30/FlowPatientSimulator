package scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import events.ActivityEvent;
import medical.Center;
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
	
	private Activity currentActivityBeingDone; //ie the events associated to that activity are being done, is used in the insert method in the class activity
	
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
	 * Throws an IndexOutOfBoundsException if there is no week of id weekId in the this schedule
	 * @param weekId
	 * @return the week in the schedule with an id equal to weekId
	 */
	public Week getWeek(int weekId) {
		Week res = null;
		if(!this.weeks.isEmpty()){
			int weekIndex = weekId - this.weeks.get(0).getWeekId();
			res = this.weeks.get(weekIndex);
		}
		else{
			throw new IndexOutOfBoundsException();
		}
		return res;
	}

	public void setWeek(int weekId, Week week) {
		this.weeks.set(weekId, week);
	}

	/**
	 * Throws an IndexOutOfBoundsException if there is day corresponding to the input
	 * It might be because the weekId and/or the dayId do not exist
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

	public Activity getActivity(int weekId, int dayId, int blockId, int activityId) {
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
		// TODO test
		int time = (int) Sim.time();
		int weekId = Time.weekCorrespondingToTime(time);
		int dayId = Time.weekDayCorrespondingToTime(time);
		int min = Time.minIntoTheDay(time);

		ArrayList<Block> blocks = this.getDay(weekId, dayId).getBlocks();

		int delay = Integer.MAX_VALUE;
		searchNext: for (Block block : blocks) {
			for (Activity activity : block.getActivities()) {
				if (activity.getStatus() == ActivityStatus.NotDone && activity.getActivityEvent().time()==-10) {
					if(activity.getActivityEvent().getLateness()!=Integer.MAX_VALUE){
						delay = Math.max(0, activity.getStart() - min)+activity.getActivityEvent().getLateness();
						activity.getActivityEvent().schedule(delay);
						break searchNext;
					}
					else{
						activity.setStatus(ActivityStatus.ToPostpone);
					}
				}
			}
		}

	}

	/**
	 * 
	 * @param weekId
	 * @return the week of id weekId, which has been created and added if it was not in the schedule
	 */
	public Week addWeek(int weekId) {
		Week res = null;
		try {
			this.getWeek(weekId);
		} catch (Exception e) {
			this.defaultWeek.setWeekId(weekId);
			res = defaultWeek.clone();
			this.defaultWeek.setWeekId(-1);
			this.weeks.add(res);
		}
		return res;
	}

	public Activity getFirstAvailability(int duration, BlockType blockType, int weekLowerBound, int dayLowerBound,
			int minuteLowerBound) {
		boolean found = false;

		while (!found) {
			Week w = null;
			try {
				w = this.getWeeks().get(weekLowerBound);
			} catch (Exception e) {
				w = this.addWeek(weekLowerBound);
			}

			Day d = w.getDays()[dayLowerBound];
			for (Block b : d.getBlocks()) {
				if (b.getType() == blockType) {
					for (Activity a : b.getActivities()) {
						if (a.getType() == ActivityType.Free
								&& a.getEnd() - Math.max(a.getStart(), minuteLowerBound) > duration) {
							return a;
						}
					}
				}
			}

			minuteLowerBound = 0;
			if (dayLowerBound == 6) {
				dayLowerBound = 0;
				weekLowerBound++;
			} else {
				dayLowerBound++;
			}
		}

		return null;
	}

	public Activity getFirstAvailability(int duration, BlockType blockType) {
		int time = (int) Sim.time();
		int weekLowerBound = Time.weekCorrespondingToTime(time);
		int dayLowerBound = Time.weekDayCorrespondingToTime(time);
		int minuteLowerBound = Time.minIntoTheDay(time);
		return getFirstAvailability(duration, blockType, weekLowerBound, dayLowerBound, minuteLowerBound);
	}

	public Activity getFirstAvailability(int duration, BlockType blockType, Date dateLB) {
		return this.getFirstAvailability(duration, blockType, dateLB.getWeekId(), dateLB.getDayId(),
				dateLB.getMinute());
	}

	public Activity getFirstAvailabilityInDay(int duration, BlockType blockType, Date dateLB) {
		Activity res = null;
		Day day = this.getDay(dateLB.getWeekId(), dateLB.getDayId()); // check
																		// for
																		// possible
																		// exception
																		// and
																		// null
																		// values
		for (Block block : day.getBlocks()) {
			if (block.getStart() >= dateLB.getMinute() && block.getType() == blockType) {
				for (Activity activity : block.getActivities()) {
					if (activity.getType() == ActivityType.Free && activity.duration() >= duration) {
						res = activity;
					}
				}
			}
		}
		return res;
	}

	/**
	 * 
	 * @param weekId
	 * @param dayId
	 * @param start
	 * @param end
	 * @return null if no free activity found
	 */
	public Activity findFreeActivityToInsertOtherActivity(int weekId, int dayId, int start, int end) {
		try {
			this.getWeek(weekId);
		} catch (Exception e) {
			int lastWeekId = this.getLastWeekId();
			for (int i = lastWeekId + 1; i <= weekId; i++) {
				this.addWeek(weekId);
			}
		}
		LinkedList<Activity> patientActivities = this.getBlock(weekId, dayId, 0).getActivities(); // blockId
																									// =
																									// 0
																									// because
																									// a
																									// patient
																									// has
																									// only
																									// one
																									// block
																									// in
																									// its
																									// schedule
																									// each
																									// day
		Activity free = null;
		for (Activity activity : patientActivities) {
			if (activity.getType() == ActivityType.Free && activity.getStart() <= start && activity.getEnd() >= end) {
				free = activity;
				break;
			}
		}
		return free;
	}

	public Activity findFreeActivityToInsertOtherActivity(Date date, int duration) {
		return this.findFreeActivityToInsertOtherActivity(date.getWeekId(), date.getDayId(), date.getMinute(),
				date.getMinute() + duration);
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
	 * @return
	 */
	public Week getLastWeek(){
		return this.getWeeks().get(this.getWeeks().size() - 1);
	}

	public int getLastWeekId() {
		return this.getWeeks().get(this.getWeeks().size() - 1).getWeekId();
	}

	public Activity getFirstAvailabilityNotWeekend(int duration, BlockType blockType, int weekLowerBound,
			int dayLowerBound, int minuteLowerBound) {
		boolean found = false;

		while (!found) {
			Week w = null;
			try {
				w = this.getWeeks().get(weekLowerBound);
			} catch (Exception e) {
				w = this.addWeeks(weekLowerBound);
			}

			Day d = w.getDays()[dayLowerBound];
			for (Block b : d.getBlocks()) {
				if (b.getType() == blockType) {
					for (Activity a : b.getActivities()) {
						if (a.getType() == ActivityType.Free
								&& a.getEnd() - Math.max(a.getStart(), minuteLowerBound) >= duration
								&& a.getDay().getDayId() != 4 && a.getDay().getDayId() != 5
								&& a.getDay().getDayId() != 6) {
							return a;
						}
					}
				}
			}

			minuteLowerBound = 0;
			if (dayLowerBound == 6) {
				dayLowerBound = 0;
				weekLowerBound++;
			} else {
				dayLowerBound++;
			}
		}

		return null;
	}

	/**
	 * Add the missing weeks from the last week of the schedule to week id'ed weekId
	 * If the weekId is inferior to the first weekId of the weeks, this method will throw an exception IndexOutOfBound
	 * @param weekId
	 * @return week id'ed weekId
	 */
	public Week addWeeks(int weekId) {
		Week res = null;
		if(weekId>=0){
			if(this.weeks.isEmpty()){
				for(int newWeekId=0 ; newWeekId <= weekId ; newWeekId++){
					this.addWeek(newWeekId);
				}
				res = this.getLastWeek();
			}
			else{ 
				int lastWeekId = this.getLastWeekId();
				if(weekId>lastWeekId){
					for(int newWeekId=lastWeekId+1 ; newWeekId <= weekId ; newWeekId++){
						this.addWeek(newWeekId);
					}
					res = this.getLastWeek();
				}
				else{
					res = this.getWeek(weekId);
				}
			}
		}
		if(res==null){
			throw new IndexOutOfBoundsException();
		}
		return res;
	}


	public Activity getFirstAvailabilityNotFriday(int duration, ArrayList<BlockType> blockTypes, int weekLowerBound,
			int dayLowerBound, int minuteLowerBound) {
		boolean found = false;

		while (!found) {

			Week w = null;

			try {
				w = this.getWeeks().get(weekLowerBound);
			} catch (Exception e) {
				w = this.addWeek(weekLowerBound);
			}

			Day d = w.getDays()[dayLowerBound];
			for (Block b : d.getBlocks()) {
				if (blockTypes.contains(b.getType())) {
					for (Activity a : b.getActivities()) {
						if (a.getType() == ActivityType.Free
								&& a.getEnd() - Math.max(a.getStart(), minuteLowerBound) > duration
								&& a.getDay().getDayId() != 4) {
							return a;
						}
					}
				}
			}
			minuteLowerBound = 0;
			if (dayLowerBound == 6) {
				dayLowerBound = 0;
				weekLowerBound++;
			} else {
				dayLowerBound++;
			}
		}

		return null;
	}

	public Activity getFirstAvailability(int duration, ArrayList<BlockType> blockTypes, Date dateLowerBound) {

		boolean found = false;
		int weekLowerBound = dateLowerBound.getWeekId();
		int dayLowerBound = dateLowerBound.getDayId();
		int minuteLowerBound = dateLowerBound.getMinute();

		while (!found) {
			Week w = null;
			try {
				w = this.getWeeks().get(weekLowerBound);
			} catch (Exception e) {
				w = this.addWeek(weekLowerBound);
			}

			Day d = w.getDays()[dayLowerBound];
			for (Block b : d.getBlocks()) {
				if (blockTypes.contains(b.getType())) {
					for (Activity a : b.getActivities()) {
						if (a.getType() == ActivityType.Free
								&& a.getEnd() - Math.max(a.getStart(), minuteLowerBound) > duration) {
							return a;
						}
					}
				}
			}

			minuteLowerBound = 0;
			if (dayLowerBound == 6) {
				dayLowerBound = 0;
				weekLowerBound++;
			} else {
				dayLowerBound++;
			}
		}

		return null;
	}

	public Activity getFirstAvailabilityFriday(int duration, BlockType blockType, Date dateLowerBound) {
		boolean found = false;
		int weekLowerBound = dateLowerBound.getWeekId();
		int dayLowerBound = dateLowerBound.getDayId();
		int minuteLowerBound = dateLowerBound.getMinute();

		while (!found) {
			Week w = null;
			try {
				w = this.getWeeks().get(weekLowerBound);
			} catch (Exception e) {
				w = this.addWeek(weekLowerBound);
			}

			Day d = w.getDays()[dayLowerBound];
			for (Block b : d.getBlocks()) {
				if (b.getType() == blockType) {
					for (Activity a : b.getActivities()) {
						if (a.getType() == ActivityType.Free
								&& a.getEnd() - Math.max(a.getStart(), minuteLowerBound) > duration
								&& a.getDay().getDayId() != 5 && a.getDay().getDayId() != 6) {
							return a;
						}
					}
				}
			}

			minuteLowerBound = 0;
			if (dayLowerBound == 6) {
				dayLowerBound = 0;
				weekLowerBound++;
			} else {
				dayLowerBound++;
			}
		}
		return null;
	}

	public Activity getFirstAvailabilityFridayReserved(int duration, ArrayList<BlockType> blockTypes,
			Date dateLowerBound) {
		boolean found = false;
		int weekLowerBound = dateLowerBound.getWeekId();
		int dayLowerBound = dateLowerBound.getDayId();
		int minuteLowerBound = dateLowerBound.getMinute();

		while (!found) {
			Week w = null;
			try {
				w = this.getWeeks().get(weekLowerBound);
			} catch (Exception e) {
				w = this.addWeek(weekLowerBound);
			}

			Day d = w.getDays()[dayLowerBound];
			for (Block b : d.getBlocks()) {
				if (blockTypes.contains(b.getType())) {
					for (Activity a : b.getActivities()) {
						if (a.getType() == ActivityType.Free
								&& a.getEnd() - Math.max(a.getStart(), minuteLowerBound) > duration
								&& a.getDay().getDayId() != 5 && a.getDay().getDayId() != 6) {
							return a;
						}
					}
				}
			}

			minuteLowerBound = 0;
			if (dayLowerBound == 6) {
				dayLowerBound = 0;
				weekLowerBound++;
			} else {
				dayLowerBound++;
			}
		}

		return null;
	}

	public Activity getFirstAvailabilityNotWeekend(int duration, BlockType blockType, int weekLowerBound,
			int dayLowerBound, int minuteLowerBound, int weekUpperBound, int dayUpperBound, int minuteUpperBound) {
		boolean found = false;
		Date date = new Date(weekLowerBound, dayLowerBound, minuteLowerBound);
		Activity activity = null;

		while (date.getWeekId() <= weekUpperBound && date.getDayId() <= dayUpperBound && !found) {

				Week w1 = null;

				try {
					w1 = this.getWeeks().get(weekLowerBound);
				} catch (Exception e) {
					w1 = this.addWeeks(weekLowerBound);
				}

				Day d = w1.getDays()[dayLowerBound];
				for (Block b : d.getBlocks()) {

					if (b.getType() == blockType) {
						for (Activity a : b.getActivities()) {
							if (a.getType() == ActivityType.Free
									&& a.getEnd() - Math.max(a.getStart(), minuteLowerBound) > duration
									&& a.getDay().getDayId() != 5 && a.getDay().getDayId() != 6) {
								return activity = a;

							}
						}
					}

				minuteLowerBound = 0;
				date = date.increaseWeekend();

			}
			activity = null;

		}
		return activity;
	}

	public Activity getFirstAvailabilityNotWeekend(int duration, java.util.ArrayList<BlockType> blockTypes,
			int weekLowerBound, int dayLowerBound, int minuteLowerBound, int weekUpperBound, int dayUpperBound,
			int minuteUpperBound) {
		boolean found = false;
		Date date = new Date(weekLowerBound, dayLowerBound, minuteLowerBound);
		Activity activity = null;


		while (date.getWeekId() <= weekUpperBound && date.getDayId() <= dayUpperBound && !found) {

			Week w1 = null;
			try {
				w1 = this.getWeeks().get(weekLowerBound);
			} catch (Exception e) {
				w1 = this.addWeeks(weekLowerBound);
			}
			


			Day d = w1.getDays()[dayLowerBound];
			for (Block b : d.getBlocks()) {
				if (blockTypes.contains(b.getType())) {
					for (Activity a : b.getActivities()) {
						if (a.getType() == ActivityType.Free
								&& a.getEnd() - Math.max(a.getStart(), minuteLowerBound) > duration
								&& a.getDay().getDayId() != 5 && a.getDay().getDayId() != 6) {
							return activity = a;

						}
					}
				}
			}

			minuteLowerBound = 0;
			date = date.increaseWeekend();

			activity = null;

		}

		return activity;

	}

	public Activity getActivityAssociated(Date date) {
		int weekId = date.getWeekId();
		int dayId = date.getDayId();

		Day day = null;
		Activity activityAssociated = null;
		day = this.getDay(weekId, dayId);
		for (Block blocks : day.getBlocks()) {
			for (Activity activity : blocks.getActivities()) {
				if (activity.getStart() <= date.getMinute() && date.getMinute() <= activity.getEnd()) {
					activityAssociated = activity;
				}
			}

		}
		return activityAssociated;
	}

	public void deleteFirstTreatmentAssociated(Date date) {

		getActivityAssociated(date).delete();

	}

	public Activity getFirstAvailabilityNotWeekendWithoutConstraint(int duration, BlockType scan, int weekUpperBound,
			int dayUpperBound, int minuteUpperBound) {
		boolean found = false;
		Date date = Date.dateNow().increase();
		Activity activity = null;

		while (date.getWeekId() <= weekUpperBound && date.getDayId() <= dayUpperBound) {
			while (!found) {

				Week w = null;
				try {
					w = this.getWeeks().get(weekUpperBound);
				} catch (Exception e) {
					w = this.addWeeks(weekUpperBound);
				}

				Day d = w.getDays()[date.getDayId()];
				for (Block b : d.getBlocks()) {
					if (b.getType() == BlockType.Scan) {
						for (Activity a : b.getActivities()) {
							if (a.getType() == ActivityType.Free
									&& a.getEnd() - Math.max(a.getStart(), date.getMinute()) > duration
									&& a.getDay().getDayId() != 5 && a.getDay().getDayId() != 6) {
								return activity = a;

							}
						}
					}
				}

				date.setMinute(0);
				date = date.increaseWeekend();
			}
			activity = null;

		}

		return activity;
	}

	public Activity getFirstAvailabilityNotWeekendWithoutConstraint(int duration,
			java.util.ArrayList<BlockType> blockTypes, int weekUpperBound, int dayUpperBound, int minuteUpperBound) {
		boolean found = false;
		Date date = Date.dateNow();
		Activity activity = null;

		while (date.getWeekId() <= weekUpperBound && date.getDayId() <= dayUpperBound) {
			while (!found) {

				Week w = null;
				try {
					w = this.getWeeks().get(weekUpperBound);
				} catch (Exception e) {
					w = this.addWeeks(weekUpperBound);
				}

				Day d = w.getDays()[date.getDayId()];
				for (Block b : d.getBlocks()) {
					if (blockTypes.contains(b.getType())) {
						for (Activity a : b.getActivities()) {
							if (a.getType() == ActivityType.Free
									&& a.getEnd() - Math.max(a.getStart(), date.getMinute()) > duration
									&& a.getDay().getDayId() != 5 && a.getDay().getDayId() != 6) {
								return activity = a;

							}
						}
					}
				}

				date.setMinute(0);
				date = date.increaseWeekend();
			}
			activity = null;

		}

		return activity;
	}

	public Activity getFirstAvailabilityFridayQuotas(int duration, BlockType blockType, Date dateLowerBound) {
		boolean found = false;
		int weekLowerBound = dateLowerBound.getWeekId();
		int dayLowerBound = dateLowerBound.getDayId();
		int minuteLowerBound = dateLowerBound.getMinute();

		while (!found) {
			Week w = null;
			try {
				w = this.getWeeks().get(weekLowerBound);
			} catch (Exception e) {
				w = this.addWeek(weekLowerBound);
			}
			
			if (w.getQuotas() > 0) {

				Day d = w.getDays()[dayLowerBound];
				for (Block b : d.getBlocks()) {
					if (b.getType() == blockType) {
						for (Activity a : b.getActivities()) {
							if (a.getType() == ActivityType.Free
									&& a.getEnd() - Math.max(a.getStart(), minuteLowerBound) > duration
									&& a.getDay().getDayId() != 5 && a.getDay().getDayId() != 6) {
								return a;
							}
						}
					}
				}

				minuteLowerBound = 0;
				if (dayLowerBound == 6) {
					dayLowerBound = 0;
					weekLowerBound++;
				} else {
					dayLowerBound++;
				}
			}
			else{
				dayLowerBound = 0;
				weekLowerBound++;
			}
		}
	
		return null;
	}

	public Activity getFirstAvailabilityBeforeTheEndOfTheDay(int duration, BlockType treatment, int arrivalMinutes) {

		Date date = Date.toDates(arrivalMinutes);
		Day day = this.getDay(date.getWeekId(), date.getDayId());
		Activity activity = null;

		for (Block block : day.getBlocks()) {
			if (block.getType() == BlockType.Treatment && block.getEnd() >= arrivalMinutes
					&& block.getEnd() <= 18 * 60) {
				for (Activity a : block.getActivities()) {
					if (a.getType() == ActivityType.Free
							&& a.getEnd() - Math.max(a.getStart(), date.getMinute()) > duration) {
						return activity = a;

					}

				}
			}

		}

		return activity;
	}

	public Activity getFirstAvailabilityFridayQuotasBeforeTheEndOfTheDay(int duration, BlockType consultation,
			Date date) {
		int weekId = date.getWeekId();
		int dayId = date.getDayId();
		int minute= date.getMinute();
		Day day= this.getDay(weekId, dayId);

		for (Block block : day.getBlocks()) {
			if (block.getType() == BlockType.Consultation && block.getEnd() >= minute
					&& block.getEnd() <= 18 * 60) {
				for (Activity a : block.getActivities()) {
					if (a.getType() == ActivityType.Free
									&& a.getEnd() - Math.max(a.getStart(), minute) > duration ) {
								return a;
					}
				}
			}
		}


		
		return null;
	}

	public ISchedule getiSchedule() {
		return iSchedule;
	}

	public void setiSchedule(ISchedule iSchedule) {
		this.iSchedule = iSchedule;
	}
	
	public Availability getFirstAvailabitlity(int duration, ArrayList<BlockType> blockTypes,
			ArrayList<Integer> daysForbidden) {
		return this.getFirstAvailability(duration, blockTypes, daysForbidden, Date.dateNow(), Date.infinity);
	}
	
	public Availability getFirstAvailability(int duration,
			ArrayList<BlockType> blockTypes, ArrayList<Integer> daysForbidden,
			Date dateLowerBound) {
		return this.getFirstAvailability(duration, blockTypes, daysForbidden, dateLowerBound, Date.infinity);
	}

	public Availability getFirstAvailability(int duration,
			ArrayList<BlockType> blockTypes, ArrayList<Integer> daysForbidden,
			Date dateLowerBound, Date dateUpperBound) {
		return this.getFirstAvailability(duration, blockTypes, daysForbidden, new ArrayList<Activity>(), dateLowerBound, dateUpperBound);
	}

	public Availability getFirstAvailability(int duration,
			ArrayList<BlockType> blockTypes,
			ArrayList<Integer> daysForbidden,
			ArrayList<Activity> activitiesToAvoid, Date dateLowerBound,
			Date dateUpperBound) {
		 
		Availability avail = null;
		
		while (avail == null && dateLowerBound.compareTo(dateUpperBound) == -1) {
			Week week = addWeeks(dateLowerBound.getWeekId());
			Day day = this.getDay(dateLowerBound.getWeekId(),
					dateLowerBound.getDayId());
			if (!daysForbidden.contains(day.getDayId())
					&& (!blockTypes.contains(BlockType.Consultation) || (blockTypes
							.contains(BlockType.Consultation) && week
							.getQuotas() > 0))) {
				if (!dateLowerBound.sameWeekAndDayAs(dateUpperBound)) {
					for (Block block : day.getBlocks()) {
						if (blockTypes.contains(block.getType())
								&& block.getEnd() >= dateLowerBound.getMinute()) {
							for (Activity activity : block.getActivities()) {
								if (activity.getType() == ActivityType.Free
										&& activity.getEnd()
												- Math.max(dateLowerBound
														.getMinute(), activity
														.getStart()) >= duration) {
									ArrayList<ArrayList<Integer>> remaining = activity.exclude(activitiesToAvoid);
									for (ArrayList<Integer> interval : remaining) {
										int end = Math.min(interval.get(1), dateUpperBound.getMinute());
										int start = Math.max(interval.get(0), dateLowerBound.getMinute());
										if(( end - start )>=duration){
											avail = new Availability(activity, start, end);
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
								&& block.getEnd() >= dateLowerBound.getMinute()
								&& block.getStart() <= dateUpperBound
										.getMinute()) {
							for (Activity activity : block.getActivities()) {
								if (activity.getType() == ActivityType.Free
										&& Math.min(dateUpperBound.getMinute(),
												activity.getEnd())
												- Math.max(dateLowerBound
														.getMinute(), activity
														.getStart()) >= duration) {
									ArrayList<ArrayList<Integer>> remaining = activity.exclude(activitiesToAvoid);
									for (ArrayList<Integer> interval : remaining) {
										int end = Math.min(interval.get(1), dateUpperBound.getMinute());
										int start = Math.max(interval.get(0), dateLowerBound.getMinute());
										if(( end - start )>=duration){
											avail = new Availability(activity, start, end);
											return avail;
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
