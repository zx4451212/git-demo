package com.czx.gogo.util;

import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;


public class ExcelUtils {
    private final AtomicBoolean aBoolean=new AtomicBoolean(false);
    //读取excel
    public  Workbook readExcel(String filePath) throws InterruptedException {
        Workbook wb = null;
        while (true){
            TimeUnit.MILLISECONDS.sleep(100);
            boolean b = aBoolean.compareAndSet(false, true);
            if (b)break;
        }
        try {
            if (filePath == null) {
                return null;
            }
            String extString = filePath.substring(filePath.indexOf('.'));
            InputStream is = null;
            try {
                is = new FileInputStream(filePath);
                if (".xls".equals(extString)) {
                    return new HSSFWorkbook(is);
                } else if (".xlsx".equals(extString)) {
                    return new XSSFWorkbook(is);
                } else {
                    return null;
                }
            } catch (IOException | NoClassDefFoundError e) {
                e.printStackTrace();
            }
            aBoolean.compareAndSet(true, false);
            return wb;
        }catch (Exception e){
            System.out.println("读取失败");
            e.printStackTrace();
            return null;
        }finally {
            aBoolean.compareAndSet(true, false);
        }

    }

    public void write(Workbook wb,OutputStream fileOutputStream) throws IOException, InterruptedException {
        while (true){
            TimeUnit.MILLISECONDS.sleep(100);
            boolean b = aBoolean.compareAndSet(false, true);
            if (b)break;
        }
        try {
            wb.write(fileOutputStream);
        }finally {
            aBoolean.compareAndSet(true, false);
        }


    }
}
