package events;

import medical.Center;
import umontreal.iro.lecuyer.simevents.Event;

public class DayEvent extends Event{
	private Center center;
	public static int nbReferredPatient=0;
	
	public DayEvent(Center center) {
		super();
		this.center = center;
	}
	
	@Override
	public void actions() {
		System.out.println("\nDayStart");
		System.out.println("nb of patients of previous day : "+nbReferredPatient);
		nbReferredPatient=0;
		getCenter().doScheduleToday();
		new DayEvent(this.getCenter()).schedule(24*60);
		new Opening(this.getCenter()).schedule(this.getCenter().getOpeningTime());
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

}
