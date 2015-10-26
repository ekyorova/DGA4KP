package org.dga4kp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.dga4kp.adt.OrderedItemSequence;

/**
 * Utility class that provides help methods.
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 */
public class Util {

	public static Random randomGenerator;

	public static void initializeRandomGenerator() {
		randomGenerator = new Random();
	}

	public static boolean doCrossover(double probability) {
		double value = randomGenerator.nextDouble();
		if (value < probability) {
			return true;
		}
		return false;
	}

	public static List<OrderedItemSequence> flatten(List<List<OrderedItemSequence>> input) throws Exception {
		List<OrderedItemSequence> result = new ArrayList<OrderedItemSequence>();
		Iterator<List<OrderedItemSequence>> iterator = input.iterator();
		while (iterator.hasNext()) {
			List<OrderedItemSequence> listOfSequences = iterator.next();
			Iterator<OrderedItemSequence> listOfSequencesIterator = listOfSequences.iterator();
			while (listOfSequencesIterator.hasNext()) {
				result.add(listOfSequencesIterator.next());
			}
		}
		if (result.size() != PopulationGenerator.populationSize) {
			throw new Exception(String.format("Result size is %d but it should be the same as population size %d! ",
					result.size(), PopulationGenerator.populationSize));
		}
		return result;
	}
}
