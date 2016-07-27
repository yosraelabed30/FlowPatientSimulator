package events;

import java.util.ArrayList;

import medical.ChefSphere;
import medical.Patient;
import tools.Time;

public class ArrivalConsultation extends ActivityEvent{
	
	public ArrivalConsultation() {
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
		Patient patient = (Patient) this.getSchedule().getiSchedule();
		System.out.println("Patient id : "+patient.getId()+ " with priority "+patient.getPriority()+" arrived, at min : "+min);
		if(patient.getSteps().size()!=0){
			System.out.println("Arrival for more than consultation !");
		}
		
		if (this.getLateness()>0 && this.getLateness()!= Integer.MAX_VALUE){
			patient.getSphere().getChefSphere().delayConsultation(patient);
			
		}

		patient.setPresent(true);
	}

	@Override
	public ActivityEvent clone() {
		return new ArrivalConsultation();
	}

	@Override
	public void generateDelay() {
		this.delay=0;
	}

}
