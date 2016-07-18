package events;

import medical.Patient;
import tools.Time;

public class Arrival extends ActivityEvent{
	
	public Arrival() {
		super();
	}

	@Override
	public void childActions() {
		// TODO Auto-generated method stub
		/*
		 * Display some data in console to check
		 */
		int time = Time.time();
		int min = Time.minIntoTheDay(time);
		Patient patient = (Patient) this.getSchedule().getResource();
		System.out.println("Patient id : "+patient.getId()+" arrived, at min : "+min);
		if(patient.getSteps().size()!=0){
			System.out.println("Arrival for more than consultation !");
		}
		patient.setPresent(true);
	}

	@Override
	public ActivityEvent clone() {
		return new Arrival();
	}

	@Override
	public void generateDelay() {
		this.delay=0;
	}

}
