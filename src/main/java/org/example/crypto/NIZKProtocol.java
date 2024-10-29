package org.example.crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class NIZKProtocol {
    private final SecureRandom random = new SecureRandom();
    private final BigInteger p,q,g;


    // Constructor that accepts p, q, and g
    public NIZKProtocol(BigInteger p, BigInteger q, BigInteger g) {
        this.p = p;
        this.q = q;
        this.g = g;
    }

    public boolean schnorrProtocol(BigInteger x, BigInteger y) {
        // Step 1: Prover generates a random commitment R
        BigInteger r = new BigInteger(q.bitLength(), random).mod(q.subtract(BigInteger.ONE)).add(BigInteger.ONE);
        BigInteger R = g.modPow(r, p);
        System.out.println("Prover's commitment (R): " + R);

        // Step 2: Verifier generates a random challenge c
        BigInteger c = new BigInteger(q.bitLength(), random).mod(q.subtract(BigInteger.ONE)).add(BigInteger.ONE);
        System.out.println("Verifier's challenge (c): " + c);

        // Step 3: Prover computes response s
        BigInteger s = r.add(c.multiply(x)).mod(q);
        System.out.println("Prover's response (s): " + s);

        // Step 4: Verifier checks if g^s == R * y^c mod p
        BigInteger verification = g.modPow(s, p);
        BigInteger rightSide = R.multiply(y.modPow(c, p)).mod(p);
        return verification.equals(rightSide);
    }

    public BigInteger[] schnorrNIZKProof(BigInteger x, BigInteger y) throws NoSuchAlgorithmException {
        // Step 1: Prover generates a random commitment R
        BigInteger r = new BigInteger(q.bitLength(), random).mod(q.subtract(BigInteger.ONE)).add(BigInteger.ONE);
        BigInteger R = g.modPow(r, p);

        // Step 2: Prover generates a challenge by hashing R and y (using Fiat-Shamir)
        BigInteger c = hash(R, y).mod(q);

        // Step 3: Prover computes response s
        BigInteger s = r.add(c.multiply(x)).mod(q);

        // The proof consists of (R, s)
        return new BigInteger[]{R, s};
    }

    public boolean verifyNIZKProof(BigInteger y, BigInteger[] proof) throws NoSuchAlgorithmException {
        BigInteger R = proof[0];
        BigInteger s = proof[1];

        // Recompute challenge c from R and y
        BigInteger c = hash(R, y).mod(q);

        // Verifier checks if g^s == R * y^c mod p
        BigInteger leftSide = g.modPow(s, p);
        BigInteger rightSide = R.multiply(y.modPow(c, p)).mod(p);

        return leftSide.equals(rightSide);
    }

    private BigInteger hash(BigInteger R, BigInteger y) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] input = (R.toString() + y.toString()).getBytes();
        byte[] hash = digest.digest(input);
        return new BigInteger(1, hash);
    }
}
