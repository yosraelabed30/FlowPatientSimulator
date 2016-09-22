package simulation;

import java.awt.Color;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import umontreal.iro.lecuyer.charts.HistogramChart;
import umontreal.iro.lecuyer.charts.HistogramSeriesCollection;
import umontreal.iro.lecuyer.charts.ScatterChart;
import umontreal.iro.lecuyer.charts.XYChart;
import umontreal.iro.lecuyer.charts.XYLineChart;
import umontreal.iro.lecuyer.charts.XYListSeriesCollection;
import umontreal.iro.lecuyer.stat.TallyStore;

public final class Statistics {
	private static int nbPatientsForPlanif=0;
	private static int plannedOnTime=0;
	private static int plannedLate=0;
	
	private static TallyStore delayConsultFTreat = new TallyStore("delays between the consultation and the first treatment");
	private static TallyStore timesDelayConsultFTreat= new TallyStore("times of acquisitions of delays");
	private static TallyStore delayP1ConsultFTreat = new TallyStore("delays between the consultation and the first treatment for P1");
	private static TallyStore timesDelayP1ConsultFTreat= new TallyStore();
	private static TallyStore delayP2ConsultFTreat = new TallyStore("delays between the consultation and the first treatment for P2");
	private static TallyStore timesDelayP2ConsultFTreat= new TallyStore();
	private static TallyStore delayP3ConsultFTreat = new TallyStore("delays between the consultation and the first treatment for P3");
	private static TallyStore timesDelayP3ConsultFTreat= new TallyStore();
	private static TallyStore delayP4ConsultFTreat = new TallyStore("delays between the consultation and the first treatment for P4");
	private static TallyStore timesDelayP4ConsultFTreat= new TallyStore();
	
	private static TallyStore delayRefFTreat = new TallyStore("delays between the referring and the first treatment");
	private static TallyStore timesDelayRefFTreat= new TallyStore();
	private static TallyStore delayP1RefFTreat = new TallyStore("delays between the referring and the first treatment for P1");
	private static TallyStore timesDelayP1RefFTreat= new TallyStore();
	private static TallyStore delayP2RefFTreat = new TallyStore("delays between the referring and the first treatment for P2");
	private static TallyStore timesDelayP2RefFTreat= new TallyStore();
	private static TallyStore delayP3RefFTreat = new TallyStore("delays between the referring and the first treatment for P3");
	private static TallyStore timesDelayP3RefFTreat= new TallyStore();
	private static TallyStore delayP4RefFTreat = new TallyStore("delays between the referring and the first treatment for P4");
	private static TallyStore timesDelayP4RefFTreat= new TallyStore();
	
	public static void displayHistograms(){
//		HistogramChart chart = new HistogramChart("test", "Nb of days between consultation and first treatment", "Nb of patients", getDelayP1ConsultFTreat());
		HistogramChart chart = new HistogramChart("test", "Nb of days between consultation and first treatment", "Nb of patients", getDelayConsultFTreat(), getDelayP1ConsultFTreat(), getDelayP2ConsultFTreat(), getDelayP3ConsultFTreat(), getDelayP4ConsultFTreat());
		// Customizes the data plots
		chart.view(500, 500);
//		chart.toLatexFile("histogramDelays", 5, 5);
		
		HistogramChart chart2 = new HistogramChart("test", "Nb of days between referring and first treatment", "Nb of patients", getDelayRefFTreat(), getDelayP2RefFTreat(), getDelayP3RefFTreat(), getDelayP4RefFTreat());
//		HistogramChart chart2 = new HistogramChart("test", "Nb of days between referring and first treatment", "Nb of patients", getDelayRefFTreat(), getDelayP1RefFTreat(), getDelayP2RefFTreat(), getDelayP3RefFTreat(), getDelayP4RefFTreat());
		chart2.view(500, 500);
//		chart.toLatexFile("histogramDelays", 5, 5);
	}
	
	public static double[][] evolutionOfDelays(TallyStore times, TallyStore values){
		int size = times.numberObs();
		double[][] result = new double[2][size];
		int ct=0;
		for (double time : times.getArray()) {
			result[0][ct] = time;
			ct++;
		}
		ct=0;
		for (double value : values.getArray()) {
			result[1][ct] = value;
			ct++;
		}
		return result;
	}
	
