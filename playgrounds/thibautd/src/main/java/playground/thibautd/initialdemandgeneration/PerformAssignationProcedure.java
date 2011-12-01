/* *********************************************************************** *
 * project: org.matsim.*
 * PerformAssignationProcedure.java
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
package playground.thibautd.initialdemandgeneration;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.Module;
import org.matsim.core.population.PopulationWriter;
import org.matsim.core.scenario.ScenarioImpl;
import org.matsim.core.scenario.ScenarioUtils;

import playground.balmermi.census2000v2.modules.PlansAnalyse;

import playground.thibautd.initialdemandgeneration.microcensusdata.MicroCensus;
import playground.thibautd.initialdemandgeneration.microcensusdata.MzGroupsModule;
import playground.thibautd.utils.MoreIOUtils;

/**
 * Executable class which assigns activity chains to agents.
 * It takes a config file as argument, with a special 
 * "Assignation" module.
 *
 * it must have the following fields:
 * <ul>
 * <li> "mzPopulationFile*": the path to the plans corresponding to mz act chains
 * <li> "dayOfWeek": "week", "saturday" or "sunday"
 * <li> "outputDir"
 * </ul>
 *
 * @author thibautd
 */
public class PerformAssignationProcedure {
	public static final String CONF_GROUP = "assignation";
	public static final String POP_FILE_FIELD_REGEXP = "mzPopulationFile.*";
	public static final String DOW_FIELD = "dayOfWeek";
	public static final String OUT_FIELD = "outputDir";

	public static void main( final String[] args ) {
		String configFile = args[ 0 ];
		MzGroupsModule groups = new MzGroupsModule();
		Config config = ConfigUtils.createConfig( );
		config.addModule( MzGroupsModule.NAME , groups );
		ConfigUtils.loadConfig( config , configFile );
		ScenarioImpl scen = (ScenarioImpl) ScenarioUtils.loadScenario( config );
		Module configGroup = config.getModule( CONF_GROUP );
		String outputDir = configGroup.getValue( OUT_FIELD );

		MoreIOUtils.initOut( outputDir );

		List<String> popFiles = new ArrayList<String>();

		for (Map.Entry<String, String> entry : configGroup.getParams().entrySet()) {
			if (entry.getKey().matches( POP_FILE_FIELD_REGEXP )) {
				popFiles.add( entry.getValue() );
			}
		}

		MicroCensus mz = new MicroCensus( groups , popFiles );
		PersonAssignActivityChains algo = new PersonAssignActivityChains(
				getDay( configGroup ),
				mz,
				scen.getKnowledges());

		for ( Person person : scen.getPopulation().getPersons().values() ) {
			algo.run( person );
		}

		new PlansAnalyse().run(scen.getPopulation());
		for ( Population pop : mz.getPopulations() ) {
			new PlansAnalyse().run(pop);
		}

		(new PopulationWriter(
				  scen.getPopulation(),
				  scen.getNetwork(),
				  scen.getKnowledges())).write(
			  outputDir + "plans-"+getDay( configGroup ) +".xml.gz");
	}

	private static PersonAssignActivityChains.DayOfWeek
			getDay( final Module configGroup ) {
		String value = configGroup.getValue( DOW_FIELD );
		return PersonAssignActivityChains.DayOfWeek.valueOf( value );
	}
}

