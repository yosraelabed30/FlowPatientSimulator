package events;

import tools.Time;
import medical.Patient;
import medical.Technologist;

public class CTSim extends ActivityEvent{
	private Patient patient;
	private boolean last;

	public CTSim(Patient patient) {
		super();
		this.patient = patient;
		this.setPriority(2); // set to 2 only to see in the console that this event is after the arrivalCTSim
		this.last = false;
	}
	
	@Override
	public void endActions() {
//		System.out.println("CTSim : patient : " + patient.getId()
//				+ ", priority : " + patient.getPriority() + ", at minute : "
//				+ Time.minIntoTheDay(Time.now()));
		patient.getSteps().add(this.getActivity());
		if (last) {
			Technologist.getFilesForPreContouring().add(patient);
			// System.out.println("file has been transfered to the technologists");
		}
		patient.setPresentInCenter(false);
		patient.getSchedule().doNextTask();
		this.getSchedule().doNextTask();
	}

	@Override
	public ActivityEvent clone() {
		CTSim clone = new CTSim(patient);
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		this.setDelay(30);
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	@Override
	public void startActions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean conditions() {
		return patient.isPresentInCenter();
	}

}
