package com.czx.gogo.util;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static Double median(List<Double> total) {

        double j = 0;

//集合排序

        Collections.sort(total);

        int size = total.size();

        if(size % 2 == 1){

            j = total.get((size-1)/2);

        }else {

//加0.0是为了把int转成double类型，否则除以2会算错

            j = (total.get(size/2-1) + total.get(size/2) + 0.0)/2;

        }

        return j;

    }

    public static String decodeUnicode(final String dataStr) {

        int start = 0;

        int end = 0;

        final StringBuffer buffer = new StringBuffer();

        while (start > -1) {

            end = dataStr.indexOf("\\u", start + 2);

            String charStr = "";

            if (end == -1) {

                charStr = dataStr.substring(start + 2, dataStr.length());

            } else {

                charStr = dataStr.substring(start + 2, end);

            }

            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。

            buffer.append(new Character(letter).toString());

            start = end;

        }

        return buffer.toString();

    }

    public static String getUnicode(String s) {

        try {

            StringBuffer out = new StringBuffer("");

            byte[] bytes = s.getBytes("unicode");

            for (int i = 0; i < bytes.length - 1; i += 2) {

                out.append("\\u");

                String str = Integer.toHexString(bytes[i + 1] & 0xff);

                for (int j = str.length(); j < 2; j++) {

                    out.append("0");

                }

                String str1 = Integer.toHexString(bytes[i] & 0xff);

                out.append(str1);

                out.append(str);

            }

            return out.toString();

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

            return null;

        }
    }

    public static String getMaxAbrade(String abrasion){
        String[] strings= {"1.00","0.90","0.76","0.63","0.50","0.45","0.42","0.41","0.40","0.39","0.38"
        ,"0.27","0.24","0.21","0.18","0.15","0.11","0.10","0.09","0.08","0.07","0.04","0.03","0.02","0.01"};
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].compareTo(abrasion)>0) {
                continue;
            }
            return strings[i-1];
        }
        return abrasion;
    }
    public static String getMinAbrade(String abrasion){
        String[] strings= {"0.90","0.76","0.63","0.50","0.45","0.42","0.41","0.40","0.39","0.38"
                ,"0.27","0.24","0.21","0.18","0.15","0.11","0.10","0.09","0.08","0.07","0.04",
                "0.03","0.02","0.01","0.00"};
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].compareTo(abrasion)<=0) {
                return strings[i];
            }
        }
        return abrasion;
    }

    public static boolean isNum(String i){
        try{
            int i1 = Integer.parseInt(i);
            return i1 > 0;
        }catch (Exception e){
            return false;
        }
    }
}
