package events;

import tools.Time;
import medical.Patient;

public class CTSim extends ActivityEvent{
	private Patient patient;
	private boolean sameDayAsConsultation;

	public CTSim(Patient patient, boolean sameDayAsConsultation) {
		super();
		this.patient = patient;
		this.setSameDayAsConsultation(sameDayAsConsultation);
	}

	public CTSim(Patient patient) {
		super();
		this.patient = patient;
		this.setSameDayAsConsultation(false);
	}
	
	@Override
	public void childActions() {
		System.out.println("CTSim for patient : "+patient.getId()+", at minute : "+Time.minIntoTheDay(Time.time()));
//		if(!patient.isCurative() && sameDayAsConsultation){
//			//then the patient's folder is considered as processed
//		}
//		else{
//			patient.getDoctor().getFolders().add(patient);
//		}
		if(patient.isMoldNeeded()){
			//TODO something? maybe the duration of the CTSim vary
		}
		else{
			
		}
		
		/*
		 * TODO the patient's file is added to the stack of a technologist to do the pre-contouring
		 */
		patient.getCenter().getTechnologist().getFilesForPreContouring().add(patient);
	}

	@Override
	public ActivityEvent clone() {
		return new CTSim(patient, sameDayAsConsultation);
	}

	@Override
	public void generateDelay() {
		this.setDelay(20);
	}

	public boolean isSameDayAsConsultation() {
		return sameDayAsConsultation;
	}

	public void setSameDayAsConsultation(boolean sameDayAsConsultation) {
		this.sameDayAsConsultation = sameDayAsConsultation;
	}

}
