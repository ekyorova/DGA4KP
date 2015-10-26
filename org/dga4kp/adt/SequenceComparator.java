package org.dga4kp.adt;

import java.util.Comparator;

/**
 * Comparator for two sequences. It compares second sequence to first sequence
 * so the result collection is sorted in descending manner.
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 *
 */
public class SequenceComparator implements Comparator<OrderedItemSequence> {

	@Override
	public int compare(OrderedItemSequence firstSequence, OrderedItemSequence secondSequence) {
		return secondSequence.compareTo(firstSequence);
	}
}