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
import events.DayEvent;
import events.Opening;
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
import umontreal.iro.lecuyer.charts.HistogramChart;
import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.simevents.Event;
import umontreal.iro.lecuyer.simevents.LinkedListStat;
import umontreal.iro.lecuyer.simevents.Sim;

/**
 * Main class of the program, contains the elements for the simulation of the flow of patients
 * @author Joffrey
 */
public class FlowOfPatients {
	/**
	 * Medical {@link Center}
	 */
	public Center center;
	/**
	 * example of patient displayed at the end of the simulation in the console
	 */
	public static Patient test1=null;
	/**
	 * example of patient displayed at the end of the simulation in the console
	 */
	public static Patient test2=null;
	
	public FlowOfPatients() {
		super();
		this.center = new Center();
	}

	/**
	 * only for tests for now
	 * check this link to understand the generation of patient's arrivals : https://www.wolframalpha.com/input/?i=inverse+poisson+distribution+mu+%3D+6.218
	 * @param args
	 */
	public static void main(String[] args) {
		Cancer.cancersFileReader();
		Cancer.distNbTreatmentsFileReader();
		Center center = Center.centerFileReader();
		ArrayList<Doctor> doctors = Doctor.doctorsFileReader(center);
		double time = System.currentTimeMillis();
		FlowOfPatients test = new FlowOfPatients();
		test.setCenter(test.CHUM());
//		test.simulateOneRun(525600*2);
//		test.simulateOneRun(525600);
		test.simulateOneRun(288000);
//		test.simulateOneRun(100000);
//		test.simulateOneRun(30000);
//		test.simulateOneRun(72*60);
		
		time = System.currentTimeMillis()-time;
		
		int nbPatientTreated = test.getCenter().getPatientsOut().size();
		int nbPatientNotYetTreated = test.getCenter().getPatients().size();
		System.out.println("\nNumber of patient treated : "+nbPatientTreated+", out of "+(nbPatientNotYetTreated+nbPatientTreated)+" referred");
		if(test1!=null){
			System.out.println("\n"+test1);
		}
		if(test2!=null){
			System.out.println("\n"+test2);
		}
		System.out.println("\n"+test.getCenter().getPatients().statSize().report());
		System.out.println(test.getCenter().getPatients().statSojourn().report());
		System.out.println("Statistics, nb of patients for planif : "+Statistics.getNbPatientsForPlanif()+", planned on time : "+Statistics.getPlannedOnTime()+", and late : "+Statistics.getPlannedLate());
		System.out.println("running time : "+time);
//		Statistics.displayHistograms();
//		Statistics.displayCharts();
//		Statistics.cumulatedRepartitionDelays();
	}

