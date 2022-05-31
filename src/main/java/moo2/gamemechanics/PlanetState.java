package moo2.gamemechanics;

import java.util.Collection;

import moo2.model.Building;
import moo2.model.PlanetClimate;
import moo2.model.PlanetMinerals;
import moo2.model.PlanetSize;

public class PlanetState {

	/* physical planet properties */
	private final PlanetClimate climate;
	private final PlanetMinerals minerals;
	private final PlanetSize size;
	private final boolean highGravity;
	private final boolean lowGravity;

	/* special planet properties */
	private final boolean artifactWorld;

	/* civilization properties */
	private final int population;
	private final Collection<Building> buildings;

	/* construction properties */
	private final int accumulatedConstructionPoints;

	public PlanetState(final PlanetState prevState, final int newPopulation, final int newConstructionPoints) {
		this(prevState.climate, prevState.minerals, prevState.size, prevState.highGravity, prevState.lowGravity, prevState.artifactWorld, newPopulation, prevState.buildings, newConstructionPoints);
	}

	public PlanetState(final PlanetState prevState, final int newPopulation, final int newConstructionPoints, final Collection<Building> buildings) {
		this(prevState.climate, prevState.minerals, prevState.size, prevState.highGravity, prevState.lowGravity, prevState.artifactWorld, newPopulation, buildings, newConstructionPoints);
	}

	public PlanetState(final PlanetClimate climate, final PlanetMinerals minerals, final PlanetSize size, final boolean highGravity, final boolean lowGravity, final boolean artifactWorld, final int population, final Collection<Building> buildings, final int accumulatedConstructionPoints) {
		super();
		this.climate = climate;
		this.minerals = minerals;
		this.size = size;
		this.highGravity = highGravity;
		this.lowGravity = lowGravity;
		this.artifactWorld = artifactWorld;
		this.population = population;
		this.buildings = buildings;
		this.accumulatedConstructionPoints = accumulatedConstructionPoints;
	}

	public PlanetClimate getClimate() {
		return this.climate;
	}

	public PlanetMinerals getMinerals() {
		return this.minerals;
	}

	public PlanetSize getSize() {
		return this.size;
	}

	public boolean isHighGravity() {
		return this.highGravity;
	}

	public boolean isLowGravity() {
		return this.lowGravity;
	}

	public boolean isArtifactWorld() {
		return this.artifactWorld;
	}

	public int getPopulation() {
		return this.population;
	}

	public int getPopulationRound() {
		return (int)Math.floor(this.population / Math.pow(10, 6));
	}

	public Collection<Building> getBuildings() {
		return this.buildings;
	}

	public int getAccumulatedConstructionPoints() {
		return this.accumulatedConstructionPoints;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.accumulatedConstructionPoints;
		result = prime * result + (this.artifactWorld ? 1231 : 1237);
		result = prime * result + ((this.buildings == null) ? 0 : this.buildings.hashCode());
		result = prime * result + ((this.climate == null) ? 0 : this.climate.hashCode());
		result = prime * result + (this.highGravity ? 1231 : 1237);
		result = prime * result + (this.lowGravity ? 1231 : 1237);
		result = prime * result + ((this.minerals == null) ? 0 : this.minerals.hashCode());
		result = prime * result + this.population;
		result = prime * result + ((this.size == null) ? 0 : this.size.hashCode());
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
		PlanetState other = (PlanetState) obj;
		if (this.accumulatedConstructionPoints != other.accumulatedConstructionPoints) {
			return false;
		}
		if (this.artifactWorld != other.artifactWorld) {
			return false;
		}
		if (this.buildings == null) {
			if (other.buildings != null) {
				return false;
			}
		} else if (!this.buildings.equals(other.buildings)) {
			return false;
		}
		if (this.climate != other.climate) {
			return false;
		}
		if (this.highGravity != other.highGravity) {
			return false;
		}
		if (this.lowGravity != other.lowGravity) {
			return false;
		}
		if (this.minerals != other.minerals) {
			return false;
		}
		if (this.population != other.population) {
			return false;
		}
		if (this.size != other.size) {
			return false;
		}
		return true;
	}
}
