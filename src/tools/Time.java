package tools;

import umontreal.iro.lecuyer.simevents.Sim;

public abstract class Time {
	
	public static int time(){
		return (int)Sim.time();
	}
	/**
	 * The first week of a schedule is the week 0, the second is week 1, the third is week 2, etc.
	 * @param time
	 * @return the week corresponding to the input time
	 */
	public static int weekCorrespondingToTime(int time) {
		return time/(24*60*7);
	}
	
	public static int dayCorrespondingToTime(int time){
		return time/(24*60);
	}
	
	public static int weekDayCorrespondingToTime(int time){
		return dayCorrespondingToTime(time)%7;
	}
	
	public static int fromIdToWeekDay(int dayNb){
		return dayNb%7;
	}
	
	public static int toMinutes(int dayId, int nbOfMinInTheDay){
		return dayId*24*60+nbOfMinInTheDay;
	}
	
	public static int minIntoTheDay(int time){
		return time%(24*60);	
	}
	
	public static int[] increaseDate(int weekId, int dayId) {
		int newWeekId = weekId;
		int newDayId = dayId+1;
		if(newDayId==7){
			newDayId=0;
			newWeekId++;
		}
		return new int[]{newWeekId, newDayId};
	}
}
