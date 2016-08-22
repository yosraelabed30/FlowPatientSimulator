package events;

import java.util.ArrayList;

import medical.Center;
import medical.ChefSphere;
import medical.Doctor;
import medical.Patient;
import medical.Priority;
import medical.TreatmentMachine;
import medical.TreatmentTechnic;
import scheduling.Activity;
import scheduling.ActivityType;
import scheduling.Availability;
import scheduling.BlockType;
import scheduling.Date;
import tools.Time;
import umontreal.iro.lecuyer.simevents.Event;
import umontreal.iro.lecuyer.simevents.LinkedListStat;

public class Closing extends Event{
	private Center center;
	
	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	public Closing(Center center) {
		super();
		this.setCenter(center);
	}

	public void actions() {
		getCenter().setWelcome(false);
		int delay = this.getCenter().getClosingTime()-this.getCenter().getOpeningTime();
		new Opening(this.getCenter()).schedule(delay);

		getCenter().getAdminAgent().processDemands();
		for (ChefSphere chef : getCenter().getChefSpheres()) {
			chef.processDemands();
		}
		getCenter().getTechnologist().processPatientFilesForPlanification();
		
//		isReadyForTheTreatment(2);
//		isReadyForTheTreatment(1);

	}

	public void isReadyForTheTreatment(int counter) {

		LinkedListStat<Patient> patients = getCenter().getPatients();
		for (Patient patient : patients) {
			if((patient.getPriority()==Priority.P3 || patient.getPriority()==Priority.P4) && patient.getPlannedStepsTreatments().size()>0){
				int duration = 45;
				Date now = Date.now();
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
						Availability best = treatmentMachine.getSchedule()
								.findAvailability(newLastTreatment, duration);

						if (best != null) {
							Treatment treatment = new Treatment(patient);
							Activity treatmentActivity = new Activity(best.getStart(), duration,
									ActivityType.Treatment, treatment);
							best.getActivity().insert(treatmentActivity);

							Activity treatmentActivityPatient = treatmentActivity.clone();
							Availability free = patient.getSchedule().findAvailability(
									best.getDate(), duration);
							free.getActivity().insert(treatmentActivityPatient);
							patient.getPlannedStepsTreatments().add(treatmentActivity);
						}
						else {
							TreatmentTechnic treatmentTechnic = patient.getTreatmentTechnic();
							ArrayList<TreatmentMachine> treatmentMachines = getCenter().getTreatmentMachines();
							ArrayList<TreatmentMachine> adequateMachines = new ArrayList<>();
							for (TreatmentMachine treatmentMachine2 : treatmentMachines) {
								if (treatmentMachine2.getTreatmentTechnics().contains(treatmentTechnic)) {
									adequateMachines.add(treatmentMachine2);
								}
							}
							for (TreatmentMachine adequateMachine : adequateMachines) {
								Availability tmp = adequateMachine.getSchedule()
										.findAvailability(newLastTreatment, duration);
								if (best == null || tmp.compareTo(best)==-1) {
									best = tmp;
								}
							}
							if (best == null) {
								ArrayList<BlockType> blockTypes = new ArrayList<>();
								blockTypes.add(BlockType.Treatment);
								ArrayList<Integer> daysForbidden = new ArrayList<>();
								daysForbidden.add(5);
								daysForbidden.add(6);
								Date dateLowerBound = patient.getPlannedStepsTreatments().getFirst().getDate();
								Date dateUpperBound = dateLowerBound.clone();
								dateUpperBound.setMinute(24*60-1);
								best = treatmentMachine.getSchedule().findFirstAvailability(ActivityType.Treatment, duration, blockTypes, daysForbidden, dateLowerBound, dateUpperBound);
							}
							if (best != null) {
								Treatment treatment = new Treatment(patient);
								Activity treatmentActivity = new Activity(best.getStart(), duration,
										ActivityType.Treatment, treatment);
								best.getActivity().insert(treatmentActivity);

								Activity treatmentActivityPatient = treatmentActivity.clone();
								Availability free = patient.getSchedule().findAvailability(
										best.getDate(), duration);
								free.getActivity().insert(treatmentActivityPatient);
								patient.getPlannedStepsTreatments().add(treatmentActivityPatient);
							}
						}
					}
				}
			}
		}	
	}
}
