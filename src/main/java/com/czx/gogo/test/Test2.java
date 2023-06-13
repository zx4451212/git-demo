package com.czx.gogo.test;

import com.czx.gogo.util.HttpClientUtils;
import com.czx.gogo.util.UU;
import com.czx.gogo.util.Utils;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Test2 {
    public static void main(String[] args) {

//        String unicode = Utils.getUnicode("中国人");
//        System.out.println(unicode);
//        System.out.println(Utils.decodeUnicode(unicode));
//        String s="0.09";
//        System.out.println(Utils.getMinAbrade(s));
//        System.out.println(Utils.getMaxAbrade(s));
//
//        System.out.println("51".compareTo("11"));
//        String name="1|2|3";
//        String[] split = name.split("\\|");
//        for (int i = 0; i < split.length; i++) {
//            System.out.println(split[i]);
//        }
        //        List<Integer> list=new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            list.add(i);
//        }
//
//        List<Integer> list1 = list.subList(0, 100);
//        System.out.println(list1);
//
//        List<Integer> list2 = list.subList(100, 200);
//        System.out.println(list2);
        System.out.println(new Timestamp(new Date().getTime())+"|"+new Date().getTime());
        System.out.println();
        System.out.println();
    }

}
