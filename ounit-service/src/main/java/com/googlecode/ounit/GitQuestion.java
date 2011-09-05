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
 */

package com.googlecode.ounit;

import static com.googlecode.ounit.OunitConfig.*;
import static com.googlecode.ounit.OunitUtil.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

public class GitQuestion extends QuestionBase {
	private org.slf4j.Logger _log;
	private org.slf4j.Logger getLog() {
		if(_log == null)
			_log = org.slf4j.LoggerFactory.getLogger(this.getClass());
		return _log;
	}
	
	static Map<String, Long> pullTimes = Collections.synchronizedMap(new HashMap<String, Long>());
	File cloneDir;
	
	// TODO: Support username/password via
	// 		 .setCredentialsProvider(credentialsProvider)
	
	/**
	 * Load question from a GIT repository.
	 * 
	 * @param questionID
	 * @param questionVersion
	 * @param questionBaseURL
	 * @throws IOException
	 */
	public GitQuestion(String id, String version, String baseURL) {
		super(id, version, baseURL);
		findRepo();
	}

	@Override
	public String findHeadRevision() {
		if(cloneDir == null)
			findRepo();

		try {
			Repository repo = Git.open(cloneDir).getRepository();
			Ref ref = repo.getRef(version);
			if(ref == null)
				ref = repo.getRef(Constants.MASTER);
			
			return ref.getObjectId().name();			
		} catch (IOException e) {
			throw new RuntimeException("Unable to find HEAD revision", e);
		}
	}

	@Override
	protected void fetchQuestion() {
		File cacheDir = new File(WORKDIR, REPO_DIR);
		srcDir = new File(cacheDir, id + "-" + revision);
		
		if(srcDir.exists()) {
			getLog().debug("Question found in {}", srcDir);
			return;
		}
		
		try {
			getLog().debug("Checking out revision {} from {} to {}",
					new Object[] { revision, cloneDir, srcDir });
			
			Git git = Git.wrap(new RepositoryBuilder()
				.setMustExist(true)
				.setIndexFile(new File(srcDir, ".gitindex"))
				.setGitDir(cloneDir)
				.setWorkTree(srcDir)
				.build());

			git.checkout()
				.setName(revision)
				.call();
		} catch(Exception e) {
			getLog().error("Error checking out revision " + revision + " from "
					+ cloneDir, e);
			throw new RuntimeException("Failed to fetch question", e);
		}

	}
	
	private void findRepo() {
		File cacheDir = new File(WORKDIR, REPO_DIR);
		cloneDir = new File(cacheDir, id + ".git");
		if(!cloneDir.isDirectory()) {
			cloneRepo();
		} else {
			// FIXME: Handle baseURL changes.
			pull();
		}
	}

	private void cloneRepo() {
		String url = baseURL + "/" + id;
		
		getLog().debug("Cloning {} to {}", new Object[] { id, cloneDir } );
		try {
			Git.cloneRepository()
				.setBare(true)
				.setDirectory(cloneDir)
				.setTimeout(SCM_TIMEOUT)
				.setURI(url)
				.call();
		} catch (Exception e) {
			getLog().error("Failed to clone question from {} to {}", new Object[] { url, cloneDir });
			deleteDirectory(cloneDir);
			throw new RuntimeException("Failed to clone question repository", e);
		}
		pullTimes.put(id, System.currentTimeMillis());
	}
	
	private void pull() {
		Long pullTime = pullTimes.get(id);
		long now = System.currentTimeMillis();
		
		if (pullTime != null && now - pullTime.longValue() < SCM_TTL * 1000) {
			getLog().debug("Skipping update, already pulled {} ms ago", now - pullTime.longValue());
			return;
		}
		
		try {
			getLog().debug("Pulling in {}", cloneDir);
			Git.open(cloneDir).fetch()
				.setTimeout(SCM_TIMEOUT)
				.call();
			pullTimes.put(id, System.currentTimeMillis());
		} catch (Exception e) {
			// Failing a pull is not to be considered fatal ...
			getLog().error("Error while updating question " + id + "-" + version, e);
		}
	}
}