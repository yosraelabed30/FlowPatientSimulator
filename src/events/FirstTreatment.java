package events;

import scheduling.Activity;
import scheduling.ActivityType;
import simulation.Statistics;
import tools.Time;
import umontreal.iro.lecuyer.simevents.Sim;
import medical.Patient;
import medical.Priority;
import medical.TreatmentMachine;

public class FirstTreatment extends ActivityEvent{
	private Patient patient;
	
	public FirstTreatment(Patient patient) {
		super();
		this.setPatient(patient);
		this.setPriority(2);
	}

	@Override
	public void endActions() {
		TreatmentMachine machine = (TreatmentMachine) this.getiSchedule();
		patient.getSteps().add(this.getActivity());
		patient.setPresentInCenter(false);
		patient.getSchedule().doNextTask();
		this.getSchedule().doNextTask();
//		System.out.println("FirstTreatment ; patient id: " + patient.getId()
//				+ ", prio : " + patient.getPriority()
//				+ ", with treatmentmachine : " + machine.getId()
//				+ ", at min : " + Time.minIntoTheDay(Time.now()));
	}

	@Override
	public ActivityEvent clone() {
		FirstTreatment clone = new FirstTreatment(patient);
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		this.setDelay(45);
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public void startActions() {
		double today = Time.today();
		int dateInMinConsultation = 0;
		for(Activity activity : this.getPatient().getSteps()){
			if(activity.getType()==ActivityType.Consultation){
				dateInMinConsultation = activity.getDate().toMinutes();
			}
		}
		double delayConsultFTreat = Math.round(Time.toDoubleNbOfDays(Time.now()-dateInMinConsultation));
		double delayRefFTreat = Math.round(Time.toDoubleNbOfDays(Time.now()-this.getPatient().getReferredDate().toMinutes()));
		Statistics.getDelayConsultFTreat().add(delayConsultFTreat);
		Statistics.getDelayRefFTreat().add(delayRefFTreat);
		Statistics.getTimesDelayConsultFTreat().add(today);
		Statistics.getTimesDelayRefFTreat().add(today);
		switch (getPatient().getPriority()) {
		case P1:
			Statistics.getDelayP1ConsultFTreat().add(delayConsultFTreat);
			Statistics.getDelayP1RefFTreat().add(delayRefFTreat);
			Statistics.getTimesDelayP1ConsultFTreat().add(today);
			Statistics.getTimesDelayP1RefFTreat().add(today);
			break;
		case P2:
			Statistics.getDelayP2ConsultFTreat().add(delayConsultFTreat);
			Statistics.getDelayP2RefFTreat().add(delayRefFTreat);
			Statistics.getTimesDelayP2ConsultFTreat().add(today);
			Statistics.getTimesDelayP2RefFTreat().add(today);
			break;
		case P3:
			Statistics.getDelayP3ConsultFTreat().add(delayConsultFTreat);
			Statistics.getDelayP3RefFTreat().add(delayRefFTreat);
			Statistics.getTimesDelayP3ConsultFTreat().add(today);
			Statistics.getTimesDelayP3RefFTreat().add(today);
			break;
		case P4:
			Statistics.getDelayP4ConsultFTreat().add(delayConsultFTreat);
			Statistics.getDelayP4RefFTreat().add(delayRefFTreat);
			Statistics.getTimesDelayP4ConsultFTreat().add(today);
			Statistics.getTimesDelayP4RefFTreat().add(today);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean conditions() {
		return patient.isPresentInCenter();
	}

}
