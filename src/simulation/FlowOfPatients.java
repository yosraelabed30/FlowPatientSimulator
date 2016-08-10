package simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import medical.AdminAgent;
import medical.Cancer;
import medical.Center;
import medical.ChefSphere;
import medical.Doctor;
import medical.Dosimetrist;
import medical.Patient;
import medical.Priority;
import medical.ScanTechnic;
import medical.Sphere;
import medical.Scan;
import medical.Technologist;
import medical.TreatmentMachine;
import medical.TreatmentTechnic;
import events.ActivityEvent;
import events.ArrivalTreatment;
import events.CalculDosi;
import events.Contouring;
import events.PreConsultation;
import events.ReferredPatient;
import events.Treatment;
import events.TreatmentPlan;
import scheduling.Activity;
import scheduling.ActivityStatus;
import scheduling.ActivityType;
import scheduling.Block;
import scheduling.BlockType;
import scheduling.Date;
import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.simevents.Event;
import umontreal.iro.lecuyer.simevents.Sim;

/**
 * Main class of the program, contains the elements for the simulation of the flow of patients
 * @author Joffrey
 */
public class FlowOfPatients {
	/**
	 * To generate the delay in between two patients arrivals
	 */
	RandomVariateGen genReferredPatient;
	/**
	 * day counter
	 */
	static int day=-1;
	/**
	 * Day of the week, from 0 (Monday) to 6 (Sunday)
	 */
	static int dayOfWeek;
	/**
	 * Week number
	 */
	static int week=-1;
	/**
	 * Just to check the number of patients per day (TODO remove it, when no longer needed)
	 */
	int patientsPerDay;
	/**
	 * Medical {@link Center}
	 */
	public Center center;
	
	/**
	 * Constructor initiating the day of the week to -1, since it is immediately increased
	 * {@link #genReferredPatient} is instanciated with an ExponentialGen, see Taobane memoire for more info
	 */
	public FlowOfPatients() {
		super();
		dayOfWeek = -1;
		this.genReferredPatient = new ExponentialGen(new MRG32k3a(), 1.16);
		this.center = new Center();
	}

	/**
	 * only for tests for now
	 * check this link to understand the generation of patient's arrivals : https://www.wolframalpha.com/input/?i=inverse+poisson+distribution+mu+%3D+6.218
	 * @param args
	 */
	public static void main(String[] args) {
		double time = System.currentTimeMillis();
		FlowOfPatients test = new FlowOfPatients();
		test.setCenter(test.CHUM());
//		test.simulateOneRun(525600*2);
//		test.simulateOneRun(525600);
//		test.simulateOneRun(288000);
//		test.simulateOneRun(100000);
		test.simulateOneRun(30000);
//		test.simulateOneRun(72*60);
		
		time = System.currentTimeMillis()-time;
		System.out.println("time : "+time);
	}

