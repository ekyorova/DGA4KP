package org.dga4kp.adt;

/**
 * The BackpackItem class provides an abstraction for an item that will or will
 * not be put in the backpack.
 * 
 * @author Elena Kyorova <elenakyorova@gmail.com>
 * @author Mihaela Stoycheva <mihaela.stoycheva@gmail.com>
 * 
 */
public class BackpackItem implements Comparable<BackpackItem> {
	// Unique identifier for the item
	private int id;
	// Value of the item
	private int value;
	// Weigh of the item
	private int weight;

	public BackpackItem() {
		this.id = 0;
		this.value = 0;
		this.weight = 0;
	}

	public BackpackItem(int id, int value, int weight) {
		this.id = id;
		this.value = value;
		this.weight = weight;
	}

	public BackpackItem(BackpackItem item) {
		this.id = item.getId();
		this.value = item.getValue();
		this.weight = item.getWeight();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return String.format("BackpackItem [id=%s, value=%s, weight=%s]", id, value, weight);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + value;
		result = prime * result + weight;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BackpackItem other = (BackpackItem) obj;
		if (id != other.id) {
			return false;
		}
		if (value != other.value) {
			return false;
		}
		if (weight != other.weight) {
			return false;
		}
		return true;
	}

	@Override
	/**
	 * Methods to compare item's value-to-weigh ratio
	 */
	public int compareTo(BackpackItem item) {
		double thisValueWeightRatio = ((double) this.value) / ((double) this.weight);
		double itemValueWeightRatio = ((double) item.getValue()) / ((double) item.getWeight());
		if (thisValueWeightRatio > itemValueWeightRatio) {
			return 1;
		} else if (thisValueWeightRatio < itemValueWeightRatio) {
			return -1;
		}
		return 0;
	}

}
