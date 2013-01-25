package hr.fer.hmo.projectscheduling.sa;

import java.util.Random;

import hr.fer.hmo.projectscheduling.common.Algorithm;
import hr.fer.hmo.projectscheduling.common.Individual;

/**
 * Simmulated Annealing.
 * 
 * SA is based on the Monte Carlo model which simulates the 
 * energy changes in the system during the cooling process 
 * until it converges to an equilibrium state.
 * 
 * @author Karlo Knezevic, karlo.knezevic@fer.hr
 * @version 1.0
 */
public class SimmulatedAnnealing extends Algorithm {

	//inital temperature
	private double T;

	//final temperature
	private double Tf;
	
	//variable temperature
	private double Tv;

	//temperature geometric progression parameter
	private double α;

	//temperature parameter reducing
	private double β;
	
	//rotation probability
	private double µ;

	//iteration number
	private int iter;

	//random
	private Random rand;

	//using geometric progression
	private boolean geometricProgression;

	//how manny neighbors will be found
	private int neighbors;

	//best solution
	private SAIndividual best;

	//current solution
	private SAIndividual S;

	//neighbor(s)
	/**
	 * Improved version of the algorithm.
	 * @author Karlo Knezevic, karlo.knezevic@fer.hr
	 */
	private SAIndividual[] Sn;


	/**
	 * Constructor.
	 */
	public SimmulatedAnnealing() {

		T = 700;
		Tf = 0.01;
		α = 0.95;
		β = 0.16;
		µ = 0.9;
		neighbors = 20;

		geometricProgression = false;

	}
	
	/**
	 * Initial Individual Setter.
	 */
	@Override
	public void setInitialIndividual(Individual initialIndividual) {
		this.initialIndividual = initialIndividual;
	}

	/**
	 * Initializing.
	 */
	private void initialize() {
		
		Tv = T;

		rand = new Random();

		if (initialIndividual == null) {

			best = new SAIndividual();

		} else {

			best = new SAIndividual(
					initialIndividual.clone().getPersonWorkLists());

		}

		S = new SAIndividual(
				best.clone().getPersonWorkLists());
			
		best.calculateFitness();
		S.calculateFitness();

		Sn = new SAIndividual[neighbors];

	}

	/**
	 * Count iterations computing.
	 */
	private void computeIterations() {

		if (geometricProgression) {

			//α NOT ZERRO!
			iter = (int)Math.ceil( (Math.log10(Tf) - Math.log10(T)) / Math.log10(α) );

		} else {

			//β, T, Tf NOT ZERRO
			iter = (int)Math.ceil( (T - Tf) / (β*T*Tf) );

		}

	}

	private void selectNeighbors() {

		for (int i = 0; i < Sn.length; i++) {
			Sn[i] = new SAIndividual(S.clone().getPersonWorkLists());
			Sn[i].neighbor(rand, i+1, µ);

		}

	}

	/**
	 * Update current solution.
	 */
	private void updateS() {

		int maxIndex = -1;
		int minIndex = -1;

		int minValue = S.getActualDuration();
		int minWait = S.getWaitDuration();
		int minDuration = Integer.MAX_VALUE;
		
		//find min value and minimum value grater then S
		for (int i = 0; i < Sn.length; i++) {

			if (Sn[i].getActualDuration() < minValue) {
				
				minIndex = i;
				minValue = Sn[i].getActualDuration();
			
			} else if (Sn[i].getActualDuration() == minValue && Sn[i].getWaitDuration() < minWait) {
				
				minIndex = i;
				minWait = Sn[i].getWaitDuration();
			
			} else if (Sn[i].getActualDuration() > S.getActualDuration() && Sn[i].getActualDuration() < minDuration) {
				
				maxIndex = i;
				minDuration = Sn[i].getActualDuration();
			}

		}

		//if min smaller then S, update S
		if (minIndex != -1) {

			S = new SAIndividual(Sn[minIndex].clone().getPersonWorkLists());
			S.calculateFitness();

		} else {

			if (maxIndex == -1) maxIndex = rand.nextInt(Sn.length);

			//if probability enough big to accept minimum solution grater then S
			double Δf = Math.abs(S.getActualDuration() - Sn[maxIndex].getActualDuration());

			double P = Math.exp(-Δf/T);

			if (rand.nextDouble() < P) {
				S = new SAIndividual(Sn[maxIndex].clone().getPersonWorkLists());
				S.calculateFitness();
			}
		}

	}

	/**
	 * Update best solution.
	 */
	private void updateBest() {

		if (S.getActualDuration() < best.getActualDuration()) {
			best = new SAIndividual(S.clone().getPersonWorkLists());
			best.calculateFitness();
		}

	}

	/**
	 * Temperature updating.
	 */
	private void updateT() {

		if (geometricProgression) {

			Tv = α*Tv;

		} else {

			Tv = Tv / (1 + β*Tv);

		}

	}

	@Override
	public void run() {

		initialize();

		computeIterations();

		for (int iteration = 0; iteration < iter; iteration++) {

			selectNeighbors();

			updateS();

			updateBest();

			updateT();

			System.out.println(
					"Iter: " + iteration + " / " +  iter + " Dur: " + best.getActualDuration()
					);

			if (configuration != null) configuration.increase();

		}

		this.bestIndividual = (Individual)best;
		System.out.println("Simmulated Annealing Best Project Duration: " + 
				this.bestIndividual.getActualDuration()
				);

	}

	@Override
	public String toString() {
		return "SA";
	}
	
	//------------------GETTERS AND SETTERS FOR GUI---------------------

	//sinitial temperature, [100 - 2000]
	public double getT() {
		return T;
	}

	public void setT(double t) {
		T = t;
	}

	//final temperature [0.001 - 10.0]
	public double getTf() {
		return Tf;
	}

	public void setTf(double tf) {
		Tf = tf;
	}

	//α, [0.5 - 1.0]
	public double getΑ() {
		return α;
	}

	public void setΑ(double α) {
		this.α = α;
	}

	//β, [0.01 - 0.1]
	public double getΒ() {
		return β;
	}

	public void setΒ(double β) {
		this.β = β;
	}

	//μ, [0.1 - 1.0]
	public double getΜ() {
		return µ;
	}

	public void setΜ(double μ) {
		µ = μ;
	}

	//geometric progression, checkbox
	//IF geometric progression  SELECTED, ALPHA IS AVAILABLE,
	//AND BETA IS UNAVAILABLE, and contrary
	public boolean isGeometricProgression() {
		return geometricProgression;
	}

	public void setGeometricProgression(boolean geometricProgression) {
		this.geometricProgression = geometricProgression;
	}

	//neighbors, [1 - 30]
	public int getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(int neighbors) {
		this.neighbors = neighbors;
	}
	
	//------------------GETTERS AND SETTERS FOR GUI---------------------

}
