package events;

import medical.Patient;

public class Treatment extends ActivityEvent{
	private Patient patient;
	
	public Treatment(Patient patient) {
		super();
	}

	@Override
	public void childActions() {
		
		
	}

	@Override
	public ActivityEvent clone() {
		return new Treatment(patient);
	}

	@Override
	public void generateDelay() {
		this.setDelay(20);
	}

}
