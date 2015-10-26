package org.dga4kp.adt;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dga4kp.PopulationGenerator;

/**
 * OrderedItemSequence is a class providing abstract structure for an individual
 * or candidate solution in GA. We represent each individual as a sequence of 0
 * and 1 (True and False) where 0 means the item with id equal to the index
 * won't be taken and 1 means the item with id equal to the index will be taken.
 * Sequence has a fitness field which represents what is the total value of all
 * items that will be put in the backpack.
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 *
 */
public class OrderedItemSequence implements Comparable<OrderedItemSequence> {
	private int fitnessValue;
	private List<Boolean> items;

	static Logger log = LogManager.getLogger(OrderedItemSequence.class);

	/**
	 * General purpose constructor
	 */
	public OrderedItemSequence() {
		this.fitnessValue = 0;
		this.items = new ArrayList<Boolean>();
	}

	/**
	 * Constructor that produces a new OrderedItemSequence object with
	 * randomized items
	 * 
	 * @param randomGenerator
	 *            - the dedicated random generator
	 */
	public OrderedItemSequence(Random randomGenerator) {
		this.fitnessValue = 0;
		long value;
		this.items = new ArrayList<Boolean>();
		for (int i = 0; i < PopulationGenerator.numberOfItems; i++) {
			value = Math.round(randomGenerator.nextDouble());
			this.items.add((value == 1) ? new Boolean(true) : new Boolean(false));
		}
	}

	/**
	 * Copying constructor
	 * 
	 * @param sequence
	 *            - OrderedItemSequence object that shoudl be copies
	 */
	public OrderedItemSequence(OrderedItemSequence sequence) {
		this.items = sequence.getItems();
		this.fitnessValue = sequence.getFitnessValue();
	}

	public OrderedItemSequence(List<Boolean> items) {
		this.items = new ArrayList<Boolean>(items);
	}

	public int getFitnessValue() {
		return fitnessValue;
	}

	public void setFitnessValue(int fitnessValue) {
		this.fitnessValue = fitnessValue;
	}

	public List<Boolean> getItems() {
		return items;
	}

	public void setItems(List<Boolean> items) {
		this.items = items;
	}

	/**
	 * Method that implements mutation operator used in GA.
	 * 
	 * @param randomGenerator
	 *            - dedicated random generator
	 */
	public void mutate(Random randomGenerator) {
		for (int i = 0; i < PopulationGenerator.numberOfItems; i++) {
			int index = (int) (randomGenerator.nextDouble() * (items.size() - 1));
			items.set(index, !items.get(index));
		}
	}

	/**
	 * Updates fitness value for this sequence. Fitness value is calculated for
	 * the sequence by sum of the values of all the items that are are marked
	 * with True in the sequence. If the weight of the values marked with True
	 * exceeds the limit of the backpack a random True item is changed to False
	 * and the fitness value is calculated again.
	 * 
	 * 
	 * @param randomGenerator
	 *            - the dedicated random generator
	 * @param backpackItems
	 *            - map of backpack values to their unique identifiers
	 * @param backpackLimitWeight
	 *            - limit of the backpack
	 */
	public void updateFitnessValue(Random randomGenerator, Map<Integer, BackpackItem> backpackItems,
			int backpackLimitWeight) {
		if (items.size() > backpackItems.size()) {
			throw new IllegalArgumentException(String.format(
					"The number of bits in the ordered item sequence is %d and the size of the map containing backpack items is %d. They must be equal!",
					items.size(), backpackItems.size()));
		}
		int totalWeight = 0;
		int totalValue = 0;
		ListIterator<Boolean> itemsIterator = this.items.listIterator();
		while (itemsIterator.hasNext()) {
			int nextIndex = itemsIterator.nextIndex();
			if (itemsIterator.next()) {
				totalWeight += backpackItems.get(new Integer(nextIndex)).getWeight();
				totalValue += backpackItems.get(new Integer(nextIndex)).getValue();
			}
		}
		while (totalWeight > backpackLimitWeight) {
			while (true) {
				int index = (int) Math.round(randomGenerator.nextDouble() * (PopulationGenerator.numberOfItems - 1));
				if (items.get(index)) {
					items.set(index, new Boolean(false));
					break;
				}
			}
			totalWeight = 0;
			totalValue = 0;
			itemsIterator = items.listIterator();
			while (itemsIterator.hasNext()) {
				int nextIndex = itemsIterator.nextIndex();
				if (itemsIterator.next()) {
					totalWeight += backpackItems.get(new Integer(nextIndex)).getWeight();
					totalValue += backpackItems.get(new Integer(nextIndex)).getValue();
				}
			}
		}
		this.fitnessValue = totalValue;
	}

