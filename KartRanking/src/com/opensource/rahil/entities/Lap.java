package com.opensource.rahil.entities;

/**
 * This Entity Class holds Lap information for each racer
 * @author Mohamed Rahil Shaikh
 *
 */
public class Lap {
	private int lapNumber;
	private String timeCompleted;
	private float avgSpeed;
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Lap) {
			return ((Lap) obj).getLapNumber() == this.lapNumber;
		}
		return super.equals(obj);
	}
	public Lap(int lapNumber, String timeCompleted, float avgSpeed) {
		super();
		this.lapNumber = lapNumber;
		this.timeCompleted = timeCompleted;
		this.avgSpeed = avgSpeed;
	}
	public int getLapNumber() {
		return lapNumber;
	}
	
	public String getTimeCompleted() {
		return timeCompleted;
	}
	
	public float getAvgSpeed() {
		return avgSpeed;
	}
	
}
