package moo2;

import moo2.gamemechanics.PlanetState;
import moo2.model.Building;
import moo2.model.PlanetSize;

public class MOO2Util {

	public static int getMaxPopulation(final PlanetState ps) {
		PlanetSize size = ps.getSize();

		int baseNum = 0;
		switch (size) {
		case WINZIG:
			baseNum = 1;
			break;
		case KLEIN:
			baseNum = 3;
			break;
		case MITTEL:
			baseNum = 4;
			break;
		case GROSS:
			baseNum = 5;
			break;
		case RIESIG:
			baseNum = 6;
			break;
		}

		if (ps.getBuildings().contains(Building.BIOSPHERES)) {
			baseNum += 2;
		}

		switch (ps.getClimate()) {
		case SUMPF:
			switch (size) {
			case WINZIG:
			case KLEIN:
				baseNum += 1;
				break;
			case MITTEL:
				baseNum += 2;
				break;
			case GROSS:
				baseNum += 3;
				break;
			case RIESIG:
				baseNum += 4;
				break;
			}
			break;
		case DUERRE:
			switch (size) {
			case WINZIG:
				baseNum += 2;
				break;
			case KLEIN:
				baseNum += 3;
				break;
			case MITTEL:
				baseNum += 5;
				break;
			case GROSS:
				baseNum += 7;
				break;
			case RIESIG:
				baseNum += 9;
				break;
			}
			break;
		case TERRANISCH:
			switch (size) {
			case WINZIG:
				baseNum += 3;
				break;
			case KLEIN:
				baseNum += 5;
				break;
			case MITTEL:
				baseNum += 8;
				break;
			case GROSS:
				baseNum += 11;
				break;
			case RIESIG:
				baseNum += 14;
				break;
			}
			break;
		case GAIA:
			switch (size) {
			case WINZIG:
				baseNum += 4;
				break;
			case KLEIN:
				baseNum += 7;
				break;
			case MITTEL:
				baseNum += 11;
				break;
			case GROSS:
				baseNum += 15;
				break;
			case RIESIG:
				baseNum += 19;
				break;
			}
			break;
		default:
			/* no change */
			break;
		}
		return baseNum * 1000000;
	}
}
