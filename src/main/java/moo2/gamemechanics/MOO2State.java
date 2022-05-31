package moo2.gamemechanics;

import java.util.Collection;
import java.util.Map;

import moo2.model.Building;
import moo2.model.ResearchLine;
import moo2.model.Technology;

public class MOO2State {

	private final int year;
	private final SolarSystemState ssState;
	private final int money;
	private final int researchPointsForCurrentResearchProject;
	private final Map<ResearchLine, Integer> nextResearchLineIndices;
	private final Collection<Building> availableBuildings;
	private final Collection<Technology> availableTechnologies;

	public MOO2State(final int year, final SolarSystemState ssState, final int money, final int researchPointsForCurrentResearchProject, final Map<ResearchLine, Integer> nextResearchLineIndices,
			final Collection<Building> availableBuildings, final Collection<Technology> availableTechnologies) {
		super();
		this.year = year;
		this.ssState = ssState;
		this.money = money;
		this.researchPointsForCurrentResearchProject = researchPointsForCurrentResearchProject;
		this.nextResearchLineIndices = nextResearchLineIndices;
		this.availableBuildings = availableBuildings;
		this.availableTechnologies = availableTechnologies;
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

	public Collection<Technology> getAvailableTechnologies() {
		return this.availableTechnologies;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.availableBuildings == null) ? 0 : this.availableBuildings.hashCode());
		result = prime * result + ((this.availableTechnologies == null) ? 0 : this.availableTechnologies.hashCode());
		result = prime * result + this.money;
		result = prime * result + ((this.nextResearchLineIndices == null) ? 0 : this.nextResearchLineIndices.hashCode());
		result = prime * result + this.researchPointsForCurrentResearchProject;
		result = prime * result + ((this.ssState == null) ? 0 : this.ssState.hashCode());
		result = prime * result + this.year;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		MOO2State other = (MOO2State) obj;
		if (this.availableBuildings == null) {
			if (other.availableBuildings != null) {
				return false;
			}
		} else if (!this.availableBuildings.equals(other.availableBuildings)) {
			return false;
		}
		if (this.availableTechnologies == null) {
			if (other.availableTechnologies != null) {
				return false;
			}
		} else if (!this.availableTechnologies.equals(other.availableTechnologies)) {
			return false;
		}
		if (this.money != other.money) {
			return false;
		}
		if (this.nextResearchLineIndices == null) {
			if (other.nextResearchLineIndices != null) {
				return false;
			}
		} else if (!this.nextResearchLineIndices.equals(other.nextResearchLineIndices)) {
			return false;
		}
		if (this.researchPointsForCurrentResearchProject != other.researchPointsForCurrentResearchProject) {
			return false;
		}
		if (this.ssState == null) {
			if (other.ssState != null) {
				return false;
			}
		} else if (!this.ssState.equals(other.ssState)) {
			return false;
		}
		if (this.year != other.year) {
			return false;
		}
		return true;
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
		sb.append("\tAvailable Buildings: " + this.availableBuildings);
		sb.append("\tAvailable Techs: " + this.availableTechnologies);
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
