package medical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import scheduling.Activity;
import scheduling.ActivityType;
import scheduling.Availability;
import scheduling.Block;
import scheduling.BlockType;
import scheduling.Date;
import scheduling.Day;
import scheduling.ISchedule;
import scheduling.Schedule;
import scheduling.Week;
import simulation.FlowOfPatients;
import tools.Time;
import umontreal.iro.lecuyer.stochprocess.GeometricBrownianMotion;
import events.ActivityEvent;
import events.ArrivalCTSim;
import events.ArrivalConsultation;
import events.ArrivalTreatment;
import events.CTSim;
import events.FirstTreatment;
import events.Treatment;
import fileComparators.FileComparator1;

public class Technologist implements ISchedule{
	private Center center;
	private LinkedList<Patient> filesForPlanification;
	private ArrayList<Scan> scans;
	private ArrayList<TreatmentMachine> tmachines;
	private static LinkedList<Patient> filesForPreContouring;
	private Schedule schedule ;
	
	
	
	public Technologist(Center center){
		this(center, new LinkedList<Patient>(), center.getCtscans(), center.getTreatmentMachines(), new LinkedList<Patient>());
	}
	
	public Technologist(Center center, LinkedList<Patient> filesForPlanification,
			ArrayList<Scan> scans, ArrayList<TreatmentMachine> tmachines,
			LinkedList<Patient> filesForPreContouring) {
		super();
		this.center = center;
		this.filesForPlanification = filesForPlanification;
		this.setScans(scans);
		this.tmachines = tmachines;
		Technologist.filesForPreContouring = filesForPreContouring;
	}

	public ArrayList<TreatmentMachine> getTmachines() {
		return tmachines;
	}

	public void setTmachines(ArrayList<TreatmentMachine> tmachines) {
		this.tmachines = tmachines;
	}
	
	/**
	 * Depending on the technic needed for the patient, the technologist decides if a mold is needed
	 * @return
	 */
	public boolean moldNeeded(TreatmentTechnic technic){
		//TODO
		return true;
	}

	public static LinkedList<Patient> getFilesForPreContouring() {
		return filesForPreContouring;
	}

	public static void setFilesForPreContouring(LinkedList<Patient> filesForPreContouring) {
		Technologist.filesForPreContouring = filesForPreContouring;
	}

	public LinkedList<Patient> getFilesForCTSimTreatment() {
		return filesForPlanification;
	}

	
	public void setFilesForCTSimTreatment(LinkedList<Patient> filesForCTSimTreatment) {
		this.filesForPlanification = filesForCTSimTreatment;
	}
	
	
	
	
	public ArrayList<Scan> getScans() {
		return scans;
	}

