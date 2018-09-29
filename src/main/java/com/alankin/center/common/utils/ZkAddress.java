package com.alankin.center.common.utils;

import org.springframework.beans.factory.FactoryBean;

public class ZkAddress implements FactoryBean<String> {

    private String zkAddress;

    /**
     * @return the zkAddress
     */
    public String getZkAddress() {
        return zkAddress;
    }

    /**
     * @param zkAddress the zkAddress to set
     */
    public void setZkAddress(String zkAddress) {
        if (!StringUtils.isEmpty(zkAddress)) {
            String address = zkAddress.replaceAll(" ", "").replaceAll("zk://", "");
            this.zkAddress = address.replaceAll("\\?", ",").replaceAll("slave=", "");
        }
    }

    @Override
    public String getObject() throws Exception {
        return zkAddress;
    }

    @Override
    public Class<?> getObjectType() {
        return String.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
