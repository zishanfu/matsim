/* *********************************************************************** *
 * project: org.matsim.*
 * Obstcales.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

package playground.gregor.sim2d_v4.simulation.physics.algorithms;

import java.util.ArrayList;
import java.util.List;

import playground.gregor.sim2d_v4.cgal.CGAL;
import playground.gregor.sim2d_v4.debugger.VisDebugger;
import playground.gregor.sim2d_v4.simulation.physics.PhysicalSim2DSection;
import playground.gregor.sim2d_v4.simulation.physics.PhysicalSim2DSection.Segment;
import playground.gregor.sim2d_v4.simulation.physics.SimpleAgent;

public class Obstacles {
	

	private VisDebugger debugger;

	public List<Segment> computeObstacles(SimpleAgent agent) {
		List<Segment> ret = new ArrayList<Segment>(100);
		
		PhysicalSim2DSection psec = agent.getPSec();
		ret.addAll(psec.getObstacles());

		double[] aPos = agent.getPos();
		
		//agents from neighboring sections
		Segment[] openings = psec.getOpenings();
		for (int i = 0; i < openings.length; i++) {
			Segment opening = openings[i];
			PhysicalSim2DSection qSec = psec.getNeighbor(opening);
			if (qSec == null) {
				continue;
			}
			//first test whether agent is inside the neighboring section, then we ignore the obstacles so that the agent does not become trapped!
			double left = CGAL.isLeftOfLine(aPos[0], aPos[1], opening.x0, opening.y0, opening.x1, opening.y1);
			if (left >= 0) {
				continue;
			}
			for (Segment obstacle : qSec.getObstacles()) {
				if (bothEndesVisible(obstacle,opening,aPos)) {
					ret.add(obstacle);
//					if (this.debugger != null)
//					this.debugger.addLine(obstacle.x0, obstacle.y0, obstacle.x1, obstacle.y1, 255, 0, 0, 128);
					
				}
			}
			
		}
		
		return ret;
	}

	private boolean bothEndesVisible(Segment obstacle, Segment opening,
			double[] aPos) {
		
		double left0 = CGAL.isLeftOfLine(aPos[0],aPos[1], opening.x0, opening.y0, opening.x1, opening.y1);
		double left1 = CGAL.isLeftOfLine(obstacle.x0,obstacle.y0, opening.x0, opening.y0, opening.x1, opening.y1);
		if (left0*left1 > 0) {
			return false;
		}
		double left2 = CGAL.isLeftOfLine(opening.x0, opening.y0,aPos[0],aPos[1], obstacle.x0,obstacle.y0);
		double left3 = CGAL.isLeftOfLine(opening.x1, opening.y1,aPos[0],aPos[1], obstacle.x0,obstacle.y0);
		if (left2*left3 > 0) {
			return false;
		}
		double left4 = CGAL.isLeftOfLine(obstacle.x1,obstacle.y1, opening.x0, opening.y0, opening.x1, opening.y1);
		if (left0*left4 > 0) {
			return false;
		}
		double left5 = CGAL.isLeftOfLine(opening.x0, opening.y0,aPos[0],aPos[1], obstacle.x1,obstacle.y1);
		double left6 = CGAL.isLeftOfLine(opening.x1, opening.y1,aPos[0],aPos[1], obstacle.x1,obstacle.y1);
		if (left5*left6 > 0) {
			return false;
		}
		return true;
	}
	
	public void addDebugger(VisDebugger debug) {
		this.debugger = debug;
	}
	
	
}
