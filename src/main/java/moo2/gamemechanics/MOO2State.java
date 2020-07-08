package moo2.gamemechanics;

import java.util.Collection;
import java.util.Map;

import moo2.model.Building;
import moo2.model.ResearchLine;

public class MOO2State {

	private final int year;
	private final SolarSystemState ssState;
	private final int money;
	private final int researchPointsForCurrentResearchProject;
	private final Map<ResearchLine, Integer> nextResearchLineIndices;
	private final Collection<Building> availableBuildings;

	public MOO2State(final int year, final SolarSystemState ssState, final int money, final int researchPointsForCurrentResearchProject, final Map<ResearchLine, Integer> nextResearchLineIndices,
			final Collection<Building> availableBuildings) {
		super();
		this.year = year;
		this.ssState = ssState;
		this.money = money;
		this.researchPointsForCurrentResearchProject = researchPointsForCurrentResearchProject;
		this.nextResearchLineIndices = nextResearchLineIndices;
		this.availableBuildings = availableBuildings;
	}

	public int getYear() {
		return this.year;
	}

	public SolarSystemState getSsState() {
		return this.ssState;
	}

	public int getMoney() {
		return this.money;
	}

	public int getResearchPointsForCurrentResearchProject() {
		return this.researchPointsForCurrentResearchProject;
	}

	public Map<ResearchLine, Integer> getCurrentResearchLineAchievements() {
		return this.nextResearchLineIndices;
	}

	public Collection<Building> getAvailableBuildings() {
		return this.availableBuildings;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("-------------------------------------------------------------------------------------\n");
		sb.append("Year: ");
		sb.append(this.year);
		sb.append("\tMoney: ");
		sb.append(this.money);
		sb.append("\tResearch Points: ");
		sb.append(this.researchPointsForCurrentResearchProject);
		sb.append("\nPlanet States:");
		for (PlanetState ps : this.ssState.getPlanetStates()) {
			sb.append("\n\tPop: ");
			int pop = ps.getPopulation();
			sb.append(pop > 0 ? pop : "      0");
			sb.append("\tConstruction Points: ");
			sb.append(ps.getAccumulatedConstructionPoints());
			sb.append("\tBuildings: ");
			sb.append(ps.getBuildings());
		}
		sb.append("\n-------------------------------------------------------------------------------------");
		return sb.toString();

	}
}
