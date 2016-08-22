package events;

import simulation.FlowOfPatients;
import tools.Time;
import medical.Patient;
import medical.Priority;
import medical.TreatmentMachine;

public class Treatment extends ActivityEvent{
	private Patient patient;
	private boolean last;
	
	public Treatment(Patient patient) {
		super();
		this.setPatient(patient);
		this.setPriority(2);
		this.last = false;
	}

	@Override
	public void childActions() {
		TreatmentMachine machine = (TreatmentMachine) this.getiSchedule();
		String msg = "";
		if(patient.isPresentInCenter()){
			patient.getSteps().add(this.getActivity());
			patient.setPresentInCenter(false);
			patient.getSchedule().doNextTask();
			this.getSchedule().doNextTask();
			if(last){
				patient.toPatientsOut();
				msg+="Last ";
				
				//TODO remove that, only for show
				if (FlowOfPatients.test1 == null
						&& (patient.getPriority() == Priority.P1 || patient.getPriority() == Priority.P2)) {
					FlowOfPatients.test1 = patient;
				}
				if (FlowOfPatients.test2 == null
						&& (patient.getPriority() == Priority.P3 || patient.getPriority() == Priority.P4)) {
					FlowOfPatients.test2=patient;
				}
				
			}
			msg += "Treatment ; patient id: "+patient.getId()+", prio : "+patient.getPriority()+", with treatmentmachine : "+machine.getId()+", at min : "+Time.minIntoTheDay(Time.now());
		}
		else{
			msg += "Treatment ; NOT HERE patient id: "+patient.getId()+", prio : "+patient.getPriority()+", with treatmentmachine : "+machine.getId()+", at min : "+Time.minIntoTheDay(Time.now());
		}
//		System.out.println(msg);
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

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

}
