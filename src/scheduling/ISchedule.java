package scheduling;

/**
 *	This interface is implemented by the resources that have a schedule
 * @author Joffrey
 */
public interface ISchedule {
	public Week addWeek(int weekId);
}
