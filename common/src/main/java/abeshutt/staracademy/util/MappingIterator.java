package abeshutt.staracademy.util;

import java.util.Iterator;
import java.util.function.Function;

public class MappingIterator<INPUT, OUTPUT> implements Iterator<OUTPUT> {

	private final Iterator<INPUT> parent;
	private final Function<INPUT, OUTPUT> mapper;

	private boolean isDirty = true;
	private boolean hasNext;
	private OUTPUT next;

	public MappingIterator(Iterator<INPUT> parent, Function<INPUT, OUTPUT> mapper) {
		this.parent = parent;
		this.mapper = mapper;
	}

	@Override
	public boolean hasNext() {
		this.compute();
		return this.hasNext;
	}

	@Override
	public OUTPUT next() {
		this.compute();
		this.isDirty = true;
		return this.next;
	}

	protected void compute() {
		if(!this.isDirty) return;

		if(this.parent.hasNext()) {
			OUTPUT value = this.mapper.apply(this.parent.next());
			this.hasNext = true;
			this.next = value;
			this.isDirty = false;
		} else {
			this.hasNext = false;
			this.next = null;
			this.isDirty = false;
		}
	}

}
