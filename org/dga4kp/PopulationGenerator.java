package org.dga4kp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dga4kp.adt.BackpackItem;
import org.dga4kp.adt.CrossoverStrategy;
import org.dga4kp.adt.OrderedItemSequence;
import org.dga4kp.adt.Population;
import org.dga4kp.runners.EvolutionRunner;
import org.dga4kp.runners.InitialPopulationRunner;

/**
 * PopulationGenerator class provides an abstraction for generating the initial
 * population and evolving the current population.
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 *
 */
public class PopulationGenerator {

	static Logger log = LogManager.getLogger(PopulationGenerator.class);

	// Static variables to hold input parameters
	public static int numberOfItems;
	public static int populationSize;
	public static double doCrossobverProbability;
	public static CrossoverStrategy crossoverStrategy;
	public static int backpackLimit;
	public static int numberOfMutationBits;
	public static int numberOfGenerations;
	public static double percentageOfInheritedPopulation;
	public static int threadCount;

	// Static variable to hold the map between IDs and backpack items
	public static Map<Integer, BackpackItem> backpackItems;
	// Static variable to hold the an unordered list of backpack items
	public static List<BackpackItem> listOfBackpackItems;
	// Static variable to hold the current population
	public static Population population;
	// Static variable to hold the best fitness value found so far
	public static int bestFitnessValue = 0;
	// Static variable to hold the sequence with the best fitness value
	public static OrderedItemSequence bestFitnessSequence = new OrderedItemSequence();
	// Static value to accumulate the summation of fitness values for the
	// current generation
	public static int sumFitness = 0;

	// Static values to hold information about the number of crossovers and
	// number of mutations
	public static int numberOfCrossovers = 0;
	public static int numberOfMutation = 0;

	/**
	 * A method that implements a procedure for generating the initial
	 * populations. Depending on the input number of threads it creates the
	 * dedicated random generators and the corresponding threads, starts them
	 * and after that joins the. Generated list of sequences is merged and the
	 * current population is updated in a synchronized manner.
	 * 
	 * @param items
	 *            - the map of IDs to backpack items
	 * @return
	 * @throws Exception
	 */
	public static Population generateInitialPopulation(Map<Integer, BackpackItem> items) throws Exception {
		int numberOfSequences = populationSize / threadCount;
		Thread tr[] = new Thread[threadCount];
		Random randomGenerators[] = new Random[threadCount];
		InitialPopulationRunner.sequences = Collections.synchronizedList(new ArrayList<List<OrderedItemSequence>>());
		for (int j = 0; j < threadCount; j++) {
			randomGenerators[j] = new Random();
			InitialPopulationRunner fitnessCalculationRunner = new InitialPopulationRunner(randomGenerators[j],
					threadCount, j, numberOfSequences);
			Thread t = new Thread(fitnessCalculationRunner);
			tr[j] = t;
			t.start();
		}
		for (int j = 0; j < threadCount; j++) {
			try {
				tr[j].join();
			} catch (InterruptedException e) {
				System.out.println("Exception");
			}
		}
		population = new Population(Util.flatten(InitialPopulationRunner.sequences));
		return population;
	}

	/**
	 * Method that is executed by the InitialPopulationRunner and generates a
	 * list of randomized sequences and return the result.
	 * 
	 * @param randomGenerator
	 *            - dedicated random generator
	 * @param numberOfSequences
	 *            - the number of sequences that need to be generated
	 * @return - the generated list of randomized sequences
	 */
	public static List<OrderedItemSequence> generatePartialInitialPopulation(Random randomGenerator,
			int numberOfSequences) {
		List<OrderedItemSequence> partialPpopulation = new ArrayList<OrderedItemSequence>();
		for (int i = 0; i < numberOfSequences; i++) {
			OrderedItemSequence item = new OrderedItemSequence(randomGenerator);
			item.updateFitnessValue(randomGenerator, PopulationGenerator.backpackItems,
					PopulationGenerator.backpackLimit);
			partialPpopulation.add(item);
		}
		return partialPpopulation;
	}

