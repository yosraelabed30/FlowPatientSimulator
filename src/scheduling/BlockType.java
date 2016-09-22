package scheduling;

public enum BlockType {
	OverTime("OverTime"),
	Consultation("Consultation"),
	Relance("Relance"),
	Contouring("Contouring"),
	TreatmentPlan("TreatmentPlan"),
    Reserved("Reserved"),
    Dosimetry("Dosimetry"),
    Treatment("Treatment"),
	All("All"), 	
	Scan("Scan");
	
	String name;

	private BlockType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static BlockType get(String name){
		for (BlockType blockType : BlockType.values()) {
			if(blockType.getName().equalsIgnoreCase(name)){
				return blockType;
			}
		}
		return null;
	}
}
