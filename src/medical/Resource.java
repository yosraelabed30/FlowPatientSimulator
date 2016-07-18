package medical;

/**
 * 
 * @author Joffrey
 *	A schedule is linked to a resource but a resource doesn't have to have a schedule
 *	Classes such as Doctor, adminAgent, ...,  and patient extend Resource (everything that is in a center really)
 *	Q : Should I add to the center a big list of Resource ? To get the resource easily with their id... Not useful for now.
 */
public class Resource {
	private Center center;
	
	public Resource(Center center) {
		super();
		this.center = center;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}
}
