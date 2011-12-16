/*
 * ounit - an OPAQUE compliant framework for Computer Aided Testing
 *
 * Copyright (C) 2010  Antti Andreimann
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.googlecode.ounit.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Parse output from testing subsystems and generate test results to be passed
 * back to the LMS.
 * We attach to the verify lifecycle phase for a good reason:
 * We can not parse results any earlier, because teacher tests normally run
 * during integration-test in order to have access to services started
 * in pre-integration-test phase. We may not use post-integration-test either
 * because this is reserved for cleanup operations and should be allowed to
 * complete before we potentially fail the build.
 *  
 * @author <a href="mailto:anttix@users.sourceforge.net">Antti Andreimann</a>
 * @version $Id$
 * 
 * @goal generate-results
 * @phase verify
 */

public class GenerateResultsMojo extends MojoData {
	private File createOutputFile(String name) throws IOException {
		File f = new File(getOunitDirectory() + "/" + name);
		f.getParentFile().mkdirs();
		f.createNewFile();

		return f;
	}

	public void execute() throws MojoExecutionException {
		Log log = getLog();

		try {
			ResultsGenerator gen = new ResultsGenerator(this);

			PrintStream fstream = new PrintStream(
					createOutputFile("results.html"));
			fstream.println(gen.generateHtmlReport());
			log.info("");
			for (String l : gen.generateTextReport().split("\n")) {
				log.info(l);
			}
			log.info("");
			
			Properties marks = gen.generateMarks();
			marks.store(new FileOutputStream(createOutputFile("marks.properties")), "");
			log.info("Total Score: " + marks.getProperty("default") + " %");
			log.info("");
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to parse test results", e);
		}
	}
}
