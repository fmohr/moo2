package moo2.gamemechanics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import moo2.model.Building;
import moo2.model.ResearchLine;

public class MOO2StateBuilder {

	public MOO2State getInitState(final SolarSystemState sss) {
		Map<ResearchLine, Integer> currentResearchIndices = new HashMap<>();
		Arrays.stream(ResearchLine.values()).forEach(rl -> currentResearchIndices.put(rl, 0));
		return new MOO2State(35000, sss, 50, 0, currentResearchIndices, Arrays.asList(Building.INFANTERYBARRACKS, Building.STARBASE, Building.COLONY_BASE), Arrays.asList());
	}
}
