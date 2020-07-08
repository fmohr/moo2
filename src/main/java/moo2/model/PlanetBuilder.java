package moo2.model;

import java.util.Collection;
import java.util.HashSet;

import moo2.gamemechanics.PlanetState;

public class PlanetBuilder {

	private PlanetSize size;
	private PlanetClimate climate;
	private PlanetMinerals minerals;
	private int population = 0;
	private boolean highGravity = false;
	private boolean lowGravity = false;
	private boolean artifacts = false;
	private Collection<Building> buildings = new HashSet<>();
	private int constructionPoints = 0;

	public PlanetBuilder withSize(final PlanetSize size) {
		this.size = size;
		return this;
	}

	public PlanetBuilder withClimate(final PlanetClimate climate) {
		this.climate = climate;
		return this;
	}

	public PlanetBuilder withMinerals(final PlanetMinerals minerals) {
		this.minerals = minerals;
		return this;
	}

	public PlanetBuilder withPopulation(final int population) {
		this.population = population;
		return this;
	}

	public PlanetBuilder withBuilding(final Building b) {
		this.buildings.add(b);
		return this;
	}

	public PlanetBuilder withStartConfiguration() {
		return this.withClimate(PlanetClimate.TERRANISCH).withSize(PlanetSize.MITTEL).withMinerals(PlanetMinerals.ERGIEBIG).withPopulation(8000000).withBuilding(Building.INFANTERYBARRACKS).withBuilding(Building.STARBASE);
	}

	public PlanetState build() {
		return new PlanetState(this.climate, this.minerals, this.size, this.highGravity, this.lowGravity, this.artifacts, this.population, this.buildings, this.constructionPoints);
	}
}
