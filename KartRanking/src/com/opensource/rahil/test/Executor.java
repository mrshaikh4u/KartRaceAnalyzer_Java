package com.opensource.rahil.test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.opensource.rahil.entities.Lap;
import com.opensource.rahil.entities.Pilot;
import com.opensource.rahil.utils.ConsolePrinter;
import com.opensource.rahil.utils.Constants;
import com.opensource.rahil.utils.INDEXES;
import com.opensource.rahil.utils.OutPutPrinter;

public class Executor {
	static Map<Integer, Pilot> racers = new HashMap<>();
	static boolean raceFinished = false;
	static Lap raceBestLap;
	static Integer bestRacerID;
	static Integer winnerID;
	static OutPutPrinter printer = null;
	static {
		if(Constants.isToPrintToConsole)
		{
			printer = new ConsolePrinter();
		}
	}
	public static void main(String[] args) {
		Stream<String> lines = null;
		try {
			printer.print(":::::::: ||  RACE STARTED || ::::::::\n");
			lines = Files.lines(Paths.get(Constants.INPUT_FILE_NAME), Charset.defaultCharset());
			lines = lines.skip(Constants.NUMBER_ONE_INT);
			lines.forEachOrdered(line -> process(line));
			printer.print(":::::::: ||  BEST LAPS FOR THE RACERS || ::::::::\n");
			displayStats();
			displayOffSetForEachRacer();
		} catch (IOException e) {
			printer.print(e.getMessage());
		} finally {
			lines.close();
		}
	}

	/**
	 * This method calculates and prints time how long each rider has arrived after the winner
	 * This method only considers racers who has completed all 4 laps . 
	 */
	private static void displayOffSetForEachRacer() {
		Pilot winner = racers.get(winnerID);
		printer.print("\n:::::::: ||  DELAYS FOR EACH RACERS || ::::::::\n");
		for (Pilot racer : racers.values()) {
			if (racer.isHasCompletedAllLaps()) {
				long difference = timeDifference(winner, racer);
				long seconds = difference / Constants.MS_TO_SEC;
				long minutes = Constants.NUMBER_ZERO_INT;
				if (seconds > Constants.SEC_TO_MIN) {
					minutes = seconds / Constants.SEC_TO_MIN;
				}
				if (difference > Constants.NUMBER_ZERO_INT) {
					if(seconds ==  Constants.NUMBER_ZERO_INT) {
						printer.print("\t   Racer : " + racer.getPilotID() + " -- " + racer.getName() + " Arrived : "+difference + " ms later than winner \n");
					}
					else {
					printer.print("\t   Racer : " + racer.getPilotID() + " -- " + racer.getName() + " Arrived : "+minutes + "m" + seconds + "s later than winner \n");
					}
					}
			}
		}
	}

