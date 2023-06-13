package com.czx.gogo.util;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

@Data
class PurchasingInfo{
    private Integer commodityId;
    private BigDecimal commodityPrice;
    private String tradeLinks;


    /**
     * 采用私钥签名
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String pvtKey = "私钥";

        PurchasingInfo purchasingInfo = new PurchasingInfo();
        purchasingInfo.setCommodityId(422542);
        purchasingInfo.setCommodityPrice(BigDecimal.valueOf(50));
        purchasingInfo.setTradeLinks("https://steamcommunity.com/tradeoffer/new/?partner=12345678912&token=LBPW679");
        List<PurchasingInfo> list = new ArrayList<>();
        list.add(purchasingInfo);
        Map<String, Object> params = new HashMap<>();
        params.put("timestamp","2022-12-02 15:31:00");
        params.put("appKey","123456");
        params.put("idempotentId","202211290105");
        params.put("purchasingInfoList",list);

        // 第一步：检查参数是否已经排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : keys) {
            Object value = params.get(key);
            if (!StringUtils.isEmpty(value)) {
                stringBuilder.append(key).append(JacksonUtils.writeValueAsString(value));
            }
        }
        System.out.println("stringBuilder:{"+ stringBuilder+"}");
        //采用私钥签名
        String sign = RSAUtils.signByPrivateKey(stringBuilder.toString().getBytes(), pvtKey);

        System.out.println("签名sign:{}"+ sign);
    }
}
