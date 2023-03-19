package com.backend.qa.util;

import com.relevantcodes.extentreports.ExtentReports;


public class ExtentManager {

	private ExtentManager(){

	}

	private static ExtentReports extentReport;

	public static synchronized ExtentReports getInstance(String reportCompleteName) {
//		if (extentReport == null) {
			extentReport = new ExtentReports(reportCompleteName, false);
//		}
		return extentReport;
	}
	
	public static synchronized void removeInstance() {
		extentReport = null;
		
	}
	
}