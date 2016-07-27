package medical;



public class SphereElement extends CenterElement {
	private Sphere sphere;

	public SphereElement( Sphere sphere) {
		super();
		this.setSphere(sphere);

	}

	public Sphere getSphere() {
		return sphere;
	}

	public void setSphere(Sphere sphere) {
		this.sphere = sphere;
	}


}
