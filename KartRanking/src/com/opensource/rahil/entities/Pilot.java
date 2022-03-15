package com.opensource.rahil.entities;

import java.util.List;

/**
 * This Entity Class holds Pilot's Data
 * @author Mohamed Rahil Shaikh
 *
 */
public class Pilot {
	private Integer pilotID;
	private String name;
	private String startTime;
	private String endTime;
	private float avgSpeedOfRace;
	private List<Lap> laps;
	public boolean isHasCompletedAllLaps() {
		return hasCompletedAllLaps;
	}
	public void setHasCompletedAllLaps(boolean hasCompletedAllLaps) {
		this.hasCompletedAllLaps = hasCompletedAllLaps;
	}
	boolean hasCompletedAllLaps ; 
	public Pilot(Integer pilotID, String name, String startTime, String endTime, List<Lap> laps,boolean hasCompletedAllLaps) {
		super();
		this.pilotID = pilotID;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.laps = laps;
		this.hasCompletedAllLaps = hasCompletedAllLaps;
	}
	public Integer getPilotID() {
		return pilotID;
	}
	
	public String getName() {
		return name;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public List<Lap> getLaps() {
		return laps;
	}
	public void setLaps(List<Lap> laps) {
		this.laps = laps;
	}
	public float getAvgSpeedOfRace() {
		return avgSpeedOfRace;
	}
	public void setAvgSpeedOfRace(float avgSpeedOfRace) {
		this.avgSpeedOfRace = avgSpeedOfRace;
	}

}
