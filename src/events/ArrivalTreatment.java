package events;

import medical.Patient;
import medical.TreatmentMachine;
import tools.Time;


public class ArrivalTreatment extends ActivityEvent {
	private TreatmentMachine treatmentMachine ;
	public ArrivalTreatment(TreatmentMachine treatmentMachine) {
		super();
		this.setPriority(0);
		this.treatmentMachine = treatmentMachine;
	}
	
	@Override
	public void childActions() {
		int time = Time.now();
		int min = Time.minIntoTheDay(time);
		Patient patient = (Patient) this.getSchedule().getiSchedule();
//		System.out.println("Arrival Treatment ; Patient id : "+patient.getId()+ " with priority "+patient.getPriority()+" arrived, at min : "+min);
		patient.setPresentInCenter(true);
	}

	@Override
	public ActivityEvent clone() {
		ArrivalTreatment clone = new ArrivalTreatment(this.getTreatmentMachine());
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		this.delay=0;
	}

	public TreatmentMachine getTreatmentMachine() {
		return treatmentMachine;
	}

	public void setTreatmentMachine(TreatmentMachine treatmentMachine) {
		this.treatmentMachine = treatmentMachine;
	}

}
