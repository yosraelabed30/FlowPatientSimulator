package medical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import fileComparators.FileComparator1;

public class AdminAgent{
	/**
	 * demands received by the administrative agent, those are not urgent, the urgent ones are sent directly to the chef de sphere
	 */
	LinkedList<Patient> demands;
	private Center center;
	
	public AdminAgent(Center center) {
		super();
		this.demands = new LinkedList<>();
		this.setCenter(center);
	}

	public void addToDemands(Patient patient) {
		this.demands.add(patient);
	}

	public void sortDemands() {
		Collections.sort(demands, new FileComparator1());
	}

	public void sendDemands(ArrayList<ChefSphere> chefSpheres) {
		Patient demand = demands.pollFirst();
		while(demand!=null){
			for (ChefSphere chef : chefSpheres) {
				if(chef.getSphere().getCancer()==demand.getCancer()){
					chef.addToDemands(demand);
					break;
				}
			}
			demand = demands.pollFirst();
		}
	}

	public void processDemands() {
		this.sortDemands();
		this.sendDemands(getCenter().getChefSpheres());
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}
	
}
