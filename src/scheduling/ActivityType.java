package scheduling;

public enum ActivityType {
	Free(),
	Consultation(60),
	Contouring(20),
	TreatmentPlan(),
	FirstTreatment(45),
	FollowUp(), 
	CTSim(30),
	Dosimetry(),
	Treatment(45);
	
	private int defaultScheduleDuration;
	
	private ActivityType(){
		this(-1);
	}
	
	private ActivityType(int scheduleDuration) {
		this.setDefaultScheduleDuration(scheduleDuration);
	}

	public int getDefaultScheduleDuration() {
		return defaultScheduleDuration;
	}

	public void setDefaultScheduleDuration(int scheduleDuration) {
		this.defaultScheduleDuration = scheduleDuration;
	}
	
}
