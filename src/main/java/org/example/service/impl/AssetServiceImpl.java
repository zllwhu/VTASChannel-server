package org.example.service.impl;

import org.example.contract.Asset;
import org.example.service.AssetService;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

@Service
public class AssetServiceImpl implements AssetService {
    static Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);

    private BcosSDK bcosSDK;
    private Client client;
    private CryptoKeyPair cryptoKeyPair;

    @Override
    public void initialize() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        bcosSDK = context.getBean(BcosSDK.class);
        client = bcosSDK.getClient(1);
        cryptoKeyPair = client.getCryptoSuite().createKeyPair();
        client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
        logger.debug(" create client for group1, account address is " + cryptoKeyPair.getAddress());
    }

    @Override
    public String loadAssetContractAddr() throws Exception {
        Properties prop = new Properties();
        final Resource contractResource = new ClassPathResource("contract_asset.properties");
        prop.load(contractResource.getInputStream());
        String contractAddress = prop.getProperty("address");
        if (contractAddress == null || contractAddress.trim().equals("")) {
            throw new Exception(" load Office contract address failed, please deploy it first. ");
        }
        logger.info(" load Office address from contract.properties, address is {}", contractAddress);
        return contractAddress;
    }

    @Override
    public BigInteger queryAssetValue(String account) {
        try {
            String contractAddress = loadAssetContractAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            Tuple2<BigInteger, BigInteger> result = asset.select(account);
            if (result.getValue1().compareTo(new BigInteger("0")) == 0) {
                System.out.printf(" asset account %s, value %s \n", account, result.getValue2());
                return result.getValue2();
            } else {
                System.out.printf(" %s asset account is not exist \n", account);
            }
        } catch (Exception e) {
            logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());
            System.out.printf(" query asset account failed, error message is %s\n", e.getMessage());
        }
        return null;
    }

    @Override
    public void registerAssetAccount(String account, BigInteger asset_value) {
        try {
            String contractAddress = loadAssetContractAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = asset.register(account, asset_value);
            List<Asset.RegisterEventEventResponse> response = asset.getRegisterEventEvents(receipt);
            if (!response.isEmpty()) {
                if (response.get(0).ret.compareTo(new BigInteger("0")) == 0) {
                    System.out.printf(
                            " register asset account success => asset: %s, value: %s \n", account, asset_value);
                } else {
                    System.out.printf(
                            " register asset account failed, ret code is %s \n", response.get(0).ret.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
        }
    }

    @Override
    public void transferAsset(String asset_from, String asset_to, BigInteger amount) {
        try {
            String contractAddress = loadAssetContractAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = asset.transfer(asset_from, asset_to, amount);
            List<Asset.TransferEventEventResponse> response = asset.getTransferEventEvents(receipt);
            if (!response.isEmpty()) {
                if (response.get(0).ret.compareTo(new BigInteger("0")) == 0) {
                    System.out.printf(
                            " transfer success => from_asset: %s, to_asset: %s, amount: %s \n",
                            asset_from, asset_to, amount);
                } else {
                    System.out.printf(
                            " transfer asset account failed, ret code is %s \n", response.get(0).ret.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
        }
    }
}
