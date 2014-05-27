package com.srlab.line;

public class IConstant {
	/* In case of change of the system change only this path */
	public static final String BASE_DIR = "/home/parvez/Documents/Research/ProjectLineMapping";
	public static final String RESULT_FILE = "MappingResult.txt";
	public static final String INDIVIDUAL_RESULT_FILE = "IndividualMappingResult.txt"; 
	public static final String Error_LOGFILE = "ErrorLog.txt"; 
	
	public static final String LDIFF_PATH = IConstant.BASE_DIR+System.getProperty("file.separator")+"sdiff-0.1.2/files/scripts/ldiff.pl";
	public static final String LDIFF_RESULT_PATH = IConstant.BASE_DIR+System.getProperty("file.separator")+"Data/ldiff";
	public static final String GIT_REPOS = IConstant.BASE_DIR+System.getProperty("file.separator")+"Data/gitRepos";
}
