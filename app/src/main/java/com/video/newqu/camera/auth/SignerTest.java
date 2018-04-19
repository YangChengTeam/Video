package com.video.newqu.camera.auth;

import android.text.TextUtils;

//import com.ksyun.ks3.util.StringUtils;
import com.video.newqu.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignerTest {

	/**
	 * 获取鉴权header所需要的值 Authorization、x-amz-date，请根据实际需求调整示例中参数值。
	 * 请求示例：
	 * GET https://ksvs.cn-beijing-6.api.ksyun.com?Action=KSDKAuth&Version=2017-04-01&Pkg=com.ksyun.java.demo
	 * Header Authorization AWS4-HMAC-SHA256 Credential=AKLT2A9GV11bRQyfdGGJlYV3ug/20170712/cn-beijing-6/ksvs/aws4_request, SignedHeaders=host;x-amz-date, Signature=22253b5b34e53568bcc9118a509cff1e81b09e86677f538b6539037eda24cb47
	 * Header x-amz-date 20170712T081314Z
	 * 
	 * 响应示例：
	 * {"Data":{"RetCode":0,"RetMsg":"success"},"RequestId":"88fc707e-0441-43c0-8467-15adb40c7dc1"}
	 */

	public Map<String, Map<String, Object>> generateAuthHeader() {
		
		String myPackageName ="com.video.newqu";
		
		//Please replace the values
		String kscAccessKey = "AKLTKdwjZMoTSHGU8AXV5cUizw"; //金山云控制台->身份与管理->身份与访问控制->身份与访问控制->主账户信息->账户秘钥
		String kscSecretKey = "OBEkPPvruWUzzfd//uEyKsxuWt4NIbk316/Ept2GYb9TAyRXPE3xb3w1612lffGupA==";
		
		String kscAPIHost = "ksvs.cn-beijing-6.api.ksyun.com";
		String kscAPIRegion = "cn-beijing-6";
		String kscAPIService = "ksvs";
		String kscAPIAction = "KSDKAuth";
		String kscAPIVersion = "2017-04-01";

		Map<String, String> paramKV = new HashMap<String, String>();
		paramKV.put("Action", kscAPIAction);
		paramKV.put("Version", kscAPIVersion);
		paramKV.put("Pkg", myPackageName);
		String query = encodeParams(paramKV);

		Signer.AWSParamBuilder builder = new Signer.AWSParamBuilder();
	    builder.setHost(kscAPIHost);
	    builder.setRegion(kscAPIRegion);
	    builder.setService(kscAPIService);
	    builder.setQuery(query);
	    String AMZDate = Signer.getAMZDate();
	    String NTD = Signer.getNoTimeDate();
	    builder.setAMZDate(AMZDate);
	    builder.setNTD(NTD);
		Signer.AWSParams awsParams = builder.build();
	    
	    Map<String, String> signResult = Signer.signRequest(awsParams, kscSecretKey, kscAccessKey);
	    
	    Map<String, Map<String, Object>> responseObject = new HashMap<String, Map<String, Object>>();
	    Map<String, Object> responseData = new HashMap<String, Object>();
	    responseData.put("RetMsg", "success");
	    responseData.put("RetCode", 0);
	    signResult.remove("host");
	    responseData.putAll(signResult);
	    responseObject.put("Data", responseData);
		return responseObject;
	}

	private String encodeParams(Map<String, String> params) {
		List<String> keyValuePairString = new ArrayList<String>();

		List<String> paramKeys = new ArrayList<String>(params.keySet());
		Collections.sort(paramKeys, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		for (String paramKey : paramKeys) {
			if (!StringUtils.isBlank(params.get(paramKey))) {
				keyValuePairString.add(paramKey + "=" + params.get(paramKey));
			} else if (TextUtils.isEmpty(params.get(paramKey))) {
				keyValuePairString.add(paramKey);
			} else {
				// Ignore
			}
		}

		return StringUtils.join(keyValuePairString.toArray(), "&");
	}
}
