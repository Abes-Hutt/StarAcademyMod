package abeshutt.staracademy.util;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class FilteringIterator<INPUT> implements Iterator<INPUT> {

	private final Iterator<INPUT> parent;
	private final Predicate<INPUT> filter;

	private boolean isDirty = true;
	private boolean hasNext;
	private INPUT next;

	public FilteringIterator(Iterator<INPUT> parent, Predicate<INPUT> filter) {
		this.parent = parent;
		this.filter = filter;
	}

	@Override
	public boolean hasNext() {
		this.compute();
		return this.hasNext;
	}

	@Override
	public INPUT next() {
		this.compute();
		this.isDirty = true;
		return this.next;
	}

	protected void compute() {
		if(!this.isDirty) return;

		do {
			if(!this.parent.hasNext()) {
				this.hasNext = false;
				this.next = null;
				this.isDirty = false;
				return;
			}

			this.next = this.parent.next();
		} while(!this.filter.test(this.next));

		this.hasNext = true;
		this.isDirty = false;
	}

}
