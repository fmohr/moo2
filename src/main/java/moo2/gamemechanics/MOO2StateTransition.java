package moo2.gamemechanics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import moo2.MOO2Util;
import moo2.model.Building;
import moo2.model.ITechnology;
import moo2.model.ResearchLine;
import moo2.model.Technology;

public class MOO2StateTransition {

	public MOO2State step(final MOO2State state, final MOO2Action action) {
		SolarSystemState sss = state.getSsState();
		Collection<Building> availableBuildings = state.getAvailableBuildings();

		int totalResearchPointsForCurrentResearchProject = state.getResearchPointsForCurrentResearchProject();
		int n = sss.getPlanetStates().size();
		int totalFoodSurplus = 0;
		int totalFreightersNeed = 0;

		List<PlanetState> newPlanetStates = new ArrayList<>();
		int newlyColonizedPlanetIndex = -1;
		List<Integer> incomesPerPlanets = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			PlanetState ps = sss.getPlanetStates().get(i);
			PlanetAction pa = action.getPlanetActions().get(i); // we implicitly assume that one action is defined for every planet in the home solar system (null if not owned)
			if (ps.getPopulation() == 0) {
				if (pa != null) {
					throw new NullPointerException("Planet " + i + " has no population but an action defined!");
				}
				if (i == newlyColonizedPlanetIndex) {
					newPlanetStates.add(new PlanetState(ps, 1000000, 0));
				}
				else {
					newPlanetStates.add(ps);
				}
			}
			else {// only if we have people at this planet, something can happen

				if (pa == null) {
					throw new NullPointerException("The planet action for planet " + i + " is NULL!");
				}



				/* population growth */
				int newPopulation = ps.getPopulation() + this.getPlanetPopulationGrowth(ps, pa);
				newPopulation = Math.min(newPopulation, MOO2Util.getMaxPopulation(ps));

				/* money income of planet */
				int incomeOfThisPlanet = this.getPlanetMoneyOutcome(ps, pa);
				incomesPerPlanets.add(incomeOfThisPlanet);

				/* food production and freighter need */
				int planetFoodSurplus = this.getPlanetFarmerOutcome(ps, pa) - ps.getPopulationRound(); // everybody eats one

				totalFoodSurplus += planetFoodSurplus;
				if (planetFoodSurplus < 0) {
					totalFreightersNeed += -1 * planetFoodSurplus;
				}

				/* construction point growth. Check whether the new construction gets finished */
				Building nextBuild = pa.getNextBuild();
				if (nextBuild != Building.COLLECT && !availableBuildings.contains(nextBuild)) {
					throw new IllegalArgumentException("Cannot build " + nextBuild + ", because this is not available and must first be researched!");
				}
				int newConstructionPoints = ps.getAccumulatedConstructionPoints() + this.getPlanetProductionOutcome(ps, pa);
				Building newBuilding = null;
				boolean constructionCompleted = newConstructionPoints >= nextBuild.getBuildCost() || pa.isPurchaseConstruction();

				/* check whether the desired build can be realized here */
				switch (nextBuild) {
				case COLONY_BASE:
					if (state.getSsState().getPlanetStates().stream().noneMatch(p -> p.getPopulation() == 0)) {
						throw new IllegalArgumentException("Cannot build colony base in a system that has only colonized planets.");
					}
					else if (constructionCompleted) { // more or less randomly decide where to colonize
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
					else if (constructionCompleted) {
						newConstructionPoints = 0;
						newBuilding = pa.getNextBuild();
					}
				}

				/* check whether there are enough freighters */
				if (totalFreightersNeed > 0) {
					throw new IllegalArgumentException("Not enough freighters! " + totalFreightersNeed + " freighters are required.");
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
		}
		SolarSystemState newSSS = new SolarSystemState(newPlanetStates);

		/* determine overall research progress */
		Collection<Technology> availableTechnologies = state.getAvailableTechnologies();
		ITechnology currentlyResearchedTechnology = action.getCurrentResearchTechnology();
		ResearchLine currentResearchLine = currentlyResearchedTechnology.getResearchLine();
		Map<ResearchLine, Integer> nextResearchLineIndices = state.getCurrentResearchLineAchievements();
		int nextResearchIndexInLine = state.getCurrentResearchLineAchievements().get(currentResearchLine);
		if (nextResearchIndexInLine != currentlyResearchedTechnology.getResearchStep()) { // check whether this research is possible
			throw new IllegalArgumentException("Cannot do research for technology " + currentlyResearchedTechnology + " in phase " + currentlyResearchedTechnology.getResearchStep() + " of line " + currentResearchLine + ". Next research index in this line would be " + nextResearchIndexInLine + " but technology is " + currentlyResearchedTechnology.getResearchStep());
		}
		if (currentResearchLine.getRequiredPoints().size() <= nextResearchIndexInLine) {
			throw new IllegalArgumentException("Research Line " + currentResearchLine + " has no entry for index " + nextResearchIndexInLine);
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
			else {
				availableTechnologies = new HashSet<>(availableTechnologies);
				availableTechnologies.add((Technology)currentlyResearchedTechnology);
			}
		}

		/* update account */
		int oldMoney = state.getMoney();
		int income = incomesPerPlanets.stream().reduce(0, Integer::sum);
		if (income > 1000) {
			System.err.println("Ridiculous incomes: " + incomesPerPlanets);
		}
		if (income < 0 && income * - 1 > oldMoney) {
			throw new IllegalArgumentException("Illegal action, because money would become negative. Money before was: " + oldMoney + " and net incomes per planets are: " + incomesPerPlanets);
		}
		int newMoney = oldMoney + income;

		/* create new state */
		return new MOO2State(state.getYear() + 1, newSSS, newMoney, totalResearchPointsForCurrentResearchProject, nextResearchLineIndices, availableBuildings, availableTechnologies);
	}

	public double getMoralOnPlanet(final PlanetState planetState) {

		/* get moral bonus */
		double moralBonus = 1.0;
		if (!planetState.getBuildings().contains(Building.INFANTERYBARRACKS)) {
			moralBonus -= 0.2;
		}
		return moralBonus;
	}

	public int getPlanetPopulationGrowth(final PlanetState ps, final PlanetAction pa) {
		int maxPop = MOO2Util.getMaxPopulation(ps);
		double livingSpace = (maxPop - ps.getPopulation() + 1.0) / maxPop;
		double efficiency = Math.pow(livingSpace, 1 + livingSpace);
		return (int)Math.round(efficiency * 40000);
	}

	public int getPlanetMoneyOutcome(final PlanetState planetState, final PlanetAction planetAction) {

		Objects.nonNull(planetAction);

		/* compute taxes */
		int taxes = planetState.getPopulationRound(); // 1 MC per 1 million population

		/* compute upkeep cost for buildings */
		int buildingCost = 0;
		Building erasedBuilding = planetAction.getErasedBuilding();
		for (Building b : planetState.getBuildings()) {
			if (b != erasedBuilding) {
				buildingCost += b.getMaintainanceCost();
			}
		}

		/* if something is purchased, consider this now */
		int costToPurchase = 0;
		if (planetAction.isPurchaseConstruction()) {
			if (planetAction.getNextBuild().getBuildCost() < Integer.MAX_VALUE) {
				int missingConstructionPointsAbsolute = planetAction.getNextBuild().getBuildCost() - planetState.getAccumulatedConstructionPoints();
				double missingConstructionPointsRelative = missingConstructionPointsAbsolute * 1.0 / planetAction.getNextBuild().getBuildCost();
				costToPurchase = missingConstructionPointsAbsolute * (missingConstructionPointsRelative < 0.5 ? 2 : 4);
				if (costToPurchase < 0) {
					throw new IllegalArgumentException("Purchase does not make sense! Negative cost to purchase " + planetAction.getNextBuild() + " with cost " + planetAction.getNextBuild().getBuildCost() + " where current construction points are " + planetState.getAccumulatedConstructionPoints());
				}
			}
			else {
				throw new IllegalArgumentException("Cannot buy infinitely costly buildings.");
			}
		}

		/* if something has been erased, consider this now */
		int eraseEarnings = 0;
		if (planetAction.getErasedBuilding() != null) {
			eraseEarnings  += planetAction.getErasedBuilding().getBuildCost() / 2; // erasing a building yields half of its building cost
		}


		int netIncome = taxes + eraseEarnings - buildingCost - costToPurchase;
		if (Math.abs(netIncome) > 250) {
			System.err.println("Strange income: " + netIncome + ". Taxes: " + taxes + ", erase earnings: " + eraseEarnings + ", building cost: " + buildingCost + ", costToPurchase: " + costToPurchase);
		}
		return netIncome;
	}

	public int getPlanetFarmerOutcome(final PlanetState planetState, final PlanetAction planetAction) {
		return (int)Math.floor(planetAction.getFarmers() * 2 * this.getMoralOnPlanet(planetState));
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

		int fixProduction = 0;

		if (planetState.getBuildings().contains(Building.AUTOMATED_FACTORIES)) {
			factor += 1;
			fixProduction += 5;
		}

		return fixProduction + (int)Math.floor(planetAction.getWorkers() * factor * this.getMoralOnPlanet(planetState));
	}

	public int getPlanetScienceOutcome(final PlanetState planetState, final PlanetAction planetAction) {
		return (int)Math.floor(planetAction.getScientists() * 3 * this.getMoralOnPlanet(planetState));
	}
}