	/**
	 * Method that implements one point crossover between two parent sequences.
	 * A random index is provided between zero and the number of items. After
	 * that all "bits" from the first parent that are after the generated index
	 * and all "bits" from the second parent that are before the generated index
	 * are transferred to the child which is "this" sequence.
	 * 
	 * @param randomIndex
	 *            - the random index that will split the parents
	 * @param firstSequence
	 *            - the first parent sequence
	 * @param secondSequence
	 *            - the second parent sequence
	 */
	public void onePointCrossover(int randomIndex, OrderedItemSequence firstSequence,
			OrderedItemSequence secondSequence) {
		ListIterator<Boolean> iterator = firstSequence.getItems().listIterator();
		while (iterator.hasNext()) {
			int nextIndex = iterator.nextIndex();
			if (nextIndex < randomIndex) {
				this.items.set(nextIndex, secondSequence.getItems().get(nextIndex));
			} else {
				this.items.set(nextIndex, firstSequence.getItems().get(nextIndex));
			}
			iterator.next();
		}
	}

	/**
	 * Method that implements two point crossover between two parent sequences.
	 * Two random indexes are expected as an input between zero and the number
	 * of items. After that all "bits" from the second parent that are between
	 * the generated indexes and all "bits" from the first parent that are
	 * before the smaller generated index and after the larger generated index
	 * are transferred to the child which is "this" sequence.
	 * 
	 * @param firstIndexPoint-
	 *            the first random index that will split the parents
	 * @param secondIndexPoint-
	 *            the second random index that will split the parents
	 * @param firstSequence
	 *            - the first parent sequence
	 * @param secondSequence
	 *            - the second parent sequence
	 */
	public void twoPointCrossover(int firstIndexPoint, int secondIndexPoint, OrderedItemSequence firstSequence,
			OrderedItemSequence secondSequence) {
		ListIterator<Boolean> iterator = firstSequence.getItems().listIterator();
		while (iterator.hasNext()) {
			if ((Math.min(firstIndexPoint, secondIndexPoint) < iterator.nextIndex())
					&& (iterator.nextIndex() < Math.max(firstIndexPoint, secondIndexPoint))) {
				this.items.set(iterator.nextIndex(), secondSequence.getItems().get(iterator.nextIndex()));
			} else {
				this.items.set(iterator.nextIndex(), firstSequence.getItems().get(iterator.nextIndex()));
			}
			iterator.next();
		}
	}

	/**
	 * Method that implements uniform crossover between two parent sequences.
	 * For every index between zero and the number of items random boolean is
	 * generated. If the generated boolean is true the corresponding item from
	 * the second parent sequence is transferred to "this" sequence. Otherwise
	 * the corresponding item from the first parent is transferred to
	 * "this sequence"
	 * 
	 * @param randomGenerator
	 *            - dedicated random generator
	 * @param firstSequence
	 *            - the first parent sequence
	 * @param secondSequence
	 *            - the second parent sequence
	 */
	public void uniformCrossover(Random randomGenerator, OrderedItemSequence firstSequence,
			OrderedItemSequence secondSequence) {
		ListIterator<Boolean> iterator = firstSequence.getItems().listIterator();
		while (iterator.hasNext()) {
			if (randomGenerator.nextBoolean()) {
				this.items.set(iterator.nextIndex(), secondSequence.getItems().get(iterator.nextIndex()));
			} else {
				this.items.set(iterator.nextIndex(), firstSequence.getItems().get(iterator.nextIndex()));
			}
			iterator.next();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("OrderedItemSequence [fitnessValue=%s, items(weight, value)=[ ", this.fitnessValue));
		ListIterator<Boolean> iterator = items.listIterator();
		while (iterator.hasNext()) {
			int nextIndex = iterator.nextIndex();
			if (iterator.next()) {
				sb.append(String.format("#%d(%d, %d) ", nextIndex,
						PopulationGenerator.backpackItems.get(nextIndex).getWeight(),
						PopulationGenerator.backpackItems.get(nextIndex).getValue()));
			}
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int compareTo(OrderedItemSequence sequence) {
		if (this.fitnessValue > sequence.getFitnessValue()) {
			return 1;
		} else if (this.fitnessValue < sequence.getFitnessValue()) {
			return -1;
		}
		return 0;
	}

}
