import org.example.Main;
import org.example.crypto.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@SpringBootTest(classes = Main.class)
public class PerformanceTest {

    BigInteger[] primes = Initialization.generateLargePrimes(200);
    BigInteger p = primes[0];
    BigInteger q = primes[1];
    BigInteger g = Initialization.findGenerator(p, q);
    KeyGen keyGen = new KeyGen(p, q, g);

    BigInteger[] aliceKeys;
    BigInteger[] bobKeys;
    BigInteger aliceSharedKey;
    BigInteger bobSharedKey;
    KeyGen.SchnorrSignature aliceSignature;
    KeyGen.SchnorrSignature bobSignature;

    BigInteger g1;
    BigInteger g2;
    BigInteger y;
    BigInteger Y;
    BigInteger yb;
    BigInteger ya;
    BigInteger Yb;
    BigInteger Ya;
    AdaptSchnorr bob = new AdaptSchnorr("Bob", keyGen);
    BigInteger[] preSignature;
    BigInteger[] preSignature1;
    private static final SecureRandom random = new SecureRandom();
    BigInteger[] Z;
    BigInteger N;
    BigInteger[] proof;
    BigInteger powValue;

    private static final String FILE_NAME = "performance_test_results.txt";
    private static FileWriter fileWriter;
    static {
        try {
            fileWriter = new FileWriter(FILE_NAME, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeResult(String testName, long totalNanoTime) throws IOException {
        fileWriter.write(testName + " Average Time: " + totalNanoTime / 100 + " ns\n");
        fileWriter.flush();
    }

    @Test
    public void testChannelInitForA() throws NoSuchAlgorithmException, IOException {
        long totalNanoTime = 0;

        for (int i = 0; i < 100; i++) {
            bobKeys = keyGen.genKey();
            BigInteger bobPrivateKey = bobKeys[0];
            BigInteger bobPublicKey = bobKeys[1];

            long startTime = System.nanoTime();
            aliceKeys = keyGen.genKey();
            long endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);

            bobSharedKey = keyGen.computeShareKey(aliceKeys[1], bobPrivateKey);
            bobSignature = keyGen.signMessage(bobSharedKey, bobPrivateKey);

            startTime = System.nanoTime();
            BigInteger alicePrivateKey = aliceKeys[0];
            BigInteger alicePublicKey = aliceKeys[1];
            aliceSharedKey = keyGen.computeShareKey(bobKeys[1], alicePrivateKey);
            aliceSignature = keyGen.signMessage(aliceSharedKey, alicePrivateKey);
            boolean isBobSignatureValid = keyGen.verifySignature(bobSignature, aliceSharedKey, bobKeys[1]);
            endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);

            g2 = Initialization.generate_large_prime(200);
            y = Initialization.generate_large_prime(10);
            Y = Initialization.bigIntegerPow(g2, y);

            startTime = System.nanoTime();
            preSignature = bob.preSign(Y);
            BigInteger c = preSignature[0];
            BigInteger s = preSignature[1];
            endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);

            BigInteger[] PrimesForComiit = Initialization.generateLargePrimes(200);
            BigInteger pp = PrimesForComiit[0];
            BigInteger qq = PrimesForComiit[1];
            N = pp.multiply(qq);
            BigInteger Vkab = bobSharedKey;
            BigInteger gg;
            do {
                gg = new BigInteger(N.bitLength(), random).mod(N);
            } while (gg.equals(BigInteger.ZERO));
            BigInteger gSquared = gg.multiply(gg).mod(N);
            g1 = gSquared.negate().mod(N);
            BigInteger k1 = Initialization.generate_large_prime(10);
            BigInteger k = BigInteger.ONE.shiftLeft(k1.intValue());
            BigInteger Zq = new BigInteger(1000, random);
            while (!Zq.isProbablePrime(100)) {
                Zq = new BigInteger(1000, random);
            }
            Commit commit = new Commit(N, Zq, g1, g2, k, y, Y, keyGen);
            Object[] result = commit.commitAlgorithm(Vkab, c, s, Y, y);
            Z = (BigInteger[]) result[0];
            powValue = (BigInteger) result[1];
            proof = (BigInteger[]) result[2];

            startTime = System.nanoTime();
            boolean isSignatureValid = bob.finalizeSign(Y, preSignature[0], preSignature[1]);
            StringBuilder sb = new StringBuilder();
            for (BigInteger z : Z) {
                sb.append(z.toString());
            }
            BigInteger inputZ = new BigInteger(sb.toString());
            BigInteger e = keyGen.H(N, g1, g2, bobSharedKey, c, s, Y, inputZ, proof[0], proof[1], proof[2]);
            BigInteger computedR1 = g1.modPow(proof[3], N).multiply(Z[0].modPow(e, N)).mod(N);
            BigInteger h = g1.modPow(powValue, N);
            BigInteger computedR2 = Z[1].modPow(e, N.multiply(N)).multiply(h.modPow(proof[3].multiply(N), N.multiply(N)))
                    .multiply(BigInteger.ONE.add(N).modPow(proof[4], N.multiply(N)))
                    .mod(N.multiply(N));
            BigInteger computedR3 = g2.modPow(proof[4], N).multiply(Y.modPow(e, N)).mod(N);
            boolean isR1Valid = computedR1.equals(proof[0]);
            boolean isR2Valid = computedR2.equals(proof[1]);
            boolean isR3Valid = computedR3.equals(proof[2]);
            endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);
        }

