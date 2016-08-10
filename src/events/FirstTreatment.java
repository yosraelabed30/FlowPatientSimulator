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
	public void childActions() {
		TreatmentMachine machine = (TreatmentMachine) this.getiSchedule();
		if(patient.isPresent()){
			patient.setPresent(false);
			patient.getSchedule().doNextTask();
			this.getSchedule().doNextTask();
			System.out.println("FirstTreatment ; patient id: "+patient.getId()+", prio : "+patient.getPriority()+", with treatmentmachine : "+machine.getId()+", at min : "+Time.minIntoTheDay(Time.time()));
		}
		else{
			System.out.println("FirstTreatment ; NOT HERE patient id: "+patient.getId()+", prio : "+patient.getPriority()+", with treatmentmachine : "+machine.getId()+", at min : "+Time.minIntoTheDay(Time.time()));
		}
	}

	@Override
	public ActivityEvent clone() {
		FirstTreatment clone = new FirstTreatment(patient);
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		this.setDelay(20);
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

}
