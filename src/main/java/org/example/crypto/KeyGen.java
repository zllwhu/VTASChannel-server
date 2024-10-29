package org.example.crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KeyGen {
    // 随机数生成器
    private static final SecureRandom random = new SecureRandom();

    private final BigInteger p, q, g;

    // 构造函数
    public KeyGen(BigInteger p, BigInteger q, BigInteger g) {
        this.p = p;
        this.q = q;
        this.g = g;
    }

    // 生成公私钥对
    public BigInteger[] nizkkeygen() {
        BigInteger x = new BigInteger(q.bitLength(), random).mod(q.subtract(BigInteger.ONE)).add(BigInteger.ONE);  // 私钥
        BigInteger y = g.modPow(x, p);  // 公钥
        return new BigInteger[]{x, y};  // 返回私钥和公钥
    }

    public BigInteger[] genKey() {
        BigInteger sec = getRandom();  // 随机私钥
        BigInteger pub = mod(g.modPow(sec, p));  // 公钥 g^sec mod p
        return new BigInteger[]{sec, pub};  // 返回私钥和公钥
    }

    public BigInteger getRandom() {
        return new BigInteger(q.bitLength(), random).mod(q);
    }

    // 哈希函数 H
    public BigInteger H(BigInteger... inputs) throws NoSuchAlgorithmException {
        StringBuilder data = new StringBuilder();
        for (BigInteger input : inputs) {
            data.append(input.toString());
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.toString().getBytes());
        return mod(new BigInteger(1, hash));
    }

    public BigInteger mod(BigInteger v) {
        return v.mod(p);
    }

    public BigInteger modq(BigInteger v) {
        return v.mod(q);
    }

    public boolean check(BigInteger R, BigInteger P, BigInteger s, BigInteger c, boolean debug) {
        BigInteger c1 = g.modPow(s, p);
        BigInteger c2 = mod(R.multiply(P.modPow(c, p)));
        if (debug) {
            System.out.println("=== Debug ===");
            System.out.println(" g^s : " + c1);
            System.out.println("R*P^c: " + c2);
            System.out.println("=============");
        }
        return c1.equals(c2);
    }

    public boolean checkT(BigInteger R, BigInteger P, BigInteger s, BigInteger c, BigInteger T, boolean debug) {
        BigInteger c1 = g.modPow(s, p);
        BigInteger c2 = mod(T.multiply(R).multiply(P.modPow(c, p)));
        if (debug) {
            System.out.println("=== Debug ===");
            System.out.println(" g^s  : " + c1);
            System.out.println("T*R*P^c: " + c2);
            System.out.println("=============");
        }
        return c1.equals(c2);
    }

    public boolean checkTT(BigInteger R, BigInteger s, BigInteger T, boolean debug) {
        BigInteger c1 = g.modPow(s, p);
        BigInteger c2 = mod(T.multiply(R));
        if (debug) {
            System.out.println("=== Debug ===");
            System.out.println("g^s: " + c1);
            System.out.println("T*R: " + c2);
            System.out.println("=============");
        }
        return c1.equals(c2);
    }

    // 公钥类
    public class PubKey {
        private final BigInteger key;
        private final BigInteger g;
        private final BigInteger p;

        public PubKey(BigInteger key) {
            this.key = key;
            this.g = KeyGen.this.g;
            this.p = KeyGen.this.p;
        }

        public BigInteger randy(BigInteger r) {
            return g.modPow(r, p);
        }

        public BigInteger tandy(BigInteger t) {
            return g.modPow(t, p);
        }

        public BigInteger getKey() {
            return this.key;
        }
    }

    // 私钥类
    public class SecKey {
        private final BigInteger key;
        private final BigInteger q;

        public SecKey(BigInteger key) {
            this.key = key;
            this.q = KeyGen.this.q;
        }

        public BigInteger modq(BigInteger v) {
            return v.mod(q);
        }

        public BigInteger sig(BigInteger c, BigInteger r) {
            return modq(r.add(c.multiply(key)));
        }

        public BigInteger aSig(BigInteger t, BigInteger c, BigInteger r) {
            return modq(t.add(r).add(c.multiply(key)));
        }

        // For debug
        public BigInteger getSec() {
            return this.key;
        }
    }

    // Time-lock puzzle generation
    public Puzzle generatePuzzle(int t) {
        // Generate 512-bit primes
        BigInteger p = BigInteger.probablePrime(512, random);
        BigInteger q = BigInteger.probablePrime(512, random);
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // AES key --- encoded into the puzzle solution
        BigInteger key = new BigInteger(128, random);

        // Random starting value for the puzzle, between 1 and n
        BigInteger a = new BigInteger(4096, random).mod(n);

        // Puzzle shortcut: compute (a^2)^t using fast modular exponentiation
        BigInteger e = BigInteger.valueOf(2).modPow(BigInteger.valueOf(t), phi);
        BigInteger b = a.modPow(e, n);

        // Encode the key into the solution
        BigInteger ck = key.add(b).mod(n);

        return new Puzzle(key, n, a, t, ck);
    }

    // Puzzle class to hold the puzzle and its components
    public static class Puzzle {
        private final BigInteger key;
        private final BigInteger n;
        private final BigInteger a;
        private final int t;
        private final BigInteger ck;

        public Puzzle(BigInteger key, BigInteger n, BigInteger a, int t, BigInteger ck) {
            this.key = key;
            this.n = n;
            this.a = a;
            this.t = t;
            this.ck = ck;
        }

        public BigInteger getKey() {
            return key;
        }

        public BigInteger getN() {
            return n;
        }

        public BigInteger getA() {
            return a;
        }

        public int getT() {
            return t;
        }

        public BigInteger getCk() {
            return ck;
        }

        @Override
        public String toString() {
            return "Puzzle{" +
                    "key=" + key +
                    ", n=" + n +
                    ", a=" + a +
                    ", t=" + t +
                    ", ck=" + ck +
                    '}';
        }
    }
    //计算共享密钥
    public BigInteger computeShareKey (BigInteger otherPublicKey, BigInteger privateKey){
        return otherPublicKey.modPow(privateKey, p);
    }
    // 生成 Schnorr 签名
    public SchnorrSignature signMessage(BigInteger message, BigInteger privateKey) throws NoSuchAlgorithmException {
        BigInteger k = getRandom(); // 随机数 k
        BigInteger r = g.modPow(k, p); // 计算 r = g^k mod p
        BigInteger e = H(r, message); // e = H(r || message)
        BigInteger s = k.subtract(e.multiply(privateKey)).mod(q); // s = k - e * sk mod q
        return new SchnorrSignature(r, s);
    }

    // 验证 Schnorr 签名
    public boolean verifySignature(SchnorrSignature signature, BigInteger message, BigInteger publicKey) throws NoSuchAlgorithmException {
        BigInteger r = signature.getR();
        BigInteger s = signature.getS();
        BigInteger e = H(r, message); // e = H(r || message)
        BigInteger left = g.modPow(s, p).multiply(publicKey.modPow(e, p)).mod(p); // g^s * pk^e mod p
        return r.equals(left); // 验证 r == g^s * pk^e mod p
    }

    // SchnorrSignature 内部类
    public static class SchnorrSignature {
        private final BigInteger r;
        private final BigInteger s;

        public SchnorrSignature(BigInteger r, BigInteger s) {
            this.r = r;
            this.s = s;
        }

        public BigInteger getR() {
            return r;
        }

        public BigInteger getS() {
            return s;
        }
    }
}
