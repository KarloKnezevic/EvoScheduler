package hr.fer.hmo.projectscheduling.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Individual
 * @author Karlo Knežević, karlo.knezevic@fer
 * @author Ivo Majić, ivo.majic2@fer.hr
 * @version 1.0
 */
public class Individual implements Comparable<Individual>, Cloneable {

	private List<ArrayList<WorkUnit>> projectWorkLists;
	protected List<ArrayList<WorkUnit>> personWorkLists;
	
	protected List<TreeSet<WorkUnitInterval>> personWorkIntervals;
	
	protected int actualDuration, waitDuration;
	
	protected int projectSize;
	
	public Individual() {
		
		try {
			this.projectWorkLists = Util.readInputFile("/HOM-project.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.actualDuration = Integer.MAX_VALUE;
		this.waitDuration = Integer.MAX_VALUE;
		
		projectSize = projectWorkLists.size();
		
		// Initialize person work list
		personWorkLists = new ArrayList<ArrayList<WorkUnit>>();
		for (int index = 0; index < projectSize; index++)
			personWorkLists.add(new ArrayList<WorkUnit>());
		
		for (ArrayList<WorkUnit> projectWorkList : projectWorkLists) {
			
			for (WorkUnit workUnit : projectWorkList) {
				
				int personID = workUnit.getWorkerId();
				
				ArrayList<WorkUnit> personWorkList = 
						personWorkLists.get(personID);
				personWorkList.add(workUnit);
				
			}
			
		}
		
	}

	public Individual(List<ArrayList<WorkUnit>> personWorkLists) {

		this();
		
		this.personWorkLists = personWorkLists;

	};

	public void calculateFitness() {

		this.personWorkIntervals = new ArrayList<TreeSet<WorkUnitInterval>>();
		List<TreeSet<WorkUnit>> unlockedPersonWorkLists = 
				new ArrayList<TreeSet<WorkUnit>>();

		int workUnitCount = projectSize * projectSize;
		boolean[] workUnitAdded = new boolean[projectSize];

		int timeOfInterest = 0;
		int finishedWorkUnits = 0;
		
		// Initialize sets and lists
		for (int index = 0; index < projectSize; index++) {
			personWorkIntervals.add(new TreeSet<WorkUnitInterval>());
			unlockedPersonWorkLists.add(new TreeSet<WorkUnit>());
		}
		
		// Add initially unlocked work units to the unlocked list
		for (ArrayList<WorkUnit> projectWorkList : projectWorkLists) {
			
			WorkUnit workUnit = projectWorkList.get(0);
			TreeSet<WorkUnit> unlockedPersonWorkList = 
					unlockedPersonWorkLists.get(workUnit.getWorkerId());
			unlockedPersonWorkList.add(workUnit);
			
		}

		// Repeat until all work units are done
		while (finishedWorkUnits < workUnitCount) {
			
			// Go trough all currently active work units and find the one with
			// the lowest end time, thats the new time of interest
			int minEndTime = Integer.MAX_VALUE;
			for (TreeSet<WorkUnitInterval> personWorkInterval : personWorkIntervals) {

				if (!personWorkInterval.isEmpty()) {
					WorkUnitInterval workUnit = personWorkInterval.last();
					int workUnitEndTime = workUnit.getEndTime();
					if (workUnitEndTime < minEndTime && !workUnit.isFinished()) 
						minEndTime = workUnitEndTime;
				}

			}
			
			for (TreeSet<WorkUnitInterval> personWorkInterval : personWorkIntervals) {

				if (!personWorkInterval.isEmpty()) {
					WorkUnitInterval workUnit = personWorkInterval.last();
					int workUnitEndTime = workUnit.getEndTime();
					if (workUnitEndTime == minEndTime) {
						workUnit.setIsFinished();
						finishedWorkUnits++;
					}
				}

			}

			if (minEndTime != Integer.MAX_VALUE)
				timeOfInterest = minEndTime;
			
			int personId = 0;
			for (TreeSet<WorkUnitInterval> personWorkInterval : personWorkIntervals) {
				
				int endTime = 0;
				if (!personWorkInterval.isEmpty()) {
					endTime = personWorkInterval.last().getEndTime();
				}
				
				if (timeOfInterest >= endTime) {
					
					if (!personWorkInterval.isEmpty()) {
						
						WorkUnit lastWorkUnit = 
								personWorkInterval.last().getWorkUnit();
						List<WorkUnit> projectWorkList = 
								projectWorkLists.get(
										lastWorkUnit.getProjectId()
								);
						int lastWorkUnitId = 
								projectWorkList.indexOf(lastWorkUnit);
						
						if (lastWorkUnitId+1 < projectWorkList.size()) {
							
							WorkUnit nextWorkUnit = 
									projectWorkList.get(lastWorkUnitId+1);
							
							if (!workUnitAdded[personId]) {
								
								unlockedPersonWorkLists.get(
										nextWorkUnit.getWorkerId()
								).add(nextWorkUnit);
								workUnitAdded[personId] = true;
								
							}
							
						}
						
					}
					
				}
				
				personId++;
				
			}
			
			personId = 0;
			for (TreeSet<WorkUnitInterval> personWorkInterval : personWorkIntervals) {
				
				int endTime = 0;
				if (!personWorkInterval.isEmpty()) {
					endTime = personWorkInterval.last().getEndTime();
				}
				
				if (timeOfInterest >= endTime) {
					
					ArrayList<WorkUnit> personWorkList = 
							personWorkLists.get(personId);
					TreeSet<WorkUnit> unlockedPersonList = 
							unlockedPersonWorkLists.get(personId);
					
					if (!unlockedPersonList.isEmpty()) {
						
						// Changes involving new way of selecting work units
						for (WorkUnit workUnit : personWorkList) {

							if (unlockedPersonList.contains(workUnit)) {
								personWorkInterval.add(
									new WorkUnitInterval(
											workUnit, timeOfInterest
									)
								);
								workUnitAdded[personId] = false;
								unlockedPersonList.remove(workUnit);
								break;
							}
						}
						
					}
					
				}
				
				personId++;
				
			}

		}
		
		this.actualDuration = 0;
		this.waitDuration = 0;

		for (TreeSet<WorkUnitInterval> personWorkInterval : personWorkIntervals) {

			if (personWorkInterval.isEmpty()) continue;

			int lastEndTime = 0;
			for (WorkUnitInterval workUnit : personWorkInterval) {
				
				this.waitDuration = 
					this.waitDuration + workUnit.getStartTime() - lastEndTime; 
				lastEndTime = workUnit.getEndTime();
				
			}

			int endTime = personWorkInterval.last().getEndTime();
			if (endTime > this.actualDuration) 
				this.actualDuration = endTime;

		}

	}

	@Override
	public int compareTo(Individual other) {

		if (this.actualDuration > other.actualDuration) return 1;
		if (this.actualDuration < other.actualDuration) return -1;

		if (this.waitDuration > other.waitDuration) return 1;
		if (this.waitDuration < other.waitDuration) return -1;
		
		return 0;

	}

	@Override
	public String toString() {
		return actualDuration+":"+waitDuration;
	}

	public int getActualDuration() {
		return actualDuration;
	}

	public List<ArrayList<WorkUnit>> getPersonWorkLists() {
		return personWorkLists;
	}
	
	public int getWaitDuration() {
		return waitDuration;
	}
	
	public void writeInputFile(String fileName) throws IOException {
		
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(
						new BufferedOutputStream(
								new FileOutputStream(fileName)),"UTF-8"));
		
		for (ArrayList<WorkUnit> personWorkList : personWorkLists) {
			
			StringBuilder builder = new StringBuilder();
			for (WorkUnit workUnit : personWorkList) {
				builder.append(workUnit.getProjectId());
				builder.append(" ");
			}
			writer.write(builder.toString());
			writer.newLine();
			
		}
		
		writer.close();
		
	}
	
	public void readInputFile(String fileName) throws IOException {
		
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new BufferedInputStream(
								new FileInputStream(fileName)), "UTF-8"));
		
