package org.dga4kp.runners;

import java.util.List;
import java.util.Random;

import org.dga4kp.PopulationGenerator;
import org.dga4kp.adt.OrderedItemSequence;

/**
 * 
 * InitialPopulationRunner class is a runnable that generates a part of the
 * initial population and appends the result to a shared variable. Important
 * note is that every thread has to use its own dedicated random generator
 * because a Random object can be accessed by only one thread at a time.
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 *
 */
public class InitialPopulationRunner implements Runnable {
	public static List<List<OrderedItemSequence>> sequences;
	public int threadCount;
	public int currentThread;
	public int numberOfSequences;
	public Random randomGenerator;

	public InitialPopulationRunner(Random randomGenerator, int threadCount, int currentThread, int numberOfSequences) {
		this.threadCount = threadCount;
		this.currentThread = currentThread;
		this.numberOfSequences = numberOfSequences;
		this.randomGenerator = randomGenerator;
	}

	@Override
	public void run() {
		if (currentThread == threadCount - 1) {
			numberOfSequences += PopulationGenerator.populationSize - ((currentThread + 1) * numberOfSequences);
		}
		List<OrderedItemSequence> temporaryResult = PopulationGenerator.generatePartialInitialPopulation(randomGenerator,
				numberOfSequences);
		synchronized (sequences) {
			sequences.add(temporaryResult);
		}
	}
}