        writeResult("testChannelInitForA", totalNanoTime);
    }

    @Test
    public void testChannelInitForB() throws NoSuchAlgorithmException, IOException {
        long totalNanoTime = 0;

        for (int i = 0; i < 100; i++) {
            long startTime = System.nanoTime();
            bobKeys = keyGen.genKey();
            long endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);

            aliceKeys = keyGen.genKey();
            BigInteger alicePrivateKey = aliceKeys[0];
            BigInteger alicePublicKey = aliceKeys[1];
            aliceSharedKey = keyGen.computeShareKey(bobKeys[1], alicePrivateKey);
            aliceSignature = keyGen.signMessage(aliceSharedKey, alicePrivateKey);

            startTime = System.nanoTime();
            BigInteger bobPrivateKey = bobKeys[0];
            BigInteger bobPublicKey = bobKeys[1];
            bobSharedKey = keyGen.computeShareKey(aliceKeys[1], bobPrivateKey);
            bobSignature = keyGen.signMessage(bobSharedKey, bobPrivateKey);
            boolean isAliceSignatureValid = keyGen.verifySignature(aliceSignature, bobSharedKey, aliceKeys[1]);

            g2 = Initialization.generate_large_prime(200);
            y = Initialization.generate_large_prime(10);
            Y = Initialization.bigIntegerPow(g2, y);

            preSignature = bob.preSign(Y);
            BigInteger c = preSignature[0];
            BigInteger s = preSignature[1];

            TimeLockPuzzles puzzleGen = new TimeLockPuzzles();
            BigInteger[] puzzle = puzzleGen.generatePuzzle(1000);
            BigInteger[] PrimesForComiit = Initialization.generateLargePrimes(200);
            BigInteger pp = PrimesForComiit[0];
            BigInteger qq = PrimesForComiit[1];
            N = pp.multiply(qq);
            BigInteger Vkab = bobSharedKey;
            BigInteger gg;
            do {
                gg = new BigInteger(N.bitLength(), random).mod(N);
            } while (gg.equals(BigInteger.ZERO));
            BigInteger gSquared = gg.multiply(gg).mod(N);
            g1 = gSquared.negate().mod(N);
            BigInteger k1 = Initialization.generate_large_prime(10);
            BigInteger k = BigInteger.ONE.shiftLeft(k1.intValue());
            BigInteger Zq = new BigInteger(1000, random);
            while (!Zq.isProbablePrime(100)) {
                Zq = new BigInteger(1000, random);
            }
            Commit commit = new Commit(N, Zq, g1, g2, k, y, Y, keyGen);
            Object[] result = commit.commitAlgorithm(Vkab, c, s, Y, y);
            Z = (BigInteger[]) result[0];
            powValue = (BigInteger) result[1];
            proof = (BigInteger[]) result[2];
            endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);
        }

        writeResult("testChannelInitForB", totalNanoTime);
    }

    @Test
    public void testChannelUpdate() throws NoSuchAlgorithmException, IOException {
        long totalNanoTime = 0;

        for (int i = 0; i < 100; i++) {
            g2 = Initialization.generate_large_prime(200);
            ya = Initialization.generate_large_prime(10);
            Ya = Initialization.bigIntegerPow(g2, ya);
            long startTime = System.nanoTime();
            yb = Initialization.generate_large_prime(10);
            Yb = Initialization.bigIntegerPow(g2, yb);
            preSignature1 = bob.preSign(Ya);
            long endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);
        }

        writeResult("testChannelUpdate", totalNanoTime);
    }

    @Test
    public void testChannelClose() throws NoSuchAlgorithmException, IOException {
        long totalNanoTime = 0;

        for (int i = 0; i < 100; i++) {
            g2 = Initialization.generate_large_prime(200);
            ya = Initialization.generate_large_prime(10);
            Ya = Initialization.bigIntegerPow(g2, ya);
            preSignature1 = bob.preSign(Ya);
            long startTime = System.nanoTime();
            BigInteger c = preSignature1[0];
            BigInteger s = preSignature1[1];
            boolean isSignatureValid = bob.finalizeSign(Ya, c, s);
            long endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);
        }

        writeResult("testChannelClose", totalNanoTime);
    }

    @Test
    public void testChannelDelay() throws NoSuchAlgorithmException, IOException {
        long totalNanoTime = 0;

        for (int i = 0; i < 100; i++) {
            g2 = Initialization.generate_large_prime(200);
            ya = Initialization.generate_large_prime(10);
            Ya = Initialization.bigIntegerPow(g2, ya);
            preSignature1 = bob.preSign(Ya);
            BigInteger c = preSignature1[0];
            BigInteger s = preSignature1[1];
            aliceKeys = keyGen.genKey();
            bobKeys = keyGen.genKey();
            bobSharedKey = keyGen.computeShareKey(aliceKeys[1], bobKeys[0]);
            BigInteger Vkab = bobSharedKey;
            BigInteger[] PrimesForComiit = Initialization.generateLargePrimes(200);
            BigInteger pp = PrimesForComiit[0];
            BigInteger qq = PrimesForComiit[1];
            N = pp.multiply(qq);
            BigInteger k1 = Initialization.generate_large_prime(10);
            BigInteger k = BigInteger.ONE.shiftLeft(k1.intValue());
            BigInteger Zq = new BigInteger(1000, random);
            while (!Zq.isProbablePrime(100)) {
                Zq = new BigInteger(1000, random);
            }
            BigInteger gg;
            do {
                gg = new BigInteger(N.bitLength(), random).mod(N);
            } while (gg.equals(BigInteger.ZERO));
            BigInteger gSquared = gg.multiply(gg).mod(N);
            g1 = gSquared.negate().mod(N);
            Commit commit = new Commit(N, Zq, g1, g2, k, ya, Ya, keyGen);
            long startTime = System.nanoTime();
            Object[] result = commit.commitAlgorithm(Vkab, c, s, Ya, ya);
            long endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);
        }

        writeResult("testChannelDelay", totalNanoTime);
    }

    @Test
    public void testChannelRetrieve() throws NoSuchAlgorithmException, IOException {
        long totalNanoTime = 0;

        for (int i = 0; i < 100; i++) {
            g2 = Initialization.generate_large_prime(200);
            ya = Initialization.generate_large_prime(10);
            Ya = Initialization.bigIntegerPow(g2, ya);
            preSignature1 = bob.preSign(Ya);
            BigInteger c = preSignature1[0];
            BigInteger s = preSignature1[1];
            long startTime = System.nanoTime();
            boolean isSignatureValid = bob.finalizeSign(Ya, c, s);
            long endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);
        }

        writeResult("testChannelRetrieve", totalNanoTime);
    }

    @Test
    public void testPrimitives() throws IOException, NoSuchAlgorithmException {
        long totalNanoTime = 0;

        for (int i = 0; i < 100; i++) {
            long startTime = System.nanoTime();
            aliceKeys = keyGen.genKey();
            long endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);
        }
        writeResult("KeyGen", totalNanoTime);
        totalNanoTime = 0;

        for (int i = 0; i < 100; i++) {
            g2 = Initialization.generate_large_prime(200);
            ya = Initialization.generate_large_prime(10);
            Ya = Initialization.bigIntegerPow(g2, ya);
            long startTime = System.nanoTime();
            preSignature1 = bob.preSign(Ya);
            long endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);
        }
        writeResult("PreSign", totalNanoTime);
        totalNanoTime = 0;

        for (int i = 0; i < 100; i++) {
            BigInteger c = preSignature1[0];
            BigInteger s = preSignature1[1];
            long startTime = System.nanoTime();
            boolean isSignatureValid = bob.finalizeSign(Ya, c, s);
            long endTime = System.nanoTime();
            totalNanoTime += (endTime - startTime);
        }
        writeResult("Adapt", totalNanoTime);
        totalNanoTime = 0;

        for (int i = 0; i < 100; i++) {

        }

    }
}
