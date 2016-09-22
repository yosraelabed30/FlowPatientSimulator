package medical;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.randvar.UniformGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.util.TextDataReader;

public class Cancer {
	private int id;
	private String name;
	private double percentage;
	private double cumulatedPercentage;
	private ArrayList<int[]> nbTreatments;
	private int nbOfCases;
	private static ArrayList<Cancer> cancers = new ArrayList<>();
	public static RandomVariateGen genCancerUnif =new UniformGen(new MRG32k3a(),0,100);
	
	Cancer (int id, String name, double percentage, double cumulatedPercentage){
		this.setId(id);
		this.setName(name);
		this.setPercentage(percentage);
		this.cumulatedPercentage=cumulatedPercentage;
		this.nbTreatments = new ArrayList<>();
		this.nbOfCases=0;
	}
	
	public static Cancer getCancer(int id){
		for (Cancer cancer : cancers) {
			if (cancer.getId()==id){
				return cancer;
			}
		}
		return null;
	}
	
	public static Cancer generateCancer(){
		Cancer cancerGen = null;
		double rnd = genCancerUnif.nextDouble();
		for (Cancer cancer : cancers) {
			if(rnd<cancer.cumulatedPercentage){
				cancerGen=cancer;
				break;
			}
		}
		cancerGen.nbOfCases++;
		return cancerGen;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public static ArrayList<Cancer> getCancers() {
		return cancers;
	}

	public static void setCancers(ArrayList<Cancer> cancers) {
		Cancer.cancers = cancers;
	}

	public static void cancersFileReader(){
		String[] strings = null;
		Path path = FileSystems.getDefault().getPath("Cancers.txt");
		Charset charset = Charset.forName("US-ASCII");
		double cumul = 0;
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			strings = TextDataReader.readStringData(reader);
			String regex="\t"; //regex for the tab character
			for (String string : strings) {
				String[] cancerInfo = string.split(regex);
				int id = Integer.parseInt(cancerInfo[0]);
				double percentage = Double.parseDouble(cancerInfo[2]);
				String name = cancerInfo[1];
				cumul+=percentage;
				Cancer cancer = new Cancer(id, name, percentage, cumul);
				Cancer.getCancers().add(cancer);
			}
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	public static void distNbTreatmentsFileReader(){
		String[] strings = null;
		Path path = FileSystems.getDefault().getPath("NumberOfTreatments.txt");
		Charset charset = Charset.forName("US-ASCII");
		int cumul = 0;
		Cancer cancer = null;
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			strings = TextDataReader.readStringData(reader);
			String regex="\t"; //regex for the tab character
			for (String string : strings) {
				if(string.length()==1){
					cancer = getCancer(Integer.parseInt(string));
				}
				else if (string.isEmpty()){
					cancer=null;
					cumul=0;
				}
				else{
					String[] cancerInfo = string.split(regex);
					int percentage = Integer.parseInt(cancerInfo[0]);
					int nbTreatments = Integer.parseInt(cancerInfo[1]);
					cumul += percentage;
					cancer.nbTreatments.add(new int[]{cumul,nbTreatments});
				}
			}
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	public String toString(){
		return this.getName();
	}

	public ArrayList<int[]> getNbTreatments() {
		return nbTreatments;
	}

	public void setNbTreatments(ArrayList<int[]> nbTreatments) {
		this.nbTreatments = nbTreatments;
	}

	public int getNbOfCases() {
		return nbOfCases;
	}

	public void setNbOfCases(int nbOfCases) {
		this.nbOfCases = nbOfCases;
	}

	public static Cancer get(String name) {
		for (Cancer cancer : cancers) {
			if(cancer.getName().equalsIgnoreCase(name)){
				return cancer;
			}
		}
		return null;
	}

}