	/**
	 * Simulates one run of the simulation
	 * @param timeHorizon
	 */
	public void simulateOneRun(double timeHorizon) {
		Sim.init();
		new EndOfSim().schedule(timeHorizon);
		new DayStart().schedule(7*60+30);
		Sim.start();
	}
	
	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	public Center CHUM(){
		Center chum = new Center();
		for (Cancer cancer : Cancer.values()) {
			Sphere sphere = new Sphere(chum,cancer, null, new ArrayList<Doctor>(), new ArrayList<Patient>());
			chum.getSpheres().add(sphere);
			
			ArrayList<ArrayList<Block>>blocksTab1 = new ArrayList<ArrayList<Block>>(7);
			for(int i=0;i<7;i++){
				ArrayList<Block> blocks1 = new ArrayList<>();
				blocks1.add(new Block(0, 0, 8*60, BlockType.NotWorking));
				blocks1.add(new Block(1, 8*60, 17*60, BlockType.Consultation));
				Block contouringBlock = new Block(2, 17*60, 19*60, BlockType.Contouring);
				Activity contouringActivity = contouringBlock.getActivities().get(0);
				contouringActivity.setType(ActivityType.Contouring);
				contouringActivity.setActivityEvent(new Contouring());
				blocks1.add(contouringBlock);
				Block treatmentPlanBlock = new Block(3, 19*60, 20*60, BlockType.TreatmentPlan);
				Activity treatmentPlanActivity = treatmentPlanBlock.getActivities().get(0);
				treatmentPlanActivity.setType(ActivityType.TreatmentPlan);
				treatmentPlanActivity.setActivityEvent(new TreatmentPlan());
				blocks1.add(treatmentPlanBlock);
				blocks1.add(new Block(4, 20*60, 24*60, BlockType.NotWorking));
				blocksTab1.add(blocks1);
			}
			ArrayList<Sphere> sphereDoctor = new ArrayList<>();
			sphereDoctor.add(sphere);
			Doctor doc1 = new Doctor(blocksTab1, sphereDoctor, chum);
			sphere.getDoctors().add(doc1);
			chum.getDoctors().add(doc1);
			
			ChefSphere chef = new ChefSphere(sphere);
			sphere.setChefSphere(chef);
			chum.getChefSpheres().add(chef);
		}
		
		ArrayList<ArrayList<Block>>blocksTab3 = new ArrayList<ArrayList<Block>>(7);
		ArrayList<ArrayList<Block>>blocksTab4 = new ArrayList<ArrayList<Block>>(7);
		for(int i=0;i<7;i++){
			ArrayList<Block> blocks1 = new ArrayList<>();
			ArrayList<Block> blocks2 = new ArrayList<>();
			blocks1.add(new Block(0, 7*60, 16*60, BlockType.Treatment));
			blocks1.add(new Block(1, 16*60, 18*60, BlockType.Reserved));
			blocks2.add(new Block(0, 7*60, 16*60, BlockType.Treatment));
			blocks2.add(new Block(1, 16*60, 18*60, BlockType.Reserved));
			blocksTab3.add(blocks1);
			blocksTab4.add(blocks2);
		}
		ArrayList <TreatmentTechnic> treatmentTechnic1 = new ArrayList<>();
//		treatmentTechnic1.add(TreatmentTechnic.TX07213D);
//		treatmentTechnic1.add(TreatmentTechnic.TX0721IMRT);
		treatmentTechnic1.addAll(new ArrayList<TreatmentTechnic>(Arrays.asList(TreatmentTechnic.values())));
		ArrayList <TreatmentTechnic> treatmentTechnic2 = new ArrayList<>();
//		treatmentTechnic2.add(TreatmentTechnic.TX0721IMRT);
//		treatmentTechnic2.add(TreatmentTechnic.TX2053D);
		treatmentTechnic2.addAll(new ArrayList<TreatmentTechnic>(Arrays.asList(TreatmentTechnic.values())));
		chum.getTreatmentMachines().add(new TreatmentMachine(chum, treatmentTechnic1, blocksTab3));
		chum.getTreatmentMachines().add(new TreatmentMachine(chum, treatmentTechnic2, blocksTab4));
		
		ScanTechnic[] scanTechnics = ScanTechnic.values();
		for (ScanTechnic scanTechnic : scanTechnics) {
			ArrayList<ArrayList<Block>>blocksTab5 = new ArrayList<ArrayList<Block>>(7);
			for(int i=0;i<7;i++){
				ArrayList<Block> blocks1 = new ArrayList<>();
				blocks1.add(new Block(0, 8*60, 15*60, BlockType.Scan));
				blocks1.add(new Block(1, 15*60, 17*60, BlockType.Reserved));
				blocksTab5.add(blocks1);
			}
			chum.getCtscans().add(new Scan(chum, true, scanTechnic, blocksTab5));
		}
		
		ArrayList<ArrayList<Block>>blocksTab6 = new ArrayList<ArrayList<Block>>(7);
		for(int i=0;i<7;i++){
			ArrayList<Block> blocks6 = new ArrayList<>();
			Block dosiBlock = new Block(0, 8*60, 17*60, BlockType.Dosimetry);
			Activity dosiActivity = dosiBlock.getActivities().get(0);
			dosiActivity.setType(ActivityType.Dosimetry);
			dosiActivity.setActivityEvent(new CalculDosi());
			blocks6.add(dosiBlock);
			blocksTab6.add(blocks6);
		}
		Dosimetrist dosi1 = new Dosimetrist(chum, blocksTab6);
		chum.getDosimetrists().add(dosi1);
		
		chum.setAdminAgent(new AdminAgent(chum));
		chum.setTechnologist(new Technologist(chum));
		return chum;
	}

	
	class DayStart extends Event {

		public void actions() {
			this.increaseDay();
			this.increaseDayOfWeek();
			
			if(dayOfWeek==0){
				this.increaseWeek();
				getCenter().addWeek();
			}
			
			if(dayOfWeek==5 || dayOfWeek==6){
				getCenter().setWelcome(false);
			}
			else{
				getCenter().setWelcome(true);
			}
			patientsPerDay = 0;
			int time = (int)Sim.time();
			Date date = Date.dateNow();
			
			System.out.println("\nDayStart");
			System.out.println("New day in minutes : "+time+" it's a "+dayOfWeek+", of week "+week+" and it is day "+day);
			
			getCenter().getTechnologist().processPatientFilesForPreContouring();
			
			new DayEnd().schedule(10*60);
			new ReferredPatient(genReferredPatient, getCenter()).schedule ((int)(genReferredPatient.nextDouble()*60));
			getCenter().doScheduleToday();
			ArrayList <ChefSphere> chefSpheres= getCenter().getChefSpheres();
			
			if (date.getWeekId()!=0 || date.getDayId()!=0){
				for (ChefSphere chefSphere : chefSpheres) {
					chefSphere.NoShowConsultation(date);
//					System.out.println("le moment de replanification" +Date.dateNow() );
				}
			}
		}

		private void increaseWeek() {
			week++;
		}

		private void increaseDay() {
			day++;
		}

		private void increaseDayOfWeek() {
			if(dayOfWeek==6){
				dayOfWeek=0;
			}
			else{
				dayOfWeek++;
			}
		}
		
	}
	
