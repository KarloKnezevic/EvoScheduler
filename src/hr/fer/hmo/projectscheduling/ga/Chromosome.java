package hr.fer.hmo.projectscheduling.ga;

import hr.fer.hmo.projectscheduling.common.Individual;
import hr.fer.hmo.projectscheduling.common.WorkUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Genetic Algorithm
 * Chromosome
 * @author Petar Čolić, petar.colic@fer.hr
 * @version 1.0
 */
public class Chromosome extends Individual {

	public Chromosome() {
		super();
	}
	
	
	public Chromosome(List<ArrayList<WorkUnit>> personWorkLists) {
		super(personWorkLists);
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
	 * Mutation
	 * @param lineProb Probability for a mutation in a single line
	 * @param rand
	 */
	public void mutate(double lineProb, Random rand) {

		for (ArrayList<WorkUnit> personWorkList : personWorkLists) {

			if (rand.nextDouble() < lineProb) {

				int indexA = rand.nextInt(projectSize);
				int indexB = rand.nextInt(projectSize);

				if (indexA == indexB) {
					if (indexB == projectSize - 1)
						indexB--;
					else
						indexB++;
				}

				WorkUnit temp = personWorkList.get(indexA);
				personWorkList.set(indexA, personWorkList.get(indexB));
				personWorkList.set(indexB, temp);
				
			}

		}

	}

}
