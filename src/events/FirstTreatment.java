package events;

import tools.Time;
import medical.Patient;
import medical.TreatmentMachine;

public class FirstTreatment extends ActivityEvent{
	private Patient patient;
	
	public FirstTreatment(Patient patient) {
		super();
		this.setPatient(patient);
		this.setPriority(2);
	}

	@Override
	public void endActions() {
		TreatmentMachine machine = (TreatmentMachine) this.getiSchedule();
		patient.getSteps().add(this.getActivity());
		patient.setPresentInCenter(false);
		patient.getSchedule().doNextTask();
		this.getSchedule().doNextTask();
//		System.out.println("FirstTreatment ; patient id: " + patient.getId()
//				+ ", prio : " + patient.getPriority()
//				+ ", with treatmentmachine : " + machine.getId()
//				+ ", at min : " + Time.minIntoTheDay(Time.now()));
	}

	@Override
	public ActivityEvent clone() {
		FirstTreatment clone = new FirstTreatment(patient);
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		this.setDelay(45);
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public void startActions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean conditions() {
		return patient.isPresentInCenter();
	}

}
