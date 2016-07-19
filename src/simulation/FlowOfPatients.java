package simulation;

import java.util.ArrayList;

import medical.AdminAgent;
import medical.Center;
import medical.ChefSphere;
import medical.Doctor;
import medical.ScanTechnic;
import medical.Scan;
import medical.Technologist;
import medical.TreatmentMachine;
import medical.TreatmentTechnic;
import events.ReferredPatient;
import scheduling.Block;
import scheduling.BlockType;
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

		ArrayList<ArrayList<Block>>blocksTab1 = new ArrayList<ArrayList<Block>>(7);
		ArrayList<ArrayList<Block>>blocksTab2 = new ArrayList<ArrayList<Block>>(7);
		for(int i=0;i<7;i++){
			ArrayList<Block> blocks1 = new ArrayList<>();
			ArrayList<Block> blocks2 = new ArrayList<>();
			blocks1.add(new Block(0, 0, 8*60, BlockType.NotWorking));
			blocks1.add(new Block(1, 8*60, 17*60, BlockType.Consultation));
			blocks1.add(new Block(2, 17*60+1, 24*60-1, BlockType.NotWorking));
			blocks2.add(new Block(0, 0, 8*60, BlockType.NotWorking));
			blocks2.add(new Block(1, 8*60, 17*60, BlockType.Consultation));
			blocks2.add(new Block(2, 17*60+1, 24*60-1, BlockType.NotWorking));
			blocksTab1.add(blocks1);
			blocksTab2.add(blocks2);
		}
		Doctor doc1 = new Doctor(test.getCenter(), blocksTab1);
		test.getCenter().getDoctors().add(doc1);
		Doctor doc2 = new Doctor(test.getCenter(), blocksTab2);
		test.getCenter().getDoctors().add(doc2);
		
		ArrayList<ArrayList<Block>>blocksTab3 = new ArrayList<ArrayList<Block>>(7);
		ArrayList<ArrayList<Block>>blocksTab4 = new ArrayList<ArrayList<Block>>(7);
		for(int i=0;i<7;i++){
			ArrayList<Block> blocks1 = new ArrayList<>();
			ArrayList<Block> blocks2 = new ArrayList<>();
			blocks1.add(new Block(0, 7*60, 18*60, BlockType.Treatment));
			blocks2.add(new Block(0, 7*60, 18*60, BlockType.Treatment));
			blocksTab3.add(blocks1);
			blocksTab4.add(blocks2);
		}
		test.getCenter().getTreatmentMachines().add(new TreatmentMachine(test.getCenter(), TreatmentTechnic.technic1, blocksTab3));
		test.getCenter().getTreatmentMachines().add(new TreatmentMachine(test.getCenter(), TreatmentTechnic.technic2, blocksTab4));
		
		ArrayList<ArrayList<Block>>blocksTab5 = new ArrayList<ArrayList<Block>>(7);
		ArrayList<ArrayList<Block>>blocksTab6 = new ArrayList<ArrayList<Block>>(7);
		for(int i=0;i<7;i++){
			ArrayList<Block> blocks1 = new ArrayList<>();
			ArrayList<Block> blocks2 = new ArrayList<>();
			blocks1.add(new Block(0, 8*60, 17*60, BlockType.Scan));
			blocks2.add(new Block(0, 8*60, 17*60, BlockType.Scan));
			blocksTab5.add(blocks1);
			blocksTab6.add(blocks2);
		}
		test.getCenter().getCtscans().add(new Scan(test.getCenter(), true, ScanTechnic.CTScan3D, blocksTab5));
		test.getCenter().getCtscans().add(new Scan(test.getCenter(), true, ScanTechnic.CTScan4D, blocksTab6));
		
		test.getCenter().setAdminAgent(new AdminAgent(test.getCenter()));
		test.getCenter().setTechnologist(new Technologist(test.getCenter()));
		
		ArrayList<Integer> specialitiesChefSphere = new ArrayList<Integer>();
		for(int i=0;i<10;i++){
			specialitiesChefSphere.add(i);
		}
		test.getCenter().getChefSpheres().add(new ChefSphere(test.getCenter(), test.getCenter().getDoctors(), specialitiesChefSphere));
		
//		test.simulateOneRun(525600*2);
//		test.simulateOneRun(525600);
//		test.simulateOneRun(288000);
//		test.simulateOneRun(100000);
//		test.simulateOneRun(30000);
		test.simulateOneRun(72*60);
		
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
			
			System.out.println("\nDayStart");
			System.out.println("New day in minutes : "+time+" it's a "+dayOfWeek+", of week "+week+" and it is day "+day);
			
			new DayEnd().schedule(10*60);
			new ReferredPatient(genReferredPatient, getCenter()).schedule ((int)(genReferredPatient.nextDouble()*60));
			getCenter().doScheduleToday();
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
			new DayStart().schedule(14*60);
			
			getCenter().getAdminAgent().processDemands();
			for (ChefSphere chef : getCenter().getChefSpheres()) {
				chef.processDemands();
			}
			center.getTechnologist().processPatientFilesForPlanification();
			
			getCenter().fromPatientsToPatientsOut();
			
		}
		
	}
	
	class EndOfSim extends Event {
		public void actions() {
			Sim.stop();
		}
	}

}
