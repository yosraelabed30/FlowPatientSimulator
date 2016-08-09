package events;

import tools.Time;
import medical.Patient;
import medical.TreatmentMachine;

public class Treatment extends ActivityEvent{
	private Patient patient;
	
	public Treatment(Patient patient) {
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
			System.out.println("Treatment ; patient id: "+patient.getId()+", prio : "+patient.getPriority()+", with treatmentmachine : "+machine.getId()+", at min : "+Time.minIntoTheDay(Time.time()));
		}
		else{
			System.out.println("Treatment ; NOT HERE patient id: "+patient.getId()+", prio : "+patient.getPriority()+", with treatmentmachine : "+machine.getId()+", at min : "+Time.minIntoTheDay(Time.time()));
		}
	}

	@Override
	public ActivityEvent clone() {
		Treatment clone = new Treatment(getPatient());
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
