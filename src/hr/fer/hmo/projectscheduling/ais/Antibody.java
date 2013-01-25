package hr.fer.hmo.projectscheduling.ais;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import hr.fer.hmo.projectscheduling.common.Individual;
import hr.fer.hmo.projectscheduling.common.WorkUnit;

/**
 * Artificial Immune System - Clonal Selection Algorithm
 * Antibody
 * @author Ivo Majić, ivo.majic2@fer.hr
 * @version 1.0
 */
public class Antibody extends Individual {
	
	/**
	 * Constructor
	 */
	public Antibody() {
		super();
	}

	/**
	 * Constructor
	 * @param projectWorkLists list of projects
	 */
	public Antibody(List<ArrayList<WorkUnit>> projectWorkLists) {
		super(projectWorkLists);
	}
	
	/**
	 * Randomize all projects
	 */
	public void randomizeAll() {
		
		for (ArrayList<WorkUnit> personWorkList : personWorkLists) {
			Collections.shuffle(personWorkList);
		}
		
	}
	
	/**
	 * Switch positions of two random work units N times per project
	 * 
	 * @param mutations number of mutations per project
	 * @param rand random number genrator
	 */
	public void hyperMutate(int mutations, Random rand) {
		
		int mutationsDone = 0;
		
		while (mutationsDone < mutations) {
					
			int indexA = rand.nextInt(projectSize);
			int indexB = rand.nextInt(projectSize);
			
			if (indexA == indexB) {
				if (indexB == projectSize-1)
					indexB--;
				else
					indexB++;
			}
			
			ArrayList<WorkUnit> personWorkList = personWorkLists.get(
					rand.nextInt(projectSize)
			);
			
			Collections.swap(personWorkList, indexA, indexB);
			
			if (rand.nextDouble() < 0.4) {
				
				personWorkList = personWorkLists.get(
						rand.nextInt(projectSize)
				);
				
				int distance = rand.nextInt(projectSize-1);
				
				int positive = 1;
				if (rand.nextBoolean())
					positive = -1;
				
				Collections.rotate(personWorkList, positive*distance);
				
			}
			
			mutationsDone++;
			
		}
		
	}

}
