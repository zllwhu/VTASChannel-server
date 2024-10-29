package org.example.controller;

import cn.hutool.core.util.IdUtil;
import org.example.crypto.*;
import org.example.dao.impl.AccountDaoImpl;
import org.example.entity.Account;
import org.example.service.impl.AssetServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@RestController
@RequestMapping("/crypto")
public class CryptoController {
    @Autowired
    private AccountDaoImpl accountDao;

    @Autowired
    private AssetServiceImpl assetService;

    BigInteger[] primes = Initialization.generateLargePrimes(200);
    BigInteger p = primes[0];
    BigInteger q = primes[1];
    BigInteger g = Initialization.findGenerator(p, q);
    KeyGen keyGen = new KeyGen(p, q, g);
    BigInteger[] aliceKeys = keyGen.genKey();
    BigInteger[] bobKeys = keyGen.genKey();
    BigInteger aliceSharedKey;
    BigInteger bobSharedKey;
    KeyGen.SchnorrSignature aliceSignature;
    KeyGen.SchnorrSignature bobSignature;
    BigInteger g1;
    BigInteger g2;
    BigInteger y;
    BigInteger Y;
    BigInteger ya;
    BigInteger Ya;
    BigInteger yb;
    BigInteger Yb;
    AdaptSchnorr bob = new AdaptSchnorr("Bob", keyGen);
    BigInteger[] preSignature;
    BigInteger[] preSignature1;
    private static final SecureRandom random = new SecureRandom();
    BigInteger[] Z;
    BigInteger N;
    BigInteger[] proof;
    BigInteger powValue;
    String id;

    @GetMapping("a11")
    public String buttonA11() throws NoSuchAlgorithmException {
        StringBuilder stringBuilder = new StringBuilder();
        BigInteger alicePrivateKey = aliceKeys[0];
        BigInteger alicePublicKey = aliceKeys[1];
        System.out.println("Alice私钥为 x: " + alicePrivateKey);
        System.out.println("Alice公钥为 y: " + alicePublicKey);
        aliceSharedKey = keyGen.computeShareKey(bobKeys[1], alicePrivateKey);
        aliceSignature = keyGen.signMessage(aliceSharedKey, alicePrivateKey);
        System.out.println("Alice的签名: (r = " + aliceSignature.getR() + ", s = " + aliceSignature.getS() + ")");
        stringBuilder.append("Alice私钥为 x: ").append(alicePrivateKey).append("\n");
        stringBuilder.append("Alice公钥为 y: ").append(alicePublicKey).append("\n");
        stringBuilder.append("Alice的签名: (r = ").append(aliceSignature.getR()).append(", s = ").append(aliceSignature.getS()).append(")\n");
        return stringBuilder.toString();
    }

    @GetMapping("a12")
    public String buttonA12() throws NoSuchAlgorithmException {
        StringBuilder stringBuilder = new StringBuilder();
        System.out.println("######Alice 验证 Bob 的签名######");
        boolean isBobSignatureValid = keyGen.verifySignature(bobSignature, aliceSharedKey, bobKeys[1]);
        System.out.println("Bob的签名合法性检验" + isBobSignatureValid);
        stringBuilder.append("Bob的签名合法性检验").append(isBobSignatureValid).append("\n");
        return stringBuilder.toString();
    }

    @GetMapping("b11")
    public String buttonB11() throws NoSuchAlgorithmException {
        StringBuilder stringBuilder = new StringBuilder();
        BigInteger bobPrivateKey = bobKeys[0];
        BigInteger bobPublicKey = bobKeys[1];
        System.out.println("Bob私钥为 x: " + bobPrivateKey);
        System.out.println("Bob公钥为 y: " + bobPublicKey);
        bobSharedKey = keyGen.computeShareKey(aliceKeys[1], bobPrivateKey);
        bobSignature = keyGen.signMessage(bobSharedKey, bobPrivateKey);
        System.out.println("Bob的签名: (r = " + bobSignature.getR() + ", s = " + bobSignature.getS() + ")");
        stringBuilder.append("Bob私钥为 x: ").append(bobPrivateKey).append("\n");
        stringBuilder.append("Bob公钥为 y: ").append(bobPublicKey).append("\n");
        stringBuilder.append("Bob的签名: (r = ").append(bobSignature.getR()).append(", s = ").append(bobSignature.getS()).append(")\n");
        return stringBuilder.toString();
    }

    @GetMapping("b12")
    public String buttonB12() throws NoSuchAlgorithmException {
        StringBuilder stringBuilder = new StringBuilder();
        System.out.println("######Bob 验证 Alice 的签名######");
        boolean isAliceSignatureValid = keyGen.verifySignature(aliceSignature, bobSharedKey, aliceKeys[1]);
        System.out.println("Alice的签名合法性检验" + isAliceSignatureValid);
        stringBuilder.append("Alice的签名合法性检验").append(isAliceSignatureValid).append("\n");
        return stringBuilder.toString();
    }

