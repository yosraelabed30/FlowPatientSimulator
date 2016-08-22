package events;

import tools.Time;
import medical.Patient;

public class PostConsultation extends ActivityEvent{
	// TODO check to which technologist the patient file is assigned
	private Patient patient;
	
	public PostConsultation(Patient patient) {
		super();
		this.patient = patient;
	}

	@Override
	public void childActions() {
		//the nurse does the education during this time
//		System.out.println("PostConsultation ; patient id : "+patient.getId()+", doctor id : "+patient.getDoctor().getId()+", at min : "+Time.minIntoTheDay(Time.time()));
		getPatient().getDoctor().getSchedule().doNextTask();
		new Planification(this.getPatient()).schedule(delay);
	}

	@Override
	public ActivityEvent clone() {
		PostConsultation clone = new PostConsultation(this.getPatient());
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		this.delay=30;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

}
