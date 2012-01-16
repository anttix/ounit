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

package com.googlecode.ounit.maven;

import java.util.ArrayList;
import java.util.List;

public class TestResults {
	private int totalTests;
	private int totalErrors;
	private int totalFailures;
	private int totalSkipped;
	private double totalElapsedTime;
	//private List <?> failureDetails;
	private List<FailureDetail> failureDetails = new ArrayList<FailureDetail>();
	
	public int getTotalSucceeded() {
		return getTotalTests() - getTotalErrors() - getTotalFailures() - getTotalSkipped();
	}

	public int getTotalTests() {
		return totalTests;
	}

	public int getTotalErrors() {
		return totalErrors;
	}

	public int getTotalFailures() {
		return totalFailures;
	}

	public int getTotalSkipped() {
		return totalSkipped;
	}
	
	public double getTotalElapsedTime() {
		return totalElapsedTime;
	}
	
	public double getTotalPercentage() {
                if(getTotalTests() == 0)
                    return 0;
		return (double)getTotalSucceeded() / getTotalTests() * 100;
	}
	
	public List <FailureDetail> getFailureDetails() {
		return failureDetails;
	}
	
	public void setTotalTests(int totalTests) {
		this.totalTests = totalTests;
	}

	public void setTotalErrors(int totalErrors) {
		this.totalErrors = totalErrors;
	}

	public void setTotalFailures(int totalFailures) {
		this.totalFailures = totalFailures;
	}

	public void setTotalSkipped(int totalSkipped) {
		this.totalSkipped = totalSkipped;
	}

	public void setTotalElapsedTime(double totalElapsedTime) {
		this.totalElapsedTime = totalElapsedTime;
	}

	public void setFailureDetails(List<FailureDetail> failureDetails) {
		this.failureDetails = failureDetails;
	}

	public void addFailure(FailureDetail failure) {
		failureDetails.add(failure);
	}
}
