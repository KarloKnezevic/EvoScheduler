package hr.fer.hmo.projectscheduling.configuration;

import hr.fer.hmo.projectscheduling.common.Algorithm;
import hr.fer.hmo.projectscheduling.ga.GA;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JProgressBar;

public class CycleGA24hRun extends RunConfiguration {

	public CycleGA24hRun(Map<String, Algorithm> algorithms,
			JProgressBar progress) {
		super(algorithms, progress);
	}

	public CycleGA24hRun(Map<String, Algorithm> algorithms) {
		super(algorithms);
	}

	@Override
	public String toString() {
		return "Cycle(GA) 24h";
	}

	@Override
	protected void run() {
		
		long startTime = System.currentTimeMillis();
		long elapsedTime = 1;
		
		GA ga = (GA) algorithms.get("GA");
		
		int sameCounter = 0;
		
		ga.run();
		runBestIndividual = ga.getBestIndividual();
		System.out.println();
		
		boolean reset = false;
		while (elapsedTime < 86400000) {
			
			if (sameCounter > 7) {
				System.out.println("RESETING ALGORITHM");
				ga.setInitialIndividual(null);
				runBestIndividual = ga.getBestIndividual();
				saveBest();
				sameCounter = 0;
				reset = true;
			} else
				ga.setInitialIndividual(ga.getBestIndividual());
		
			ga.run();
			if (reset) {
				runBestIndividual = ga.getBestIndividual();
				reset = false;
			}
			
			if (ga.getBestIndividual().getActualDuration() < runBestIndividual.getActualDuration()) {
				runBestIndividual = ga.getBestIndividual();
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
		
		Algorithm ga = new GA();
		algorithms.put(ga.toString(), ga);
		
		RunConfiguration cycleGA24hRun = 
				new CycleGA24hRun(algorithms);
		cycleGA24hRun.runConfiguration();

	}

}
