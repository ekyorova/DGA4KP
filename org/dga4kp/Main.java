package org.dga4kp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dga4kp.adt.BackpackItem;
import org.dga4kp.adt.CrossoverStrategy;
import org.dga4kp.adt.Population;

/**
 * Main flow
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 *
 */
public class Main {
	static Logger log = LogManager.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		Population initialPopulation = new Population();
		Util.initializeRandomGenerator();
		try {
			Options opt = new Options();
			opt.addOption("numberOfItems", true, "The number of items that we should choose from.");
			opt.addOption("backpackWeightLimit", true, "The weight limit of the backpack.");
			opt.addOption("populationSize", true, "The size of the population.");
			opt.addOption("numberOfGenerations", true, "The numnber of generation.");
			opt.addOption("percentageOfInheritedPopulation", true,
					"The percentage of the population that will be inherited via elitism.");
			opt.addOption("doCrossoverProbability", true, "The probrability to do a crossover for a certain sequence.");
			opt.addOption("crossoverStrategy", true,
					"1 for one-point crossover, 2 for two-point crossover and 3 for uniform crossover.");
			opt.addOption("numberOfMutationBits", true, "The number of bits that will be mutated.");
			opt.addOption("fileWithValues", true, "File with items' values.");
			opt.addOption("fileWithWeights", true, "File with items' weights.");
			opt.addOption("numberOfThreads", true, "Number of threads.");

			CommandLineParser parser = new DefaultParser();
			CommandLine cl = parser.parse(opt, args);

			if (cl.hasOption("numberOfItems") && cl.hasOption("populationSize") && cl.hasOption("fileWithValues")
					&& cl.hasOption("fileWithWeights") && cl.hasOption("numberOfThreads")
					&& cl.hasOption("backpackWeightLimit") && cl.hasOption("numberOfGenerations")
					&& cl.hasOption("doCrossoverProbability") && cl.hasOption("crossoverStrategy")
					&& cl.hasOption("percentageOfInheritedPopulation") && cl.hasOption("numberOfMutationBits")) {

				// Read values and weights from file and put it in allItems
				// array list
				Scanner valuesScanner = new Scanner(new File(cl.getOptionValue("fileWithValues")));
				Scanner weightScanner = new Scanner(new File(cl.getOptionValue("fileWithWeights")));
				int index = 0;
				PopulationGenerator.backpackItems = new HashMap<Integer, BackpackItem>();
				PopulationGenerator.listOfBackpackItems = new ArrayList<BackpackItem>();
				while (valuesScanner.hasNextInt() && weightScanner.hasNextInt()) {
					BackpackItem item = new BackpackItem(index++, valuesScanner.nextInt(), weightScanner.nextInt());
					PopulationGenerator.backpackItems.put(item.getId(), item);
					PopulationGenerator.listOfBackpackItems.add(item);
				}
				PopulationGenerator.numberOfItems = Integer.parseInt(cl.getOptionValue("numberOfItems"));
				PopulationGenerator.populationSize = Integer.parseInt(cl.getOptionValue("populationSize"));
				PopulationGenerator.doCrossobverProbability = Double
						.parseDouble(cl.getOptionValue("doCrossoverProbability"));
				PopulationGenerator.crossoverStrategy = CrossoverStrategy
						.findByKey(Integer.parseInt(cl.getOptionValue("crossoverStrategy")));
				PopulationGenerator.numberOfMutationBits = Integer.parseInt(cl.getOptionValue("numberOfMutationBits"));
				PopulationGenerator.backpackLimit = Integer.parseInt(cl.getOptionValue("backpackWeightLimit"));
				PopulationGenerator.numberOfGenerations = Integer.parseInt(cl.getOptionValue("numberOfGenerations"));
				PopulationGenerator.percentageOfInheritedPopulation = Double
						.parseDouble(cl.getOptionValue("percentageOfInheritedPopulation"));
				PopulationGenerator.threadCount = Integer.parseInt(cl.getOptionValue("numberOfThreads"));

				// The main flow
				initialPopulation = PopulationGenerator.generateInitialPopulation(PopulationGenerator.backpackItems);
				initialPopulation.sort();
				PopulationGenerator.population = new Population(initialPopulation);
				PopulationGenerator.evolve();
			} else {
				HelpFormatter f = new HelpFormatter();
				f.printHelp("Option Helper", opt);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
