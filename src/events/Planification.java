package events;

import medical.Patient;
import medical.Priority;
import tools.Time;
/**
 * Event representing the end of the post-consultation, during which the patient's file is sent to the technologist
 * TODO rename it DepartureConsultation
 * @author Joffrey
 *
 */
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
		// We assign a file to a technologist
		int time = Time.time();
		int min = Time.minIntoTheDay(time);
		System.out.println("Planification ; PostConsultation, Consultation and PreConsultation done for patient id : "+this.getPatient().getId()+" with priority "+patient.getPriority()+", at minute : "+min+", with doctor id : "+patient.getDoctor().getId());
		if (patient.getPriority()==Priority.P1 || patient.getPriority()==Priority.P2){
			patient.getCenter().getTechnologist().processFileForPlanification(patient);
		}
		else if (patient.getPriority()==Priority.P3 || patient.getPriority()==Priority.P4){
			patient.getCenter().getTechnologist().getFilesForCTSimTreatment().add(this.getPatient());
		}
		patient.setPresent(false);
		getPatient().getSchedule().doNextTask();
	}

	@Override
	public ActivityEvent clone() {
		Planification clone = new Planification(this.getPatient());
		clone.setActivity(this.getActivity());
		return clone;
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
