package top.kracker1911.vproject.business.weixin.service;

public interface IWeixinCacheService {

    void associateSeqOpnByEvent(String QRSequences, String QROpenId);
    void associateOpnPersonByUploadData(String QROpenId, Integer personId);
}
