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

	public Collection<Building> getBuildings() {
		return this.buildings;
	}

	public int getAccumulatedConstructionPoints() {
		return this.accumulatedConstructionPoints;
	}
}
