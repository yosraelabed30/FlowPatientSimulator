package scheduling;

import java.util.ArrayList;

import medical.Patient;
import medical.Priority;
import tools.Time;

public class Date implements Comparable<Date>{
	private int weekId;
	private int dayId;
	private int minute;
	
	public Date(int week, int day, int minute){
		this.setWeekId(week);
		this.setDayId(day);
		this.setMinute(minute);
		
	}
	
	@Override
	public String toString() {
		return "Date [weekId=" + weekId + ", dayId=" + dayId + ", minute=" + minute + "]";
	}

	public int toMinutes(){
		return (this.getWeekId()*7*24*60+this.getDayId()*24*60+this.getMinute()) ;	
	}
	
	public static Date toDates(int nbMinutes){
		return new Date(nbMinutes/(24*60*7), (nbMinutes/(24*60))%7, nbMinutes%(24*60));
	}
	
	
	public static Date dateNow(){
		return toDates(Time.time());
	}

	public int getWeekId() {
		return weekId;
	}

	public void setWeekId(int weekId) {
		this.weekId = weekId;
	}

	public int getDayId() {
		return dayId;
	}

	public void setDayId(int dayId) {
		this.dayId = dayId;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	@Override
	public int compareTo(Date d) {
		int toto = this.toMinutes();
		int lala = d.toMinutes();
		int res = 0;
		if (toto < lala) {
			res = -1;
		} else if (toto > lala) {
			res = 1;
		}

		return res;
	}

	public Date increase() {

		
		int newWeekId = this.getWeekId();
		int newDayId = this.getDayId();
		int newMinute = this.getMinute();
		if(this.getDayId()==6){
			newWeekId++;
			newDayId=0;
		}
		else{
			newDayId++;
		}
		return new Date(newWeekId, newDayId, newMinute);
	}

	public Date decrease() {

		
		int newWeekId = this.getWeekId();
		int newDayId = this.getDayId();
		int newMinute = this.getMinute();
		if(this.getDayId()==0){
			newWeekId--;
			newDayId=6;
		}
		else{
			newDayId--;
		}
		return new Date(newWeekId, newDayId, newMinute);
	}
	 
	public Date increaseWeekend() {
		int newDayId = this.getDayId();
		int newWeekId = this.getWeekId();
		int newMinute = this.getMinute();
		Date newDate = null;
		if (this.getDayId()==4){
			newWeekId++;
			newDayId=0;
			newDate = new Date(newWeekId, newDayId, newMinute);
		}
		else {
			newDate = this.increase();
		}
		return newDate;
	}

	public Date decreaseDays(Patient patient) {
		int newWeekId = this.getWeekId();
		int newDayId = this.getDayId();
		int newMinute = this.getMinute();
		int i = patient.getDelaysFirstTreatmentCTSim();
		boolean businessDay = isBusinessDays(newDayId);

		if (patient.getPriority() == Priority.P2 || patient.getPriority() == Priority.P3
				|| patient.getPriority() == Priority.P4) {
			while (i != 0) {
				if (businessDay == true) {
					i--;

				}
				if (newDayId != 0) {
					newDayId--;

				} else {
					newDayId = 6;
					newWeekId--;
				}

			}

		} else {
			newMinute = newMinute - 3 * 60;
			//verifier comment faire ça avec le chum
		}

		return new Date(newWeekId, newDayId, newMinute);
	}
	
	public static boolean isBusinessDays (int day){
		
	
		boolean businessDay= false;
		if (day<=4 && day>=0){
			businessDay= true;
		}

		return businessDay;
	}

	


}
