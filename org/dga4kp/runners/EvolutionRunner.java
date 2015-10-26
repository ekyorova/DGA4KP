package org.dga4kp.runners;

import java.util.List;
import java.util.Random;

import org.dga4kp.PopulationGenerator;
import org.dga4kp.adt.OrderedItemSequence;

/**
 * EvolutionRunner class is a runnable that executes the evolution from specific
 * start point to a specific end point in the population and appends the result
 * to a shared variable. Important note is that every thread has to use its own
 * dedicated random generator because a Random object can be accessed by only
 * one thread at a time.
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 *
 */
public class EvolutionRunner implements Runnable {
	private static List<List<OrderedItemSequence>> sequences;
	private int threadCount;
	private int currentThread;
	private int scope;
	private Random randomGenerator;

	public static void setSequences(List<List<OrderedItemSequence>> sequences) {
		EvolutionRunner.sequences = sequences;
	}

	public static List<List<OrderedItemSequence>> getSequences() {
		return EvolutionRunner.sequences;
	}

	public EvolutionRunner(Random randomGenerator, int threadCount, int currentThread, int scope) {
		this.threadCount = threadCount;
		this.currentThread = currentThread;
		this.scope = scope;
		this.randomGenerator = randomGenerator;
	}

	@Override
	public void run() {
		// Starting point for the evolution of current population
		int start = this.scope * this.currentThread;
		// End point for the evolution of current population
		int end = (this.currentThread + 1) * this.scope - 1;
		if (this.currentThread == this.threadCount - 1) {
			// If this is the last thread created then the end should be the
			// maximum size of the population
			end = PopulationGenerator.populationSize - 1;
		}
		List<OrderedItemSequence> temporaryResult = PopulationGenerator.evolveSequences(start, end, randomGenerator);
		synchronized (sequences) {
			sequences.add(temporaryResult);
		}
	}
}