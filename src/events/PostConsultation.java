package events;

import medical.Patient;

public class PostConsultation extends ActivityEvent{
	//private Nurse nurse; // TODO check to which technologist the patient file is assigned
	private Patient patient;
	
	public PostConsultation(Patient patient) {
		super();
		this.patient = patient;
	}

	@Override
	public void childActions() {
		// TODO Auto-generated method stub
		//the nurse does the education during this time
		getPatient().getDoctor().getSchedule().doNextTask();
		new Planification(this.getPatient()).schedule(delay);
	}

	@Override
	public ActivityEvent clone() {
		return new PostConsultation(this.getPatient());
	}

	@Override
	public void generateDelay() {
		// TODO Do sthg representing reality
		this.delay=30;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

}
