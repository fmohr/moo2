package moo2.model;

public interface ITechnology {


	public ResearchLine getResearchLine();

	public int getResearchStep();

	default public int getRequiredResearchPoints() {
		return this.getResearchLine().getRequiredPoints().get(this.getResearchStep());
	}
}
