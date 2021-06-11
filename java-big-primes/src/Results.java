import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

public class Results {
    private SortedSet<BigInteger> primes;

    public Results() {
        this.primes = new TreeSet<>();
    }

    public int getSize() {
        synchronized (this) {
            return this.primes.size();
        }
    }

    public void addPrime(BigInteger prime) {
        synchronized (this) {
            this.primes.add(prime);
        }
    }

    public void print() {
        synchronized (this) {
            this.primes.forEach(System.out::println);
        }
    }
}
