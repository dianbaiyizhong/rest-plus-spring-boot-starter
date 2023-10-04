package com.nntk.restplus.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class IoUtil {


    /**
     * byte数组转文件
     *
     * @param bytes
     * @return
     * @throws IOException
     */
    public static File byteToFile(byte[] bytes, File file) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             BufferedOutputStream bout = new BufferedOutputStream(fileOutputStream)) {
            bout.write(bytes);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
