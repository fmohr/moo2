package moo2.gamemechanics;

import moo2.model.Building;

public class PlanetAction {

	private final int farmers;
	private final int workers;
	private final int scientists;

	private final Building nextBuild;
	private final Building erasedBuilding;
	private final boolean purchaseConstruction;

	public PlanetAction(final int farmers, final int workers, final int scientists, final Building nextBuild, final Building erasedBuilding, final boolean purchaseConstruction) {
		super();
		this.farmers = farmers;
		this.workers = workers;
		this.scientists = scientists;
		this.nextBuild = nextBuild;
		this.erasedBuilding = erasedBuilding;
		this.purchaseConstruction = purchaseConstruction;
	}

	public int getFarmers() {
		return this.farmers;
	}

	public int getWorkers() {
		return this.workers;
	}

	public int getScientists() {
		return this.scientists;
	}

	public Building getNextBuild() {
		return this.nextBuild;
	}

	public Building getErasedBuilding() {
		return this.erasedBuilding;
	}

	public boolean isPurchaseConstruction() {
		return this.purchaseConstruction;
	}

	@Override
	public String toString() {
		return "PlanetAction [farmers=" + this.farmers + ", workers=" + this.workers + ", scientists=" + this.scientists + ", nextBuild=" + this.nextBuild + ", erasedBuilding=" + this.erasedBuilding + ", purchaseConstruction=" + this.purchaseConstruction + "]";
	}
}
