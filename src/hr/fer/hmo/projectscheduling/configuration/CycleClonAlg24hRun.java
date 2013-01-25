package hr.fer.hmo.projectscheduling.configuration;

import hr.fer.hmo.projectscheduling.ais.ClonAlg;
import hr.fer.hmo.projectscheduling.common.Algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JProgressBar;

public class CycleClonAlg24hRun extends RunConfiguration {

	public CycleClonAlg24hRun(Map<String, Algorithm> algorithms,
			JProgressBar progress) {
		super(algorithms, progress);
	}

	public CycleClonAlg24hRun(Map<String, Algorithm> algorithms) {
		super(algorithms);
	}

	@Override
	public String toString() {
		return "Cycle(ClonAlg) 24h";
	}

	@Override
	protected void run() {
		
		long startTime = System.currentTimeMillis();
		long elapsedTime = 1;
		
		ClonAlg clonAlg = (ClonAlg) algorithms.get("ClonAlg");
		
		int sameCounter = 0;
		
		clonAlg.run();
		runBestIndividual = clonAlg.getBestIndividual();
		System.out.println();
		
		boolean reset = false;
		while (elapsedTime < 86400000) {
			
			if (sameCounter > 7) {
				System.out.println("RESETING ALGORITHM");
				clonAlg.setInitialIndividual(null);
				runBestIndividual = clonAlg.getBestIndividual();
				saveBest();
				sameCounter = 0;
				reset = true;
			} else
				clonAlg.setInitialIndividual(clonAlg.getBestIndividual());
		
			clonAlg.run();
			if (reset) {
				runBestIndividual = clonAlg.getBestIndividual();
				reset = false;
			}
			
			if (clonAlg.getBestIndividual().getActualDuration() < runBestIndividual.getActualDuration()) {
				runBestIndividual = clonAlg.getBestIndividual();
				saveBest();
				System.out.println("NEW BEST " + runBestIndividual);
				sameCounter = 0;
			} else {
				sameCounter++;
			}
			
			elapsedTime = System.currentTimeMillis() - startTime;
			
			System.out.println(
					String.format("%d h %d min / 24 h", 
				    TimeUnit.MILLISECONDS.toHours(elapsedTime),
				    TimeUnit.MILLISECONDS.toMinutes(elapsedTime) - 
				    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(elapsedTime)))
			);
			System.out.println();
			
		}

	}

	public static void main(String[] args) {
		
		Map<String, Algorithm> algorithms = new HashMap<String, Algorithm>();
		
		Algorithm clonAlg = new ClonAlg();
		algorithms.put(clonAlg.toString(), clonAlg);
		
		RunConfiguration cycleClonAlg24hRun = 
				new CycleClonAlg24hRun(algorithms);
		cycleClonAlg24hRun.runConfiguration();

	}

}
