package events;

import java.util.ArrayList;

import medical.Center;
import medical.ChefSphere;
import scheduling.Date;
import tools.Time;
import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.simevents.Event;
import static events.ReferredPatient.genReferredPatient;

/**
 * Opening of a center
 * @author Joffrey
 *
 */
public class Opening extends Event{
	private Center center;

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}
	
	public Opening(Center center) {
		super();
		this.setCenter(center);
	}
	/**
	 * the center opens and the patients are referred until closing.
	 */
	public void actions() {
		int time = Time.now();
		int dayOfWeek = Time.weekDayCorrespondingToTime(Time.now());
		int week = Time.weekCorrespondingToTime(time);
		int day = Time.dayCorrespondingToTime(time);
		if(dayOfWeek==0){
			getCenter().addWeek();
		}
		if(dayOfWeek==5 || dayOfWeek==6){
			getCenter().setWelcome(false);
		}
		else{
			getCenter().setWelcome(true);
		}
		Date date = Date.now();
		
		System.out.println("\nDayStart");
		System.out.println("New day in minutes : "+time+" it's a "+dayOfWeek+", of week "+week+" and it is day "+day);
		
		getCenter().getTechnologist().processPatientFilesForPreContouring();
		
		new Closing(this.getCenter()).schedule(10*60);
		new ReferredPatient(getCenter()).schedule ((int)(genReferredPatient.nextDouble()*60));
		getCenter().doScheduleToday();
		ArrayList <ChefSphere> chefSpheres= getCenter().getChefsSphere();
		
		if (date.getWeekId()!=0 || date.getDayId()!=0){
			for (ChefSphere chefSphere : chefSpheres) {
				chefSphere.NoShowConsultation(date);
//				System.out.println("le moment de replanification" +Date.dateNow() );
			}
		}
	}

}
