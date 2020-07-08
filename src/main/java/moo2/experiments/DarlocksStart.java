package moo2.experiments;

import java.util.Arrays;

import moo2.gamemechanics.MOO2Action;
import moo2.gamemechanics.MOO2State;
import moo2.gamemechanics.MOO2StateBuilder;
import moo2.gamemechanics.MOO2StateTransition;
import moo2.gamemechanics.PlanetAction;
import moo2.gamemechanics.PlanetState;
import moo2.gamemechanics.SolarSystemState;
import moo2.model.Building;
import moo2.model.PlanetBuilder;
import moo2.model.PlanetClimate;
import moo2.model.PlanetMinerals;
import moo2.model.PlanetSize;
import moo2.model.Technology;

public class DarlocksStart {

	public static void main(final String[] args) {

		/* get MOO2 mechanic */
		MOO2StateTransition sts = new MOO2StateTransition();

		/* create initial game state */
		PlanetState initPState = new PlanetBuilder().withStartConfiguration().build();
		PlanetState secondPlanet = new PlanetBuilder().withClimate(PlanetClimate.UNFRUCHTBAR).withMinerals(PlanetMinerals.ERGIEBIG).withSize(PlanetSize.MITTEL).build();
		MOO2State initState = new MOO2StateBuilder().getInitState(new SolarSystemState(Arrays.asList(initPState, secondPlanet)));
		System.out.println(initState);

		/* define first action */
		PlanetAction pAction = new PlanetAction(4, 2, 2, Building.COLONY_BASE, Building.STARBASE, false);
		MOO2Action action = new MOO2Action(Arrays.asList(pAction, null), Technology.REINFORCED_HULL);

		/* next state */
		MOO2State state = initState;
		state = sts.step(state, action);
		System.out.println(state);

		/* second action */
		pAction = new PlanetAction(4, 4, 0, Building.COLONY_BASE, null, false);
		action = new MOO2Action(Arrays.asList(pAction, null), Technology.REINFORCED_HULL);

		state = sts.step(state, action);
		System.out.println(state);

		pAction = new PlanetAction(4, 4, 0, Building.COLONY_BASE, null, false);
		action = new MOO2Action(Arrays.asList(pAction, null), Technology.REINFORCED_HULL);
		state = sts.step(state, action);
		System.out.println(state);
	}
}