	public static void displayCharts(){
		int size = getDelayConsultFTreat().numberObs();
		double[][] delaysAndTimesConsultFTreat = evolutionOfDelays(timesDelayConsultFTreat, delayConsultFTreat);
		double[][] delaysAndTimesRefFTreat = evolutionOfDelays(timesDelayRefFTreat, delayRefFTreat);
		double[][] delaysAndTimesP1ConsultFTreat = evolutionOfDelays(timesDelayP1ConsultFTreat, delayP1ConsultFTreat);
		double[][] delaysAndTimesP1RefFTreat = evolutionOfDelays(timesDelayP1RefFTreat, delayP1RefFTreat);
		double[][] delaysAndTimesP2ConsultFTreat = evolutionOfDelays(timesDelayP2ConsultFTreat, delayP2ConsultFTreat);
		double[][] delaysAndTimesP2RefFTreat = evolutionOfDelays(timesDelayP2RefFTreat, delayP2RefFTreat);
		double[][] delaysAndTimesP3ConsultFTreat = evolutionOfDelays(timesDelayP3ConsultFTreat, delayP3ConsultFTreat);
		double[][] delaysAndTimesP3RefFTreat = evolutionOfDelays(timesDelayP3RefFTreat, delayP3RefFTreat);
		double[][] delaysAndTimesP4ConsultFTreat = evolutionOfDelays(timesDelayP4ConsultFTreat, delayP4ConsultFTreat);
		double[][] delaysAndTimesP4RefFTreat = evolutionOfDelays(timesDelayP4RefFTreat, delayP4RefFTreat);

		ScatterChart chart = new ScatterChart("delays consult-firstTreatment per day of firstTreatment", "day", "delay", delaysAndTimesP1ConsultFTreat, delaysAndTimesP2ConsultFTreat, delaysAndTimesP3ConsultFTreat, delaysAndTimesP4ConsultFTreat);
		chart.view(500, 500);
		ScatterChart chart2 = new ScatterChart("delays ref-firstTreatment per day of firstTreatment", "day", "delay", delaysAndTimesP1RefFTreat, delaysAndTimesP2RefFTreat, delaysAndTimesP3RefFTreat, delaysAndTimesP4RefFTreat);
		chart2.view(500, 500);
	}
	
	public static void cumulatedRepartitionDelays(){
		delayConsultFTreat.quickSort();
		double max = delayConsultFTreat.max();
		double min = delayConsultFTreat.min();
		int size = (int)(max-min+1);
		double[][] delays = new double[2][size];
		for(double delay : delayConsultFTreat.getArray()){
			delays[1][(int)(delay-min)]++;
		}
		for(int i=0;i<size;i++){
			delays[0][i]=i;
			if(i==0){
				delays[1][i] = delays[1][i]/delayConsultFTreat.numberObs();
			}
			else{
				delays[1][i]= (delays[1][i]/delayConsultFTreat.numberObs())+delays[1][i-1];
			}
		}
		XYLineChart chart1 = new XYLineChart("Cumulated delays", "days", "cumulated % of delays", delays);
		XYListSeriesCollection collec = chart1.getSeriesCollection();
//		collec.setPlotStyle(0, "ycomb"); //Does not work for obscure reasons
		collec.setMarksType(0, "*");
		chart1.view(500, 500);
		chart1.toLatexFile("cumulatedRepartition", 5, 5);

	}
	
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

	public static TallyStore getDelayConsultFTreat() {
		return delayConsultFTreat;
	}

	public static void setDelayConsultFTreat(TallyStore delayConsultFTreat) {
		Statistics.delayConsultFTreat = delayConsultFTreat;
	}


	public static TallyStore getDelayP1ConsultFTreat() {
		return delayP1ConsultFTreat;
	}

	public static void setDelayP1ConsultFTreat(TallyStore delayP1ConsultFTreat) {
		Statistics.delayP1ConsultFTreat = delayP1ConsultFTreat;
	}

	public static TallyStore getDelayP2ConsultFTreat() {
		return delayP2ConsultFTreat;
	}

	public static void setDelayP2ConsultFTreat(TallyStore delayP2ConsultFTreat) {
		Statistics.delayP2ConsultFTreat = delayP2ConsultFTreat;
	}

	public static TallyStore getDelayP3ConsultFTreat() {
		return delayP3ConsultFTreat;
	}

	public static void setDelayP3ConsultFTreat(TallyStore delayP3ConsultFTreat) {
		Statistics.delayP3ConsultFTreat = delayP3ConsultFTreat;
	}

	public static TallyStore getDelayP4ConsultFTreat() {
		return delayP4ConsultFTreat;
	}

	public static void setDelayP4ConsultFTreat(TallyStore delayP4ConsultFTreat) {
		Statistics.delayP4ConsultFTreat = delayP4ConsultFTreat;
	}

