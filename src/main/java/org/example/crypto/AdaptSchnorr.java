package org.example.crypto;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public class AdaptSchnorr {
    private final String name;
    private final KeyGen gen;
    private final KeyGen.SecKey seckey;
    private final KeyGen.PubKey pubkey;
    private final BigInteger __r;

    // Constructor
    public AdaptSchnorr(String name, KeyGen gen) {
        this.name = name;
        this.gen = gen;
        BigInteger[] keys = gen.genKey();
        this.seckey = gen.new SecKey(keys[0]);
        this.pubkey = gen.new PubKey(keys[1]);
        this.__r = gen.getRandom();
    }

    // Get public key
    public BigInteger getPubK() {
        return this.pubkey.getKey();
    }

    // Get R
    public BigInteger getR() {
        return this.pubkey.randy(this.__r);
    }

    // Get T
    public BigInteger getT(BigInteger t) {
        return this.pubkey.tandy(t);
    }

    // Sign function
    public BigInteger[] sign(BigInteger c) {
        return new BigInteger[]{seckey.sig(c, this.__r)};
    }

    // Adapted sign function
    public BigInteger adaptSign(BigInteger t, BigInteger c) {
        return seckey.aSig(t, c, this.__r);
    }

    // 预签名阶段
    public BigInteger[] preSign(BigInteger Y) throws NoSuchAlgorithmException {
        BigInteger c = gen.H(this.getR(), this.getPubK(), Y); // 计算哈希值 c
        BigInteger s = this.sign(c)[0]; // 计算签名
        return new BigInteger[]{c, s}; // 返回预签名的 c 和 s
    }

    public boolean finalizeSign(BigInteger Y, BigInteger c, BigInteger s) {
        BigInteger R = this.getR();
        System.out.println("这是适配器签名：" + R);
        return gen.check(R, this.getPubK(), s, c, false); // 验证签名
    }

}

