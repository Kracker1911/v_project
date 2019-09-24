package top.kracker1911.vproject.business.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.kracker1911.vproject.business.ov.WeixinSeqOpenIdRegistry;
import top.kracker1911.vproject.business.service.IWeixinCacheService;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeixinCacheService implements IWeixinCacheService {

    private static final Logger logger = LoggerFactory.getLogger(WeixinCacheService.class);

    private static Map<String, Date> QR_SEQUENCES_M_DATE_MAP = new ConcurrentHashMap<>();
    private static Map<String, String> QR_SEQUENCES_MAC_MAP = new ConcurrentHashMap<>();
    private static Map<String, Date> QR_SEQUENCES_R_DATE_MAP = new ConcurrentHashMap<>();
    private static Map<String, WeixinSeqOpenIdRegistry> QR_SEQUENCES_REGISTRY_MAP = new ConcurrentHashMap<>();

    private static CleanThread cleanThread;

    @Override
    public void associateSeqOpnByEvent(String QRSequences, String QROpenId) {

    }

    @Override
    public void associateOpnPersonByUploadData(String QROpenId, Integer personId) {

    }

    //启动map延时清理
    private void startCleaner() {
        if (cleanThread == null || !cleanThread.isAlive()) {
            cleanThread = new CleanThread();
            cleanThread.start();
        }
    }

    private static void cleanCacheMap() {
        long currentMS = System.currentTimeMillis();

        long rExpTimeStamp = currentMS - (2 * 60 * 60 * 1000L);//seq与reg的关联有效期2小时
        Set<String> rKeySet = QR_SEQUENCES_R_DATE_MAP.keySet();
        if (rKeySet.size() > 0) {
            for (String k : rKeySet) {
                if (QR_SEQUENCES_R_DATE_MAP.get(k).getTime() < rExpTimeStamp && QR_SEQUENCES_REGISTRY_MAP.get(k) != null) {
                    QR_SEQUENCES_R_DATE_MAP.remove(k);
                    QR_SEQUENCES_REGISTRY_MAP.remove(k);
                }
            }
        }

        long mExpTimeStamp = currentMS - (2 * 24 * 60 * 60 * 1000L);//seq与mac的关联有效期2天
        Set<String> mKeySet = QR_SEQUENCES_M_DATE_MAP.keySet();
        if (mKeySet.size() > 0) {
            for (String k : mKeySet) {
                if (QR_SEQUENCES_M_DATE_MAP.get(k).getTime() < mExpTimeStamp && QR_SEQUENCES_MAC_MAP.get(k) != null) {
                    QR_SEQUENCES_M_DATE_MAP.remove(k);
                    QR_SEQUENCES_MAC_MAP.remove(k);
                }
            }
        }
        logger.info("QR info cache cleaned up, now mac cache size: " + QR_SEQUENCES_M_DATE_MAP.size()
                + ", reg cache size: " + QR_SEQUENCES_R_DATE_MAP.size());
    }

    private class CleanThread extends Thread {
        private long emptyInterval = 5 * 60 * 60 * 1000L;
        private long normalInterval = 2 * 60 * 60 * 1000L;

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    if (QR_SEQUENCES_R_DATE_MAP.size() > 0 || QR_SEQUENCES_M_DATE_MAP.size() > 0) {
                        Thread.sleep(normalInterval);
                    } else {
                        Thread.sleep(emptyInterval);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                cleanCacheMap();
            }
        }
    }
}
