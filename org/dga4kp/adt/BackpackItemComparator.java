package org.dga4kp.adt;

import java.util.Comparator;

/**
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 */
public class BackpackItemComparator implements Comparator<BackpackItem> {
	@Override
	public int compare(BackpackItem item1, BackpackItem item2) {
		return item1.compareTo(item2);
	}
}