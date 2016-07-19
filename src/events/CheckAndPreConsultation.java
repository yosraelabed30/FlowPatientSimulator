package events;

import medical.AdminAgent;
import medical.Patient;
import scheduling.ActivityStatus;
import tools.Time;

public class CheckAndPreConsultation extends ActivityEvent{
	private Patient patient;
	private boolean alreadyChecked;
	private AdminAgent adminAgent; //is absolutely useless in this version
	
	public CheckAndPreConsultation(Patient patient, boolean alreadyChecked, AdminAgent adminAgent) {
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
		/*
		 * check if the patient is here
		 * if not check again in 15 min
		 */
		if(!isAlreadyChecked()){
			//check patient presence
			if(getPatient().isPresent()){
				//schedule the pre-consultation event
				new Consultation(this.patient).schedule(0);
				getPatient().getSteps().add(getActivity());
			}
			else{
				//schedule another CheckPatientPresence
				new CheckAndPreConsultation(getPatient(), true, getAdminAgent()).schedule(15); //TODO check the real value used in the CHUM
			}
		}
		else{
			// if the patient is referred in another center we do not call the patient again
			boolean inAnotherCenter = false ; //TODO check how often (statistically) this happens
			// else we consider that the patient demand is processed again by the admin agent
			if(!inAnotherCenter){
				//find the activity and set it as Activity.toPostPone
				getActivity().setStatus(ActivityStatus.ToPostpone);
				getAdminAgent().addToDemands(getPatient());
				getPatient().getDoctor().getSchedule().doNextTask();
			} 
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
		return new CheckAndPreConsultation(getPatient(), alreadyChecked, adminAgent);
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