	/**
	 * Method that evolves the current population to generate the new
	 * population. It creates the specified amount of threads as well as their
	 * dedicated random generators, and starts them. After the threads are
	 * joined the population is updated and is checked for stop condition.
	 * 
	 * @throws Exception
	 */
	public static void evolve() throws Exception {
		Random[] randomGenerators = new Random[threadCount];
		for (int i = 0; i < threadCount; i++) {
			randomGenerators[i] = new Random();
		}
		for (int i = 0; i < numberOfGenerations; i++) {
			int scope = populationSize / threadCount;
			Thread tr[] = new Thread[threadCount];
			EvolutionRunner.setSequences(Collections.synchronizedList(new ArrayList<List<OrderedItemSequence>>()));
			for (int j = 0; j < threadCount; j++) {
				EvolutionRunner evolutionRunner = new EvolutionRunner(randomGenerators[j], threadCount, j, scope);
				Thread t = new Thread(evolutionRunner);
				tr[j] = t;
				t.start();
			}
			for (int j = 0; j < threadCount; j++) {
				try {
					tr[j].join();
				} catch (InterruptedException e) {
					System.out.println("Exception");
				}

			}
			population = new Population(Util.flatten(EvolutionRunner.getSequences()));
			population.sort();
			int initialValue = population.getIndividuals().get(0).getFitnessValue();
			boolean terminate = true;
			for (int m = 0; m < population.getIndividuals().size(); m++) {
				if (m < (populationSize * 0.95)) {
					if (initialValue != population.getIndividuals().get(m).getFitnessValue()) {
						terminate = false;
					}
				}
			}
			if (terminate) {
				break;
			}
			sumFitness = 0;
			numberOfCrossovers = 0;
			numberOfMutation = 0;
		}
		log.info(String.format("Best fitness value: %s\n", bestFitnessValue));
	}

	/**
	 * Method that is executed by EvolutionRunner and evolved a part of the
	 * current population. It generated a list that will be a part pf the new
	 * population.
	 * 
	 * @param start
	 *            - the index of the start element
	 * @param end
	 *            - the index of the end element
	 * @param randomGenerator
	 *            - dedicated random generator
	 * @return
	 */
	public static List<OrderedItemSequence> evolveSequences(int start, int end, Random randomGenerator) {
		List<OrderedItemSequence> partialPpopulation = new ArrayList<OrderedItemSequence>();
		int i = 0;
		int elementsTobeInherited = (int) Math.round(percentageOfInheritedPopulation * populationSize);
		for (int j = start; j <= end; j++) {
			partialPpopulation.add(population.getIndividuals().get(j));
		}
		if (start < elementsTobeInherited) {
			i = elementsTobeInherited - start;
		}
		for (; i < partialPpopulation.size(); i++) {
			if (randomGenerator.nextDouble() < doCrossobverProbability) {
				numberOfCrossovers++;
				int firstParentIndex = (int) Math.round(randomGenerator.nextDouble() * ((populationSize / 2) - 1));
				int secondParentIndex = (int) Math.round(randomGenerator.nextDouble() * ((populationSize / 2) - 1));
				switch (crossoverStrategy) {
				case ONE_POINT_CROSSOVER:
					int randomIndex = (int) Math
							.round((randomGenerator.nextDouble() * (PopulationGenerator.numberOfItems - 1)));
					partialPpopulation.get(i).onePointCrossover(randomIndex,
							population.getIndividuals().get(firstParentIndex),
							population.getIndividuals().get(secondParentIndex));
					break;

				case TWO_POINT_CROSSOVER:
					int firstIndexPoint = (int) Math
							.round(randomGenerator.nextDouble() * (PopulationGenerator.numberOfItems - 1));
					int secondIndexPoint = (int) Math
							.round(randomGenerator.nextDouble() * (PopulationGenerator.numberOfItems - 1));
					partialPpopulation.get(i).twoPointCrossover(firstIndexPoint, secondIndexPoint,
							population.getIndividuals().get(firstParentIndex),
							population.getIndividuals().get(secondParentIndex));
					break;

				case UNIFORM_CROSSOVER:
					partialPpopulation.get(i).uniformCrossover(randomGenerator,
							population.getIndividuals().get(firstParentIndex),
							population.getIndividuals().get(secondParentIndex));
					break;

				default:
					break;
				}
			} else {
				numberOfMutation++;
				partialPpopulation.get(i).mutate(randomGenerator);
			}
			partialPpopulation.get(i).updateFitnessValue(randomGenerator, backpackItems, backpackLimit);
			synchronized (bestFitnessSequence) {
				if (partialPpopulation.get(i).getFitnessValue() > bestFitnessValue) {
					bestFitnessValue = partialPpopulation.get(i).getFitnessValue();
					bestFitnessSequence = new OrderedItemSequence(partialPpopulation.get(i));
				}
			}
			sumFitness += partialPpopulation.get(i).getFitnessValue();
		}
		return partialPpopulation;
	}
}
