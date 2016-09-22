package medical;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import scheduling.Activity;
import scheduling.ActivityStatus;
import scheduling.ActivityType;
import scheduling.Availability;
import scheduling.Block;
import scheduling.BlockType;
import scheduling.Date;
import scheduling.Day;
import tools.Time;
import events.ActivityEvent;
import events.ArrivalConsultation;
import events.PreConsultation;
import events.Consultation;

public class ChefSphere {
	LinkedList<Patient> demands;
	private Sphere sphere;
	
	public ChefSphere( Sphere sphere) {
		this.sphere=sphere;
		this.demands = new LinkedList<>();
	}

	public void processDemands(Patient patient){
		int duration = ActivityType.Consultation.getDefaultScheduleDuration();
		/**
		 * TODO fix the lateness issue : a patient is late for everything
		 */
		int lateness= -15;
		Date dateLowerBound= Date.now(); 
		Availability best= null; 
		ArrayList<Doctor> competentDoctors = new ArrayList<>();
		ArrayList<BlockType> blockTypes = new ArrayList<>();
		blockTypes.add(BlockType.Consultation);
		ArrayList<Integer> daysForbidden = new ArrayList<>();
		daysForbidden.add(5);
		daysForbidden.add(6);
		for (Doctor doctor : this.getSphere().getDoctors()) {
			Availability tmp = null;
			if (doctor.canTreat(patient)){
				competentDoctors.add(doctor);
				tmp=doctor.getSchedule().findFirstAvailability(ActivityType.Consultation, duration, blockTypes, daysForbidden, dateLowerBound);
				if (best == null || tmp.compareTo(best)==-1 ) {
					best = tmp;
				}
			}
					
		}
		if (best==null || !(best.getDate().compareTo(patient.getDeadLine())==-1)){
			blockTypes.remove(BlockType.Consultation);
			blockTypes.add(BlockType.OverTime);
			for (Doctor doctor : competentDoctors) {
				Availability tmp = null;
				if (doctor.isOverTime()== true){
					tmp=doctor.getSchedule().findFirstAvailability(ActivityType.Consultation, duration, blockTypes, daysForbidden, dateLowerBound);
					if (best == null || tmp.compareTo(best)==-1 ) {
						best = tmp;	
					}
				}
			}
			
		}
	
		if(best!=null){
			Doctor firstAvailable = (Doctor) best.getActivity().getiSchedule();
			patient.setDoctor(firstAvailable);

			PreConsultation check = new PreConsultation(patient, this.getSphere().getCenter().getAdminAgent());
			Activity consultationForDoctor = new Activity(best.getStart(), duration, ActivityType.Consultation, check);
			best.getActivity().insert(consultationForDoctor);
			
			//Quotas of the week for the doc are decreased
			consultationForDoctor.getWeek().decreaseQuotas();
			
			Activity consultationForPatient = consultationForDoctor.clone(); //at that moment, consultationForPatient's block is the one of consultationForDoctor
			ArrivalConsultation arrival =  new ArrivalConsultation();
			arrival.setLateness(lateness);
			arrival.setPriority(0); //patient arrives first then the presence is checked
			consultationForPatient.setActivityEvent(arrival);
			patient.getPlannedStepsPreTreatment().add(consultationForPatient);
			Availability free = patient.getSchedule().findAvailability(consultationForDoctor.getDate(), duration);
			if(free==null){
				System.out.println();
			}
			free.getActivity().insert(consultationForPatient); //and when inserted, consultationForPatient's block is the one of free
//			System.out.println("consultation : "+consultationForPatient.getDate()+" pour ce patient "+patient.getId()+" avec prio  "+patient.getPriority());
		}
		else{
			System.out.println("A consultation could not be found for a patient : "+patient.getId()+", "+patient.getPriority());
		}
	}
	
	public void addToDemands(Patient patient) {
		this.demands.add(patient);
	}

	/**
	 * A consultation can always be found for a patient
	 */
	public void processDemands(){
		Iterator<Patient> demandsIter = demands.iterator();
		while (demandsIter.hasNext()) {
			Patient patient = demandsIter.next();
			this.processDemands(patient);
			demandsIter.remove();
		}
	}
	
	public void delayConsultation(Patient patient){
		int duration=ActivityType.Consultation.getDefaultScheduleDuration();
		Availability best= null;
		Doctor doctor=patient.getDoctor();
		ArrayList<BlockType> blockTypes = new ArrayList<>();
		blockTypes.add(BlockType.Consultation);
		ArrayList<Integer> daysForbidden = new ArrayList<>();
		Date dateLowerBound = Date.now();
		Date dateUpperBound = dateLowerBound.clone();
		dateUpperBound.setMinute(24*60);
		best = doctor.getSchedule().findFirstAvailability(ActivityType.Consultation, duration, blockTypes, daysForbidden, dateLowerBound, dateUpperBound);
		if (best == null){
			if(doctor.isOverTime()== true){
			blockTypes.remove(BlockType.Consultation);
			blockTypes.add(BlockType.OverTime);
			best = doctor.getSchedule().findFirstAvailability(ActivityType.Consultation, duration, blockTypes, daysForbidden, dateLowerBound, dateUpperBound);
			}
			else {
				this.processDemands(patient);
				// on suppose que si le patient est arrivé, il va être traité comme urgent et pris en charge avant qu'il quitte
			}
		}
		if (best!=null){
			PreConsultation check = new PreConsultation(patient, this.getSphere().getCenter().getAdminAgent());
			Activity consultationForDoctor = new Activity(best.getStart(), duration, ActivityType.Consultation, check);
			best.getActivity().insert(consultationForDoctor);

			Activity consultationForPatient = consultationForDoctor.clone(); 
			Availability free = patient.getSchedule().findAvailability(best.getDate(), duration);
			free.getActivity().insert(consultationForPatient); 
			patient.getPlannedStepsPreTreatment().removeFirst();
			patient.getPlannedStepsPreTreatment().add(consultationForPatient);	
		}
	}

	public void NoShowConsultation (Date date){
		ArrayList <Doctor> doctorsSphere = this.getSphere().getDoctors();
		Date dateOfNoShow = date.decrease();
		int weekId=dateOfNoShow.getWeekId();
		int dayId= dateOfNoShow.getDayId();

		for (Doctor doctor : doctorsSphere) {
			ArrayList<Block> blocks =doctor.getSchedule().getDay(weekId, dayId).getBlocks();
			
			for (Block block : blocks) {
				if (block.getType()==BlockType.Consultation){
					LinkedList<Activity> activities =  block.getActivities();
					for (Activity activity : activities) {
						if (activity.getStatus()==ActivityStatus.ToPostpone){
							PreConsultation activityEvent= (PreConsultation) activity.getActivityEvent();
							if (activityEvent.getPatient().isPresentInCenter() && activityEvent.getPatient().getSphere()==this.getSphere()){
								activityEvent.getPatient().getPlannedStepsPreTreatment().removeFirst();
								this.processDemands(activityEvent.getPatient());
								
							}
							else{
								activityEvent.getPatient().toPatientsOut();
							}
						}
					}
				}	
			}	
		}
	}
	
	public Sphere getSphere() {
		return sphere;
	}


	public void setSphere(Sphere sphere) {
		this.sphere = sphere;
	}






}
