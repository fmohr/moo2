package moo2.model;

public enum Technology implements ITechnology {

	REINFORCED_HULL(ResearchLine.FERTIGUNG, 0)
	;

	ResearchLine researchLine;
	int researchStep; // in which step of its research line is this building contained

	private Technology(final ResearchLine researchLine, final int researchStep) {
		this.researchLine = researchLine;
		this.researchStep = researchStep;
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
