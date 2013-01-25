package hr.fer.hmo.projectscheduling.sa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import hr.fer.hmo.projectscheduling.common.Individual;
import hr.fer.hmo.projectscheduling.common.WorkUnit;

/**
 * SAIndividual.
 * @author Karlo Knezevic, karlo.knezevic@fer.hr
 * @version 1.0
 */
public class SAIndividual extends Individual {

	/**
	 * Constructor.
	 */
	public SAIndividual() {
		super();
	}

	/**
	 * Constructor.
	 * @param personWorkLists
	 */
	public SAIndividual(List<ArrayList<WorkUnit>> personWorkLists) {
		super(personWorkLists);
	}

	/**
	 * Neighbor creation. 
	 * @param rand
	 * @param sDuration
	 */
	public void neighbor(Random rand, int shift, double µ) {

		for (ArrayList<WorkUnit> personWorkList : personWorkLists) {

			/**
			 * Mutation simulation.
			 */
			if (rand.nextDouble() < µ) {
				Collections.rotate(personWorkList, shift);
			}

			Collections.swap(personWorkList, rand.nextInt(projectSize), rand.nextInt(projectSize));

		}

		calculateFitness();


	}
}