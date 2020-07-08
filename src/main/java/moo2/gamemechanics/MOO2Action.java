package moo2.gamemechanics;

import java.util.List;

import moo2.model.ITechnology;
import moo2.model.ResearchLine;

public class MOO2Action {

	private final List<PlanetAction> planetActions;
	private final ResearchLine currentResearchLine; // for creative folks
	private final ITechnology currentResearchTechnology; // for non-creative folks

	public MOO2Action(final List<PlanetAction> planetActions, final ResearchLine currentResearchLine) {
		super();
		this.planetActions = planetActions;
		this.currentResearchLine = currentResearchLine;
		this.currentResearchTechnology = null;
	}

	public MOO2Action(final List<PlanetAction> planetActions, final ITechnology currentResearchTechnology) {
		super();
		this.planetActions = planetActions;
		this.currentResearchTechnology = currentResearchTechnology;
		this.currentResearchLine = null;
	}

	public ResearchLine getCurrentResearchLine() {
		return this.currentResearchLine;
	}

	public ITechnology getCurrentResearchTechnology() {
		return this.currentResearchTechnology;
	}

	public List<PlanetAction> getPlanetActions() {
		return this.planetActions;
	}
}
