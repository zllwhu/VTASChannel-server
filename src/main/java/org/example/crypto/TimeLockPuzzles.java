package org.example.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class TimeLockPuzzles {

    private static final SecureRandom random = new SecureRandom();

    // Generates a time-lock puzzle
    public BigInteger[] generatePuzzle(int t) {
        // Generate large primes p and q
        BigInteger p = BigInteger.probablePrime(512, random);
        BigInteger q = BigInteger.probablePrime(512, random);
        BigInteger n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Random AES key
        BigInteger key = new BigInteger(128, random);

        // Generate random starting value a
        BigInteger a = new BigInteger(4096, random).mod(n);

        // Fast way to compute (a^2)^t mod n
        BigInteger e = BigInteger.valueOf(2).modPow(BigInteger.valueOf(t), phi);
        BigInteger b = a.modPow(e, n);

        // Compute ck = (key + b) % n
        BigInteger ck = (key.add(b)).mod(n);

        // Return the puzzle (n, a, t, ck)
        return new BigInteger[]{n, a, BigInteger.valueOf(t), ck};
    }

    // Solves the time-lock puzzle
    public BigInteger solvePuzzle(BigInteger n, BigInteger a, BigInteger t, BigInteger ck) {
        BigInteger tmp = a;
        for (BigInteger i = BigInteger.ZERO; i.compareTo(t) < 0; i = i.add(BigInteger.ONE)) {
            tmp = tmp.modPow(BigInteger.valueOf(2), n);
        }

        // Return (ck - tmp) % n, which is the key
        return ck.subtract(tmp).mod(n);
    }
}
