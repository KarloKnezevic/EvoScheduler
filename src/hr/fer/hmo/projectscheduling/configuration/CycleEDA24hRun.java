package hr.fer.hmo.projectscheduling.configuration;

import hr.fer.hmo.projectscheduling.common.Algorithm;
import hr.fer.hmo.projectscheduling.eda.EDA;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JProgressBar;

public class CycleEDA24hRun extends RunConfiguration {

	public CycleEDA24hRun(Map<String, Algorithm> algorithms,
			JProgressBar progress) {
		super(algorithms, progress);
	}

	public CycleEDA24hRun(Map<String, Algorithm> algorithms) {
		super(algorithms);
	}

	@Override
	public String toString() {
		return "Cycle(EDA) 24h";
	}

	@Override
	protected void run() {
		
		long startTime = System.currentTimeMillis();
		long elapsedTime = 1;
		
		EDA eda = (EDA) algorithms.get("EDA");
		
		int sameCounter = 0;
		
		eda.run();
		runBestIndividual = eda.getBestIndividual();
		System.out.println();
		
		boolean reset = false;
		while (elapsedTime < 86400000) {
			
			if (sameCounter > 7) {
				System.out.println("RESETING ALGORITHM");
				eda.setInitialIndividual(null);
				runBestIndividual = eda.getBestIndividual();
				saveBest();
				sameCounter = 0;
				reset = true;
			} else
				eda.setInitialIndividual(eda.getBestIndividual());
		
			eda.run();
			if (reset) {
				runBestIndividual = eda.getBestIndividual();
				reset = false;
			}
			
			if (eda.getBestIndividual().getActualDuration() < runBestIndividual.getActualDuration()) {
				runBestIndividual = eda.getBestIndividual();
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
		
		Algorithm eda = new EDA();
		algorithms.put(eda.toString(), eda);
		
		RunConfiguration cycleEDA24hRun = 
				new CycleEDA24hRun(algorithms);
		cycleEDA24hRun.runConfiguration();

	}

}
