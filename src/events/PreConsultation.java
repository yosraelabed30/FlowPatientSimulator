package events;

import medical.AdminAgent;
import medical.Patient;
import scheduling.ActivityStatus;
import tools.Time;

public class PreConsultation extends ActivityEvent{
	private Patient patient;
	private boolean alreadyChecked;
	private AdminAgent adminAgent; //is absolutely useless in this version
	
	public PreConsultation(Patient patient, boolean alreadyChecked, AdminAgent adminAgent) {
		super();
		this.patient=patient;
		this.setAlreadyChecked(alreadyChecked);
		this.setAdminAgent(adminAgent);
	}

	@Override
	public void childActions() {
		/*
		 * Display some data in console to check
		 */
		int time = Time.time();
		int min = Time.minIntoTheDay(time);
		System.out.println("checking if patient id : "+getPatient().getId()+" with priority "+getPatient().getPriority()+" is present : "+getPatient().isPresent()+", at min : "+min);
		if(getPatient().isPresent()){
			//schedule the pre-consultation event
			new Consultation(this.patient).schedule(0);
			getPatient().getSteps().add(getActivity());
		}
		else{
			getActivity().setStatus(ActivityStatus.ToPostpone);
			getPatient().getDoctor().getSchedule().doNextTask();
		}
	}

	public boolean isAlreadyChecked() {
		return alreadyChecked;
	}

	public void setAlreadyChecked(boolean alreadyChecked) {
		this.alreadyChecked = alreadyChecked;
	}

	public AdminAgent getAdminAgent() {
		return adminAgent;
	}

	public void setAdminAgent(AdminAgent adminAgent) {
		this.adminAgent = adminAgent;
	}

	@Override
	public ActivityEvent clone() {
		return new PreConsultation(getPatient(), alreadyChecked, adminAgent);
	}

	@Override
	public void generateDelay() {
		this.delay=0;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
}