	/**
	 * This method calculate time difference between passed racers end times
	 * @param racer1
	 * @param racer2
	 * @return end time difference between passed racers
	 */
	private static long timeDifference(Pilot racer1, Pilot racer2) {
		String startTime = racer1.getEndTime();
		String endTime = racer2.getEndTime();

		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.RACE_TIME_FORMAT);
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = dateFormat.parse(startTime);
			endDate = dateFormat.parse(endTime);
		} catch (Exception ex) {
			printer.print("Error !! unable to parse race time "+ex.getMessage());
		}
		return (endDate.getTime() - startDate.getTime());
	}

	/**
	 * This method calculated and displays following statistics a> Best Lap of each
	 * racer b> Avg Speed of each racer c> Overall Best Lap of the race
	 */
	private static void displayStats() {
		for (Pilot racer : racers.values()) {
			float avgSpeed = Constants.NUMBER_ZERO_INT;
			int numberOfLaps = Constants.NUMBER_ZERO_INT;
			Lap bestLap = null;
			for (Lap lap : racer.getLaps()) {
				if (bestLap == null) {
					bestLap = lap;
					avgSpeed += lap.getAvgSpeed();
					numberOfLaps++;
					continue;
				}
				bestLap = calculateBestLap(bestLap, lap);
				avgSpeed += lap.getAvgSpeed();
				numberOfLaps++;

			}
			printer.print("\tRACER : " + racer.getPilotID() + " -- " + racer.getName() + "\n\t    Best Lap number : "
					+ bestLap.getLapNumber() + " Completed in time : " + bestLap.getTimeCompleted());
			avgSpeed = avgSpeed / numberOfLaps;
			printer.print("\t    Avg Speed for him in entire race was : "+ avgSpeed);
			if (raceBestLap == null) {
				raceBestLap = bestLap;
				bestRacerID = racer.getPilotID();
				continue;
			} else {
				Lap calculatedBestLap = calculateBestLap(raceBestLap, bestLap);
				// This is to check if current bestLap is not the one which is saved already
				if (calculatedBestLap.getTimeCompleted().equals(raceBestLap.getTimeCompleted())) {
					continue;
				} else {
					raceBestLap = calculatedBestLap;
					bestRacerID = racer.getPilotID();
				}

			}
		}
		printer.print("\n:::::::::: BEST LAP OF THE RACE  :::::: \n");
		printer.print("\t  RACER : " + racers.get(bestRacerID).getPilotID() + " -- "+racers.get(bestRacerID).getName()+"  LAP : " + raceBestLap.getLapNumber()
		+ "  Completed in time : " + raceBestLap.getTimeCompleted());
	}

	/**
	 * This method calculates best lap out of 2 passed laps by comparing their
	 * completed time
	 * 
	 * @param prevBestLap
	 * @param currentLap
	 * @return Best lap of the 2 passed laps
	 */
	private static Lap calculateBestLap(Lap prevBestLap, Lap currentLap) {
		String oldTime = prevBestLap.getTimeCompleted();
		String currentTime = currentLap.getTimeCompleted();
		SimpleDateFormat timestamp = new SimpleDateFormat(Constants.LAP_TIME_FORMAT);
		Date oldTimeD = null;
		Date currTimeD = null;
		try {
			oldTimeD = timestamp.parse(oldTime);
			currTimeD = timestamp.parse(currentTime);
			long diff = currTimeD.getTime() - oldTimeD.getTime();
			if (diff < Constants.NUMBER_ZERO_INT) {
				prevBestLap = currentLap;
			}
		} catch (ParseException e) {
			printer.print("Error !! Unable to parse times , ex" + e.getMessage());
		}
		return prevBestLap;
	}

	/**
	 * This method process input file line by line ignoring first (title line) first
	 * checks if racer doesn't exist it adds as a new racer if racer already exist
	 * it updates his lap information at last checks if race finished then updates
	 * end time for racer
	 * 
	 * @param line - read from input file
	 * @returns nothing
	 */
	private static Object process(String line) {
		List<String> list = Arrays.asList(line.split(Constants.WHITE_SPACE_IGNORE));
		if (!racerExist(list.get(INDEXES.PILOT_ID.ordinal()))) {
			addNewRacer(list);
		} else {
			addLapForExistingRacer(list);
		}
		if (raceFinished) {
			Pilot racer = racers.get(Integer.parseInt(list.get(INDEXES.PILOT_ID.ordinal())));
			racer.setEndTime(list.get(INDEXES.START_TIME.ordinal()));
		}
		return Constants.EMPTY_OBJ;
	}

	/**
	 * This method adds new lap for existing racer and also check if this was the
	 * 4th lap of the racer then mark race as finished and declare winner also mark
	 * for the racers who has completed his 4th lap for later use
	 * 
	 * @param list
	 */
	private static void addLapForExistingRacer(List<String> list) {
		Pilot racer = racers.get(Integer.parseInt(list.get(INDEXES.PILOT_ID.ordinal())));
		List<Lap> laps = racer.getLaps();
		Lap lap = new Lap(Integer.parseInt(list.get(INDEXES.LAP_NUM.ordinal())), list.get(INDEXES.LAP_TIME.ordinal()),
				Float.parseFloat(
						list.get(INDEXES.VELOCITY.ordinal()).replace(Constants.COMA_CHAR, Constants.DOT_CHAR)));
		laps.add(lap);
		if (Integer.parseInt(list.get(INDEXES.LAP_NUM.ordinal())) == Constants.NUMBER_FOUR_INT) {
			if (!raceFinished) {
				printer.print("\n:::::::: ||  RACE ENDED || ::::::::\n"
						+ "\n\t WINNER :: "
						+ "Racer  : " + list.get(INDEXES.PILOT_ID.ordinal())
						+ " -- "+list.get(INDEXES.PILOT_NAME.ordinal())+"\n");
				raceFinished = Constants.BOOLEAN_TRUE;
				winnerID = Integer.parseInt(list.get(INDEXES.PILOT_ID.ordinal()));
			}
			racer.setHasCompletedAllLaps(Constants.BOOLEAN_TRUE);
		}
	}

	/**
	 * This methods registers new racer
	 * 
	 * @param list line read from input file
	 */
	private static void addNewRacer(List<String> list) {
		printer.print("\tADDING RACER ID : " + list.get(INDEXES.PILOT_ID.ordinal()));
		Lap lap = new Lap(Integer.parseInt(list.get(INDEXES.LAP_NUM.ordinal())), list.get(INDEXES.LAP_TIME.ordinal()),
				Float.parseFloat(
						list.get(INDEXES.VELOCITY.ordinal()).replace(Constants.COMA_CHAR, Constants.DOT_CHAR)));
		List<Lap> laps = new ArrayList<>();
		laps.add(lap);
		Pilot racer = new Pilot(Integer.parseInt(list.get(INDEXES.PILOT_ID.ordinal())),
				list.get(INDEXES.PILOT_NAME.ordinal()), list.get(INDEXES.START_TIME.ordinal()), Constants.EMPTY_OBJ,
				laps, Constants.BOOLEAN_FALSE);
		racers.put(Integer.parseInt(list.get(INDEXES.PILOT_ID.ordinal())), racer);
	}

	/**
	 * This methods checks if pilot already exist in created hashMap or not
	 * 
	 * @param pilotID - to check
	 * @return true - if already exist else false
	 */
	private static boolean racerExist(String pilotID) {
		return racers.containsKey(Integer.parseInt(pilotID));
	}
}
