package moo2.gamemechanics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import moo2.MOO2Util;
import moo2.model.Building;
import moo2.model.ITechnology;
import moo2.model.ResearchLine;

public class MOO2StateTransition {

	public MOO2State step(final MOO2State state, final MOO2Action action) {
		SolarSystemState sss = state.getSsState();

		int totalIncome = 0;
		int totalResearchPointsForCurrentResearchProject = state.getResearchPointsForCurrentResearchProject();
		int n = sss.getPlanetStates().size();
		int totalFoodSurplus = 0;
		int totalFreightersNeed = 0;

		List<PlanetState> newPlanetStates = new ArrayList<>();
		int newlyColonizedPlanetIndex = -1;
		for (int i = 0; i < n; i++) {
			PlanetState ps = sss.getPlanetStates().get(i);
			PlanetAction pa = action.getPlanetActions().get(i); // we implicitly assume that one action is defined for every planet in the home solar system (null if not owned)
			if (ps.getPopulation() != 0) { // only if we have people at this planet, something can happen

				/* population growth */
				int newPopulation = ps.getPopulation() + this.getPlanetPopulationGrowth(ps, pa);
				newPopulation = Math.min(newPopulation, MOO2Util.getMaxPopulation(ps));

				/* money income of planet */
				totalIncome += this.getPlanetMoneyOutcome(ps, pa);

				/* food production and freighter need */
				int planetFoodSurplus = this.getPlanetFarmerOutcome(ps, pa) - ps.getPopulation(); // everybody eats one
				totalFoodSurplus += planetFoodSurplus;
				if (planetFoodSurplus < 0) {
					totalFreightersNeed += -1 * planetFoodSurplus;
				}

				/* construction point growth. Check whether the new construction gets finished */
				Building nextBuild = pa.getNextBuild();
				int newConstructionPoints = ps.getAccumulatedConstructionPoints() + this.getPlanetProductionOutcome(ps, pa);
				Building newBuilding = null;

				/* check whether the desired build can be realized here */
				switch (nextBuild) {
				case COLONY_BASE:
					if (state.getSsState().getPlanetStates().stream().noneMatch(p -> p.getPopulation() == 0)) {
						throw new IllegalArgumentException("Cannot build colony base in a system that has only colonized planets.");
					}
					else if (newConstructionPoints >= nextBuild.getBuildCost()) { // more or less randomly decide where to colonize
						newConstructionPoints = 0;
						for (int j = 0; j < n; j++) {
							PlanetState psColonized = sss.getPlanetStates().get(j);
							if (psColonized.getPopulation() == 0) {
								newlyColonizedPlanetIndex = j;
								break;
							}
						}
					}
					break;

				default:
					if (ps.getBuildings().contains(nextBuild)) {
						throw new IllegalArgumentException("Cannot build " + nextBuild + " because it is already there.");
					}
					else if (newConstructionPoints >= nextBuild.getBuildCost()) {
						newConstructionPoints = 0;
						newBuilding = pa.getNextBuild();
					}
				}

				/* science points */
				totalResearchPointsForCurrentResearchProject += this.getPlanetScienceOutcome(ps, pa);

				/* derive new planet state */
				Collection<Building> buildings = new HashSet<>(ps.getBuildings());
				if (newBuilding != null) {
					buildings.add(newBuilding);
				}
				if (pa.getErasedBuilding() != null) {
					if (!buildings.contains(pa.getErasedBuilding())) {
						throw new IllegalArgumentException("Cannot destroy building " + pa.getErasedBuilding() + ", because it is not available at the planet.");
					}
					buildings.remove(pa.getErasedBuilding());
				}
				PlanetState newPs = new PlanetState(ps, newPopulation, newConstructionPoints, buildings);
				newPlanetStates.add(newPs);
			}
			else if (i == newlyColonizedPlanetIndex) {
				newPlanetStates.add(new PlanetState(ps, 1000000, 0));
			}
			else {
				newPlanetStates.add(ps);
			}
		}
		SolarSystemState newSSS = new SolarSystemState(newPlanetStates);

		/* determine overall research progress */
		ITechnology currentlyResearchedTechnology = action.getCurrentResearchTechnology();
		ResearchLine currentResearchLine = currentlyResearchedTechnology.getResearchLine();
		Map<ResearchLine, Integer> nextResearchLineIndices = state.getCurrentResearchLineAchievements();
		Collection<Building> availableBuildings = state.getAvailableBuildings();
		int nextResearchIndexInLine = state.getCurrentResearchLineAchievements().get(currentResearchLine);
		if (nextResearchIndexInLine != currentlyResearchedTechnology.getResearchStep()) { // check whether this research is possible
			throw new IllegalArgumentException("Cannot do research for technology " + currentlyResearchedTechnology + " in phase " + currentlyResearchedTechnology.getResearchStep() + " of line " + currentResearchLine);
		}
		int requiredResearchPoints = currentResearchLine.getRequiredPoints().get(nextResearchIndexInLine);
		if (totalResearchPointsForCurrentResearchProject >= requiredResearchPoints) {
			totalResearchPointsForCurrentResearchProject = 0;
			nextResearchLineIndices = new EnumMap<>(nextResearchLineIndices);
			nextResearchLineIndices.put(currentResearchLine, nextResearchIndexInLine + 1);
			if (currentlyResearchedTechnology instanceof Building) {
				availableBuildings = new HashSet<>(availableBuildings);
				availableBuildings.add((Building)currentlyResearchedTechnology);
			}
		}

		/* create new state */
		return new MOO2State(state.getYear() + 1, newSSS, state.getMoney() + totalIncome, totalResearchPointsForCurrentResearchProject, nextResearchLineIndices, availableBuildings);
	}

