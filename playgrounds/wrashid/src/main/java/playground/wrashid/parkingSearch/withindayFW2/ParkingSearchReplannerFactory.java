/* *********************************************************************** *
 * project: org.matsim.*
 * RandomSearchReplannerFactory.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package playground.wrashid.parkingSearch.withindayFW2;

import org.matsim.api.core.v01.Scenario;
import org.matsim.withinday.mobsim.WithinDayEngine;
import org.matsim.withinday.replanning.replanners.interfaces.WithinDayDuringLegReplannerFactory;

public class ParkingSearchReplannerFactory extends WithinDayDuringLegReplannerFactory {

	private final Scenario scenario;
	private final ParkingAgentsTracker parkingAgentsTracker;
	
	public ParkingSearchReplannerFactory(WithinDayEngine withinDayEngine, Scenario scenario, ParkingAgentsTracker parkingAgentsTracker) {
		super(withinDayEngine);
		
		this.scenario = scenario;
		this.parkingAgentsTracker = parkingAgentsTracker;
	}

	@Override
	public ParkingSearchReplanner createReplanner() {
		ParkingSearchReplanner replanner = new ParkingSearchReplanner(super.getId(), scenario, 
				this.getWithinDayEngine().getInternalInterface(), 
				this.getWithinDayEngine().getTripRouterFactory().instantiateAndConfigureTripRouter(), parkingAgentsTracker);
		return replanner;
	}

}
