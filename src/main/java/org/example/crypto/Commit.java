package org.example.crypto;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Commit {

    private final SecureRandom random = new SecureRandom();
    private final BigInteger N; // N 的值
    private final BigInteger q; // q 的值
    private final BigInteger g1; // g1 的值
    private final BigInteger g2; // g2 的值
    private final BigInteger k; // k 的值

    private final BigInteger y; // y 的值
    private final BigInteger Y;
    private final KeyGen gen;

    public Commit(BigInteger N,BigInteger q, BigInteger g1, BigInteger g2, BigInteger k, BigInteger y, BigInteger Y,KeyGen gen) {
        this.N = N;
        this.q = q;
        this.g1 = g1;
        this.g2 = g2;
        this.k = k;
        this.y = y;
        this.Y = Y;
        this.gen = gen;
    }

    public Object[] commitAlgorithm(BigInteger vkAB, BigInteger cSigma,BigInteger sSigma, BigInteger Y, BigInteger y) throws NoSuchAlgorithmException {
        // 验证输入

        // 生成时间锁谜题
        BigInteger two = BigInteger.valueOf(2); // 2
        BigInteger exponent = BigInteger.valueOf(2).modPow(k,N); // 2^(2^k)

        // 计算 h = g1^(exponent) mod N
        BigInteger h = g1.modPow(exponent, N);
        BigInteger r = Initialization.generateRandomInZStarN2(N); // 随机选取 r ∈ Z∗N²
        BigInteger u = g1.modPow(r, N);
        BigInteger v = h.modPow(r.multiply(N), N.multiply(N)).multiply(BigInteger.ONE.add(N).modPow(y, N.multiply(N))).mod(N.multiply(N));

        StringBuilder sb = new StringBuilder();
        BigInteger[] Z = {u, v};
        for (BigInteger z : Z) {
            sb.append(z.toString());  // 拼接每个 BigInteger 的字符串表示
        }
        //处理Z数组
        BigInteger inputZ = new BigInteger(sb.toString());


        // 生成 NIZK 证明
        // 这是\alpha \beta
        BigInteger alpha = Initialization.generateRandomInZStarN2(N);
        BigInteger beta = new BigInteger(q.bitLength(), random).mod(q);

        BigInteger R1 = g1.modPow(alpha, N);
        BigInteger R2 = h.modPow(alpha.multiply(N), N.multiply(N)).multiply(BigInteger.ONE.add(N).modPow(beta, N.multiply(N))).mod(N.multiply(N));
        System.out.println("R2:" + R2);
        BigInteger R3 = g2.modPow(beta, N);
        //BigInteger R3 = g2.pow(beta.intValue());
        //BigInteger R3 = Initialization.bigIntegerPow(g2,beta);
        System.out.println("R3:" + R3);

        // 计算 e 由于Z是数组形式，因此我先处理了Z（拼接）
        BigInteger e = gen.H(N, g1, g2, vkAB, cSigma,sSigma, Y, inputZ, R1, R2, R3);

        BigInteger z1 = alpha.subtract(e.multiply(r));
        BigInteger z2 = beta.subtract(e.multiply(y));

        BigInteger[] proof = {R1, R2, R3, z1, z2};

        // 输出 C = (Z, 2^k) 和 π
        return new Object[]{Z, exponent, proof,h};
    }


}
