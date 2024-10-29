package org.example.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;


public class Initialization {
    private static final SecureRandom random = new SecureRandom();
    private static final BigInteger TWO = BigInteger.valueOf(2);  // 自定义的 TWO

    public static BigInteger[] generateLargePrimes(int bits) {
        BigInteger q, p;
        while (true) {
            q = new BigInteger(bits, random);
            if (q.isProbablePrime(100)) {
                p = q.multiply(TWO).add(BigInteger.ONE);  // 使用自定义的 TWO
                if (p.isProbablePrime(100)) {
                    return new BigInteger[]{p, q};  // 返回 p 和 q
                }
            }
        }
    }
    public static BigInteger generate_large_prime(int bits) {
        BigInteger y;
        while (true) {
            y = new BigInteger(bits, random);
            if (y.isProbablePrime(100)) {
                y = y.multiply(TWO).add(BigInteger.ONE);
                return y;
            }

        }
    }



    public static BigInteger findGenerator(BigInteger p, BigInteger q) {
        BigInteger g = TWO;  // 使用自定义的 TWO
        while (g.compareTo(p) < 0) {
            if (g.modPow(q, p).equals(BigInteger.ONE)) {
                return g;  // 返回生成元 g
            }
            g = g.add(BigInteger.ONE);
        }
        return null;  // 如果没有找到生成元，返回 null
    }
    public static BigInteger generateRandomInZStarN2(BigInteger N) {
        BigInteger N2 = N.multiply(N); // 计算 N^2
        BigInteger r;

        do {
            r = new BigInteger(N.bitLength() * 2, random).mod(N2); // 生成随机数 r ∈ Z*_N²
        } while (r.equals(BigInteger.ZERO)); // 确保 r 不为 0

        return r;
    }
    public static BigInteger bigIntegerPow(BigInteger base, BigInteger exponent) {
        if (exponent.equals(BigInteger.ZERO)) {
            return BigInteger.ONE; // 任意数的0次方是1
        } else if (exponent.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            BigInteger halfPow = bigIntegerPow(base, exponent.divide(BigInteger.valueOf(2)));
            return halfPow.multiply(halfPow); // 如果是偶数
        } else {
            return base.multiply(bigIntegerPow(base, exponent.subtract(BigInteger.ONE))); // 如果是奇数
        }
    }



}