	class DayEnd extends Event {

		public void actions() {
			getCenter().setWelcome(false);
			new DayStart().schedule(14 * 60);

			getCenter().getAdminAgent().processDemands();
			for (ChefSphere chef : getCenter().getChefSpheres()) {
				chef.processDemands();
			}
			center.getTechnologist().processPatientFilesForPlanification();

			getCenter().fromPatientsToPatientsOut();
			
//			isReadyForTheTreatment(2);
//			isReadyForTheTreatment(1);

		}

		public void isReadyForTheTreatment(int counter) {

			LinkedList<Patient> patients = getCenter().getPatients();
			for (Patient patient : patients) {
				if((patient.getPriority()==Priority.P3 || patient.getPriority()==Priority.P4) && patient.getPlannedStepsTreatments().size()>0){
					int duration = 45;
					Date now = Date.dateNow();
					Date daysBeforeTheFirstTreatment = now.increase(counter);
					Date firstTreatment = patient.getPlannedStepsTreatments().getFirst().getDate();
					if (firstTreatment.getWeekId() == daysBeforeTheFirstTreatment.getWeekId()
							&& firstTreatment.getDayId() == daysBeforeTheFirstTreatment.getDayId()) {
						Doctor doctor = patient.getDoctor();
						ArrivalTreatment arrivalTreatment = (ArrivalTreatment) patient.getPlannedStepsTreatments().getFirst().getActivityEvent();
						TreatmentMachine treatmentMachine = (TreatmentMachine) arrivalTreatment.getTreatmentMachine();
						if (doctor.getFilesForContouring().contains(patient)) {

							patient.getPlannedStepsTreatments().poll();
							treatmentMachine.getSchedule().getActivityAssociated(firstTreatment).delete();
							patient.getSchedule().getActivityAssociated(firstTreatment).delete();
							Date lastTreatment = patient.getPlannedStepsTreatments().getLast().getDate();
							Date newLastTreatment = lastTreatment.increase();
							Activity best = treatmentMachine.getSchedule()
									.findFreeActivityToInsertOtherActivity(newLastTreatment, duration);

							patient.setFirstTreatment(patient.getPlannedStepsTreatments().getFirst());

							if (best != null) {
								int start = best.getStart();
								int end = start + duration;

								Treatment treatment = new Treatment(patient);
								Activity treatmentActivity = new Activity(best.getBlock(), start, end,
										ActivityType.Treatment, treatment);
								best.insert(treatmentActivity);

								Activity treatmentActivityPatient = treatmentActivity.clone();
								Activity free = patient.getSchedule().findFreeActivityToInsertOtherActivity(
										best.getDate().getWeekId(), best.getDate().getDayId(), start, end);
								free.insert(treatmentActivityPatient);
								patient.getPlannedStepsTreatments().add(best);

							}

							else {
								TreatmentTechnic treatmentTechnic = patient.getTreatmentTechnic();
								ArrayList<TreatmentMachine> treatmentMachines = getCenter().getTreatmentMachines();
								ArrayList<TreatmentMachine> adequateMachines = null;
								for (TreatmentMachine treatmentMachine2 : treatmentMachines) {
									if (treatmentMachine2.getTreatmentTechnics().contains(treatmentTechnic)) {
										adequateMachines.add(treatmentMachine2);
									}
								}
								for (TreatmentMachine adequateMachine : adequateMachines) {
									Activity tmp = adequateMachine.getSchedule()
											.findFreeActivityToInsertOtherActivity(newLastTreatment, duration);
									if (best == null || tmp.startsEarlierThan(best)) {
										best = tmp;
									}
								}
								if (best == null) {
									best = treatmentMachine.getSchedule().getFirstAvailabilityNotWeekend(duration,
											BlockType.Treatment, patient.getPlannedStepsTreatments().getFirst().getDate().getWeekId(),
											patient.getPlannedStepsTreatments().getFirst().getDate().getDayId(),
											patient.getPlannedStepsTreatments().getFirst().getDate().getMinute(),
											patient.getPlannedStepsTreatments().getFirst().getDate().getWeekId(),
											patient.getPlannedStepsTreatments().getFirst().getDate().getDayId(), 18 * 60);
								}
								if (best != null) {
									int start = best.getStart();
									int end = start + duration;

									Treatment treatment = new Treatment(patient);
									Activity treatmentActivity = new Activity(best.getBlock(), start, end,
											ActivityType.Treatment, treatment);
									best.insert(treatmentActivity);

									Activity treatmentActivityPatient = treatmentActivity.clone();
									Activity free = patient.getSchedule().findFreeActivityToInsertOtherActivity(
											best.getDate().getWeekId(), best.getDate().getDayId(), start, end);
									free.insert(treatmentActivityPatient);
									patient.getPlannedStepsTreatments().add(best);
								}
							}
						}
					}
				}
			}	
		}
	}

	class EndOfSim extends Event {
		public void actions() {
			Sim.stop();
		}
	}

}
