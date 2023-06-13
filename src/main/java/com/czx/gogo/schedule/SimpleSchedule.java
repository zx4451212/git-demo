package com.czx.gogo.schedule;

import com.alibaba.fastjson.JSONObject;
import com.czx.gogo.util.UU;
import org.apache.http.conn.HttpHostConnectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class SimpleSchedule {
    private final String[] email;
    private final String file;
    private int error=0;
    private int sleepNum=10;
    @Autowired
    private UU uu;
    public SimpleSchedule(){
        String str = getStr(new File("config.json"));
        Map<String,Object> parse = (Map<String,Object>)JSONObject.parse(str);
        file=(String) parse.get("file_name");
        List<String> list=(List<String>) parse.get("email");
        email = list.toArray(new String[]{});
        if (StringUtils.isEmpty(file)){
            throw new RuntimeException("无xlsx文件");
        }

    }

//    @Async("taskExecutor")
    @Scheduled(fixedDelay = 1*500)   //定时器定义，设置执行时间 0.5s
    public void process1() throws Exception {
        try {
            //        uu.update("包租记录.xlsx");
//        UU.update("关注饰品.xlsx");
//
            if (error<5){
            uu.update(file, email, 0.08,false);
//            uu.update("包租记录.xlsx", new String[]{},0.04,true);
            }
            else {
                TimeUnit.MINUTES.sleep(sleepNum);
                sleepNum++;
                error=0;
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("线程异常");
            error++;
        }
    }
    public static String getStr(File jsonFile){
        String jsonStr = "";
        try {
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
