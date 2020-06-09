package com.sankuai.inf.leaf.service;

import com.google.common.base.Preconditions;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.exception.InitException;
import com.sankuai.inf.leaf.snowflake.SnowflakeIDGenImpl;
import com.sankuai.inf.leaf.snowflake.SnowflakeIDWittIDCGenImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnowflakeIDCService {
    private Logger logger = LoggerFactory.getLogger(SnowflakeIDCService.class);

    private IDGen idGen;

    public SnowflakeIDCService(String zkpath, int port, long idcId) throws InitException {
        Preconditions.checkNotNull(zkpath, "zookeeper path can not be null");
        Preconditions.checkNotNull(port, "zookeeper port  can not be null");
        idGen = new SnowflakeIDWittIDCGenImpl(zkpath, port, idcId);
        if (idGen.init()) {
            logger.info("Snowflake IDC Service Init Successfully");
        } else {
            throw new InitException("Snowflake IDC  Service Init Fail");
        }
    }

    public Result getId(String key) {
        return idGen.get(key);
    }
}