		List<ArrayList<WorkUnit>> personWorkLists = 
				new ArrayList<ArrayList<WorkUnit>>();
		
		String line;
		String[] splitedLine;
		
		int personID = 0;
		while ((line = reader.readLine()) != null) {
			
			ArrayList<WorkUnit> personWorkList = new ArrayList<WorkUnit>();
			
			splitedLine = line.split("\\s+");
			for (int i = 0; i < splitedLine.length; i++) {
				
				int projectID = Integer.parseInt(splitedLine[i]);
				ArrayList<WorkUnit> projectWorkList = 
					projectWorkLists.get(projectID);
				
				for (WorkUnit workUnit : projectWorkList) {
					if (workUnit.getWorkerId() == personID) {
						personWorkList.add(workUnit);
						break;
					}
				}
				
			}
			
			personWorkLists.add(personWorkList);
			personID++;
			
		}
		
		reader.close();
		
		this.personWorkLists = personWorkLists;
		
	}

	public void writeOutputFile(String fileName) throws IOException {
		
		this.calculateFitness();

		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(
						new BufferedOutputStream(
								new FileOutputStream(fileName)),"UTF-8"));

		writer.write(String.valueOf(this.getActualDuration()));
		writer.newLine();
		writer.newLine();

		int personId = 0;
		for (TreeSet<WorkUnitInterval> personWorkInterval : personWorkIntervals) {

			StringBuilder builder = new StringBuilder();
			builder.append(personId);
			builder.append(": ");
			for (WorkUnitInterval workUnit : personWorkInterval) {
				builder.append(workUnit);
				builder.append("-");
			}

			builder.deleteCharAt(builder.length()-1);
			writer.write(builder.toString());
			writer.newLine();
			writer.newLine();

			personId++;

		}

		writer.close();

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Individual clone() {
		
		Class currentClass = this.getClass();
		List<ArrayList<WorkUnit>> clonedPersonWorkLists = 
				new ArrayList<ArrayList<WorkUnit>>();
		
		for (ArrayList<WorkUnit> personWorkList : personWorkLists) {
			
			ArrayList<WorkUnit> clonedPersonWorkList = 
					new ArrayList<WorkUnit>();
			
			for (WorkUnit workUnit : personWorkList) {
				clonedPersonWorkList.add(workUnit);
			}
			
			clonedPersonWorkLists.add(clonedPersonWorkList);
			
		}
		
		try {
			return (Individual) currentClass.getConstructor(List.class)
									.newInstance(clonedPersonWorkLists);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	protected class WorkUnitInterval implements Comparable<WorkUnitInterval> {

		private WorkUnit workUnit;
		private int startTime;
		private int endTime;
		
		private boolean finished;

		public WorkUnitInterval(WorkUnit workUnit, int startTime) {
			this.workUnit = workUnit;
			this.startTime = startTime;
			this.endTime = startTime + workUnit.getWorkLoad();
			this.finished = false;
		}

		@Override
		public int compareTo(WorkUnitInterval other) {

			if (this.endTime < other.startTime) return -1;
			if (this.endTime > other.startTime) return 1;

			if (this.startTime < other.endTime) return -1;
			return 1;

		}

		public int getEndTime() {
			return endTime;
		}

		public int getStartTime() {
			return startTime;
		}

		public WorkUnit getWorkUnit() {
			return workUnit;
		}

		public boolean isFinished() {
			return finished;
		}

		public void setIsFinished() {
			this.finished = true;
		}

		@Override
		public String toString() {
			return workUnit.getProjectId()+"("+startTime+";"+endTime+")";
		}

	}

}
