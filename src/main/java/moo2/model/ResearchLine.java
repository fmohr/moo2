package moo2.model;

import java.util.Arrays;
import java.util.List;

public enum ResearchLine {

	FERTIGUNG(80, 100, 150), POWER(), CHEMIE(), SOZIOLOGIE(), COMPUTER(),
	BIOLOGIE(8000),
	PHYSIK(), KRAFTFELDER();

	private ResearchLine(final Integer... requiredPoints) {
		this.requiredPoints = Arrays.asList(requiredPoints);
	}

	List<Integer> requiredPoints;

	public List<Integer> getRequiredPoints() {
		return this.requiredPoints;
	}
}
