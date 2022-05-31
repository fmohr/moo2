package moo2.experiments;

import java.util.Collection;
import java.util.function.Predicate;

import moo2.gamemechanics.MOO2State;
import moo2.model.Building;
import moo2.model.Technology;

public class GoalDefinition implements Predicate<MOO2State> {
	private final Collection<Technology> requiredTechnologies;
	private final Collection<Building> requiredBuildings;

	public GoalDefinition(final Collection<Technology> requiredTechnologies, final Collection<Building> requiredBuildings) {
		super();
		this.requiredTechnologies = requiredTechnologies;
		this.requiredBuildings = requiredBuildings;
	}

	@Override
	public boolean test(final MOO2State t) {
		if (!t.getAvailableTechnologies().containsAll(this.requiredTechnologies)) {
			return false;
		}
		if (!t.getSsState().getPlanetStates().get(0).getBuildings().containsAll(this.requiredBuildings)) {
			return false;
		}
		return true;
	}
}
