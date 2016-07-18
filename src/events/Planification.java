package events;

import medical.Patient;
import tools.Time;

public class Planification extends ActivityEvent{
	//private ArrayList<Technologist> techno; we don't affect a patient to a technologist.
	private Patient patient;
	
	public Planification(Patient patient) {
		super();
		this.setPatient(patient);
	}

	@Override
	public void childActions() {
		/*
		 * The postConsultation is now done
		 */
		getPatient().getSchedule().doNextTask();
		// We assign a file to a technologist
		int time = Time.time();
		int min = Time.minIntoTheDay(time);
		System.out.println("PostConsultation, Consultation and PreConsultation done for patient id : "+this.getPatient().getId()+", at minute : "+min);
		patient.getCenter().getTechnologist().getFilesForCTSimTreatment().add(this.getPatient());
	}

	@Override
	public ActivityEvent clone() {
		return new Planification(this.getPatient());
	}

	@Override
	public void generateDelay() {
		this.delay=0;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

}
