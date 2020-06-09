package com.sankuai.inf.leaf.snowflake;

import com.google.common.base.Preconditions;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import com.sankuai.inf.leaf.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 生成的id  1位不使用 + 41位时间戳 + 3位idc标识 + 7位服务节点标识 + 12位序列
 */
public class SnowflakeIDWittIDCGenImpl extends SnowflakeIDGenImpl {


    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeIDWittIDCGenImpl.class);

    /**
     * 服务节点范围 [0-127]
     */
    private final long workerIdBits = 7L;
    /**
     * idc 标识范围 [0-7]
     */
    private final long idcBits = 3L;
    /**
     * 最大能够分配的workerid =127
     */
    private final long maxWorkerId = ~(-1L << workerIdBits);
    private final long workerIdShift = sequenceBits;
    private final long idcIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + idcBits;
    private final long sequenceMask = ~(-1L << sequenceBits);
    private long idcId;


    public SnowflakeIDWittIDCGenImpl(String zkAddress, int port , long idcId) {
        //Thu Nov 04 2010 09:42:54 GMT+0800 (中国标准时间)
        super(zkAddress, port, 1288834974657L);

        this.idcId = idcId;
        LOGGER.info("START SUCCESS USE ZK idcId-{}", idcId);

    }


    @Override
    public synchronized Result get(String key) {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        return new Result(-1, Status.EXCEPTION);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("wait interrupted");
                    return new Result(-2, Status.EXCEPTION);
                }
            } else {
                return new Result(-3, Status.EXCEPTION);
            }
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //seq 为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //如果是新的ms开始
            sequence = RANDOM.nextInt(100);
        }
        lastTimestamp = timestamp;
        long id = ((timestamp - twepoch) << timestampLeftShift) | (idcId << idcIdShift)| (workerId << workerIdShift) | sequence;
        return new Result(id, Status.SUCCESS);

    }


}
