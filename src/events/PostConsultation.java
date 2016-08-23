package events;

import tools.Time;
import medical.Patient;
import medical.Priority;

public class PostConsultation extends ActivityEvent{
	// TODO check to which technologist the patient file is assigned
	private Patient patient;
	
	public PostConsultation(Patient patient) {
		super();
		this.patient = patient;
	}

	@Override
	public void endActions() {
		//the nurse does the education during this time
//		System.out.println("PostConsultation ; patient id : "+patient.getId()+", doctor id : "+patient.getDoctor().getId()+", at min : "+Time.minIntoTheDay(Time.time()));
//		getPatient().getDoctor().getSchedule().doNextTask();
//		new Planification(this.getPatient()).schedule(delay);
		if (patient.getPriority()==Priority.P1 || patient.getPriority()==Priority.P2){
			patient.getSphere().getCenter().getTechnologist().processFileForPlanification(patient);
		}
		else if (patient.getPriority()==Priority.P3 || patient.getPriority()==Priority.P4){
			patient.getSphere().getCenter().getTechnologist().getFilesForCTSimTreatment().add(this.getPatient());
		}
		patient.setPresentInCenter(false);
		getPatient().getSchedule().doNextTask();
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

	@Override
	public void startActions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean conditions() {
		return true;
	}

}
