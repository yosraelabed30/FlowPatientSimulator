package tools;

import umontreal.iro.lecuyer.simevents.Sim;

public abstract class Time {
	/**
	 * Note to create something (block, activity), with a duration of 0 you simply have to set the end to start-1
	 * However that SHOULD NOT be useful, since you never schedule an activity with a time equal to 0
	 */
	public static final int DAY_END = 24*60-1;
	public static final int DAY_START = 0;
	
	/**
	 * 
	 * @return the time now in the simulation as an int
	 */
	public static int now(){
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
	
	/**
	 * Ex if start == 0 and end == 59 then duration returns 60 because here [0,60[ is coded as [0,59] and the code follows that convention
	 * @param start must be inferior to end
	 * @param end must be superior to start, obviously
	 * @return end+1-start
	 */
	public static int duration(int start, int end){
		return end+1-start;
	}

	public static int end(int start, int duration) {
		return start+duration-1;
	}
}
