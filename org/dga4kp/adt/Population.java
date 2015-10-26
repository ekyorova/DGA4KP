package org.dga4kp.adt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Population class provides abstract representation for a population in GA. It
 * has an array of sequences that are also called individuals.
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 *
 */
public class Population {
	private List<OrderedItemSequence> individuals;

	public Population() {
		this.individuals = new ArrayList<OrderedItemSequence>();
	}

	public Population(Population population) {
		this.individuals = new ArrayList<OrderedItemSequence>(population.getIndividuals());
	}

	public Population(List<OrderedItemSequence> individuals) {
		this.individuals = individuals;
	}

	public List<OrderedItemSequence> getIndividuals() {
		return individuals;
	}

	public void setIndividuals(List<OrderedItemSequence> individuals) {
		this.individuals = individuals;
	}

	public void addIndividual(OrderedItemSequence inidividual) {
		individuals.add(inidividual);
	}

	public void sort() {
		Collections.sort(individuals, new SequenceComparator());
	}

	@Override
	public String toString() {
		return String.format("Population [individuals=%s]", individuals);
	}
}