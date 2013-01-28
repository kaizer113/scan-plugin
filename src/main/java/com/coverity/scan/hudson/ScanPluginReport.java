package com.coverity.scan.hudson;

import hudson.model.Action;

public class ScanPluginReport implements Action {

	private boolean dataUpdated=false;
	private int buildNumber;
	private String projectName;
	
	public ScanPluginReport(String theProjectName, int theBuildNumber) {
		buildNumber=theBuildNumber;
		projectName=theProjectName;
		dataUpdated=false;		
	}
	
    /**
     * {@inheritDoc}
     */
	public String getIconFileName() {
		// TODO Auto-generated method stub
		 return ScanPluginConfiguration.ICON_FILE_NAME;
	}

    /**
     * {@inheritDoc}
     */
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return ScanPluginConfiguration.DISPLAY_NAME;
	}

    /**
     * {@inheritDoc}
     */
	public String getUrlName() {
		// TODO Auto-generated method stub
		return ScanPluginConfiguration.URL;
	}

    /**
     * Returns no data yet message
     */
	public String getNoDataYet() {
		// TODO Auto-generated method stub
		return ScanPluginConfiguration.NO_DATA_YET;
	}
	
    /**
     * Returns yes data message
     */
	public String getYesData() {
		// TODO Auto-generated method stub
		return ScanPluginConfiguration.YES_DATA;
	}
	
    /**
     * Returns <code>true</code> if Analysis data has already been obtained.
     *
     * @return Value for property 'dataUpdated'.
     */
    public boolean isDataUpdated() {
    	return dataUpdated;
    }
	
    /**
     * Returns build number for this report
     */
	public int getBuildNumber() {
	
		// TODO Auto-generated method stub
		return buildNumber;
	}    
    
    /**
     * Returns the project name for this report
     */
	public String getProjectName() {
	
		// TODO Auto-generated method stub
		return projectName;
	}
	
}
