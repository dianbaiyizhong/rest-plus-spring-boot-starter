package com.nntk.sb.controller;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class ApiController {


    @RequestMapping(value = "/api/list/{id}", method = RequestMethod.GET)
    public Object list(@PathVariable("id") Integer id) {
        log.info("====id:{}", id);
        return JSON.parseObject(ResourceUtil.readStr("test.json", Charset.defaultCharset()));
    }


    @PostMapping("/api/login")
    public Object login(@RequestBody Map<String, String> param) {
        log.info("=====login:{}", param);
        Map<String, Object> ret = new HashMap<>();
        ret.put("code", 0);
        ret.put("message", "success");
        return ret;
    }

    @PostMapping("/api/register")
    public Object register(@RequestParam("name") String name) {
        log.info("=====register:{}", name);

        Map<String, Object> ret = new HashMap<>();
        ret.put("code", 0);
        ret.put("message", "success");
        return ret;

    }

    @PostMapping("/api/upload")
    public Object upload(@RequestParam("file")
                         MultipartFile blobFile, @RequestParam("type") Integer type) {
        log.info("=====upload:{},{}", blobFile, type);

        Map<String, Object> ret = new HashMap<>();
        ret.put("code", 0);
        ret.put("message", "success");
        return ret;

    }


    @GetMapping("/api/download")
    public ResponseEntity<ByteArrayResource> download() throws Exception {
        byte[] bytes = Files.readAllBytes(new File("C:\\Users\\hhm\\Downloads\\Capture001.png").toPath());
        ByteArrayResource bar = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-disposition", "attachment; filename=test.png")
                .body(bar);
    }


}