	/**
	 * Simulates one run of the simulation
	 * @param timeHorizon
	 */
	public void simulateOneRun(double timeHorizon) {
		Sim.init();
		new EndOfSim().schedule(timeHorizon);
		new DayEvent(this.getCenter()).schedule(0);
//		new Opening(this.getCenter()).schedule(this.getCenter().getOpeningTime());
		Sim.start();
	}
	
	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	/**
	 * 
	 * @return an instance of Center modeling the Centre Hospitalier de l'Université de Montréal or CHUM for short.
	 */
	public Center CHUM(){
		Center chum = new Center();
		for (Cancer cancer : Cancer.getCancers()) {
			Sphere sphere = new Sphere(chum,cancer, null, new ArrayList<Doctor>(), new ArrayList<Patient>());
			chum.getSpheres().add(sphere);
			
			ArrayList<ArrayList<Block>>blocksTab = new ArrayList<ArrayList<Block>>(7);
			for(int i=0;i<7;i++){
				ArrayList<Block> blocks = new ArrayList<>();
				blocks.add(new Block(0, 7*60, 8*60-1, BlockType.OverTime));
				blocks.add(new Block(1, 8*60, 17*60-1, BlockType.Consultation));
				Block contouringBlock = new Block(2, 17*60, 19*60-1, BlockType.Contouring);
				Activity contouringActivity = contouringBlock.getActivities().get(0);
				contouringActivity.setType(ActivityType.Contouring);
				contouringActivity.setActivityEvent(new Contouring());
				blocks.add(contouringBlock);
				Block treatmentPlanBlock = new Block(3, 19*60, 20*60-1, BlockType.TreatmentPlan);
				Activity treatmentPlanActivity = treatmentPlanBlock.getActivities().get(0);
				treatmentPlanActivity.setType(ActivityType.TreatmentPlan);
				treatmentPlanActivity.setActivityEvent(new TreatmentPlan());
				blocks.add(treatmentPlanBlock);
				blocks.add(new Block(4, 20*60, 24*60-1, BlockType.OverTime));
				blocksTab.add(blocks);
			}
			ArrayList<Sphere> sphereDoctor = new ArrayList<>();
			sphereDoctor.add(sphere);
			Doctor doc = new Doctor(blocksTab, sphereDoctor);
			sphere.getDoctors().add(doc);
			chum.getDoctors().add(doc);
			
			ChefSphere chef = new ChefSphere(sphere);
			sphere.setChefSphere(chef);
		}
		
		int nbTreatmentMachines=30;
		for(int i=0 ; i<nbTreatmentMachines ; i++){
			ArrayList<ArrayList<Block>>blocksTab = new ArrayList<ArrayList<Block>>(7);
			for(int j=0;j<7;j++){
				ArrayList<Block> blocks = new ArrayList<>();
				blocks.add(new Block(0, 7*60, 16*60-1, BlockType.Treatment));
				blocks.add(new Block(1, 16*60, 18*60-1, BlockType.Reserved));
				blocksTab.add(blocks);
			}
			ArrayList <TreatmentTechnic> treatmentTechnics = new ArrayList<>();
			treatmentTechnics.addAll(new ArrayList<TreatmentTechnic>(Arrays.asList(TreatmentTechnic.values())));
			chum.getTreatmentMachines().add(new TreatmentMachine(chum, treatmentTechnics, blocksTab));
		}
		
		ScanTechnic[] scanTechnics = ScanTechnic.values();
		for (ScanTechnic scanTechnic : scanTechnics) {
			for(int j=0;j<4;j++){
				ArrayList<ArrayList<Block>>blocksTab = new ArrayList<ArrayList<Block>>(7);
				for(int i=0;i<7;i++){
					ArrayList<Block> blocks = new ArrayList<>();
					blocks.add(new Block(0, 8*60, 15*60-1, BlockType.Scan));
					blocks.add(new Block(1, 15*60, 17*60-1, BlockType.Reserved));
					blocksTab.add(blocks);
				}
				chum.getCtscans().add(new Scan(chum, true, scanTechnic, blocksTab));
			}
		}
		
		int nbDosimetrists = 2;
		for(int i=0 ; i<nbDosimetrists ; i++){
			ArrayList<ArrayList<Block>>blocksTab = new ArrayList<ArrayList<Block>>(7);
			for(int j=0;j<7;j++){
				ArrayList<Block> blocks = new ArrayList<>();
				Block dosiBlock = new Block(0, 8*60, 17*60-1, BlockType.Dosimetry);
				Activity dosiActivity = dosiBlock.getActivities().get(0);
				dosiActivity.setType(ActivityType.Dosimetry);
				dosiActivity.setActivityEvent(new CalculDosi());
				blocks.add(dosiBlock);
				blocksTab.add(blocks);
			}
			Dosimetrist dosi = new Dosimetrist(chum, blocksTab);
			chum.getDosimetrists().add(dosi);
		}
		
		chum.setAdminAgent(new AdminAgent(chum));
		chum.setTechnologist(new Technologist(chum));
		return chum;
	}
	
	/**
	 * Common event class for a simulation corresponding to the end of the simulation.
	 * @author Joffrey
	 *
	 */
	class EndOfSim extends Event {
		public void actions() {
			Sim.stop();
		}
	}

}
