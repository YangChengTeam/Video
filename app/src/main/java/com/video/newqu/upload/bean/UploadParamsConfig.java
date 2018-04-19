package com.video.newqu.upload.bean;

import com.video.newqu.contants.Constant;

/**
 * TinyHung@Outlook.com
 * 2017/8/17.
 * OSS文件上传配置
 */

public class UploadParamsConfig {

    private  String stsServer= Constant.STS_SERVER;
    private  String callbackAddress=Constant.STS_CALLBACKADDRESS;
    private  String bucket=Constant.STS_BUCKET;
    private  String endpoint=Constant.STS_ENDPOINT;
    private  String callBackHost=Constant.STS_HOST;
    private  String callBackType=Constant.STS_CALLBACL_CONTENT_TYPE;
    private  boolean isEncryptResponse=true;


    public UploadParamsConfig(){
        super();
    }

    public UploadParamsConfig(String stsServer, String callbackAddress, String bucket, String endpoint, String callBackHost, String callBackType, boolean isEncryptResponse) {
        this.stsServer = stsServer;
        this.callbackAddress = callbackAddress;
        this.bucket = bucket;
        this.endpoint = endpoint;
        this.callBackHost = callBackHost;
        this.callBackType = callBackType;
        this.isEncryptResponse = isEncryptResponse;
    }

    public String getStsServer() {
        return stsServer;
    }

    public void setStsServer(String stsServer) {
        this.stsServer = stsServer;
    }

    public String getCallbackAddress() {
        return callbackAddress;
    }

    public void setCallbackAddress(String callbackAddress) {
        this.callbackAddress = callbackAddress;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getCallBackHost() {
        return callBackHost;
    }

    public void setCallBackHost(String callBackHost) {
        this.callBackHost = callBackHost;
    }

    public String getCallBackType() {
        return callBackType;
    }

    public void setCallBackType(String callBackType) {
        this.callBackType = callBackType;
    }

    public boolean isEncryptResponse() {
        return isEncryptResponse;
    }

    public void setEncryptResponse(boolean encryptResponse) {
        isEncryptResponse = encryptResponse;
    }

    @Override
    public String toString() {
        return "UploadParamsConfig{" +
                "stsServer='" + stsServer + '\'' +
                ", callbackAddress='" + callbackAddress + '\'' +
                ", bucket='" + bucket + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", callBackHost='" + callBackHost + '\'' +
                ", callBackType='" + callBackType + '\'' +
                ", isEncryptResponse=" + isEncryptResponse +
                '}';
    }
}
