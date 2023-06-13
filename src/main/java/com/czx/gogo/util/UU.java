package com.czx.gogo.util;

import com.alibaba.fastjson.JSONObject;
import com.czx.gogo.schedule.SimpleSchedule;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.omg.PortableServer.LifespanPolicyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UU {

    @Autowired
    private EmailUtils emailUtils;

    static ExcelUtils excelUtils = new ExcelUtils();

    public static final Map<String, Object> saveKeyWords = new HashMap<>();

    public static Workbook wb = null;

    private static String Authorization;

    private static final long sleep = 1300;

    public static String url1 = "https://api.youpin898.com/api/homepage/es/template/GetCsGoPagedList";
    public static String url2 = "https://api.youpin898.com/api/homepage/v2/es/commodity/GetCsGoPagedList";
    public static String url3 = "https://api.youpin898.com/api/trade/Order/GetTopOfferOrderList";
    public static String url4 = "https://api.youpin898.com/api/user/Auth/PwdSignIn";


    static {
        try {
            Authorization = login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String login() throws InterruptedException {
        String username;
        String password;
        String str = SimpleSchedule.getStr(new File("config.json"));
        Map<String,Object> parse1 = (Map<String,Object>)JSONObject.parse(str);
        username=(String) parse1.get("username");
        password=(String) parse1.get("password");
        if (StringUtils.isEmpty(username)||StringUtils.isEmpty(password)){
            throw new RuntimeException("无登录账号");
        }

        String json;
        Map<String, Object> parse = new HashMap<>();

        parse.put("UserName", username);//13318746138   15011776681
        parse.put("UserPwd", password);
        parse.put("SessionId", "");
        parse.put("Code", "");
        parse.put("TenDay", "1");
        json = JSONObject.toJSONString(parse);

        String postText = HttpClientUtils.post(url4, json);
        parse = (Map<String, Object>) JSONObject.parse(postText);
        Map<String, String> data = (Map<String, String>) parse.get("Data");
        TimeUnit.MILLISECONDS.sleep(sleep);
        return data.get("Token");
    }

    public static String getTemplateId(String keyWords, String quality) throws InterruptedException {
        try {
            if (saveKeyWords.containsKey(keyWords + "|" + quality)) {
                return (String) saveKeyWords.get(keyWords + "|" + quality);
            } else {
                String json;
                Map<String, Object> parse = new HashMap<>();
                parse.put("listType", "10");
                parse.put("pageIndex", "1");
                parse.put("pageSize", "20");
                parse.put("sortType", "0");
                parse.put("listSortType", "1");
                parse.put("keyWords", keyWords);

                switch (quality) {
                    case "★":
                        parse.put("quality", "unusual");
                        break;
                    case "普通":
                        parse.put("quality", "normal");
                        break;
                    case "纪念品":
                        parse.put("quality", "tournament");
                        break;
                    case "StatTrak™":
                        parse.put("quality", "strange");
                        break;
                    case "★ StatTrak™":
                        parse.put("quality", "unusual_strange");
                        break;
                    case "纯正":
                        parse.put("quality", "genuine");
                        break;
                }
                json = JSONObject.toJSONString(parse);
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("Authorization", Authorization);
                String postText = HttpClientUtils.post(url1, json, map1);
                parse = (Map<String, Object>) JSONObject.parse(postText);
                List<Map<String, Object>> data = (List<Map<String, Object>>) parse.get("Data");
                Map<String, Object> map = null;
                if (data != null) {
                    for (int i = 0; i < data.size(); i++) {
                        map = data.get(i);
                        String quality1 = map.get("Quality").toString();
                        if (quality1.equals(quality)) {
                            String commodityName = (String) map.get("CommodityName");

                            if (commodityName.contains("多普勒")) {
                                if (!(keyWords.contains("伽玛") == commodityName.contains("伽玛"))) {
                                    continue;
                                }
                            }

                            if (keyWords.contains("折刀")) {
                                if (keyWords.contains("折刀") && commodityName.contains("折刀")) {
                                    break;
                                }
                            } else if (commodityName.contains("刺刀")) {
                                if (keyWords.contains("M9") && commodityName.contains("M9")) {
                                    break;
                                }
                                if (!keyWords.contains("M9") && !commodityName.contains("M9")) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }else {
                    TimeUnit.MILLISECONDS.sleep(sleep);
                    throw new RuntimeException("获取"+keyWords+"的id失败");
                }
                String id = map.get("Id").toString();
                saveKeyWords.put(keyWords + "|" + quality, id);
                TimeUnit.MILLISECONDS.sleep(sleep);
                return id;
            }
        } catch (Exception e) {
            e.printStackTrace();
            TimeUnit.SECONDS.sleep(2);
            throw new RuntimeException("获取id失败");
        }
    }

    public static String getTemplateId(String keyWords, String quality, String exterior) throws InterruptedException {
        try {
            if (saveKeyWords.containsKey(keyWords + "|" + quality + "|" + exterior)) {
                return (String) saveKeyWords.get(keyWords + "|" + quality + "|" + exterior);
            } else {
                Map<String, Object> parse = new HashMap<>();
                parse.put("listType", "10");
                parse.put("pageIndex", "1");
                parse.put("pageSize", "20");
                parse.put("sortType", "0");
                parse.put("listSortType", "1");
                parse.put("keyWords", keyWords);

                switch (quality) {
                    case "★":
                        parse.put("quality", "unusual");
                        break;
                    case "普通":
                        parse.put("quality", "normal");
                        break;
                    case "纪念品":
                        parse.put("quality", "tournament");
                        break;
                    case "StatTrak™":
                        parse.put("quality", "strange");
                        break;
                    case "★ StatTrak™":
                        parse.put("quality", "unusual_strange");
                        break;
                    case "纯正":
                        parse.put("quality", "genuine");
                        break;
                }

                switch (exterior) {
                    case "无涂装":
                        parse.put("exterior", "WearCategoryNA");
                        break;
                    case "崭新出厂":
                        parse.put("exterior", "WearCategory0");
                        break;
                    case "略有磨损":
                        parse.put("exterior", "WearCategory1");
                        break;
                    case "久经沙场":
                        parse.put("exterior", "WearCategory2");
                        break;
                    case "破损不堪":
                        parse.put("exterior", "WearCategory3");
                        break;
                    case "战痕累累":
                        parse.put("exterior", "WearCategory4");
                        break;
                }
                String json = JSONObject.toJSONString(parse);
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("Authorization", Authorization);
                String postText = HttpClientUtils.post(url1, json, map1);
                parse = (Map<String, Object>) JSONObject.parse(postText);
                List<Map<String, Object>> data = (List<Map<String, Object>>) parse.get("Data");
                Map<String, Object> map = null;
                if (data != null){
                    for (int i = 0; i < data.size(); i++) {
                        map = data.get(i);
                        String quality1 = map.get("Quality").toString();
                        String exterior1 = map.get("Exterior").toString();
                        if (quality1.equals(quality) && exterior1.equals(exterior)) {
                            String commodityName = (String) map.get("CommodityName");

                            if (commodityName.contains("多普勒")) {
                                if (!(keyWords.contains("伽玛") == commodityName.contains("伽玛"))) {
                                    continue;
                                }
                            }

                            if (keyWords.contains("折刀")) {
                                if (keyWords.contains("折刀") && commodityName.contains("折刀")) {
                                    break;
                                }
                            } else if (commodityName.contains("刺刀")) {
                                if (keyWords.contains("M9") && commodityName.contains("M9")) {
                                    break;
                                }
                                if (!keyWords.contains("M9") && !commodityName.contains("M9")) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }else {
                    TimeUnit.MILLISECONDS.sleep(sleep);
                    throw new RuntimeException("获取"+keyWords+"的id失败");
                }
                String id = map.get("Id").toString();
                saveKeyWords.put(keyWords + "|" + quality + "|" + exterior, id);
                TimeUnit.MILLISECONDS.sleep(sleep);
                return id;
            }
        } catch (Exception e) {
            e.printStackTrace();
            TimeUnit.SECONDS.sleep(2);
            throw new RuntimeException("获取id失败");
        }

    }

    public static Map<String, Object> getHome(String keyWords, String quality) throws InterruptedException {
        try {
            if (saveKeyWords.containsKey(keyWords + "|" + quality)) {
                return (Map<String, Object>) saveKeyWords.get(keyWords + "|" + quality);
            } else {
                Map<String, Object> parse = new HashMap<>();
                parse.put("listType", "10");
                parse.put("pageIndex", "1");
                parse.put("pageSize", "20");
                parse.put("sortType", "0");
                parse.put("listSortType", "1");
                parse.put("keyWords", keyWords);

                switch (quality) {
                    case "★":
                        parse.put("quality", "unusual");
                        break;
                    case "普通":
                        parse.put("quality", "normal");
                        break;
                    case "纪念品":
                        parse.put("quality", "tournament");
                        break;
                    case "StatTrak™":
                        parse.put("quality", "strange");
                        break;
                    case "★ StatTrak™":
                        parse.put("quality", "unusual_strange");
                        break;
                    case "纯正":
                        parse.put("quality", "genuine");
                        break;
                }
                String json = JSONObject.toJSONString(parse);
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("Authorization", Authorization);
                String postText = HttpClientUtils.post(url1, json, map1);
                parse = (Map<String, Object>) JSONObject.parse(postText);
                List<Map<String, Object>> data = (List<Map<String, Object>>) parse.get("Data");
                Map<String, Object> map = null;
                if (data != null)
                    for (int i = 0; i < data.size(); i++) {
                        map = data.get(i);
                        String quality1 = map.get("Quality").toString();
                        if (quality1.equals(quality)) {
                            String commodityName = (String) map.get("CommodityName");

                            if (commodityName.contains("多普勒")) {
                                if (!(keyWords.contains("伽玛") == commodityName.contains("伽玛"))) {
                                    continue;
                                }
                            }

                            if (keyWords.contains("折刀")) {
                                if (keyWords.contains("折刀") && commodityName.contains("折刀")) {
                                    break;
                                }
                            } else if (commodityName.contains("刺刀")) {
                                if (keyWords.contains("M9") && commodityName.contains("M9")) {
                                    break;
                                }
                                if (!keyWords.contains("M9") && !commodityName.contains("M9")) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                saveKeyWords.put(keyWords + "|" + quality, map);
                TimeUnit.MILLISECONDS.sleep(800);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            TimeUnit.SECONDS.sleep(3);
            throw new RuntimeException();
        }

    }

    public static Map<String, Object> getHome(String keyWords, String quality, String exterior) throws InterruptedException {
        try {
            if (saveKeyWords.containsKey(keyWords + "|" + quality + "|" + exterior)) {
                return (Map<String, Object>) saveKeyWords.get(keyWords + "|" + quality + "|" + exterior);
            } else {
                Map<String, Object> parse = new HashMap<>();
                parse.put("listType", "10");
                parse.put("pageIndex", "1");
                parse.put("pageSize", "20");
                parse.put("sortType", "0");
                parse.put("listSortType", "1");
                parse.put("keyWords", keyWords);

                switch (quality) {
                    case "★":
                        parse.put("quality", "unusual");
                        break;
                    case "普通":
                        parse.put("quality", "normal");
                        break;
                    case "纪念品":
                        parse.put("quality", "tournament");
                        break;
                    case "StatTrak™":
                        parse.put("quality", "strange");
                        break;
                    case "★ StatTrak™":
                        parse.put("quality", "unusual_strange");
                        break;
                    case "纯正":
                        parse.put("quality", "genuine");
                        break;
                }

                switch (exterior) {
                    case "无涂装":
                        parse.put("exterior", "WearCategoryNA");
                        break;
                    case "崭新出厂":
                        parse.put("exterior", "WearCategory0");
                        break;
                    case "略有磨损":
                        parse.put("exterior", "WearCategory1");
                        break;
                    case "久经沙场":
                        parse.put("exterior", "WearCategory2");
                        break;
                    case "破损不堪":
                        parse.put("exterior", "WearCategory3");
                        break;
                    case "战痕累累":
                        parse.put("exterior", "WearCategory4");
                        break;
                }
                String json = JSONObject.toJSONString(parse);
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("Authorization", Authorization);
                String postText = HttpClientUtils.post(url1, json, map1);
                parse = (Map<String, Object>) JSONObject.parse(postText);
                List<Map<String, Object>> data = (List<Map<String, Object>>) parse.get("Data");
                Map<String, Object> map = null;
                if (data != null)
                    for (int i = 0; i < data.size(); i++) {
                        map = data.get(i);
                        String quality1 = map.get("Quality").toString();
                        String exterior1 = map.get("Exterior").toString();
                        if (quality1.equals(quality) && exterior1.equals(exterior)) {
                            String commodityName = (String) map.get("CommodityName");

                            if (commodityName.contains("多普勒")) {
                                if (!(keyWords.contains("伽玛") == commodityName.contains("伽玛"))) {
                                    continue;
                                }
                            }

                            if (keyWords.contains("折刀")) {
                                if (keyWords.contains("折刀") && commodityName.contains("折刀")) {
                                    break;
                                }
                            } else if (commodityName.contains("刺刀")) {
                                if (keyWords.contains("M9") && commodityName.contains("M9")) {
                                    break;
                                }
                                if (!keyWords.contains("M9") && !commodityName.contains("M9")) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                saveKeyWords.put(keyWords + "|" + quality + "|" + exterior, map);
                TimeUnit.MILLISECONDS.sleep(800);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            TimeUnit.SECONDS.sleep(3);
            throw new RuntimeException();
        }

    }

    /***
     * 根据饰品id获取当前在售的底价
     * */
    public static String getLowPrice(String id) throws InterruptedException {
        String postText = null;
        try {
            if (id == null) return null;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("templateId", id);
            jsonMap.put("pageSize", "3");
            jsonMap.put("pageIndex", "1");
            jsonMap.put("sortType", "1");
            jsonMap.put("listSortType", "1");
            jsonMap.put("listType", "10");
            HashMap<String, String> map1 = new HashMap<>();
            map1.put("Authorization", Authorization);
            postText = HttpClientUtils.post(url2, JSONObject.toJSONString(jsonMap), map1);
            Map<String, Object> parse = (Map<String, Object>) JSONObject.parse(postText);
            //获取
            String msg = (String) parse.get("Msg");
            if (!"success".equals(msg)) throw new RuntimeException();
            Map<String, Object> data = (Map<String, Object>) parse.get("Data");
            List<Map<String, Object>> commodityList = (List<Map<String, Object>>) data.get("CommodityList");
            Map<String, Object> map = commodityList.get(0);
            TimeUnit.MILLISECONDS.sleep(sleep);
            return map.get("Price").toString();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(postText);
            TimeUnit.MILLISECONDS.sleep(sleep);
            throw new RuntimeException("获取底价失败");
        }
    }

    public static String getLowPrice(String id, String minAbrade, String maxAbrade) throws InterruptedException {
        String postText = null;
        try {
            if (id == null) return null;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("templateId", id);
            jsonMap.put("pageSize", "3");
            jsonMap.put("pageIndex", "1");
            jsonMap.put("sortType", "1");
            jsonMap.put("listSortType", "1");
            jsonMap.put("listType", "10");
            jsonMap.put("maxAbrade", maxAbrade);
            jsonMap.put("minAbrade", minAbrade);
            HashMap<String, String> map1 = new HashMap<>();
            map1.put("Authorization", Authorization);
            postText = HttpClientUtils.post(url2, JSONObject.toJSONString(jsonMap), map1);
            Map<String, Object> parse = (Map<String, Object>) JSONObject.parse(postText);
            //获取
            String msg = (String) parse.get("Msg");
            if (!"success".equals(msg)) throw new RuntimeException();
            Map<String, Object> data = (Map<String, Object>) parse.get("Data");
            List<Map<String, Object>> commodityList = (List<Map<String, Object>>) data.get("CommodityList");
            Map<String, Object> map = commodityList.get(0);
            TimeUnit.MILLISECONDS.sleep(sleep);
            return map.get("Price").toString();
        } catch (Exception e) {
            return null;
        }
    }

    /***
     * 根据饰品id获取当前在售的底价
     * */
    public static String getLowPrice(String id, String key) throws InterruptedException {
        String postText = null;
        try {
            if (id == null) return null;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("templateId", id);
            jsonMap.put("pageSize", "3");
            jsonMap.put("pageIndex", "1");
            jsonMap.put("sortType", "1");
            jsonMap.put("listSortType", "1");
            jsonMap.put("listType", "10");
            jsonMap.put("keyWords", key);
            HashMap<String, String> map1 = new HashMap<>();
            map1.put("Authorization", Authorization);
            postText = HttpClientUtils.post(url2, JSONObject.toJSONString(jsonMap), map1);
            Map<String, Object> parse = (Map<String, Object>) JSONObject.parse(postText);
            //获取
            String msg = (String) parse.get("Msg");
            if (!"success".equals(msg)) throw new RuntimeException("获取底价失败");
            Map<String, Object> data = (Map<String, Object>) parse.get("Data");
            List<Map<String, Object>> commodityList = (List<Map<String, Object>>) data.get("CommodityList");
            Map<String, Object> map = commodityList.get(0);
            TimeUnit.MILLISECONDS.sleep(sleep);
            return (String) map.get("Price");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(postText);
            throw new RuntimeException("获取底价失败");
        }
    }

    public static String getLowPrice(String id, String key, String minAbrade, String maxAbrade) throws InterruptedException {
        try {
            if (id == null) return null;
            String json = "{\n" +
                    "  \"keyWords\": \"" + key +
                    "\",\n\"templateId\": \"" + id +
                    "\",\n" +
                    "  \"pageSize\": 3,\n" +
                    "  \"pageIndex\": 1,\n" +
                    "\"maxAbrade\":\"" + maxAbrade + "\",\n" +
                    "  \"minAbrade\":\"" + minAbrade + "\",\n" +
                    "  \"sortType\": 1,\n" +
                    "  \"listSortType\": 1,\n" +
                    "  \"listType\": 10\n" +
                    "}";
            HashMap<String, String> map1 = new HashMap<>();
            map1.put("Authorization", Authorization);
            String postText = HttpClientUtils.post(url2, json, map1);
            Map<String, Object> parse = (Map<String, Object>) JSONObject.parse(postText);
            //获取
            String msg = (String) parse.get("Msg");
            if (!"success".equals(msg)) throw new RuntimeException("获取底价失败");
            Map<String, Object> data = (Map<String, Object>) parse.get("Data");
            List<Map<String, Object>> commodityList = (List<Map<String, Object>>) data.get("CommodityList");
            if (commodityList == null) throw new RuntimeException();;
            Map<String, Object> map = commodityList.get(0);
            TimeUnit.MILLISECONDS.sleep(sleep);
            return (String) map.get("Price");
        } catch (Exception e) {
            throw new RuntimeException("获取底价失败");
        }
    }

    public static String GetTopOfferOrderList(String id) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("TemplateId", id));
        String s = HttpClientUtils.get(url3, params);
        Map<String, Object> parse = (Map<String, Object>) JSONObject.parse(s);
        List<Map<String, Object>> data = (List<Map<String, Object>>) parse.get("Data");
        ArrayList<Double> objects = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Double price = Double.parseDouble(data.get(i).get("Price").toString());
            objects.add(price);
        }
        Double v = Utils.median(objects);
        return v.toString();
    }

    public static void update(String filePath) throws InterruptedException, IOException {
        boolean flag = false;

        List<Map<String, Object>> list = new ArrayList<>();
        if (wb == null) wb = excelUtils.readExcel(filePath);
        Sheet sheetAt = wb.getSheetAt(0);
        int rowNum = sheetAt.getPhysicalNumberOfRows();
        Row row = sheetAt.getRow(0);
        for (int i = 3; i < rowNum; i++) {
            row = sheetAt.getRow(i);
            if (row != null) {
                String details = "";
                //第一列 饰品名称
                Cell cellAppName = row.getCell(0);
                if (cellAppName == null || cellAppName.toString().equals("")) {
                    flag = true;
                    continue;
                }
                //第二列 类型名称
                Cell cell = row.getCell(1);
                //第三列 磨损级别
                Cell cell1 = row.getCell(2);
                //第五列 买入价
                Cell cell4 = row.getCell(4);
                double buyPrice;
                try {
                    buyPrice = cell4.getNumericCellValue();
                } catch (Exception e) {
                    buyPrice = 0;
                }

                //饰品名称
                String keyWords = cellAppName.getRichStringCellValue().getString();
                details += keyWords;
                String key = null;
                //若饰品名称为空，不进行爬取
                if (keyWords != null) {
                    //若饰品为多普勒系列，获取多普勒等级
                    if (keyWords.contains("多普勒")) {
                        String[] split = keyWords.split("\\|");
                        keyWords = split[0] + "|" + split[1];
                        key = split[2];
                    }
                    //类型名称
                    String quality = cell.getRichStringCellValue().getString();
                    //磨损级别
                    String exterior = cell1.getRichStringCellValue().getString();
                    String templateId = null;
                    String lowPrice = null;
                    String abrasionPrice = null;
                    //若无磨损，即为无磨损类型饰品
                    if (StringUtils.isEmpty(exterior)) {
                        templateId = UU.getTemplateId(keyWords, quality);
                        if (key != null) {
                            lowPrice = UU.getLowPrice(templateId, key);
                        } else {
                            lowPrice = UU.getLowPrice(templateId);
                        }

                        abrasionPrice = null;
                    } else {
                        details += "|(" + exterior + ")";
                        templateId = UU.getTemplateId(keyWords, quality, exterior);
                        //第四列 磨损值
                        Cell cell2 = row.getCell(3);
                        if (key != null) {
                            lowPrice = UU.getLowPrice(templateId, key);
                        } else {
                            lowPrice = UU.getLowPrice(templateId);
                        }
                        double abrasion;
                        try {
                            abrasion = cell2.getNumericCellValue();
                        } catch (Exception e) {
                            abrasion = 0;
                        }
                        String maxAbrade = Utils.getMaxAbrade(Double.toString(abrasion));
                        String minAbrade = Utils.getMinAbrade(Double.toString(abrasion));
                        if (abrasion != 0) {
                            if (key != null) {
                                abrasionPrice = UU.getLowPrice(templateId, key, minAbrade, maxAbrade);
                            } else {
                                abrasionPrice = UU.getLowPrice(templateId, minAbrade, maxAbrade);
                            }
                        }
                    }


                    Cell cell2 = row.getCell(5);
                    Cell cell3 = row.getCell(6);
                    Double lastPrice = cell2.getNumericCellValue();
                    if (lowPrice != null) {
                        if (abrasionPrice != null) {
                            double i1 = Double.parseDouble(abrasionPrice);
                            double i2 = Double.parseDouble(lowPrice);
                            if (keyWords.contains("手套") || keyWords.contains("裹手")) {
                                lowPrice = abrasionPrice;
                            }
                            cell2.setCellValue(Double.parseDouble(lowPrice));

                        } else {
                            cell2.setCellValue(Double.parseDouble(lowPrice));
                        }
                    }
                    if (abrasionPrice != null) {
                        cell3.setCellValue(Double.parseDouble(abrasionPrice));
                    }
                    if (!flag) {
                        HashMap<String, Object> map = new HashMap<>();
                        ArrayList<Object> list1 = new ArrayList<>();
                        list1.add(Double.toString(buyPrice));
                        list1.add(lowPrice);
                        list1.add(abrasionPrice);
                        list1.add(lastPrice);
                        map.put(details, list1);
                        list.add(map);
                    }
                }
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            excelUtils.write(wb, fileOutputStream);
        } catch (IOException e) {
            System.out.println("写入excel失败");
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            wb.close();
        }
        double sum = 0.00;

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                String key = next.getKey();
                List<Object> value = (List<Object>) next.getValue();
                String buyPrice = (String) value.get(0);
                String lowPrice = (String) value.get(1);
                String abrasionPrice = (String) value.get(2);
                Double lastPrice = (Double) value.get(3);
                double i1 = 0.00;
                if (lowPrice != null && buyPrice != null) {
                    i1 = Double.parseDouble(lowPrice) - Double.parseDouble(buyPrice);
                    lastPrice = (Double.parseDouble(lowPrice)) - lastPrice;
                }

                if (!(buyPrice == null) && !buyPrice.equals("0.0"))
                    sum += i1;
                if (lastPrice != 0.00) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(key);
                    if (!(buyPrice == null) && !buyPrice.equals("0.0")) {
                        stringBuilder.append("\t 买入价：").append(buyPrice);
                    }
                    stringBuilder.append("\t 底价：").append(lowPrice);
                    if (!(abrasionPrice == null) && !abrasionPrice.equals("0.0")) {
                        stringBuilder.append("\t磨损底价：").append(abrasionPrice);
                    }
                    if (!(buyPrice == null) && !buyPrice.equals("0.0")) {
                        stringBuilder.append("\t涨价：").append(String.format("%.2f", i1));
                    }
                    stringBuilder.append("\t变化：").append(String.format("%.2f", lastPrice));
                    String content = stringBuilder.toString();
                    System.out.println(content);
                }
            }
        }
        System.out.print("总涨价:" + String.format("%.2f", sum));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        System.out.println("\t日期：" + format);

    }

    /**
     * @param filePath 表格路径
     * @param email    接受信息的邮箱
     * @param price    饰品达到发送邮箱的降价值（输入正数）
     * @param write    是否写入excel
     */
    public void update(String filePath, String[] email, double price,boolean write) throws Exception {
        boolean flag = false;
        double sum = 0.00;
        List<Map<String, Object>> list = new ArrayList<>();
        if (wb == null) wb = excelUtils.readExcel(filePath);
        Sheet sheetAt = wb.getSheetAt(0);
        int rowNum = sheetAt.getPhysicalNumberOfRows();
        Row row = sheetAt.getRow(0);
        for (int i = 3; i < rowNum; i++) {
            row = sheetAt.getRow(i);
            if (row != null) {
                String details = "";
                //第一列 饰品名称
                Cell cellAppName = row.getCell(0);
                if (cellAppName == null || cellAppName.toString().equals("")) {
                    flag = true;
                    break;
                }
                //第二列 类型名称
                Cell cell = row.getCell(1);
                //第三列 磨损级别
                Cell cell1 = row.getCell(2);
                //第五列 买入价
                Cell cell4 = row.getCell(4);
                double buyPrice;
                try {
                    buyPrice = cell4.getNumericCellValue();
                } catch (Exception e) {
                    buyPrice = 0;
                }

                //饰品名称
                String keyWords = cellAppName.getRichStringCellValue().getString();
                details += keyWords;
                String key = null;
                //若饰品名称为空，不进行爬取
                if (keyWords != null) {
                    //若饰品为多普勒系列，获取多普勒等级
                    if (keyWords.contains("多普勒")) {
                        String[] split = keyWords.split("\\|");
                        keyWords = split[0] + "|" + split[1];
                        key = split[2];
                    }
                    //类型名称
                    String quality = cell.getRichStringCellValue().getString();
                    //磨损级别
                    String exterior ;
                    try{
                        exterior = cell1.getRichStringCellValue().getString();
                    }catch (Exception e){
                        int cellValue = (int)cell1.getNumericCellValue();
                        exterior =  String.valueOf(cellValue);
                    }
                    String templateId = null;
                    String lowPrice = null;
                    String abrasionPrice = null;
                    //若无磨损，即为无磨损类型饰品
                    if (StringUtils.isEmpty(exterior)) {
                        templateId = UU.getTemplateId(keyWords, quality);
                        if (key != null) {
                            lowPrice = UU.getLowPrice(templateId, key);
                        } else {
                            lowPrice = UU.getLowPrice(templateId);
                        }

                        abrasionPrice = null;
                    } else {
                        if(Utils.isNum(exterior)){
                            templateId=exterior;
                        }else {
                            details += "|(" + exterior + ")";
                            templateId = UU.getTemplateId(keyWords, quality, exterior);
                        }
                        //第四列 磨损值
                        Cell cell2 = row.getCell(3);
                        if (key != null) {
                            lowPrice = UU.getLowPrice(templateId, key);
                        } else {
                            lowPrice = UU.getLowPrice(templateId);
                        }
                        double abrasion;
                        try {
                            abrasion = cell2.getNumericCellValue();
                            if (abrasion==0.0)abrasion=1;
                        } catch (Exception e) {
                            abrasion = 1;
                        }

                        if (abrasion < 1) {
                            String maxAbrade = Utils.getMaxAbrade(Double.toString(abrasion));
                            String minAbrade = Utils.getMinAbrade(Double.toString(abrasion));
                            if (key != null) {
                                abrasionPrice = UU.getLowPrice(templateId, key, minAbrade, maxAbrade);
                            } else {
                                abrasionPrice = UU.getLowPrice(templateId, minAbrade, maxAbrade);
                            }
                        }
                    }


                    Cell cell2 = row.getCell(5);
                    Cell cell3 = row.getCell(6);
                    double lastPrice = cell2.getNumericCellValue();
                    if (lowPrice != null) {
                        if (abrasionPrice != null) {
                            double i1 = Double.parseDouble(abrasionPrice);
                            double i2 = Double.parseDouble(lowPrice);
                            if (keyWords.contains("手套") || keyWords.contains("裹手")) {
                                lowPrice = abrasionPrice;
                            }
                            cell2.setCellValue(Double.parseDouble(lowPrice));

                        } else {
                            cell2.setCellValue(Double.parseDouble(lowPrice));
                        }
                    }
                    //第七列 写入磨损底价
//                    if (abrasionPrice != null) {
//                        cell3.setCellValue(Double.parseDouble(abrasionPrice));
//                    }
                    if (!flag) {
//                        HashMap<String, Object> map = new HashMap<>();
//                        ArrayList<Object> list1 = new ArrayList<>();
                        String buy = Double.toString(buyPrice);
//                        list1.add();
//                        list1.add(lowPrice);
//                        list1.add(abrasionPrice);
//                        list1.add(lastPrice);
//                        map.put(details, list1);
//                        list.add(map);

                        double i1 = 0.00;
                        if (lowPrice != null && buy != null) {
                            i1 = Double.parseDouble(lowPrice) - Double.parseDouble(buy);
                            lastPrice = (Double.parseDouble(lowPrice)) - lastPrice;
                        }
                        if (!(buy == null) && !buy.equals("0.0"))
                            sum += i1;
                        if (lastPrice != 0.00) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(details);
                            if (!(buy == null) && !buy.equals("0.0")) {
                                stringBuilder.append("\t 买入价：").append(buyPrice);
                            }
                            stringBuilder.append("\t 底价：").append(lowPrice);
                            if (!(abrasionPrice == null) && !abrasionPrice.equals("0.0")) {
                                stringBuilder.append("\t磨损底价：").append(abrasionPrice);
                            }
                            if (!(buy == null) && !buy.equals("0.0")) {
                                stringBuilder.append("\t涨价：").append(String.format("%.2f", i1));
                            }
                            stringBuilder.append("\t变化：").append(String.format("%.2f", lastPrice));
                            String content = stringBuilder.toString();
                            if (price != 0 && (price * Double.parseDouble(lowPrice)) + lastPrice < 0) {
                                if (email.length > 1) {
                                    String[] cc = new String[email.length - 1];

                                    System.arraycopy(email, 1, cc, 0, email.length - 1);
                                    emailUtils.sendAttachmentsMail(email[0], cc, details + "，" + lowPrice + "底价下调" + lastPrice, content);
                                } else if (email.length > 0) {
                                    emailUtils.sendAttachmentsMail(email[0], details + "，" + lowPrice + "底价下调" + lastPrice, content);
                                }
                            }
                            System.out.println(content);
                        }
                    }
                }
            }
        }
        if (write){
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(filePath);
                excelUtils.write(wb, fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("写入excel失败");
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//        double sum=0.00;

//        for (int i = 0; i < list.size(); i++) {
//            Map<String, Object> map = list.get(i);
//            Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
//            while(iterator.hasNext()){
//                Map.Entry<String, Object> next = iterator.next();
//                String key = next.getKey();
//                List<Object> value = (List<Object>)next.getValue();
//                String buyPrice = (String)value.get(0);
//                String lowPrice = (String)value.get(1);
//                String abrasionPrice = (String)value.get(2);
//                Double lastPrice = (Double)value.get(3);
//                double i1 =0.00;
//                if (lowPrice!=null&& buyPrice!=null) {
//                    i1 = Double.parseDouble(lowPrice) - Double.parseDouble(buyPrice);
//                    lastPrice=(Double.parseDouble(lowPrice))-lastPrice;
//                }
//                if (!(buyPrice ==null) && !buyPrice.equals("0.0"))
//                sum+=i1;
//                if (lastPrice!=0.00) {
//                    StringBuilder stringBuilder=new StringBuilder();
//                    stringBuilder.append(key);
//                    if (!(buyPrice ==null) && !buyPrice.equals("0.0")){
//                        stringBuilder.append("\t 买入价：").append(buyPrice);
//                    }
//                    stringBuilder.append("\t 底价：").append(lowPrice);
//                    if (!(abrasionPrice ==null) && !abrasionPrice.equals("0.0")){
//                        stringBuilder.append("\t磨损底价：").append(abrasionPrice);
//                    }
//                    if (!(buyPrice ==null) && !buyPrice.equals("0.0")){
//                        stringBuilder.append("\t涨价：").append(String.format("%.2f", i1));
//                    }
//                    stringBuilder.append("\t变化：").append(String.format("%.2f", lastPrice));
//                    String content=stringBuilder.toString();
//                    if (price!=0 && (price*Double.parseDouble(lowPrice))+lastPrice<0){
//                        if (email.length>1) {
//                            String[] cc = new String[email.length-1];
//
//                            System.arraycopy(email, 1, cc, 0, email.length - 1);
//                            emailUtils.sendAttachmentsMail(email[0], cc, "饰品：" + key + "，底价下调"+lastPrice, content);
//                        }else if (email.length>0){
//                            emailUtils.sendAttachmentsMail(email[0],"饰品：" + key + "，底价下调"+lastPrice, content);
//                        }
//                    }
//                    System.out.println(content);
//                }
//            }
//        }
        System.out.print("总涨价:" + String.format("%.2f", sum));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        System.out.println("\t日期：" + format);

    }

    /**
     * @param filePath 表格路径
     */
    public static void newUpdate(String filePath) throws Exception {
        boolean flag = false;
        double sum = 0.00;
        if (wb == null) wb = excelUtils.readExcel(filePath);
        Sheet sheetAt = wb.getSheetAt(0);
        int rowNum = sheetAt.getPhysicalNumberOfRows();
        Row row;
        for (int i = 3; i < rowNum; i++) {
            row = sheetAt.getRow(i);
            if (row != null) {
                String details = "";
                //第一列 饰品名称
                Cell cellAppName = row.getCell(0);
                if (cellAppName == null || cellAppName.toString().equals("")) {
                    flag = true;
                    continue;
                }
                //第二列 类型名称
                Cell cell = row.getCell(1);
                //第三列 磨损级别
                Cell cell1 = row.getCell(2);
                //第五列 买入价
                Cell cell4 = row.getCell(4);
                double buyPrice;
                try {
                    buyPrice = cell4.getNumericCellValue();
                } catch (Exception e) {
                    buyPrice = 0;
                }

                //饰品名称
                String keyWords = cellAppName.getRichStringCellValue().getString();
                details += keyWords;
                String key = null;
                //若饰品名称为空，不进行爬取
                if (keyWords != null) {
                    //若饰品为多普勒系列，获取多普勒等级
                    if (keyWords.contains("多普勒")) {
                        String[] split = keyWords.split("\\|");
                        keyWords = split[0] + "|" + split[1];
                        key = split[2];
                    }
                    //类型名称
                    String quality = cell.getRichStringCellValue().getString();
                    //磨损级别
                    String exterior = cell1.getRichStringCellValue().getString();
                    Map<String, Object> map = null;
                    String lowPrice = null;
                    //若无磨损，即为无磨损类型饰品
                    if (StringUtils.isEmpty(exterior)) {
                        map = UU.getHome(keyWords, quality);
                        lowPrice = (String) map.get("Price");

                    } else {
                        details += "|(" + exterior + ")";
                        //第四列 磨损值
                        Cell cell2 = row.getCell(3);
                        map = UU.getHome(keyWords, quality, exterior);
                        lowPrice = (String) map.get("Price");
                    }
                    Cell cell2 = row.getCell(5);
                    Cell cell3 = row.getCell(6);
                    double lastPrice = cell2.getNumericCellValue();
                    if (lowPrice != null) {
                        cell2.setCellValue(Double.parseDouble(lowPrice));

                    } else {
                        cell2.setCellValue(Double.parseDouble(lowPrice));
                    }
                    if (!flag) {
                        String buy = Double.toString(buyPrice);

                        double i1 = 0.00;
                        if (lowPrice != null && buy != null) {
                            i1 = Double.parseDouble(lowPrice) - Double.parseDouble(buy);
                            lastPrice = (Double.parseDouble(lowPrice)) - lastPrice;
                        }
                        if (!(buy == null) && !buy.equals("0.0"))
                            sum += i1;
                        if (lastPrice != 0.00) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(details);
                            if (!(buy == null) && !buy.equals("0.0")) {
                                stringBuilder.append("\t 买入价：").append(buyPrice);
                            }
                            stringBuilder.append("\t 底价：").append(lowPrice);
                            if (!(buy == null) && !buy.equals("0.0")) {
                                stringBuilder.append("\t涨价：").append(String.format("%.2f", i1));
                            }
                            stringBuilder.append("\t变化：").append(String.format("%.2f", lastPrice));
                            String content = stringBuilder.toString();
                            System.out.println(content);
                        }
                    }
                }
            }
        }
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(filePath);
                excelUtils.write(wb, fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("写入excel失败");
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        System.out.print("总涨价:" + String.format("%.2f", sum));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        System.out.println("\t日期：" + format);

    }

    /**
     * @param filePath 表格路径
     * @param email    接受信息的邮箱
     * @param price    饰品达到发送邮箱的降价值（输入正数）
     */
    public void newUpdate(String filePath, String[] email, double price) throws Exception {
        boolean flag = false;
        double sum = 0.00;
        if (wb == null) wb = excelUtils.readExcel(filePath);
        Sheet sheetAt = wb.getSheetAt(0);
        int rowNum = sheetAt.getPhysicalNumberOfRows();
        Row row;
        for (int i = 3; i < rowNum; i++) {
            row = sheetAt.getRow(i);
            if (row != null) {
                String details = "";
                //第一列 饰品名称
                Cell cellAppName = row.getCell(0);
                if (cellAppName == null || cellAppName.toString().equals("")) {
                    flag = true;
                    continue;
                }
                //第二列 类型名称
                Cell cell = row.getCell(1);
                //第三列 磨损级别
                Cell cell1 = row.getCell(2);
                //第五列 买入价
                Cell cell4 = row.getCell(4);
                double buyPrice;
                try {
                    buyPrice = cell4.getNumericCellValue();
                } catch (Exception e) {
                    buyPrice = 0;
                }

                //饰品名称
                String keyWords = cellAppName.getRichStringCellValue().getString();
                details += keyWords;
                String key = null;
                //若饰品名称为空，不进行爬取
                if (keyWords != null) {
                    //若饰品为多普勒系列，获取多普勒等级
                    if (keyWords.contains("多普勒")) {
                        String[] split = keyWords.split("\\|");
                        keyWords = split[0] + "|" + split[1];
                        key = split[2];
                    }
                    //类型名称
                    String quality = cell.getRichStringCellValue().getString();
                    //磨损级别
                    String exterior = cell1.getRichStringCellValue().getString();
                    Map<String, Object> map = null;
                    String lowPrice = null;
                    //若无磨损，即为无磨损类型饰品
                    if (StringUtils.isEmpty(exterior)) {
                        map = UU.getHome(keyWords, quality);
                        lowPrice = (String) map.get("Price");

                    } else {
                        details += "|(" + exterior + ")";
                        //第四列 磨损值
                        Cell cell2 = row.getCell(3);
                        map = UU.getHome(keyWords, quality, exterior);
                        lowPrice = (String) map.get("Price");
                    }
                    Cell cell2 = row.getCell(5);
                    Cell cell3 = row.getCell(6);
                    double lastPrice = cell2.getNumericCellValue();
                    if (lowPrice != null) {
                        cell2.setCellValue(Double.parseDouble(lowPrice));

                    } else {
                        cell2.setCellValue(Double.parseDouble(lowPrice));
                    }
                    if (!flag) {
                        String buy = Double.toString(buyPrice);

                        double i1 = 0.00;
                        if (lowPrice != null && buy != null) {
                            i1 = Double.parseDouble(lowPrice) - Double.parseDouble(buy);
                            lastPrice = (Double.parseDouble(lowPrice)) - lastPrice;
                        }
                        if (!(buy == null) && !buy.equals("0.0"))
                            sum += i1;
                        if (lastPrice != 0.00) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(details);
                            if (!(buy == null) && !buy.equals("0.0")) {
                                stringBuilder.append("\t 买入价：").append(buyPrice);
                            }
                            stringBuilder.append("\t 底价：").append(lowPrice);
                            if (!(buy == null) && !buy.equals("0.0")) {
                                stringBuilder.append("\t涨价：").append(String.format("%.2f", i1));
                            }
                            stringBuilder.append("\t变化：").append(String.format("%.2f", lastPrice));
                            String content = stringBuilder.toString();
                            if (price != 0 && (price * Double.parseDouble(lowPrice)) + lastPrice < 0) {
                                if (email.length > 1) {
                                    String[] cc = new String[email.length - 1];

                                    System.arraycopy(email, 1, cc, 0, email.length - 1);
                                    emailUtils.sendAttachmentsMail(email[0], cc, details + "，" + lowPrice + "底价下调" + lastPrice, content);
                                } else if (email.length > 0) {
                                    emailUtils.sendAttachmentsMail(email[0], details + "，" + lowPrice + "底价下调" + lastPrice, content);
                                }
                            }
                            System.out.println(content);
                        }
                    }
                }
            }
        }
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(filePath);
                excelUtils.write(wb, fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("写入excel失败");
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        System.out.print("总涨价:" + String.format("%.2f", sum));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        System.out.println("\t日期：" + format);

    }
}
