package events;

import medical.Patient;
import tools.Time;


public class ArrivalCTSim extends ActivityEvent {
	public ArrivalCTSim() {
		super();
		this.setPriority(0);
	}
	
	@Override
	public void endActions() {
		int time = Time.now();
		int min = Time.minIntoTheDay(time);
		Patient patient = (Patient) this.getSchedule().getiSchedule();
//		System.out.println("Arrival CTSim ; Patient id : "+patient.getId()+ " with priority "+patient.getPriority()+" arrived, at min : "+min);
		patient.setPresentInCenter(true);
	}

	@Override
	public ActivityEvent clone() {
		ArrivalCTSim clone = new ArrivalCTSim();
		clone.setActivity(this.getActivity());
		return clone;
	}

	@Override
	public void generateDelay() {
		this.delay=0;
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
