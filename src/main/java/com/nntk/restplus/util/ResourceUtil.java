package com.nntk.restplus.util;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class ResourceUtil {

    public static String readStr(String fileName) {
        try {
            ClassPathResource cpr = new ClassPathResource(fileName);
            InputStream in = cpr.getInputStream();
            String result = new BufferedReader(new InputStreamReader(in))
                    .lines().collect(Collectors.joining("\n"));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
