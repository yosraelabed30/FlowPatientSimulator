package scheduling;

public enum ActivityType {
	NotWorking(),
	Free(),
	Consultation(60),
	Contouring(20),
	TreatmentPlan(),
	FirstTreatment(),
	FollowUp(), 
	CTSim(),
	Dosimetry(),
	Treatment();
	
	private int scheduleDuration;
	
	private ActivityType(){
		this(-1);
	}
	
	private ActivityType(int scheduleDuration) {
		this.setScheduleDuration(scheduleDuration);
	}

	public int getScheduleDuration() {
		return scheduleDuration;
	}

	public void setScheduleDuration(int scheduleDuration) {
		this.scheduleDuration = scheduleDuration;
	}
	
}
