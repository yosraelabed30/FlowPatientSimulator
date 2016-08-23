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
import simulation.Statistics;
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
	private static LinkedList<Patient> filesForPreContouring;
	private Schedule schedule ;
	
	public Technologist(Center center){
		this(center, new LinkedList<Patient>(), new LinkedList<Patient>());
	}
	
	public Technologist(Center center, LinkedList<Patient> filesForPlanification,
			LinkedList<Patient> filesForPreContouring) {
		super();
		this.center = center;
		this.filesForPlanification = filesForPlanification;
		Technologist.filesForPreContouring = filesForPreContouring;
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

	/**
	 * 
	 * @param patient
	 * @return 
	 * @author Yosra Elabed
	 */
	public Activity DelayTreatment(Patient patient) {
		Activity treatment=null;
		Availability best = null;
		Availability tmp = null;
		int duration = ActivityType.Treatment.getDefaultScheduleDuration();
		ArrayList<TreatmentMachine> adequateMachine = new ArrayList<>();
		
		Date dateLowerBound = patient.getReferredDate();
		Date dateUpperBound = dateLowerBound.clone();
		dateUpperBound.setMinute(24*60-1);
		ArrayList<BlockType> blockTypes = new ArrayList<>();
		blockTypes.add(BlockType.Treatment);
		ArrayList<Integer> daysForbidden = new ArrayList<>();
		for (TreatmentMachine treatmentMachine : this.getCenter().getTreatmentMachines()) {
			if (treatmentMachine.getTreatmentTechnics() .contains(patient.getTreatmentTechnic()) ) {
				adequateMachine.add(treatmentMachine);
				tmp = treatmentMachine.getSchedule().findFirstAvailability(ActivityType.Treatment, duration, blockTypes, daysForbidden, dateLowerBound, dateUpperBound);
			}
			if (best == null || tmp.compareTo(best)==-1) {
				best = tmp;
			}
		}
		if (best == null) {
			for (TreatmentMachine treatmentMachine : adequateMachine) {
				blockTypes.remove(BlockType.Treatment);
				blockTypes.add(BlockType.OverTime);
				tmp = treatmentMachine.getSchedule().findFirstAvailability(ActivityType.Treatment, duration, blockTypes, daysForbidden, dateLowerBound, dateUpperBound);
			}
			if (best == null || tmp.compareTo(best)==-1) {
				best = tmp;
			}

		}
		if (best != null) {
			int start = best.getStart();
			ActivityType type = ActivityType.Treatment;
			ActivityEvent event = new Treatment(patient);
			treatment = new Activity(start, duration, type, event);
			best.getActivity().insert(treatment);
		}
		return treatment;
	}
		
	public Activity NoShowsTreatment(Patient patient){
		int duration = ActivityType.Treatment.getDefaultScheduleDuration();
		boolean scheduled = false;
		Activity best = patient.getPlannedStepsTreatments().getLast();
		Date date = best.getDate().increase();
		TreatmentMachine machine = (TreatmentMachine) best.getiSchedule();
		scheduled = this.scheduleTreatment(patient, machine, date, duration, true);
		if (!scheduled) {
			System.out.println("A treatment could not be scheduled");
		}
		else{
			((Treatment) best.getActivityEvent()).setLast(false);
		}
		return best;
	}
			
	private boolean scheduleTreatment(Patient patient,
			TreatmentMachine machine, Date date, int duration, boolean isLast) {
		boolean couldBeDone = true;
		ArrayList<TreatmentMachine> adequateMachine = new ArrayList<>();
		int start = date.getMinute();
		Availability freeActMachine = machine.getSchedule()
				.findAvailability(date, duration);
		if (freeActMachine == null) {
			for (TreatmentMachine treatmentMachine : patient.getSphere().getCenter()
					.getTreatmentMachines()) {
				if (treatmentMachine.getTreatmentTechnics().contains(
						patient.getTreatmentTechnic())) {
					adequateMachine.add(treatmentMachine);
					freeActMachine = treatmentMachine.getSchedule()
							.findAvailability(date,
									duration);
					if (freeActMachine != null) {
						break;
					}
				}
			}
			if (freeActMachine == null) {
				ArrayList<BlockType> blockTypes = new ArrayList<>();
				blockTypes.add(BlockType.Treatment);
				ArrayList<Integer> daysForbidden = new ArrayList<Integer>();
				Date dateUpperBound = date.clone();
				dateUpperBound.setMinute(24 * 60 - 1);
				// this freeAct is obviously more than a day in the future, so
				// we can schedule the treatment starting at the same time as
				// freeAct
				freeActMachine = machine.getSchedule().findFirstAvailability(
						ActivityType.Treatment, duration, blockTypes,
						daysForbidden, date, dateUpperBound);
				if (freeActMachine == null) {
					for (TreatmentMachine treatmentMachine : adequateMachine) {
						freeActMachine = treatmentMachine.getSchedule()
								.findFirstAvailability(ActivityType.Treatment,
										duration, blockTypes, daysForbidden,
										date, dateUpperBound);
					}
					if (freeActMachine == null) {
						blockTypes.remove(BlockType.Treatment);
						blockTypes.add(BlockType.OverTime);
						freeActMachine = machine.getSchedule()
								.findFirstAvailability(ActivityType.Treatment,
										duration, blockTypes, daysForbidden,
										date, dateUpperBound);
						if (freeActMachine == null) {
							for (TreatmentMachine treatmentMachine : adequateMachine) {
								freeActMachine = treatmentMachine.getSchedule()
										.findFirstAvailability(
												ActivityType.Treatment,
												duration, blockTypes,
												daysForbidden, date,
												dateUpperBound);
							}
							if (freeActMachine == null) {
								// TODO treat those cases
								// System.out.println("issue");
								couldBeDone=false;
							}
						}
					}
				}
				start = freeActMachine.getStart();
			}
		}
		Treatment treatmentEvent = new Treatment(patient);
		Activity treatmentForMachine = new Activity(start, duration,
				ActivityType.Treatment, treatmentEvent);
		treatmentEvent.setLast(isLast);
		freeActMachine.getActivity().insert(treatmentForMachine);
		Activity treatmentForPatient = treatmentForMachine.clone();
		TreatmentMachine treatmentMachine = (TreatmentMachine) treatmentForMachine
				.getiSchedule();
		treatmentForPatient.setActivityEvent(new ArrivalTreatment(
				treatmentMachine));
		Availability freeActPatient = patient.getSchedule()
				.findAvailability(
						treatmentForPatient.getDate(), duration);
		freeActPatient.getActivity().insert(treatmentForPatient);
		patient.getPlannedStepsTreatments().add(treatmentForPatient);
		return couldBeDone;
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
		Statistics.increaseNbPatientsForPlanif();
		Activity firstTreatmentForMachine = null;
		int nbCTSims = patient.getScanTechnics().size();
		ArrayList<Activity> CTSims = Technologist.nullList(nbCTSims);
		Date dateLowerBound = Date.now();
		Date dateFirstTreatment = null;
		Date deadLine = patient.getDeadLine();
		boolean relaxingConstraintFirstTreatmentCTSim = false;
		
		boolean couldBePlannedOnTime = true;
		
		while(firstTreatmentForMachine == null || CTSims.contains(null)){
			firstTreatmentForMachine = searchFirstTreatment(patient, dateLowerBound);
			dateFirstTreatment = firstTreatmentForMachine.getDate();
			dateLowerBound = firstTreatmentForMachine.getEndDate();
			dateLowerBound.increaseMinute();
			CTSims = searchCTSims(patient, dateFirstTreatment,
					relaxingConstraintFirstTreatmentCTSim);
			if(CTSims.contains(null)){
				firstTreatmentForMachine.delete();
				firstTreatmentForMachine=null;
			}
			if(dateFirstTreatment.compareTo(deadLine) >= 0 && !relaxingConstraintFirstTreatmentCTSim){
				relaxingConstraintFirstTreatmentCTSim = true;
				dateLowerBound = Date.now();
				if(firstTreatmentForMachine!=null){
					firstTreatmentForMachine.delete();
					firstTreatmentForMachine=null;
				}
				CTSims = Technologist.nullList(nbCTSims);
			}
			else if(dateFirstTreatment.compareTo(deadLine) >=0 && relaxingConstraintFirstTreatmentCTSim && !CTSims.contains(null)){ // we found a first treatment, for the machine, but it is not before the deadline and we already relaxed
				Statistics.increasePlannedLate();
				System.out.println("------------------->  patient id : "+patient.getId()+", with priority : "+patient.getPriority()+", at "+Date.now().getMinute()+", cannot be treated on time. Date first treatment : "+dateFirstTreatment+" and deadline : "+deadLine);
				couldBePlannedOnTime=false;
			}
		}
		
		if(couldBePlannedOnTime){
			Statistics.increasePlannedOnTime();
			System.out.println("At "+Date.now().getMinute()+", patient can be treated on time <----------------------- patient id : "+patient.getId()+", with priority : "+patient.getPriority());
		}
		Availability freeForFirstTreatment = patient.getSchedule().findAvailability(firstTreatmentForMachine.getDate(), firstTreatmentForMachine.duration());
		Activity firstTreatmentForPatient = firstTreatmentForMachine.clone();
		TreatmentMachine treatmentMachine = (TreatmentMachine) firstTreatmentForMachine.getiSchedule();
		firstTreatmentForPatient.setActivityEvent(new ArrivalTreatment(treatmentMachine));
		freeForFirstTreatment.getActivity().insert(firstTreatmentForPatient);
		patient.getPlannedStepsTreatments().add(firstTreatmentForPatient);
		
		for (Activity ctSimForScan : CTSims) {
			Availability freeForCTSim = patient.getSchedule().findAvailability(ctSimForScan.getDate(), ctSimForScan.duration());
			Activity ctSimForPatient = ctSimForScan.clone();
			ctSimForPatient.setActivityEvent(new ArrivalCTSim());
			freeForCTSim.getActivity().insert(ctSimForPatient);
			patient.getPlannedStepsPreTreatment().add(ctSimForPatient);
		}
		
		//only remains to program the other treatments
		this.programOtherTreatments(firstTreatmentForPatient, (TreatmentMachine)firstTreatmentForMachine.getiSchedule());
	}
	
	private void programOtherTreatments(Activity firstTreatmentForPatient,
			TreatmentMachine machine) {
		Patient patient = (Patient) firstTreatmentForPatient.getiSchedule();
		Date date = firstTreatmentForPatient.getDate();
		int duration = firstTreatmentForPatient.duration();
		boolean isLast = false;
		for(int i=1;i<patient.getNbTreatments();i++){
			if(patient.getPriority()==Priority.P3 || patient.getPriority()==Priority.P4){
				date = date.increaseWeekend();
			}
			else{
				date = date.increase();
			}
			if (i == patient.getNbTreatments() - 1) {
				isLast = true;
			}
			scheduleTreatment(patient, machine, date, duration, isLast);
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
		dateFirstTreatment.decreaseMinute();
		Date now  = Date.now();
		int duration = ActivityType.CTSim.getDefaultScheduleDuration();
		int nbCTSims = patient.getScanTechnics().size();
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
		Date dateLBCopy1 = dateLowerBound.clone();
		Date dateUBCopy1 = dateUpperBound.clone();
		Date dateLBCopy2 = dateLowerBound.clone();
		
		ArrayList<BlockType> blockTypesP3P4 = new ArrayList<>();
		blockTypesP3P4.add(BlockType.Scan);
		ArrayList<BlockType> blockTypesP1P2 = new ArrayList<>();
		blockTypesP1P2.add(BlockType.Scan);
		blockTypesP1P2.add(BlockType.Reserved);
		ArrayList<Integer> daysForbidden = new ArrayList<>();
		daysForbidden.add(5);
		daysForbidden.add(6);
		
		for (LinkedList<Integer> order : allPerms) {
			dateLBCopy2 = dateLBCopy1;
			while(CTSims.contains(null) && dateLBCopy2.compareTo(dateUBCopy1)<=0){
				dateLowerBound = dateLBCopy2;
				dateUpperBound = dateUBCopy1;
				for(int i=0;i<nbCTSims;i++){
					ScanTechnic scanTechnic = patient.getScanTechnics().get(order.get(i));
					tmp = null;
					best = null;
					for (Scan scan : this.getCenter().getScans()) {
						if(scanTechnic==scan.getImageryTechnic()){
							if(patient.getPriority()==Priority.P3 || patient.getPriority()==Priority.P4){
								tmp = scan.getSchedule().findFirstAvailability(ActivityType.CTSim, duration, blockTypesP3P4, daysForbidden, CTSims, dateLowerBound, dateUpperBound);
							}
							else if (patient.getPriority()==Priority.P1 || patient.getPriority()==Priority.P2){
								tmp = scan.getSchedule().findFirstAvailability(ActivityType.CTSim, duration, blockTypesP1P2, daysForbidden, CTSims, dateLowerBound, dateUpperBound);
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
						dateLBCopy2 = dateLBCopy2.increase(); 
						dateLBCopy2.setMinute(0);
						break;
					}
					else{
						Activity ctSim = new Activity(best.getStart(), duration, ActivityType.CTSim, new CTSim(patient));
						best.getActivity().insert(ctSim);
						CTSims.set(i, ctSim);
						
						//The following scans have to be scheduled on the same day as the first scan found
						if(!ctSim.getDate().checkSameWeekAndDayAs(dateLowerBound)){
							dateLowerBound = ctSim.getDate();
							dateLowerBound.setMinute(0);
						}
						if(!ctSim.getDate().checkSameWeekAndDayAs(dateUpperBound)){
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
		}
		return CTSims;
	}

	private Activity searchFirstTreatment(Patient patient, Date dateLowerBound) {
		Activity firstTreatmentForMachine = null;
		int duration = ActivityType.FirstTreatment.getDefaultScheduleDuration();
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
				tmp = treatmentMachine.getSchedule().findFirstAvailability(ActivityType.FirstTreatment, duration, blockTypeTreatment, days456Forbidden, dateLowerBound);
				if(best==null || (best!=null && tmp.compareTo(best)<=0)){
					best = tmp;
				}
			}
		}
		if(best != null && best.getDate().compareTo(patient.getDeadLine())==1){
			for (TreatmentMachine treatmentMachine : appropriateMachines) {
				if(patient.getPriority()==Priority.P1 || patient.getPriority()==Priority.P2){
					tmp = treatmentMachine.getSchedule().findFirstAvailability(ActivityType.FirstTreatment, duration, blockTypeReserved, days56Forbidden, dateLowerBound);
				}
				else{
					tmp = treatmentMachine.getSchedule().findFirstAvailability(ActivityType.FirstTreatment, duration, blockTypeTreatment, days56Forbidden, dateLowerBound);
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

