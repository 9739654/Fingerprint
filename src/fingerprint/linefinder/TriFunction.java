package fingerprint.linefinder;

@FunctionalInterface
public interface TriFunction<S, T, U, V> {
	V accept(S a, T b, U c);
}
