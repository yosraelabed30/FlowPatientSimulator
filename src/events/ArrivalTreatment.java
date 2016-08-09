package events;

import medical.Patient;
import tools.Time;


public class ArrivalTreatment extends ActivityEvent {
	public ArrivalTreatment() {
		super();
		this.setPriority(0);
	}
	
	@Override
	public void childActions() {
		int time = Time.time();
		int min = Time.minIntoTheDay(time);
		Patient patient = (Patient) this.getSchedule().getiSchedule();
		System.out.println("Arrival Treatment ; Patient id : "+patient.getId()+ " with priority "+patient.getPriority()+" arrived, at min : "+min);
		patient.setPresent(true);
	}

	@Override
	public ActivityEvent clone() {
		ArrivalTreatment clone = new ArrivalTreatment();
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		this.delay=0;
	}

}
