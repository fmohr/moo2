package moo2.gamemechanics;

import java.util.List;

public class SolarSystemState {

	private final List<PlanetState> planetStates;

	public SolarSystemState(final List<PlanetState> planetStates) {
		super();
		this.planetStates = planetStates;
	}

	public List<PlanetState> getPlanetStates() {
		return this.planetStates;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.planetStates == null) ? 0 : this.planetStates.hashCode());
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
		SolarSystemState other = (SolarSystemState) obj;
		if (this.planetStates == null) {
			if (other.planetStates != null) {
				return false;
			}
		} else if (!this.planetStates.equals(other.planetStates)) {
			return false;
		}
		return true;
	}
}
