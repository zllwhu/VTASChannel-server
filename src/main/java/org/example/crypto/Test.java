package org.example.crypto;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class Test {
    public static void main(String[] args) {
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPoint G = ecSpec.getG();
        BigInteger n = ecSpec.getN();
        System.out.println(G.getAffineXCoord().toBigInteger());
    }
}