	public int getMoralOnPlanet(final PlanetState planetState) {
		return 0;
	}

	public int getPlanetPopulationGrowth(final PlanetState ps, final PlanetAction pa) {
		int maxPop = MOO2Util.getMaxPopulation(ps);
		double livingSpace = (maxPop - ps.getPopulation() + 1.0) / maxPop;
		double efficiency = Math.pow(livingSpace, 1 + livingSpace);
		System.out.println(maxPop);
		System.out.println(livingSpace);
		System.out.println(efficiency);
		return (int)Math.round(efficiency * 40000);
	}

	public int getPlanetMoneyOutcome(final PlanetState planetState, final PlanetAction planetAction) {

		/* compute net income */
		int taxes = (int)Math.floor(planetState.getPopulation() / 1000000.0); // 1 MC per 1 million population
		int buildingCost = 0;
		Building erasedBuilding = planetAction.getErasedBuilding();
		for (Building b : planetState.getBuildings()) {
			if (b != erasedBuilding) {
				buildingCost += b.getMaintainanceCost();
			}
		}
		int netIncome = taxes - buildingCost;

		/* if something is purchased, consider this now */
		if (planetAction.isPurchaseConstruction()) {
			throw new UnsupportedOperationException("Purchase cost currently not considered");
		}

		/* if something has been erased, consider this now */
		if (planetAction.getErasedBuilding() != null) {
			netIncome += planetAction.getErasedBuilding().getBuildCost() / 2; // erasing a building yields half of its building cost
		}
		return netIncome;
	}

	public int getPlanetFarmerOutcome(final PlanetState planetState, final PlanetAction planetAction) {
		return planetAction.getFarmers() * 2;
	}

	public int getPlanetProductionOutcome(final PlanetState planetState, final PlanetAction planetAction) {
		int factor;
		switch (planetState.getMinerals()) {
		case ULTRAARM:
			factor = 1;
			break;
		case ARM:
			factor = 2;
			break;
		case ERGIEBIG:
			factor = 3;
			break;
		case REICH:
			factor = 4;
			break;
		case ULTRAREICH:
			factor = 5;
			break;
		default:
			throw new IllegalStateException();
		}
		return planetAction.getWorkers() * factor;
	}

	public int getPlanetScienceOutcome(final PlanetState planetState, final PlanetAction planetAction) {
		return planetAction.getScientists() * 3;
	}
}
