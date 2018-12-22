package com.speed.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {
	private static final Logger logger = LoggerFactory.getLogger(WebController.class);
	
	   // inject via application.properties
	   @Value("${welcome.message:test}")
	   private String message = "Hello World";
	    
	   @RequestMapping("/")
	   public String welcome(Map<String, Object> model) {
	      model.put("message", this.message);
	      return "speedtest/example-gauges";
	   }
	   
	   @RequestMapping("/contact-us")
	    public String contactUs() {
	        return "contact-us";
	    }
	    
	/*

	    @GetMapping("/")
	    public String index() {
	        return "upload";
	    }
	*/

	    @PostMapping("/upload") // //new annotation since 4.3
	    public String singleFileUpload(@RequestBody() String fileContent,
	                                   RedirectAttributes redirectAttributes) {

	        if (fileContent.isEmpty()) {
	            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
	            return "welcome";
	        }
	        try {
	/*
	            // Get the file and save it somewhere
	            byte[] bytes = fileContent.getBytes();
	            Path path = Paths.get(UPLOADED_FOLDER + System.currentTimeMillis()+"_"+fileContent);
	            Files.write(path, bytes);*/

	            redirectAttributes.addFlashAttribute("message",
	                    "You successfully uploaded '" + fileContent + "'");

	        } catch (Exception e) {
	        	logger.error("Error while uploading the file"+e.getMessage());
	            e.printStackTrace();
	        }

	        return "welcome";
	    }

	    @GetMapping("/uploadStatus")
	    public String uploadStatus() {
	        return "uploadStatus";
	    }

}
