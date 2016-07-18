package events;

import medical.Doctor;

public class ContouringBlockEnd extends ActivityEvent{
	private Doctor doctor;
	
	public ContouringBlockEnd() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void childActions() {
		doctor.getCenter().getDosimetrist().getFilesForDosi().addAll(doctor.getFilesForDosi());
	}

	@Override
	public ActivityEvent clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void generateDelay() {
		// TODO Auto-generated method stub
		
	}
	
}