	public static TallyStore getDelayRefFTreat() {
		return delayRefFTreat;
	}

	public static void setDelayRefFTreat(TallyStore delayRefFTreat) {
		Statistics.delayRefFTreat = delayRefFTreat;
	}

	public static TallyStore getDelayP1RefFTreat() {
		return delayP1RefFTreat;
	}

	public static void setDelayP1RefFTreat(TallyStore delayP1RefFTreat) {
		Statistics.delayP1RefFTreat = delayP1RefFTreat;
	}

	public static TallyStore getDelayP2RefFTreat() {
		return delayP2RefFTreat;
	}

	public static void setDelayP2RefFTreat(TallyStore delayP2RefFTreat) {
		Statistics.delayP2RefFTreat = delayP2RefFTreat;
	}

	public static TallyStore getDelayP3RefFTreat() {
		return delayP3RefFTreat;
	}

	public static void setDelayP3RefFTreat(TallyStore delayP3RefFTreat) {
		Statistics.delayP3RefFTreat = delayP3RefFTreat;
	}

	public static TallyStore getDelayP4RefFTreat() {
		return delayP4RefFTreat;
	}

	public static void setDelayP4RefFTreat(TallyStore delayP4RefFTreat) {
		Statistics.delayP4RefFTreat = delayP4RefFTreat;
	}

	public static TallyStore getTimesDelayConsultFTreat() {
		return timesDelayConsultFTreat;
	}

	public static void setTimesDelayConsultFTreat(TallyStore timesDelay) {
		Statistics.timesDelayConsultFTreat = timesDelay;
	}

	public static TallyStore getTimesDelayP1ConsultFTreat() {
		return timesDelayP1ConsultFTreat;
	}

	public static void setTimesDelayP1ConsultFTreat(
			TallyStore timesDelayP1ConsultFTreat) {
		Statistics.timesDelayP1ConsultFTreat = timesDelayP1ConsultFTreat;
	}

	public static TallyStore getTimesDelayP2ConsultFTreat() {
		return timesDelayP2ConsultFTreat;
	}

	public static void setTimesDelayP2ConsultFTreat(
			TallyStore timesDelayP2ConsultFTreat) {
		Statistics.timesDelayP2ConsultFTreat = timesDelayP2ConsultFTreat;
	}

	public static TallyStore getTimesDelayP3ConsultFTreat() {
		return timesDelayP3ConsultFTreat;
	}

	public static void setTimesDelayP3ConsultFTreat(
			TallyStore timesDelayP3ConsultFTreat) {
		Statistics.timesDelayP3ConsultFTreat = timesDelayP3ConsultFTreat;
	}

	public static TallyStore getTimesDelayP4ConsultFTreat() {
		return timesDelayP4ConsultFTreat;
	}

	public static void setTimesDelayP4ConsultFTreat(
			TallyStore timesDelayP4ConsultFTreat) {
		Statistics.timesDelayP4ConsultFTreat = timesDelayP4ConsultFTreat;
	}

	public static TallyStore getTimesDelayRefFTreat() {
		return timesDelayRefFTreat;
	}

	public static void setTimesDelayRefFTreat(TallyStore timesDelayRefFTreat) {
		Statistics.timesDelayRefFTreat = timesDelayRefFTreat;
	}

	public static TallyStore getTimesDelayP1RefFTreat() {
		return timesDelayP1RefFTreat;
	}

	public static void setTimesDelayP1RefFTreat(TallyStore timesDelayP1RefFTreat) {
		Statistics.timesDelayP1RefFTreat = timesDelayP1RefFTreat;
	}

	public static TallyStore getTimesDelayP2RefFTreat() {
		return timesDelayP2RefFTreat;
	}

	public static void setTimesDelayP2RefFTreat(TallyStore timesDelayP2RefFTreat) {
		Statistics.timesDelayP2RefFTreat = timesDelayP2RefFTreat;
	}

	public static TallyStore getTimesDelayP3RefFTreat() {
		return timesDelayP3RefFTreat;
	}

	public static void setTimesDelayP3RefFTreat(TallyStore timesDelayP3RefFTreat) {
		Statistics.timesDelayP3RefFTreat = timesDelayP3RefFTreat;
	}

	public static TallyStore getTimesDelayP4RefFTreat() {
		return timesDelayP4RefFTreat;
	}

	public static void setTimesDelayP4RefFTreat(TallyStore timesDelayP4RefFTreat) {
		Statistics.timesDelayP4RefFTreat = timesDelayP4RefFTreat;
	}



}
