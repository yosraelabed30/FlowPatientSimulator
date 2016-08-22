package medical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.randvar.UniformGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomPermutation;
import umontreal.iro.lecuyer.rng.RandomStream;

public enum ScanTechnic {
	CTScan3D(0),
	CTScan4D(1),
	IRM(2),
	DEST3D(3),
	DEST4D(4),
	TEP(5);
	public static RandomStream rndStreamScanTech =new MRG32k3a();
	
	private int index ;
	private ScanTechnic( int index ) {
		this.setIndex(index) ;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public static ScanTechnic getScanTechnic(int index){
		return ScanTechnic.values()[index];
	}
	
	public static ArrayList<ScanTechnic> generateScanTechnic(int nbOfTechnics){
		ScanTechnic[] values =  ScanTechnic.values();
		RandomPermutation.shuffle(values, rndStreamScanTech);
		return new ArrayList<ScanTechnic> (Arrays.asList(values).subList(0, nbOfTechnics));
	}
}
