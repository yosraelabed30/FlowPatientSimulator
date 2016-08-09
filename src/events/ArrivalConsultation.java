package events;

import java.util.ArrayList;

import medical.ChefSphere;
import medical.Patient;
import tools.Time;

public class ArrivalConsultation extends ActivityEvent{
	
	public ArrivalConsultation() {
		super();
		this.priority=0;
	}

	@Override
	public void childActions() {
		int time = Time.time();
		int min = Time.minIntoTheDay(time);
		Patient patient = (Patient) this.getSchedule().getiSchedule();
		System.out.println("Arrival Consultation ; Patient id : "+patient.getId()+ " with priority "+patient.getPriority()+" arrived, at min : "+min+", with doctor id : "+patient.getDoctor().getId());
		if(patient.getSteps().size()!=0){
			System.out.println("Arrival for more than consultation !");
		}
		
		if (this.getLateness()>0 && this.getLateness()!= Integer.MAX_VALUE){
			System.out.println("late arrival");
			patient.getSphere().getChefSphere().delayConsultation(patient);
			
		}

		patient.setPresent(true);
	}

	@Override
	public ActivityEvent clone() {
		ArrivalConsultation clone = new ArrivalConsultation();
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		this.delay=0;
	}

}
