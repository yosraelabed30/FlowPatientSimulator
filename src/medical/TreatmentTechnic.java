package medical;

import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.randvar.UniformGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;

public enum TreatmentTechnic {
	TXUroRA(0),
	TXColonneCK(1),
	TXColonne3D(2),
	TXColonneIMRT(3),
	TXColonneRA(4),
	TXColonneSBRTRA(5),
	TXColonneSBRTTomo(6),
	TXCrane3D(7),
	TXCraneCK(8),
	TXCraneIMRT(9),
	TXCraneRA(10),
	TXCraneTomo(11),
	TXCranioSpin(12),
	TXCranioSpinAnesthésie(13),
	TXDigBasMargeRA(14),
	TXDigBasRectum3D(15),
	TXDigBasRectumIMRT(16),
	TXDigBasRectumRA(17),
	TXDigHautCKClips(18),
	TXDigHautCKMarq(19),
	TXDigHautIMRT(20),
	TXDigHautRA(21),
	TXDigHautRPM(22),
	TXDigHautSBRTRA(23),
	TXDigHautSBRTRPM(24),
	TXGeneral3D(25),
	TXGeneralCK(26),
	TXGeneralElectron(27),
	TXGeneralIMRT(28),
	TXGeneralRA(29),
	TXGeneralSBRT(30),
	TXGyn3D(31),
	TXGynIMRT(32),
	TXGynRA(33),
	TXGynLomboPelvRA(34),
	TXLoge3D(35),
	TXLogeCK(36),
	TXLogeIMRT(37),
	TXLympCou3D(38),
	TXLympIMRT(39),
	TXLympCouMed3D(40),
	TXLympCouMedIMRT(41),
	TXLympCouMedRA(42),
	TXLympCouMedTOMO(43),
	TXLympCouRA(44),
	TXLympCouTOMO(45),
	TXLympMed3D(46),
	TXLympMedIMRT(47),
	TXLympMedRA(48),
	TXLympMedRPM(49),
	TXMAVCK(50),
	TXMelanCK(51),
	TXOrl3D(52),
	TXOrlIMRT(53),
	TXOrlRA(54),
	TXOrlTOMO(55),
	TXOrthovoltage(56),
	TX07213D(57),
	TX0721IMRT(58),
	TX0721RA(59),
	TX2053D(60),
	TX205IMRT(61),
	TX205RA(62),
	TX30103D(63),
	TX3010IMRT(64),
	TX3010RA(65),
	TX8003D(66),
	TX800IMRT(67),
	TX800RA(68),
	TXPlaceReserve(69),
	TXPmn3D(70),
	TXPmnCKMarq(71),
	TXPmnXsl(72),
	TXPmnCKXss(73),
	TXPmnIMRT(74),
	TXPmnRABdf(75),
	TXPmnRABdfVac(76),
	TXPmnRPM(77),
	TXPmnSBRTRA(78),
	TXPmnSBRTTOMO(79),
	TXPmnTOMO(80),
	TXProst3D(81),
	TXProstCK(82),
	TXProstIMRT(83),
	TXProstRA(84),
	TXSeinPlusRPMAbches(85),
	TXSeinBilatPlus(86),
	TXSeinBilat(87),
	TXSeinCoqIMRT(88),
	TXSeinCoqTOMO(89),
	TXSeinRPMAbches(90),
	TXSeinTg(91),
	TXSeinTgPlus(92),
	TXSeinTgAvecBolus(93),
	TXSeinTgPlusAvecBolus(94),
	TXTrijCK(95),
	TXUro3D(96),
	TXUroIMRT(97);
	public static RandomVariateGen genTreatTechUnif =new UniformGen(new MRG32k3a(),0,1);
	private int index ;
	private TreatmentTechnic( int index) {
		this.index=index;
		
		
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public static TreatmentTechnic getTreatmentTechnic(int index){
		for (TreatmentTechnic treatmentTechnic : TreatmentTechnic.values()) {
			if (treatmentTechnic.getIndex()==index){
				return treatmentTechnic;
			}
		}
		return null;
	}
	
	public static TreatmentTechnic generateTreatmentTechnic(){
		int length = TreatmentTechnic.values().length;
		int index = (int) (genTreatTechUnif.nextDouble()*length);
		TreatmentTechnic treatmentTechnic =getTreatmentTechnic(index);
		return treatmentTechnic;
		
	}

	
}
