package fingerprint.linefinder;

@FunctionalInterface
public interface TriFunction<S, T, U, V> {
	V apply(S a, T b, U c);
}