    @GetMapping("b2")
    public String buttonB2() {
        StringBuilder stringBuilder = new StringBuilder();
        g2 = Initialization.generate_large_prime(200);
        y = Initialization.generate_large_prime(10);
        Y = Initialization.bigIntegerPow(g2, y);
        stringBuilder.append("已生成困难关系：\n");
        stringBuilder.append("y: ").append(y).append("\n");
        stringBuilder.append("Y: ").append(Y).append("\n");
        return stringBuilder.toString();
    }

    @GetMapping("a2")
    public String buttonA2() throws NoSuchAlgorithmException {
        StringBuilder stringBuilder = new StringBuilder();
        preSignature = bob.preSign(Y);
        BigInteger c = preSignature[0];
        BigInteger s = preSignature[1];
        System.out.println("TX_R的预签名已生成：" + c + s);
        stringBuilder.append("TX_R的预签名已生成：\n");
        stringBuilder.append("c: ").append(c).append("\n");
        stringBuilder.append("s: ").append(s).append("\n");
        return stringBuilder.toString();
    }

    @GetMapping("b3")
    public String buttonB3() throws NoSuchAlgorithmException {
        StringBuilder stringBuilder = new StringBuilder();
        BigInteger c = preSignature[0];
        BigInteger s = preSignature[1];
        System.out.println("TX_R的预签名已生成：" + c + s);
        stringBuilder.append("TX_R的预签名已生成：\n");
        stringBuilder.append("c: ").append(c).append("\n");
        stringBuilder.append("s: ").append(s).append("\n\n");

        TimeLockPuzzles puzzleGen = new TimeLockPuzzles();
        BigInteger[] puzzle = puzzleGen.generatePuzzle(1000);
        System.out.println("Generated Puzzle:");
        System.out.println("n: " + puzzle[0]);
        System.out.println("a: " + puzzle[1]);
        System.out.println("t: " + puzzle[2]);
        System.out.println("ck: " + puzzle[3]);
        BigInteger solution = puzzleGen.solvePuzzle(puzzle[0], puzzle[1], puzzle[2], puzzle[3]);
        System.out.println("Solved Puzzle Key: " + solution);
        stringBuilder.append("生成时间锁谜题：\n");
        stringBuilder.append("n: ").append(puzzle[0]).append("\n");
        stringBuilder.append("a: ").append(puzzle[1]).append("\n");
        stringBuilder.append("t: ").append(puzzle[2]).append("\n");
        stringBuilder.append("ck: ").append(puzzle[3]).append("\n\n");

        BigInteger[] PrimesForComiit = Initialization.generateLargePrimes(200);
        BigInteger pp = PrimesForComiit[0];
        BigInteger qq = PrimesForComiit[1];
        N = pp.multiply(qq); // N
        BigInteger Vkab = bobSharedKey; // pk
        BigInteger gg;
        do {
            gg = new BigInteger(N.bitLength(), random).mod(N);
        } while (gg.equals(BigInteger.ZERO)); // 确保 gg 不为 0
        // 计算 g^2
        BigInteger gSquared = gg.multiply(gg).mod(N);
        // 计算 g1 = -g^2 mod N
        g1 = gSquared.negate().mod(N);
        //计算 g2
        //BigInteger g2 = Initialization.generate_large_prime(200);
        BigInteger k1 = Initialization.generate_large_prime(10);  // 设置位数，比如 512 位
        BigInteger k = BigInteger.ONE.shiftLeft(k1.intValue()); // 2^k1
        BigInteger Zq = new BigInteger(1000, random);  // 生成1000位的素数p
        while (!Zq.isProbablePrime(100)) {
            Zq = new BigInteger(1000, random);
        }
        Commit commit = new Commit(N, Zq, g1, g2, k, y, Y, keyGen);
        Object[] result = commit.commitAlgorithm(Vkab, c, s, Y, y);
        Z = (BigInteger[]) result[0];
        powValue = (BigInteger) result[1];
        proof = (BigInteger[]) result[2];
        System.out.println("Z: " + Z[0] + ", " + Z[1]);
        System.out.println("2^k: " + k);
        System.out.println("Proof: \n" + proof[0] + "\n, " + proof[1] + ",\n " + proof[2]);
        stringBuilder.append("生成承诺与证明：\n");
        stringBuilder.append("Z: ").append(Z[0]).append(", ").append(Z[1]).append("\n");
        stringBuilder.append("2^k: ").append(k).append("\n");
        stringBuilder.append("Proof: \n");
        stringBuilder.append(proof[0]).append("\n");
        stringBuilder.append(proof[1]).append("\n");
        stringBuilder.append(proof[2]).append("\n");
        return stringBuilder.toString();
    }

