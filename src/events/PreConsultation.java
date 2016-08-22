package events;

import medical.AdminAgent;
import medical.Patient;
import scheduling.ActivityStatus;
import tools.Time;

public class PreConsultation extends ActivityEvent{
	private Patient patient;
	private AdminAgent adminAgent; //is absolutely useless in this version
	
	public PreConsultation(Patient patient, AdminAgent adminAgent) {
		super();
		this.patient=patient;
		this.setAdminAgent(adminAgent);
	}

	@Override
	public void childActions() {
		/*
		 * Display some data in console to check
		 */
		int time = Time.now();
		int min = Time.minIntoTheDay(time);
//		System.out.println("PreConsultation : checking if patient id : "+getPatient().getId()+" with priority "+getPatient().getPriority()+" is present : "+getPatient().isPresentInCenter()+", at min : "+min+", with doctor id : "+getPatient().getDoctor().getId());
		if(getPatient().isPresentInCenter()){
			//schedule the pre-consultation event
			getPatient().getSteps().add(this.getActivity());
			new Consultation(this.patient).schedule(0);
		}
		else{
			getActivity().setStatus(ActivityStatus.ToPostpone);
			getPatient().getDoctor().getSchedule().doNextTask();
		}
	}

	public AdminAgent getAdminAgent() {
		return adminAgent;
	}

	public void setAdminAgent(AdminAgent adminAgent) {
		this.adminAgent = adminAgent;
	}

	@Override
	public ActivityEvent clone() {
		PreConsultation clone = new PreConsultation(getPatient(), adminAgent);
		clone.setActivity(this.getActivity());
		return clone;
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
