package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${file.path:H:\\idea\\temp\\file}")
    private String basePath;

    /*文件上传*/
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info(file.toString());
        String fileName = "";
        try {
            File filePath = new File(basePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            fileName = UUID.randomUUID() + suffix;
            file.transferTo(new File(basePath + File.separator + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            String fileName = basePath + File.separator + name;
            File file = new File(fileName);
            if (!file.exists()){
                log.error("文件不存在,文件路径是{}", fileName);
                return;
            }
            fileInputStream = new FileInputStream(fileName);
            response.setContentType("image/jpeg");
            outputStream = response.getOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert outputStream != null;
                outputStream.close();
            } catch (IOException e) {
                log.error("输出流关闭失败");
            }
            try {
                fileInputStream.close();
            } catch (IOException e) {
                log.error("输出流关闭失败");
            }
        }

    }
}
