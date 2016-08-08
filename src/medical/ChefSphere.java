package medical;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import scheduling.Activity;
import scheduling.ActivityStatus;
import scheduling.ActivityType;
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


	public void processUrgentDemands(Patient patient){
		int duration =45;
		int lateness= Integer.MAX_VALUE;
		Date dateLowerBound= Date.dateNow();
		Activity best= null; 
		ArrayList<Doctor> competentDoctors = new ArrayList<>();
		for (Doctor doctor : this.getSphere().getDoctors()) {
			Activity tmp = null;
			if (doctor.canTreat(patient)){
				competentDoctors.add(doctor);
				tmp=doctor.getSchedule().getFirstAvailabilityFridayQuotas(duration, BlockType.Consultation,dateLowerBound);
				if (best == null ||tmp.startsEarlierThan(best) ) {
					best = tmp;
				}
			}
					
		}
		if (best==null || !(best.getDate().compareTo(patient.getDeadLine())==-1)){
			for (Doctor doctor : competentDoctors) {
				Activity tmp = null;
				if (doctor.isOverTime()== true){
					tmp= doctor.getSchedule().getFirstAvailabilityFridayQuotas(duration, BlockType.NotWorking, dateLowerBound);
					if (best == null ||tmp.startsEarlierThan(best) ) {
						best = tmp;	
					}
				}
			}
			
		}
	
		if(best!=null){
			Doctor firstAvailable = (Doctor) best.getiSchedule();
			patient.setDoctor(firstAvailable);
			int weekId = best.getWeek().getWeekId();
			int dayId = best.getDay().getDayId();
			int start = (weekId==dateLowerBound.getWeekId() && dayId==dateLowerBound.getDayId())? Math.max(dateLowerBound.getMinute(), best.getStart()) : best.getStart();
			int end = start+duration;
			
			PreConsultation check = new PreConsultation(patient, false, this.getSphere().getCenter().getAdminAgent());
			Activity consultationForDoctor = new Activity(best.getBlock(), start, end, ActivityType.Consultation, check);
			best.insert(consultationForDoctor);
			
			//Quotas of the week for the doc are decreased
			best.getWeek().decreaseQuotas();
			
			Activity consultationForPatient = consultationForDoctor.clone(); //at that moment, consultationForPatient's block is the one of consultationForDoctor
			ArrivalConsultation arrival =  new ArrivalConsultation();
			arrival.setLateness(lateness);
			arrival.setPriority(0); //patient arrives first then the presence is checked
			consultationForPatient.setActivityEvent(arrival);
			patient.getPlannedSteps().add(consultationForPatient);
			Activity free = patient.getSchedule().findFreeActivityToInsertOtherActivity(weekId, dayId, start, end);

			free.insert(consultationForPatient); //and when inserted, consultationForPatient's block is the one of free
			System.out.println("consultation : "+consultationForPatient.getDate()+" pour ce patient "+patient.getId()+" avec prio  "+patient.getPriority());
			patient.getPlannedStepsPreTreatment().add(best);
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
			this.processUrgentDemands(patient);
			demandsIter.remove();
		}
	}
	
	public void delayConsultation(Patient patient){
		int duration=30;
		Activity best= null;
		Doctor doctor=patient.getDoctor();
		best= doctor.getSchedule().getFirstAvailabilityFridayQuotasBeforeTheEndOfTheDay(duration, BlockType.Consultation,Date.dateNow());
		if (best == null){
			if(doctor.isOverTime()== true){
			 best= doctor.getSchedule().getFirstAvailabilityFridayQuotasBeforeTheEndOfTheDay(duration, BlockType.NotWorking, Date.dateNow());
				
			}
			else {
				this.processUrgentDemands(patient);
				// on suppose que si le patient est arrivé, il va être traité comme urgent et pris en charge avant qu'il quitte
			}
		}
		if (best!=null){
			int start = best.getStart();
			int end = start+duration;
			
			PreConsultation check = new PreConsultation(patient, false, this.getSphere().getCenter().getAdminAgent());
			Activity consultationForDoctor = new Activity(best.getBlock(), start, end, ActivityType.Consultation, check);
			best.insert(consultationForDoctor);

			Activity consultationForPatient = consultationForDoctor.clone(); 
			Activity free = patient.getSchedule().findFreeActivityToInsertOtherActivity(best.getDate().getWeekId(), best.getDate().getDayId(), start, end);
			free.insert(consultationForPatient); 
			patient.getPlannedStepsPreTreatment().removeFirst();
			patient.getPlannedStepsPreTreatment().add(free);
			
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
							if (activityEvent.getPatient().isInCenter() && activityEvent.getPatient().getSphere()==this.getSphere()){
								activityEvent.getPatient().getPlannedStepsPreTreatment().removeFirst();
								this.processUrgentDemands(activityEvent.getPatient());
								
							}
							else{
								activityEvent.getPatient().setOut(true);
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
