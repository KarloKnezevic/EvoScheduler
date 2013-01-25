package hr.fer.hmo.projectscheduling.configuration;

import hr.fer.hmo.projectscheduling.common.Algorithm;
import hr.fer.hmo.projectscheduling.sa.SimmulatedAnnealing;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JProgressBar;

public class CycleSA24hRun extends RunConfiguration {

	public CycleSA24hRun(Map<String, Algorithm> algorithms,
			JProgressBar progress) {
		super(algorithms, progress);
	}

	public CycleSA24hRun(Map<String, Algorithm> algorithms) {
		super(algorithms);
	}

	@Override
	public String toString() {
		return "Cycle(SA) 24h";
	}

	@Override
	protected void run() {
		
		long startTime = System.currentTimeMillis();
		long elapsedTime = 1;
		

		SimmulatedAnnealing sa = (SimmulatedAnnealing) algorithms.get("SA");
		
		int sameCounter = 0;
		
		sa.run();
		runBestIndividual = sa.getBestIndividual();
		System.out.println();
		
		boolean reset = false;
		while (elapsedTime < 86400000) {
			
			if (sameCounter > 7) {
				System.out.println("RESETING ALGORITHM");
				sa.setInitialIndividual(null);
				runBestIndividual = sa.getBestIndividual();
				saveBest();
				sameCounter = 0;
				reset = true;
			} else
				sa.setInitialIndividual(sa.getBestIndividual());
		
			sa.run();
			if (reset) {
				runBestIndividual = sa.getBestIndividual();
				reset = false;
			}
			
			if (sa.getBestIndividual().getActualDuration() < runBestIndividual.getActualDuration()) {
				runBestIndividual = sa.getBestIndividual();
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
		
		Algorithm sa = new SimmulatedAnnealing();
		algorithms.put(sa.toString(), sa);
		
		RunConfiguration cycleSA24hRun = 
				new CycleSA24hRun(algorithms);
		cycleSA24hRun.runConfiguration();

	}

}
