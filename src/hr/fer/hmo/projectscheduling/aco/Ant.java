package hr.fer.hmo.projectscheduling.aco;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hr.fer.hmo.projectscheduling.common.Individual;
import hr.fer.hmo.projectscheduling.common.WorkUnit;

/**
 * ACO Individual.
 * @author Karlo Knezevic, karlo.knezevic@fer.hr
 * @version 2.0
 */
public class Ant extends Individual {

	private int[] projectWorkUnitIndexes;
	private Map<Integer, WorkUnit> indexes2WorkUnitsMap;

	//Constructor.
	public Ant() {
		super();
	}
	
	public Ant(List<ArrayList<WorkUnit>> personWorkLists) {

		super(personWorkLists);

		indexes2WorkUnitsMap = 
				ACOUtil.createIndexes2WorkUnitMap(personWorkLists);
		
		projectWorkUnitIndexes = 
				new int[personWorkLists.size()*personWorkLists.get(0).size()];

	}

	//Recreate Individual from Indexes
	private void createIndividualFromIndexes() {

		int subProjectActivities = personWorkLists.get(0).size();
		for (int i = 0; i < personWorkLists.size(); i++) {
			
			ArrayList<WorkUnit> workUnitList = personWorkLists.get(i);
			for (int j = 0; j < subProjectActivities; j++) {
				workUnitList.set(j, indexes2WorkUnitsMap.get(
						Integer.valueOf(
								projectWorkUnitIndexes[i*subProjectActivities+j]
										)
								)
						);
			}
			personWorkLists.set(i, workUnitList);
		
		}

	}

	//Set Work Unit Index
	public void setWorkUnitIndex(int index, int value) {

		projectWorkUnitIndexes[index] = value;

	}

	//Get Work Unit Index
	public int getWorkUnitIndex(int index) {

		return projectWorkUnitIndexes[index];

	}

	//Copy method
	public void copyAnt(Ant ant) {
		
		System.arraycopy(ant.projectWorkUnitIndexes, 0, 
				this.projectWorkUnitIndexes, 0, 
				ant.projectWorkUnitIndexes.length);
		evaluate();

	}

	/**
	 * Calculate Fitness
	 * IMPORTANT: before calculateFitness execution, 
	 * createIndividualFromIndexes must be invoked!
	 */
	public void evaluate() {

		createIndividualFromIndexes();  
		calculateFitness();

	}
}