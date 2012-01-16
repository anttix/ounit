/*
 * OUnit - an OPAQUE compliant framework for Computer Aided Testing
 *
 * Copyright (C) 2010, 2011  Antti Andreimann
 *
 * This file is part of OUnit.
 *
 * OUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OUnit.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Based partly on org.apache.maven.cli.MavenCli
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 */

package com.googlecode.ounit.executor;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.maven.Maven;
import org.apache.maven.cli.ExecutionEventLogger;
import org.apache.maven.cli.MavenLoggerManager;
import org.apache.maven.cli.PrintStreamLogger;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Activation;
import org.apache.maven.model.Build;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.properties.internal.EnvironmentUtils;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.sonatype.aether.transfer.TransferListener;

/**
 * This is frigging magic. Look at org.apache.maven.cli.MavenCli
 * 
 * @author anttix
 *
 */
public class MavenRunner {
	public static final List<String> DEFAULT_GOAL = null;

	private int logLevel = MavenExecutionRequest.LOGGING_LEVEL_INFO;
	private Properties systemProperties;
	private Maven maven;
	private DefaultPlexusContainer container;
	private TransferListener transferListener;
	private ExecutionListener executionListener;
    private ModelProcessor modelProcessor;
	private PrintStreamLogger logger;
	private List<Profile> baseProfiles;
	
	private static ClassWorld world = null;
	
	MavenRunner() throws PlexusContainerException, ComponentLookupException,
			IOException {
		logger = new PrintStreamLogger( System.out );
		initialize();
	}

	MavenRunner(PrintStreamLogger logger) throws PlexusContainerException,
			ComponentLookupException, IOException {
		this.logger = logger;
		initialize();
	}

	protected void initialize() throws PlexusContainerException,
			ComponentLookupException, IOException {

		logger.setThreshold( logLevel );
		
		if(world == null)
			world = new ClassWorld( "plexus.core", Thread.currentThread().getContextClassLoader() );
        ContainerConfiguration cc = new DefaultContainerConfiguration()
            .setClassWorld( world )
            .setName( "maven" );
		container = new DefaultPlexusContainer( cc );
		executionListener = new ExecutionEventLogger( logger );
		transferListener = new MavenTransferListener( logger );
		container.setLoggerManager( new MavenLoggerManager( logger ) );
		
        Thread.currentThread().setContextClassLoader( container.getContainerRealm() );

		maven = container.lookup( Maven.class );
		modelProcessor = container.lookup( ModelProcessor.class );

		// A hack to mitigate the damage done by mvn -Dtest=....
		System.clearProperty("test");

        systemProperties = new Properties();
        systemProperties.putAll( System.getProperties() );
        EnvironmentUtils.addEnvVars( systemProperties );
        
        // Load base profiles from a config file in resources
		baseProfiles = modelProcessor.read(
				getClass().getResourceAsStream("/maven/baseProfiles.xml"), null)
				.getProfiles();
	}

	public MavenExecutionResult execute(File baseDirectory, List<String> goals,
			String outputDirectory) {

		baseDirectory = baseDirectory.getAbsoluteFile();
		File pom = modelProcessor.locatePom( baseDirectory );

		MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request
        	.setBaseDirectory( baseDirectory )
        	.setPom( pom )
            .setExecutionListener( executionListener )
            .setTransferListener( transferListener )
            .setSystemProperties( systemProperties )
            //.setUserProperties( userProperties )
            .setLoggingLevel( logLevel )
            .setInteractiveMode( false )
            //.setOffline( true )
            .setCacheNotFound( true )
            .setCacheTransferError( false )
            .setGoals( goals );

        /* Apply base profiles */
        for(Profile p: baseProfiles)
        	request.addProfile( p.clone() );

		// TODO: Implement system specific settings.xml file

        /* Create a build profile to set outputDirectory */
		if (outputDirectory != null) {
			Build b = new Build();
			b.setDirectory(outputDirectory);
			Activation a = new Activation();
			a.setActiveByDefault(true);
			Profile p = new Profile();
			p.setId("ounitExecutorCustomOutputDirectoryProfile");
			p.setActivation(a);
			p.setBuild(b);
			request.addProfile(p);
		}

        container.getLoggerManager().setThresholds( request.getLoggingLevel() );
        
        MavenExecutionResult r = maven.execute(request);
        
		return r;
	}

	protected PrintStreamLogger getLogger() {
		return logger;
	}

	public void setLog(String fname) throws FileNotFoundException {
		setLog( new PrintStream(fname) );
	}
	
	public void setLog(File logFile) throws FileNotFoundException {
		setLog(new PrintStream(logFile) );
	}
	
	public void setLog(OutputStream logStream) {
		setLog(new PrintStream(logStream) );
	}
	
	public void setLog(PrintStream logStream) {
		logger.setStream( logStream );
	}

	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
		logger.setThreshold( logLevel );
	}

	public MavenExecutionResult execute(File baseDirectory) {
		String [] goals = { "clean", "verify" };
		return execute(baseDirectory, goals);
	}
	
	public MavenExecutionResult execute(File baseDirectory, String goal) {
		return execute(baseDirectory, goal, null);
	}

	public MavenExecutionResult execute(File baseDirectory, String [] goals) {
		return execute(baseDirectory, goals, null);		
	}
	
	public MavenExecutionResult execute(File baseDirectory, String[] goals,
			String outputDirectory) {

		return execute(baseDirectory, Arrays.asList(goals), outputDirectory);		
	}

	public MavenExecutionResult execute(File baseDirectory, String goal,
			String outputDirectory) {

		String [] goals = { goal };
		return execute(baseDirectory, goals, outputDirectory);
	}
	
	public Properties getModelProperties(File dir) throws IOException  {
		return modelProcessor.read(
				new FileInputStream(new File(dir, "pom.xml")), null)
				.getProperties();
	}
}