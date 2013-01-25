package hr.fer.hmo.projectscheduling.configuration;

import hr.fer.hmo.projectscheduling.common.Algorithm;
import hr.fer.hmo.projectscheduling.sa.SimmulatedAnnealing;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JProgressBar;

public class SARun extends RunConfiguration {

	public SARun(Map<String, Algorithm> algorithms) {
		super(algorithms);
	}
	
	public SARun(Map<String, Algorithm> algorithms, 
			JProgressBar progress) {
		super(algorithms, progress);
	}

	@Override
	public String toString() {
		return "SA";
	}

	@Override
	protected void run() {
		
		SimmulatedAnnealing SA = (SimmulatedAnnealing) algorithms.get("SA");
		// No iteration counter
		//SA.setConfiguration(this);
		
		SA.run();
		runBestIndividual = SA.getBestIndividual();

		
	}
	
	public static void main(String[] args) {
		
		Map<String, Algorithm> algorithms = new HashMap<String, Algorithm>();
		
		Algorithm SA = new SimmulatedAnnealing();
		algorithms.put(SA.toString(), SA);
		
		RunConfiguration saRun = new SARun(algorithms);
		saRun.runConfiguration();
		
	}

}
