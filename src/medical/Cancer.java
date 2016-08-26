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
	private static ArrayList<Cancer> cancers = new ArrayList<>();
	public static RandomVariateGen genCancerUnif =new UniformGen(new MRG32k3a(),0,1);
	
	Cancer (int id, String name, double percentage){
		this.setId(id);
		this.setName(name);
		this.setPercentage(percentage);
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
		int length = cancers.size();
		int index = (int) (genCancerUnif.nextDouble()*length);
		Cancer cancer = getCancer(index);
		return cancer;
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
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			strings = TextDataReader.readStringData(reader);
			String regex="\t"; //regex for the tab character
			for (String string : strings) {
				String[] cancerInfo = string.split(regex);
				int id = Integer.parseInt(cancerInfo[0]);
				double percentage = Double.parseDouble(cancerInfo[2]);
				String name = cancerInfo[1];
				Cancer cancer = new Cancer(id, name, percentage);
				Cancer.getCancers().add(cancer);
			}
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	public String toString(){
		return this.getName();
	}
}
