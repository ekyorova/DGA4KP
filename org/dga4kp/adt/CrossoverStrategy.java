package org.dga4kp.adt;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum that provides direct mapping from specific crossover strategy to
 * integer.
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 *
 */
public enum CrossoverStrategy {
	ONE_POINT_CROSSOVER(1), TWO_POINT_CROSSOVER(2), UNIFORM_CROSSOVER(3);
	private int id;

	private CrossoverStrategy(int s) {
		id = s;
	}

	public int getId() {
		return id;
	}

	private static final Map<Integer, CrossoverStrategy> map;

	static {
		map = new HashMap<Integer, CrossoverStrategy>();
		for (CrossoverStrategy strategy : CrossoverStrategy.values()) {
			map.put(strategy.getId(), strategy);
		}
	}

	public static CrossoverStrategy findByKey(int i) {
		return map.get(i);
	}

}