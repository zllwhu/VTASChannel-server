package org.example.crypto;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // 从 Initialization 类生成 p, q 和 g
        BigInteger[] primes = Initialization.generateLargePrimes(200);  // 设置位数，比如 512 位
        BigInteger p = primes[0];
        BigInteger q = primes[1];
        BigInteger g = Initialization.findGenerator(p, q);
        System.out.println("Generated prime p: " + p);
        System.out.println("Generated prime q: " + q);

        // 创建密钥生成器实例
        KeyGen keyGen = new KeyGen(p, q, g);
        System.out.println("Generator g: " + g);
        BigInteger[] keys = keyGen.nizkkeygen();
        BigInteger x = keys[0];
        BigInteger y = keys[1];
        System.out.println("Private Key x: " + x);
        System.out.println("Public Key y: " + y);

        // 创建 Schnorr 协议实例
        NIZKProtocol protocol = new NIZKProtocol(p, q, g);

        // 交互式 Schnorr 协议
        System.out.println("\nSchnorr's Protocol:");
        if (protocol.schnorrProtocol(x, y)) {
            System.out.println("Verification successful.");
        } else {
            System.out.println("Ve d.");
        }

        // 非交互式 Schnorr (NIZK)
        System.out.println("\nNIZK Proof:");
        BigInteger[] proof = protocol.schnorrNIZKProof(x, y);
        if (protocol.verifyNIZKProof(y, proof)) {
            System.out.println("NIZK verification successful.");
        } else {
            System.out.println("NIZK verification failed.");
        }

        //适配器签名
        AdaptSchnorr alice = new AdaptSchnorr("Alice", keyGen);
        AdaptSchnorr bob = new AdaptSchnorr("Bob", keyGen);

        // 调用适配器签名的 mySignAS 方法，模拟 Alice 和 Bob 的交互
        //alice.mySignAS(bob);
        //时间锁加密谜题生成和解决调用接口
        System.out.println("\nTime-Lock Puzzle:");
        TimeLockPuzzles puzzleGen = new TimeLockPuzzles();
        BigInteger[] puzzle = puzzleGen.generatePuzzle(1000000);  // 生成谜题

        System.out.println("Generated Puzzle:");
        System.out.println("n: " + puzzle[0]);
        System.out.println("a: " + puzzle[1]);
        System.out.println("t: " + puzzle[2]);
        System.out.println("ck: " + puzzle[3]);

        BigInteger solution = puzzleGen.solvePuzzle(puzzle[0], puzzle[1], puzzle[2], puzzle[3]);  // 解决谜题
        System.out.println("Solved Puzzle Key: " + solution);

        //带普通Schnorr签名的DH密钥协商

        BigInteger[] aliceKeys = keyGen.genKey();
        BigInteger[] bobKeys = keyGen.genKey();

        BigInteger alicePrivateKey = aliceKeys[0];
        BigInteger alicePublicKey = aliceKeys[1];
        BigInteger bobPrivateKey = bobKeys[0];
        BigInteger bobPublicKey = bobKeys[1];

        // Alice 和 Bob 计算共享密钥
        BigInteger aliceSharedKey = keyGen.computeShareKey(bobPublicKey, alicePrivateKey);
        BigInteger bobSharedKey = keyGen.computeShareKey(alicePublicKey, bobPrivateKey);

        System.out.println("Alice's shared key: " + aliceSharedKey);
        System.out.println("Bob's shared key: " + bobSharedKey);

        // Alice 对她的公钥进行 Schnorr 签名并发送给 Bob
        KeyGen.SchnorrSignature aliceSignature = keyGen.signMessage(alicePublicKey, alicePrivateKey);
        System.out.println("Alice's signature: (r = " + aliceSignature.getR() + ", s = " + aliceSignature.getS() + ")");

        // Bob 验证 Alice 的签名
        boolean isAliceSignatureValid = keyGen.verifySignature(aliceSignature, alicePublicKey, alicePublicKey);
        System.out.println("Is Alice's signature valid? " + isAliceSignatureValid);

        // Bob 对他的公钥进行 Schnorr 签名并发送给 Alice
        KeyGen.SchnorrSignature bobSignature = keyGen.signMessage(bobPublicKey, bobPrivateKey);
        System.out.println("Bob's signature: (r = " + bobSignature.getR() + ", s = " + bobSignature.getS() + ")");

        // Alice 验证 Bob 的签名
        boolean isBobSignatureValid = keyGen.verifySignature(bobSignature, bobPublicKey, bobPublicKey);
        System.out.println("Is Bob's signature valid? " + isBobSignatureValid);
    }
}


