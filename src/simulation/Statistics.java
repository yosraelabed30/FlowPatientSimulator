package simulation;

public final class Statistics {
	private static int nbPatientsForPlanif=0;
	private static int plannedOnTime=0;
	private static int plannedLate=0;

	public static void increaseNbPatientsForPlanif(){
		Statistics.nbPatientsForPlanif++;
	}
	
	public static void increasePlannedOnTime(){
		Statistics.plannedOnTime++;
	}
	
	public static void increasePlannedLate(){
		Statistics.plannedLate++;
	}
	
	public static int getNbPatientsForPlanif() {
		return nbPatientsForPlanif;
	}
	public static void setNbPatientsForPlanif(int nbPatientsForPlanif) {
		Statistics.nbPatientsForPlanif = nbPatientsForPlanif;
	}
	public static int getPlannedOnTime() {
		return plannedOnTime;
	}
	public static void setPlannedOnTime(int plannedOnTime) {
		Statistics.plannedOnTime = plannedOnTime;
	}
	public static int getPlannedLate() {
		return plannedLate;
	}
	public static void setPlannedLate(int plannedLate) {
		Statistics.plannedLate = plannedLate;
	}

}
