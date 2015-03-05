package fingerprint;

import java.util.function.Supplier;

/**
 * @author mati
 */
public class LazyLoad<T> {

	private Supplier<T> supplier = () -> null;
	private T value;

	public LazyLoad() {}

	public LazyLoad withSupplier(Supplier<T> supplier) {
		this.supplier = supplier;
		return this;
	}

	public T get() {
		if (value == null)
			value = supplier.get();
		return value;
	}
}
