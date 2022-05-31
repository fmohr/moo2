package moo2.experiments;

import java.util.Arrays;
import java.util.function.Predicate;

import org.aeonbits.owner.ConfigFactory;
import org.api4.java.algorithm.exceptions.AlgorithmException;
import org.api4.java.algorithm.exceptions.AlgorithmExecutionCanceledException;
import org.api4.java.algorithm.exceptions.AlgorithmTimeoutedException;

import ai.libs.jaicore.search.algorithms.standard.astar.AStar;
import ai.libs.jaicore.search.algorithms.standard.bestfirst.IBestFirstConfig;
import ai.libs.jaicore.search.model.other.EvaluatedSearchGraphPath;
import ai.libs.jaicore.search.probleminputs.GraphSearchWithNumberBasedAdditivePathEvaluation;
import moo2.MOO2Util;
import moo2.gamemechanics.MOO2Action;
import moo2.gamemechanics.MOO2State;
import moo2.gamemechanics.MOO2StateBuilder;
import moo2.gamemechanics.MOO2StateTransition;
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

		//Predicate<MOO2State> moneyGoal = s -> s.getMoney() > 257;
		Predicate<MOO2State> factoryGoal = s -> s.getSsState().getPlanetStates().get(0).getBuildings().containsAll(Arrays.asList(Building.INFANTERYBARRACKS, Building.STARBASE)) && s.getAvailableTechnologies().containsAll(Arrays.asList(Technology.REINFORCED_HULL)) && s.getMoney() > 0;
		//Predicate<MOO2State> colonizeGoal = s -> s.getSsState().getPlanetStates().get(1).getPopulation() > 0;
		//Predicate<MOO2State> overallGoal = s -> s.getSsState().getPlanetStates().get(0).getBuildings().containsAll(Arrays.asList(Building.INFANTERYBARRACKS, Building.AUTOMATED_FACTORIES, Building.STARBASE)) && s.getSsState().getPlanetStates().get(1).getPopulation() > 0 && s.getMoney() > 0;
		GraphSearchWithNumberBasedAdditivePathEvaluation<MOO2State, MOO2Action> problem = MOO2Util.getGraphSearchProblem(initState, factoryGoal, p -> {

			MOO2State state = p.getHead();
			PlanetState ps = state.getSsState().getPlanetStates().get(0);

			/* compute missing research points to goal */
			int missingResearchPoints = 0;
			if (!state.getAvailableTechnologies().contains(Technology.REINFORCED_HULL)) {
				missingResearchPoints += Technology.REINFORCED_HULL.getRequiredResearchPoints();
			}
			if (!state.getAvailableBuildings().contains(Building.AUTOMATED_FACTORIES)) {
				missingResearchPoints += Building.AUTOMATED_FACTORIES.getRequiredResearchPoints();
			}

			missingResearchPoints -= state.getResearchPointsForCurrentResearchProject();
			missingResearchPoints = Math.max(0, missingResearchPoints);
			int maxScientists = ps.getPopulationRound() / 2;
			int researchUnderCurrentMorale = (int)Math.floor(maxScientists * 3 * sts.getMoralOnPlanet(ps));
			double optimisticEstimateForNumberOfTurnsForResearch =  Math.ceil(missingResearchPoints * 1.0 / researchUnderCurrentMorale);

			/* compute missing construction points to goal */
			int missingConstructionPointsAbsolute = 0;
			if (!ps.getBuildings().contains(Building.INFANTERYBARRACKS)) {
				missingConstructionPointsAbsolute += Building.INFANTERYBARRACKS.getBuildCost();
			}
			if (!ps.getBuildings().contains(Building.AUTOMATED_FACTORIES)) {
				missingConstructionPointsAbsolute += Building.AUTOMATED_FACTORIES.getBuildCost();
			}
			if (!ps.getBuildings().contains(Building.STARBASE)) {
				missingConstructionPointsAbsolute += Building.STARBASE.getBuildCost();
			}
			//			if (state.getSsState().getPlanetStates().get(1).getPopulation() == 0) {
			//				missingConstructionPointsAbsolute += Building.COLONY_BASE.getBuildCost();
			//			}
			missingConstructionPointsAbsolute -= ps.getAccumulatedConstructionPoints();
			double missingConstructionPointsRelative = 0.6;//missingConstructionPointsAbsolute / Building.AUTOMATED_FACTORIES.getBuildCost();
			missingConstructionPointsAbsolute -= state.getMoney() / (missingConstructionPointsRelative > 0.5 ? 4 : 2); // calculate the ability to purchase
			missingConstructionPointsAbsolute = Math.max(0, missingConstructionPointsAbsolute);
			int maxWorkers = ps.getPopulationRound() / 2;
			boolean hasFactories = ps.getBuildings().contains(Building.AUTOMATED_FACTORIES);
			int productionUnderCurrentMorale = (hasFactories ? 5 : 0) +  (int)Math.floor(maxWorkers * (3 + (hasFactories ? 1 : 0)) * sts.getMoralOnPlanet(ps));
			double optimisticEstimateForNumberOfTurnsForConstruction =  Math.ceil(missingConstructionPointsAbsolute * 1.0 / productionUnderCurrentMorale);

			double optimisticEstimateForNumberOfTurns = optimisticEstimateForNumberOfTurnsForResearch + optimisticEstimateForNumberOfTurnsForConstruction;
			//			System.out.println(optimisticEstimateForNumberOfTurns + " = " + optimisticEstimateForNumberOfTurnsForResearch + " + " + optimisticEstimateForNumberOfTurnsForConstruction);
			return optimisticEstimateForNumberOfTurns;
		});
		AStar<MOO2State, MOO2Action> astar = new AStar<>(problem);
		astar.setLoggerName("astar");
		IBestFirstConfig config = ConfigFactory.create(IBestFirstConfig.class);
		config.setProperty(IBestFirstConfig.K_PD, "ALL");
		config.setProperty(IBestFirstConfig.K_OE, "" + true);
		astar.setConfig(config);

		try {
			for (int j = 0; j < 1000; j++) {
				EvaluatedSearchGraphPath<MOO2State, MOO2Action, Double> solution = astar.nextSolutionCandidate();
				if (solution != null) {
					System.out.println(solution.getScore());
					if (solution.getScore() < 20) {
						for (int i = 0; i < solution.getNumberOfNodes() - 1; i++) {
							MOO2State state = solution.getNodes().get(i + 1);
							MOO2Action a = solution.getArcs().get(i);
							System.out.println("\t" + a.getPlanetActions().get(0));
							System.out.println(state);
						}
					}
				}
				else {
					System.out.println("NO SOLUTION");
					break;
				}
			}
		} catch (AlgorithmTimeoutedException | InterruptedException | AlgorithmExecutionCanceledException | AlgorithmException e) {
			e.printStackTrace();
		}


		//		/* next state */
		//		MOO2State state = initState;
		//		PlanetAction pAction;
		//		MOO2Action action;
		//
		//		/* define first action */
		//		while (!state.getAvailableTechnologies().contains(Technology.REINFORCED_HULL)) {
		//			pAction = new PlanetAction(4, 0, 4, Building.COLONY_BASE, null, false);
		//			action = new MOO2Action(Arrays.asList(pAction, null), Technology.REINFORCED_HULL);
		//			state = sts.step(state, action);
		//			System.out.println(state);
		//		}
		//
		//		for (int i = 0; i < 8; i++) {
		//			pAction = new PlanetAction(4, 0, 4, Building.COLONY_BASE, null, false);
		//			action = new MOO2Action(Arrays.asList(pAction, null), state.getAvailableTechnologies().contains(Technology.REINFORCED_HULL) ? Building.AUTOMATED_FACTORIES : Technology.REINFORCED_HULL);
		//			state = sts.step(state, action);
		//			System.out.println(state);
		//		}
		//		pAction = new PlanetAction(4, 2, 2, Building.COLONY_BASE, null, false);
		//		action = new MOO2Action(Arrays.asList(pAction, null), state.getAvailableTechnologies().contains(Technology.REINFORCED_HULL) ? Building.AUTOMATED_FACTORIES : Technology.REINFORCED_HULL);
		//		state = sts.step(state, action);
		//		System.out.println(state);
		//
		//		for (int i = 0; i < 2; i++) {
		//			pAction = new PlanetAction(4, 4, 0, Building.AUTOMATED_FACTORIES, null, false);
		//			action = new MOO2Action(Arrays.asList(pAction, null), Building.BIOSPHERES);
		//			state = sts.step(state, action);
		//			System.out.println(state);
		//		}
		//		pAction = new PlanetAction(4, 4, 0, Building.AUTOMATED_FACTORIES, null, true);
		//		action = new MOO2Action(Arrays.asList(pAction, null), Building.BIOSPHERES);
		//		state = sts.step(state, action);
		//		System.out.println(state);
	}
}
