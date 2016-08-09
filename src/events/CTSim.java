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
		this.setPriority(2); // set to 2 only to see in the console that this event is after the arrivalCTSim
	}
	
	@Override
	public void childActions() {
		if(patient.isPresent()){
			System.out.println("CTSim : patient : "+patient.getId()+", priority : "+patient.getPriority()+", at minute : "+Time.minIntoTheDay(Time.time()));
			patient.setPresent(false);
			patient.getSchedule().doNextTask();
			this.getSchedule().doNextTask();
			
			/*
			 * TODO the patient's file is added to the stack of a technologist to do the pre-contouring, but only if it is the last scan of the day actually
			 */
			//patient.getCenter().getTechnologist().getFilesForPreContouring().add(patient);
		}
	}

	@Override
	public ActivityEvent clone() {
		CTSim clone = new CTSim(patient, sameDayAsConsultation);
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		if(patient.isMoldNeeded()){
			//TODO something? maybe the duration of the CTSim vary
		}
		this.setDelay(20);
	}

	public boolean isSameDayAsConsultation() {
		return sameDayAsConsultation;
	}

	public void setSameDayAsConsultation(boolean sameDayAsConsultation) {
		this.sameDayAsConsultation = sameDayAsConsultation;
	}

}
