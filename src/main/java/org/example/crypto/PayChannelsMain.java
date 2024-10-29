package org.example.crypto;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PayChannelsMain {
    private static final SecureRandom random = new SecureRandom();

    public static void main(String[] args) throws NoSuchAlgorithmException {
        //系统初始化 生成素数p,q
        System.out.println("######系统初始化######");
        BigInteger[] primes = Initialization.generateLargePrimes(200);  // 设置位数，比如 512 位
        BigInteger p = primes[0];
        BigInteger q = primes[1];
        BigInteger g = Initialization.findGenerator(p, q);
        System.out.println("生成大素数p（200位）: " + p);
        System.out.println("生成大素数q（200位）: " + q);

        // 创建密钥生成器实例
        KeyGen keyGen = new KeyGen(p, q, g);
        System.out.println("设置生成元 g: " + g);
        //BigInteger[] keys = keyGen.nizkkeygen();
        // BigInteger x = keys[0];
        // BigInteger y = keys[1];


        //首先 Alice 和 Bob 进行密钥协商，共同获得自己的私钥和v_ab
        System.out.println("######Alice Bob密钥协商开始######");
        BigInteger[] aliceKeys = keyGen.genKey();
        BigInteger[] bobKeys = keyGen.genKey();

        BigInteger alicePrivateKey = aliceKeys[0];
        BigInteger alicePublicKey = aliceKeys[1];
        System.out.println("Alice私钥为 x: " + alicePrivateKey);
        System.out.println("Alice公钥为 y: " + alicePublicKey);
        BigInteger bobPrivateKey = bobKeys[0];
        BigInteger bobPublicKey = bobKeys[1];
        System.out.println("Alice私钥为 x: " + bobPrivateKey);
        System.out.println("Bob公钥为 y: " + bobPublicKey);
        System.out.println("######Alice Bob分别计算共享密钥######");

        BigInteger aliceSharedKey = keyGen.computeShareKey(bobPublicKey, alicePrivateKey);
        BigInteger bobSharedKey = keyGen.computeShareKey(alicePublicKey, bobPrivateKey);

        System.out.println("Alice的共享公钥: " + aliceSharedKey);
        System.out.println("Bob的共享公钥: " + bobSharedKey);

        System.out.println("######Alice 对她的公钥进行 Schnorr 签名并发送给 Bob######");
        KeyGen.SchnorrSignature aliceSignature = keyGen.signMessage(aliceSharedKey, alicePrivateKey);
        System.out.println("Alice的签名: (r = " + aliceSignature.getR() + ", s = " + aliceSignature.getS() + ")");

        System.out.println("######Bob 验证 Alice 的签名######");
        boolean isAliceSignatureValid = keyGen.verifySignature(aliceSignature, bobSharedKey, alicePublicKey);
        System.out.println("Alice的签名合法性检验" + isAliceSignatureValid);

        System.out.println("######Bob 对他的公钥进行 Schnorr 签名并发送给 Alice######");
        KeyGen.SchnorrSignature bobSignature = keyGen.signMessage(bobSharedKey, bobPrivateKey);
        System.out.println("Bob的签名: (r = " + bobSignature.getR() + ", s = " + bobSignature.getS() + ")");

        System.out.println("######Alice 验证 Bob 的签名######");
        boolean isBobSignatureValid = keyGen.verifySignature(bobSignature, bobSharedKey, bobPublicKey);
        System.out.println("Bob的签名合法性检验" + isBobSignatureValid);
        System.out.println("######共享密钥v_ab已确定######");
        System.out.println("######输出(y,Y)######");
        BigInteger y1 = Initialization.generate_large_prime(200);  // 设置位数，比如 512 位
        BigInteger Y1 = g.modPow(y1, bobPrivateKey);
        System.out.println("这是y和Y:" + "y1 = " + y1 + "Y2 = " + Y1);
        System.out.println("######共同获得TX_R的预签名######");

        AdaptSchnorr aliceAdapt = new AdaptSchnorr("Alice", keyGen);
        AdaptSchnorr bobAdapt = new AdaptSchnorr("Bob", keyGen);
        AdaptSchnorr alice = new AdaptSchnorr("Alice", keyGen);
        BigInteger[] preSignature = alice.preSign(Y1);
        BigInteger c = preSignature[0];
        BigInteger s = preSignature[1];
        System.out.println("TX_R的预签名已生成：" + c + s);

//        AdaptSchnorr bob = new AdaptSchnorr("Alice", keyGen);
//        BigInteger[] preSignatureBob = bob.preSign(Y1);
//        BigInteger cBob = preSignatureBob[0];
//        BigInteger sBob = preSignatureBob[1];
//        System.out.println("TX_R的预签名已生成：" + cBob + sBob);

        // Commit
        // 首先验证 签名正确性
        boolean isSignatureValid = alice.finalizeSign(Y1, c, s);
        System.out.println("Alice 的签名验证结果: " + isSignatureValid);
        // 生成时间锁
        //时间锁加密谜题生成和解决调用接口
        System.out.println("\nTime-Lock Puzzle:");
        TimeLockPuzzles puzzleGen = new TimeLockPuzzles();
        BigInteger[] puzzle = puzzleGen.generatePuzzle(1000);  // 生成谜题

        System.out.println("Generated Puzzle:");
        System.out.println("n: " + puzzle[0]);
        System.out.println("a: " + puzzle[1]);
        System.out.println("t: " + puzzle[2]);
        System.out.println("ck: " + puzzle[3]);

        BigInteger solution = puzzleGen.solvePuzzle(puzzle[0], puzzle[1], puzzle[2], puzzle[3]);  // 解决谜题
        System.out.println("Solved Puzzle Key: " + solution);
        System.out.println("######生成一个时间锁谜题######");

        BigInteger[] PrimesForComiit = Initialization.generateLargePrimes(200);  // 设置位数，比如 512 位
        BigInteger pp = PrimesForComiit[0];
        BigInteger qq = PrimesForComiit[1];

        BigInteger N = pp.multiply(qq); // N
        BigInteger Vkab = bobSharedKey; // pk

        BigInteger gg;
        do {
            gg = new BigInteger(N.bitLength(), random).mod(N);
        } while (gg.equals(BigInteger.ZERO)); // 确保 gg 不为 0

        // 计算 g^2
        BigInteger gSquared = gg.multiply(gg).mod(N);

        // 计算 g1 = -g^2 mod N
        BigInteger g1 = gSquared.negate().mod(N);
        //计算 g2
        BigInteger g2 = Initialization.generate_large_prime(200);
        BigInteger k1 = Initialization.generate_large_prime(10);  // 设置位数，比如 512 位
        BigInteger k = BigInteger.ONE.shiftLeft(k1.intValue()); // 2^k1
        // System.out.println("k: " + k);

        // BigInteger phiN = (pp.subtract(BigInteger.ONE)).multiply(qq.subtract(BigInteger.ONE));
        BigInteger Zq = new BigInteger(1000, random);  // 生成1000位的素数p
        while (!Zq.isProbablePrime(100)) {
            Zq = new BigInteger(1000, random);
        }
        // 小y 前面已经 生成
        BigInteger y = Initialization.generate_large_prime(10);
        System.out.println("------" + y);
        //BigInteger Y = g2.pow(y.intValue());
        BigInteger Y = Initialization.bigIntegerPow(g2, y);
        Commit commit = new Commit(N, Zq, g1, g2, k, y, Y, keyGen);

        // 假设这些值也已被初始化
        // 调用 commitAlgorithm 方法
        Object[] result = commit.commitAlgorithm(Vkab, c, s, Y, y);
        // 处理返回值
        BigInteger[] Z = (BigInteger[]) result[0];
        BigInteger powValue = (BigInteger) result[1];
        BigInteger[] proof = (BigInteger[]) result[2];
        // 输出结果
        System.out.println("Z: " + Z[0] + ", " + Z[1]);
        System.out.println("2^k: " + k);
        System.out.println("Proof: \n" + proof[0] + "\n, " + proof[1] + ",\n " + proof[2]);
        BigInteger h = g1.modPow(powValue, N);
        // 验证 verification
        System.out.println("签名验证结果: " + isSignatureValid);
        StringBuilder sb = new StringBuilder();
        for (BigInteger z : Z) {
            sb.append(z.toString());  // 拼接每个 BigInteger 的字符串表示
        }
        //处理Z数组
        BigInteger inputZ = new BigInteger(sb.toString());
        // 计算 e
        // Z = [u,v]
        // proof = [R1, R2, R3, z1, z2]
        BigInteger e = keyGen.H(N, g1, g2, Vkab, c, s, Y, inputZ, proof[0], proof[1], proof[2]);
        // 检查 几个等式是否相等
        BigInteger computedR1 = g1.modPow(proof[3], N).multiply(Z[0].modPow(e, N)).mod(N);

        // Calculate R2 from the equation R2 = v^(e) * h^(z1 * N) * (1 + N)^(z2)
        BigInteger computedR2 = Z[1].modPow(e, N.multiply(N)).multiply(h.modPow(proof[3].multiply(N), N.multiply(N)))
                .multiply(BigInteger.ONE.add(N).modPow(proof[4], N.multiply(N)))
                .mod(N.multiply(N));
        System.out.println("compR2" + computedR2);

        // Calculate R3 from the equation R3 = g2^(z2) * Y^(e)
        BigInteger computedR3 = g2.modPow(proof[4], N).multiply(Y.modPow(e, N)).mod(N);
        //BigInteger computedR3 = g2.pow(proof[4].intValue()).multiply(Y.pow(e.intValue()));
        //BigInteger computedR3one = Initialization.bigIntegerPow(g2,proof[4]);
        //BigInteger computedR3two = Initialization.bigIntegerPow(Y,e);
        //BigInteger computedR3 = computedR3one.multiply(computedR3two);
        System.out.println("compR3" + computedR3);
        // Verify all equations
        boolean isR1Valid = computedR1.equals(proof[0]);
        boolean isR2Valid = computedR2.equals(proof[1]);
        boolean isR3Valid = computedR3.equals(proof[2]);
        System.out.println("结果: " + isR1Valid);
        System.out.println("结果: " + isR2Valid);
        System.out.println("结果: " + isR3Valid);
        boolean valueVrif = isR1Valid && isR2Valid && isR3Valid;
        System.out.println("Commit结果: " + valueVrif);

        BigInteger w = Z[0].modPow(powValue, N);
        System.out.println("w:" + w);
        BigInteger wN = w.modPow(N, N.multiply(N)).modInverse(N.multiply(N)); // 计算 w^N % N^2
        System.out.println("wN:" + wN);
        System.out.println("v:" + Z[1]);
        BigInteger yOpen = ((Z[1].multiply(wN).mod(N.multiply(N))).subtract(BigInteger.ONE)).divide(N);
        //  BigInteger yOpen = Z[1].divide(wN);
        System.out.println(Z[1].subtract(wN));
        System.out.println("通道超时 调用Fopen后输出的时间锁谜题计算结果：y = " + yOpen);
    }
}

