package com.speed.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.speed.service.UploadSpeedCheck;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by e057497 on 8/20/2018.
 */
@RestController  // Also possible to @Controller here
public class StreamingResponseBodyController {
	
	private static final Logger logger = LoggerFactory.getLogger(StreamingResponseBodyController.class);
	
    private static String DOWNLOADED_FILE = "100KB.txt";

    @RequestMapping(value = "downloadFile", method = RequestMethod.GET)
    public StreamingResponseBody getSteamingFile(HttpServletResponse response) throws IOException {
        response.setContentType("application/txt");
        response.setHeader("Content-Disposition", "attachment; filename=\"results.txt\"");
        ClassPathResource classPathResource = new ClassPathResource(DOWNLOADED_FILE);
        InputStream inputStream = classPathResource.getInputStream();
        return outputStream -> {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, nRead);
            }
            logger.info("Done with sending file !!");
        };
    }
    /*@RequestMapping(value = "downloadFile1", method = RequestMethod.GET)
    public void getSteamingFile1(HttpServletResponse response) throws IOException {
        response.setContentType("application/txt");
        response.setHeader("Content-Disposition", "attachment; filename=\"results.txt\"");
        InputStream inputStream = new FileInputStream(new File(DOWNLOADED_FILE));
        int nRead;
        while ((nRead = inputStream.read()) != -1) {
            response.getWriter().write(nRead);
        }
        System.out.println("downloadFile 11");
    }

    @RequestMapping(value = "downloadFile2", method = RequestMethod.GET)
    public InputStreamResource FileSystemResource (HttpServletResponse response) throws IOException {
        response.setContentType("application/txt");
        response.setHeader("Content-Disposition", "attachment; filename=\"results.txt\"");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(DOWNLOADED_FILE));
        System.out.println("downloadFile 22");
        return resource;
    }*/
}

