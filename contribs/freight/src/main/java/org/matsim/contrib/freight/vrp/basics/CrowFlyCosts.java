/*******************************************************************************
 * Copyright (C) 2011 Stefan Schroeder.
 * eMail: stefan.schroeder@kit.edu
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/**
 * 
 */
package org.matsim.contrib.freight.vrp.basics;

import org.apache.log4j.Logger;



/**
 * @author stefan schroeder
 *
 */
public class CrowFlyCosts implements Costs{
	
	private static Logger logger = Logger.getLogger(CrowFlyCosts.class);
	
	public int speed = 1;
	
	public double detourFactor = 1.0;
	
	private Locations locations;
	
	private CarrierCostParams costParams = new CarrierCostParams(1.0,1.0,0.0,0.0,0.0,1000.0); 
		
	public CrowFlyCosts(Locations locations) {
		super();
		this.locations = locations;
	}

	@Override
	public Double getTransportCost(String fromId, String toId, double time) {
		Double dist;
		try{
			dist = EuclideanDistanceCalculator.calculateDistance(locations.getCoord(fromId), locations.getCoord(toId))*detourFactor;
		}
		catch(NullPointerException e){
			logger.debug(fromId + " " + toId + " no dist found");
			throw new NullPointerException();
		}
		return costParams.transportCost_per_meter*dist; 
	}

	@Override
	public Double getTransportTime(String fromId, String toId, double time) {
		return getTransportCost(fromId, toId, 0.0)/speed;
	}

	@Override
	public Double getBackwardTransportCost(String fromId, String toId, double arrivalTime) {
		return getTransportCost(fromId, toId, arrivalTime);
	}

	@Override
	public Double getBackwardTransportTime(String fromId, String toId, double arrivalTime) {
		return getTransportTime(fromId, toId, arrivalTime);
	}

	@Override
	public CarrierCostParams getCostParams() {
		return costParams;
	}

}
