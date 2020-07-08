package moo2.model;

public enum Building implements ITechnology {

	WOHNUNGEN(0, Integer.MAX_VALUE, null, -1), AUTOMATED_FACTORIES(1, 60, ResearchLine.FERTIGUNG, 1),
	BIOSPHERES(1, 60, ResearchLine.BIOLOGIE, 0),
	INFANTERYBARRACKS(1, 10, null, -1),
	COLONY_BASE(0, 18, null, -1),
	STARBASE(2, 400, null, -1);

	int maintainanceCost;
	int buildCost;
	ResearchLine researchLine;
	int researchStep; // in which step of its research line is this building contained

	private Building(final int maintainanceCost, final int buildCost, final ResearchLine researchLine, final int researchStep) {
		this.maintainanceCost = maintainanceCost;
		this.buildCost = buildCost;
		this.researchLine = researchLine;
		this.researchStep = researchStep;
	}

	public int getMaintainanceCost() {
		return this.maintainanceCost;
	}

	public int getBuildCost() {
		return this.buildCost;
	}

	@Override
	public ResearchLine getResearchLine() {
		return this.researchLine;
	}

	@Override
	public int getResearchStep() {
		return this.researchStep;
	}

}
