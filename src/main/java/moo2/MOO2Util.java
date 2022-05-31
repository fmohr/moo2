package moo2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.api4.java.ai.graphsearch.problem.implicit.graphgenerator.INodeGoalTester;
import org.api4.java.ai.graphsearch.problem.pathsearch.pathevaluation.IPathEvaluator;
import org.api4.java.datastructure.graph.implicit.IGraphGenerator;
import org.api4.java.datastructure.graph.implicit.INewNodeDescription;
import org.api4.java.datastructure.graph.implicit.ISingleRootGenerator;
import org.api4.java.datastructure.graph.implicit.ISuccessorGenerator;

import ai.libs.jaicore.search.model.NodeExpansionDescription;
import ai.libs.jaicore.search.probleminputs.GraphSearchWithNumberBasedAdditivePathEvaluation;
import moo2.gamemechanics.MOO2Action;
import moo2.gamemechanics.MOO2State;
import moo2.gamemechanics.MOO2StateTransition;
import moo2.gamemechanics.PlanetAction;
import moo2.gamemechanics.PlanetState;
import moo2.model.Building;
import moo2.model.ITechnology;
import moo2.model.PlanetSize;
import moo2.model.Technology;

public class MOO2Util {

	public static final MOO2StateTransition STS = new MOO2StateTransition();

	public static GraphSearchWithNumberBasedAdditivePathEvaluation<MOO2State, MOO2Action> getGraphSearchProblem(final MOO2State initState, final Predicate<MOO2State> goalPredicate,
			final IPathEvaluator<MOO2State, MOO2Action, Double> heuristic) {
		IGraphGenerator<MOO2State, MOO2Action> gg = new IGraphGenerator<MOO2State, MOO2Action>() {

			@Override
			public ISingleRootGenerator<MOO2State> getRootGenerator() {
				return () -> initState;
			}

			@Override
			public ISuccessorGenerator<MOO2State, MOO2Action> getSuccessorGenerator() {
				return new ISuccessorGenerator<MOO2State, MOO2Action>() {

					@Override
					public List<INewNodeDescription<MOO2State, MOO2Action>> generateSuccessors(final MOO2State node) throws InterruptedException {

						List<INewNodeDescription<MOO2State, MOO2Action>> successors = new ArrayList<>();
						for (MOO2Action a : MOO2Util.getAllPossibleActions(node)) {
							MOO2State sPrime = STS.step(node, a);
							successors.add(new NodeExpansionDescription<>(sPrime, a));
						}
						if (successors.isEmpty()) {
							System.err.println("There is a dead-end in the following node:\n" + node);
						}
						return successors;
					}
				};
			}
		};

		return new GraphSearchWithNumberBasedAdditivePathEvaluation<>(gg, new INodeGoalTester<MOO2State, MOO2Action>() {

			@Override
			public boolean isGoal(final MOO2State node) {
				return goalPredicate.test(node);
			}

		}, (n1, n2) -> 1.0, heuristic);
	}

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

	public static Collection<PlanetAction> getAllPossibleActionsForPlanet(final MOO2State gameState, final PlanetState planetState) {
		int population = planetState.getPopulationRound();

		Collection<Building> missingAffordableBuildings = gameState.getAvailableBuildings().stream().filter(b -> b.getBuildCost() <= planetState.getAccumulatedConstructionPoints() && !planetState.getBuildings().contains(b)).collect(Collectors.toList());
		missingAffordableBuildings.add(Building.COLLECT);
		Collection<Building> eraseableBuildings = new ArrayList<>(planetState.getBuildings());
		eraseableBuildings.add(null);

		List<PlanetAction> actions = new ArrayList<>();
		for (int farmers = 0; farmers <= population; farmers++) {
			for (int workers = 0; workers <= population - farmers; workers++) {
				int scientists = population - farmers - workers;
				for (Building nextBuild : missingAffordableBuildings) {
					for (Building eraseBuilding : eraseableBuildings) {
						for (boolean purchaseConstruction : Arrays.asList(true, false)) {
							actions.add(new PlanetAction(farmers, workers, scientists, nextBuild, eraseBuilding, purchaseConstruction));
						}
					}
				}
			}
		}
		return actions;
	}

	public static Collection<MOO2Action> getAllPossibleActions(final MOO2State gameState) {

		ITechnology researchedTechnology;
		if (!gameState.getAvailableTechnologies().contains(Technology.REINFORCED_HULL)) {
			researchedTechnology = Technology.REINFORCED_HULL;
		} else if (!gameState.getAvailableBuildings().contains(Building.AUTOMATED_FACTORIES)) {
			researchedTechnology = Building.AUTOMATED_FACTORIES;
		} else if (!gameState.getAvailableBuildings().contains(Building.BIOSPHERES)) {
			researchedTechnology = Building.BIOSPHERES;
		}
		else {
			throw new IllegalStateException("Don't know what to research!");
		}

		MOO2StateTransition sts = new MOO2StateTransition(); // to simulate the options

		Collection<MOO2Action> possibleActions = new ArrayList<>();
		Collection<PlanetAction> planetActions = MOO2Util.getAllPossibleActionsForPlanet(gameState, gameState.getSsState().getPlanetStates().get(0));
		for (PlanetAction a : planetActions) {

			MOO2Action simulatedAction = new MOO2Action(Arrays.asList(a, null), researchedTechnology);
			try {
				sts.step(gameState, simulatedAction);
				possibleActions.add(simulatedAction);
			} catch (Exception e) {
				// System.err.println("Ignoring possible action " + a);
				// e.printStackTrace();
			}
		}
		return possibleActions;
	}
}
