package events;

import medical.Patient;
import medical.TreatmentMachine;

public class FirstTreatment extends ActivityEvent{
	private Patient patient;
	
	public FirstTreatment(Patient patient) {
		// TODO Auto-generated constructor stub
		super();
		this.setPatient(patient);
	}

	@Override
	public void childActions() {
		// TODO Auto-generated method stub
		if(patient.isPresent()){
			System.out.println("FirstTreatment");
		}
	}

	@Override
	public ActivityEvent clone() {
		return new FirstTreatment(patient);
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