    @GetMapping("a3")
    public String buttonA3() throws NoSuchAlgorithmException {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isSignatureValid = bob.finalizeSign(Y, preSignature[0], preSignature[1]);
        System.out.println("Bob 的签名验证结果: " + isSignatureValid);
        stringBuilder.append("Bob预签名验证结果：").append(isSignatureValid).append("\n");
        StringBuilder sb = new StringBuilder();
        for (BigInteger z : Z) {
            sb.append(z.toString());  // 拼接每个 BigInteger 的字符串表示
        }
        //处理Z数组
        BigInteger inputZ = new BigInteger(sb.toString());
        // 计算 e
        // Z = [u,v]
        // proof = [R1, R2, R3, z1, z2]
        BigInteger c = preSignature[0];
        BigInteger s = preSignature[1];
        BigInteger e = keyGen.H(N, g1, g2, bobSharedKey, c, s, Y, inputZ, proof[0], proof[1], proof[2]);
        // 检查 几个等式是否相等
        BigInteger computedR1 = g1.modPow(proof[3], N).multiply(Z[0].modPow(e, N)).mod(N);

        // Calculate R2 from the equation R2 = v^(e) * h^(z1 * N) * (1 + N)^(z2)
        BigInteger h = g1.modPow(powValue, N);
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

        stringBuilder.append("校验Bob的承诺与证明：\n");
        stringBuilder.append("compute R1: ").append(computedR1).append("\n");
        stringBuilder.append("compute R2: ").append(computedR2).append("\n");
        stringBuilder.append("compute R3: ").append(computedR3).append("\n");
        stringBuilder.append("校验结果：").append(valueVrif).append("\n\n");
        stringBuilder.append("通道初始状态已生成。\n");
        return stringBuilder.toString();
    }

    @GetMapping("b4")
    public String buttonB4() {
        return "通道初始状态已生成。\n";
    }

    @GetMapping("a4")
    public String buttonA4() {
        StringBuilder stringBuilder = new StringBuilder();
        id = IdUtil.simpleUUID();
        if (accountDao.addAccount(new Account(id))) {
            assetService.initialize();
            assetService.registerAssetAccount(id, BigInteger.ZERO);
            stringBuilder.append("通道已开启，通道地址为：").append(id).append("\n");
            assetService.transferAsset("f5f2f567ec4f4a36824454cfdeeaefde", id, BigInteger.valueOf(30));
        }
        return stringBuilder.toString();
    }

    @GetMapping("a5")
    public String buttonA5() {
        StringBuilder stringBuilder = new StringBuilder();
        ya = Initialization.generate_large_prime(10);
        Ya = Initialization.bigIntegerPow(g2, ya);
        stringBuilder.append("生成用于本次更新状态的困难关系：\n");
        stringBuilder.append("ya: ").append(ya).append("\n");
        stringBuilder.append("Ya: ").append(Ya).append("\n");
        stringBuilder.append("通道状态已更新。\n");
        return stringBuilder.toString();
    }

    @GetMapping("b5")
    public String buttonB5() {
        StringBuilder stringBuilder = new StringBuilder();
        yb = Initialization.generate_large_prime(10);
        Yb = Initialization.bigIntegerPow(g2, yb);
        stringBuilder.append("生成用于本次更新状态的困难关系：\n");
        stringBuilder.append("yb: ").append(yb).append("\n");
        stringBuilder.append("Yb: ").append(Yb).append("\n");
        stringBuilder.append("通道状态已更新。\n");
        return stringBuilder.toString();
    }

    @GetMapping("a6")
    public String buttonA6() throws NoSuchAlgorithmException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("已收到Alice的困难关系解：\n");
        stringBuilder.append("yb: ").append(yb).append("\n");
        stringBuilder.append("Yb: ").append(Yb).append("\n");
        preSignature1 = bob.preSign(Ya);
        BigInteger c = preSignature1[0];
        BigInteger s = preSignature1[1];
        stringBuilder.append("最新状态的预签名如下：\n");
        stringBuilder.append("c: ").append(c).append("\n");
        stringBuilder.append("s: ").append(s).append("\n");
        boolean isSignatureValid = bob.finalizeSign(Ya, c, s);
        stringBuilder.append("两方适配签名并验证结果：").append(isSignatureValid).append("\n");
        stringBuilder.append("通道已关闭，根据最新状态分配双方资金。\n");
        accountDao.deleteAccount(id);
        assetService.initialize();
        assetService.transferAsset(id, "f5f2f567ec4f4a36824454cfdeeaefde", BigInteger.valueOf(10));
        assetService.transferAsset(id, "202f6a51ed1a4318b4b7d5c0965711b1", BigInteger.valueOf(20));
        return stringBuilder.toString();
    }

    @GetMapping("b6")
    public String buttonB6() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("已收到Bob的困难关系解：\n");
        stringBuilder.append("ya: ").append(ya).append("\n");
        stringBuilder.append("Ya: ").append(Ya).append("\n");
        BigInteger c = preSignature1[0];
        BigInteger s = preSignature1[1];
        stringBuilder.append("最新状态的预签名如下：\n");
        stringBuilder.append("c: ").append(c).append("\n");
        stringBuilder.append("s: ").append(s).append("\n");
        boolean isSignatureValid = bob.finalizeSign(Ya, c, s);
        stringBuilder.append("两方适配签名并验证结果：").append(isSignatureValid).append("\n");
        return stringBuilder.toString();
    }
}