	public void setScans(ArrayList<Scan> scans) {
		this.scans = scans;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	@Override
	public Week addWeek(int weekId) {
	return (this.getSchedule().addWeek(weekId));
	}

	
	public Activity DelayTreatment(Patient patient) {
		Activity best = null;
		Activity tmp = null;
		int arrivalMinutes;
		int duration = 45;
		ArrayList<TreatmentMachine> adequateMachine = new ArrayList<>();

		arrivalMinutes = patient.getArrivalMinutes();

		for (TreatmentMachine treatmentMachine : this.getCenter().getTreatmentMachines()) {
			if (treatmentMachine.getTreatmentTechnics() .contains(patient.getTreatmentTechnic()) ) {
				adequateMachine.add(treatmentMachine);
				tmp = treatmentMachine.getSchedule().getFirstAvailabilityBeforeTheEndOfTheDay(duration,
						BlockType.Treatment, arrivalMinutes);
			}
			if (best == null || tmp.startsEarlierThan(best)) {
				best = tmp;
			}
		}
		if (best == null) {
			for (TreatmentMachine treatmentMachine : adequateMachine) {
				tmp = findOvertimeFreeActivity(treatmentMachine, Date.dateNow(), duration);
			}
			if (best == null || tmp.startsEarlierThan(best)) {
				best = tmp;
			}

		}
		if (best != null) {
			TreatmentMachine machine = (TreatmentMachine) best.getiSchedule();
			int start = best.getStart();
			ActivityType type = ActivityType.Treatment;
			ActivityEvent event = new Treatment(patient);
			Activity Treatment = new Activity(start, duration, type, event);
			best.insert(Treatment);

		}
		return best;
	}
		
	public Activity NoShowsTreatment(Patient patient){
		
		int duration =45;
		boolean scheduled = false;
		boolean planned = false;
		ArrayList<TreatmentMachine> adequateMachine = new ArrayList<>();
		Activity best = patient.getPlannedStepsTreatments().getLast();
		Date date = best.getDate().increase();
		TreatmentMachine machine = (TreatmentMachine) best.getiSchedule();
		for (TreatmentMachine treatmentMachine : this.getCenter().getTreatmentMachines()) {
			if (treatmentMachine.getTreatmentTechnics().contains(patient.getTreatmentTechnic()) ) {
				adequateMachine.add(treatmentMachine);
			}
		}
		scheduled = this.scheduleTreatment(patient, machine, date, duration, adequateMachine);
		planned = planned && scheduled;
		if (!scheduled) {
			System.out.println("A treatment could not be scheduled");

		

	}

	return best;
	}
			
		
	
			
	
		
	
	
	
	public Activity findOvertimeFreeActivity(TreatmentMachine treatmentMachine, Date date, int duration){
		Activity freeAct = null;
		Day day = treatmentMachine.getSchedule().getDay(date.getWeekId(), date.getDayId());
		for (Block currentBlock : day.getBlocks()){
			if (currentBlock.getType() == BlockType.Treatment){
				int endActivty= currentBlock.getActivities().getLast().getEnd();
				int endBlock= currentBlock.getEnd();
				int elongationMinutes = duration- endBlock + endActivty;
				if(elongationMinutes+endActivty<19*60+30){
					freeAct = currentBlock.elongation(elongationMinutes);
					break;
				}
				
			}
		}
		return freeAct;
	}

	public boolean scheduleTreatment(Patient patient, TreatmentMachine machine, Date date, int duration, ArrayList<TreatmentMachine> adequateMachine){
		int start = date.getMinute();
		Activity freeAct = machine.getSchedule().findFreeActivityToInsertOtherActivity(date, duration);
		if (freeAct == null){
			for (TreatmentMachine treatmentMachine : adequateMachine) {
				freeAct = treatmentMachine.getSchedule().findFreeActivityToInsertOtherActivity(date, duration);
				if(freeAct!=null){
					break;
				}
			}
			if (freeAct == null){
				freeAct = machine.getSchedule().getFirstAvailabilityInDay(duration,BlockType.Treatment, date); //this freeAct is obviously more than a day in the future, so we can schedule the treatment starting at the same time as freeAct
				if (freeAct == null){
					for (TreatmentMachine treatmentMachine : adequateMachine) {
						freeAct = treatmentMachine.getSchedule().getFirstAvailabilityInDay(duration, BlockType.Treatment, date);
					}
					if (freeAct == null){
						freeAct = this.findOvertimeFreeActivity(machine, date, duration);
						if(freeAct==null){
							for (TreatmentMachine treatmentMachine : adequateMachine) {
								freeAct = this.findOvertimeFreeActivity(treatmentMachine, date, duration);
							}
							if (freeAct==null){
								//TODO treat those cases
								//System.out.println("issue");
								return false;
							}
							
						}
					}
				}
				start = freeAct.getStart();
			}

		}
		Activity treatment= new Activity(start, duration, ActivityType.Treatment, new Treatment (patient));
		freeAct.insert(treatment);
		return true;
	}

	public LinkedList<LinkedList<Integer>> allPerms(LinkedList<Integer> list){
		LinkedList<LinkedList<Integer>> res = new LinkedList<>();
		if(list.size()==1){
			res.add(list);
			return res;
		}
		else{
			int indexLast = list.size()-1;
			list.remove(indexLast);
			LinkedList<LinkedList<Integer>> ll = allPerms(list);
			for (LinkedList<Integer> l : ll) {
				for(int i=indexLast ; i>=0 ; i--){
					@SuppressWarnings("unchecked")
					LinkedList<Integer> clonel = (LinkedList<Integer>) l.clone();
					clonel.add(i, indexLast);
					res.add(clonel);
				}
			}
			return res;
		}
	}
	
	public void processFileForPlanification(Patient patient){
		Activity firstTreatmentForMachine = null;
		int nbCTSims = patient.getImageryTechnics().size();
		ArrayList<Activity> CTSims = Technologist.nullList(nbCTSims);
		Date dateLowerBound = Date.dateNow();
		Date dateFirstTreatment = Date.dateNow();
		Date deadLine = patient.getDeadLine();
		boolean relaxingConstraintFirstTreatmentCTSim = false;
		
		boolean couldBePlannedOnTime = true;
		
		while(firstTreatmentForMachine == null || CTSims.contains(null)){
			firstTreatmentForMachine = searchFirstTreatment(patient, dateLowerBound);
			dateFirstTreatment = firstTreatmentForMachine.getDate();
			dateLowerBound = firstTreatmentForMachine.getEndDate();
			if (dateFirstTreatment.compareTo(deadLine) == -1) {
				CTSims = searchCTSims(patient, dateFirstTreatment,
						relaxingConstraintFirstTreatmentCTSim);
				if(CTSims.contains(null)){
					firstTreatmentForMachine.delete();
					firstTreatmentForMachine=null;
				}
			} else if(dateFirstTreatment.compareTo(deadLine) >= 0 && !relaxingConstraintFirstTreatmentCTSim){
				relaxingConstraintFirstTreatmentCTSim = true;
				dateLowerBound = Date.dateNow();
				firstTreatmentForMachine.delete();
				firstTreatmentForMachine=null;
				CTSims = Technologist.nullList(nbCTSims);
			}
			else if(dateFirstTreatment.compareTo(deadLine) >=0 && relaxingConstraintFirstTreatmentCTSim){ // we found a first treatment, for the machine, but it is not before the deadline and we already relaxed
				firstTreatmentForMachine.delete();
				firstTreatmentForMachine=null;
				System.out.println("------------------->  patient id : "+patient.getId()+", with priority : "+patient.getPriority()+" cannot be treated on time, we do not treat him... (for now)");
				couldBePlannedOnTime=false;
				break;
			}
		}
		
		if(couldBePlannedOnTime){
			System.out.println("Patient can be treated on time <----------------------- patient id : "+patient.getId()+", with priority : "+patient.getPriority());
			Activity freeForFirstTreatment = patient.getSchedule().findFreeActivityToInsertOtherActivity(firstTreatmentForMachine.getDate(), firstTreatmentForMachine.duration());
			Activity firstTreatmentForPatient = firstTreatmentForMachine.clone();
			TreatmentMachine treatmentMachine = (TreatmentMachine) firstTreatmentForMachine.getiSchedule();
			firstTreatmentForPatient.setActivityEvent(new ArrivalTreatment(treatmentMachine));
			freeForFirstTreatment.insert(firstTreatmentForPatient);
			patient.getPlannedStepsTreatments().add(firstTreatmentForPatient);
			
			for (Activity ctSimForScan : CTSims) {
				Activity freeForCTSim = patient.getSchedule().findFreeActivityToInsertOtherActivity(ctSimForScan.getDate(), ctSimForScan.duration());
				Activity ctSimForPatient = ctSimForScan.clone();
				ctSimForPatient.setActivityEvent(new ArrivalCTSim());
				freeForCTSim.insert(ctSimForPatient);
				patient.getPlannedStepsPreTreatment().add(ctSimForPatient);
			}
			
			//only remains to program the other treatments
			this.programOtherTreatments(firstTreatmentForPatient, (TreatmentMachine)firstTreatmentForMachine.getiSchedule());
		}
	}
	
	private void programOtherTreatments(Activity firstTreatmentForPatient,
			TreatmentMachine machine) {
		Patient patient = (Patient) firstTreatmentForPatient.getiSchedule();
		Date date = firstTreatmentForPatient.getDate();
		int duration = firstTreatmentForPatient.duration();
		ArrayList<TreatmentMachine> adequateMachine = new ArrayList<>();
		for(int i=1;i<patient.getNbTreatments();i++){
			if(patient.getPriority()==Priority.P3 || patient.getPriority()==Priority.P4){
				date = date.increaseWeekend();
			}
			else{
				date = date.increase();
			}
			int start = date.getMinute();
			Activity freeActMachine = machine.getSchedule().findFreeActivityToInsertOtherActivity(date, duration);
			if (freeActMachine == null){
				for (TreatmentMachine treatmentMachine : patient.getCenter().getTreatmentMachines()) {
					if(treatmentMachine.getTreatmentTechnics().contains(patient.getTreatmentTechnic())){
						adequateMachine.add(treatmentMachine);
						freeActMachine = treatmentMachine.getSchedule().findFreeActivityToInsertOtherActivity(date, duration);
						if(freeActMachine!=null){
							break;
						}
					}
				}
				if (freeActMachine == null){
					freeActMachine = machine.getSchedule().getFirstAvailabilityInDay(duration,BlockType.Treatment, date); //this freeAct is obviously more than a day in the future, so we can schedule the treatment starting at the same time as freeAct
					if (freeActMachine == null){
						for (TreatmentMachine treatmentMachine : adequateMachine) {
							freeActMachine = treatmentMachine.getSchedule().getFirstAvailabilityInDay(duration, BlockType.Treatment, date);
						}
						if (freeActMachine == null){
							freeActMachine = this.findOvertimeFreeActivity(machine, date, duration);
							if(freeActMachine==null){
								for (TreatmentMachine treatmentMachine : adequateMachine) {
									freeActMachine = this.findOvertimeFreeActivity(treatmentMachine, date, duration);
								}
								if (freeActMachine==null){
									//TODO treat those cases
									//System.out.println("issue");
									break;
								}
							}
						}
					}
					start = freeActMachine.getStart();
				}

			}
			Treatment treatmentEvent = new Treatment (patient);
			Activity treatmentForMachine= new Activity(start, duration, ActivityType.Treatment, treatmentEvent);
			if(i==patient.getNbTreatments()-1){
				treatmentEvent.setLast(true);
			}
			freeActMachine.insert(treatmentForMachine);
			Activity treatmentForPatient = treatmentForMachine.clone();
			TreatmentMachine treatmentMachine = (TreatmentMachine) treatmentForMachine.getiSchedule();
			treatmentForPatient.setActivityEvent(new ArrivalTreatment(treatmentMachine));
			Activity freeActPatient = patient.getSchedule().findFreeActivityToInsertOtherActivity(treatmentForPatient.getDate(), duration);
			freeActPatient.insert(treatmentForPatient);
			patient.getPlannedStepsTreatments().add(treatmentForPatient);
		}
	}

	private static ArrayList<Activity> nullList(int i){
		ArrayList<Activity> nullList = new ArrayList<>(i);
		for(int j=0;j<i;j++){
			nullList.add(null);
		}
		return nullList;
	}

	private ArrayList<Activity> searchCTSims(Patient patient,
			Date dateFirstTreatment, boolean relaxingConstraintFirstTreatmentCTSim) {
		Date now  = Date.dateNow();
		int duration = 30;
		int nbCTSims = patient.getImageryTechnics().size();
		ArrayList<Activity> CTSims = Technologist.nullList(nbCTSims);
		LinkedList<Integer> list = new LinkedList<>();
		for(int i=0;i<nbCTSims;i++){
			list.add(i);
		}
		LinkedList<LinkedList<Integer>> allPerms = this.allPerms(list);
		Availability best = null;
		Availability tmp = null;
		Date dateFirstTreatmentDecreased = dateFirstTreatment.decreaseDays(patient);
		
		Date dateLowerBound = now.compareTo(dateFirstTreatmentDecreased)==-1 ? dateFirstTreatmentDecreased : now;
		if(relaxingConstraintFirstTreatmentCTSim){
			dateLowerBound = now;
		} 
		Date dateUpperBound = dateFirstTreatment;
		Date dateLBCopy = dateLowerBound.clone();
		Date dateUBCopy = dateUpperBound.clone();
		
		ArrayList<BlockType> blockTypesP3P4 = new ArrayList<>();
		blockTypesP3P4.add(BlockType.Scan);
		ArrayList<BlockType> blockTypesP1P2 = new ArrayList<>();
		blockTypesP1P2.add(BlockType.Scan);
		blockTypesP1P2.add(BlockType.Reserved);
		ArrayList<Integer> daysForbidden = new ArrayList<>();
		daysForbidden.add(5);
		daysForbidden.add(6);
		
		
		for (LinkedList<Integer> order : allPerms) {
			for(int i=0;i<nbCTSims;i++){
				ScanTechnic scanTechnic = patient.getImageryTechnics().get(order.get(i));
				tmp = null;
				best = null;
				for (Scan scan : this.getCenter().getScans()) {
					if(scanTechnic==scan.getImageryTechnic()){
						if(patient.getPriority()==Priority.P3 || patient.getPriority()==Priority.P4){
							tmp = scan.getSchedule().getFirstAvailability(duration, blockTypesP3P4, daysForbidden, CTSims, dateLowerBound, dateUpperBound);
						}
						else if (patient.getPriority()==Priority.P1 || patient.getPriority()==Priority.P2){
							tmp = scan.getSchedule().getFirstAvailability(duration, blockTypesP1P2, daysForbidden, CTSims, dateLowerBound, dateUpperBound);
						}
						if(best==null || (tmp!=null && tmp.compareTo(best)<=-1)){
							best = tmp;
						}
					}
				}
				if(best==null){
					for (Activity activity : CTSims) {
						if(activity!=null){
							activity.delete();
						}
					}
					CTSims = Technologist.nullList(nbCTSims);
					dateLowerBound = dateLBCopy;
					dateUpperBound = dateUBCopy;
					break;
				}
				else{
					Activity ctSim = new Activity(best.getStart(), duration, ActivityType.CTSim, new CTSim(patient));
					best.getActivity().insert(ctSim);
					CTSims.set(i, ctSim);
					
					//The following scans have to be scheduled on the same day as the first scan found
					if(!ctSim.getDate().sameWeekAndDayAs(dateLowerBound)){
						dateLowerBound = ctSim.getDate();
						dateLowerBound.setMinute(0);
					}
					if(!ctSim.getDate().sameWeekAndDayAs(dateUpperBound)){
						dateUpperBound = ctSim.getDate();
						dateUpperBound.setMinute(24*60-1);
					}
					
					if(!CTSims.contains(null)){
						Collections.sort(CTSims);
						CTSim lastCtsim = (CTSim) CTSims.get(CTSims.size()-1).getActivityEvent();
						lastCtsim.setLast(true);
						return CTSims;
					}
				}
			}
		}
		return CTSims;
	}

	private Activity searchFirstTreatment(Patient patient, Date dateLowerBound) {
		Activity firstTreatmentForMachine = null;
		int duration = 30;
		Availability best = null;
		Availability tmp = null;
		ArrayList<TreatmentMachine> appropriateMachines = new ArrayList<>();
		ArrayList<BlockType> blockTypeTreatment = new ArrayList<BlockType>();
		blockTypeTreatment.add(BlockType.Treatment);
		ArrayList<BlockType> blockTypeReserved = new ArrayList<BlockType>();
		blockTypeReserved.add(BlockType.Reserved);
		ArrayList<Integer> days456Forbidden = new ArrayList<>();
		for(int i=4;i<=6;i++){
			days456Forbidden.add(i);
		}
		ArrayList<Integer> days56Forbidden = new ArrayList<>();
		for(int i=5;i<=6;i++){
			days56Forbidden.add(i);
		}
		
		for (TreatmentMachine treatmentMachine : this.getCenter().getTreatmentMachines()) {
			if(treatmentMachine.getTreatmentTechnics().contains(patient.getTreatmentTechnic())){
				tmp = treatmentMachine.getSchedule().getFirstAvailability(duration, blockTypeTreatment, days456Forbidden, dateLowerBound);
				if(best==null || (best!=null && tmp.compareTo(best)<=0)){
					best = tmp;
				}
			}
		}
		if(best != null && best.getDate().compareTo(patient.getDeadLine())==1){
			for (TreatmentMachine treatmentMachine : appropriateMachines) {
				if(patient.getPriority()==Priority.P1 || patient.getPriority()==Priority.P2){
					tmp = treatmentMachine.getSchedule().getFirstAvailability(duration, blockTypeReserved, days56Forbidden, dateLowerBound);
				}
				else{
					tmp = treatmentMachine.getSchedule().getFirstAvailability(duration, blockTypeTreatment, days56Forbidden, dateLowerBound);
				}
				if(best==null || (best!=null && tmp.compareTo(best)<=0)){
					best = tmp;
				}
			}
		}
		if(best!=null){
			firstTreatmentForMachine = new Activity(best.getStart(), duration, ActivityType.Treatment, new FirstTreatment(patient));
			best.getActivity().insert(firstTreatmentForMachine);
		}
		else{
			System.out.println("A first treatment is supposed to be found by now");
		}
		return firstTreatmentForMachine;
	}
	
	public void processPatientFilesForPlanification(){
		Collections.sort(getFilesForCTSimTreatment(),new FileComparator1());
		Iterator<Patient> filesForPlanificationIter = filesForPlanification.iterator();
		while (filesForPlanificationIter.hasNext()) {
			Patient patient = filesForPlanificationIter.next();
			processFileForPlanification(patient);
			filesForPlanificationIter.remove();
		}
	}
	
	
	public void processFileForPreContouring(Patient patient){
		patient.getDoctor().getFilesForContouring().add(patient);
	}
	
	public void processPatientFilesForPreContouring(){
		LinkedList<Patient> filesForPreContouring = Technologist.getFilesForPreContouring();
		for (Patient patient : filesForPreContouring) {
			processFileForPreContouring(patient);
		}
//		System.out.println("preContouring done");
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}
}

