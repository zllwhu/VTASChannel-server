package org.example.service;

import java.math.BigInteger;

public interface AssetService {
    public void initialize();
    public String loadAssetContractAddr() throws Exception;
    public BigInteger queryAssetValue(String account);
    public void registerAssetAccount(String account, BigInteger asset_value);
    public void transferAsset(String asset_from, String asset_to, BigInteger amount);
}
