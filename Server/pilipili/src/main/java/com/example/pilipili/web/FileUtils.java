package com.example.pilipili.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class FileUtils {
    private FileUtils(){

    }
    public static void uploadFile(byte[] file, String filePath, String fileName) {
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            Object o = targetFile.mkdirs();
        }
        FileOutputStream out = null;
        try{
            out = new FileOutputStream(filePath+fileName);
            out.write(file);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
