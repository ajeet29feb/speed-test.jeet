package com.speed.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class UploadSpeedCheck {
	private static final Logger logger = LoggerFactory.getLogger(UploadSpeedCheck.class);
	
	public float calculateUploadRate(int waitTime, FTPClient client, InputStream fis)
			throws Exception {
		int bufferSize = 512;
		client.setBufferSize(bufferSize);
		//log("BUFFER : "+client.getBufferSize());
		byte[] data = new byte[bufferSize]; // buffer
		int count = 0;
		long startedAt = System.currentTimeMillis();
		long stoppedAt;
		float rate;		
		OutputStream outputStream = client.storeUniqueFileStream();
		/*String rc = client.getReplyString();
		log("RCCCCC : "+rc);*/
		int read = 0;
		//Util.copyStream(fis, outputStream, bufferSize);
		
		while (((stoppedAt = System.currentTimeMillis()) - startedAt) < waitTime) {
			if ((read = fis.read(data)) != -1) {
				 outputStream.write(data, 0 , read);
				count++;
			} else {
				logger.info("Finished");
				return Float.NaN;
			}
		}

		/*StopWatch sw = new StopWatch();
		sw.start();
		Util.copyStream(fis, outputStream, bufferSize);
		sw.stop();*/
		outputStream.close();
		client.completePendingCommand();
		
		long diff = stoppedAt - startedAt;
		logger.info("Count: "+count + " Diff : " + diff);
		rate = 1000 * (((float) count * bufferSize * 8 / diff)) / (1024 * 1024);// rate
																				// in
																				// Mbps
		return rate;
	}

	public float calculateAverageUploadRate() throws Exception {

		int times[] = { 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900, 2000 };
		float bw = 0, curBw;
		int i = 0, len = times.length, num=0;

		FTPClient client = new FTPClient();
		InputStream fis = null;
		logger.info("starting upload speed");
		try {
			String filename = "1MB.zip";
			//logger.info(this.getClass().getClassLoader().getResource(filename));
			//File file = new File(this.getClass().getClassLoader().getResource(filename).getPath());
			/*File file = ResourceUtils.getFile("classpath:"+filename);
			Long fileSize = null;
			if (file.exists()) {
				fileSize = file.length();
			}*/
			client.connect("ftp.dlptest.com");
			//after connecting to the server set the local passive mode
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
			client.login("dlpuser@dlptest.com", "3D6XZV9MKdhM5fF");
			//
			// Create an InputStream of the file to be uploaded
			//			
			//fis = new FileInputStream(this.getClass().getClassLoader().getResource(filename).getPath());
			ClassPathResource classPathResource = new ClassPathResource(filename);
			fis = classPathResource.getInputStream();
			/*File file = ResourceUtils.getFile("classpath:"+filename);
            fis = new FileInputStream(file);*/
			while (i < len) {
				curBw = calculateUploadRate(times[i++], client, fis);
				if(!Float.isNaN(curBw)){
					if(curBw > 0){
						num++;
						bw += curBw;
						logger.info("Current rate : " + Float.toString(curBw));
					}else{
						logger.info("Skipping--");
					}
				}else{
					break;
				}
			}

			client.logout();
			//log("Done");
		} catch (IOException e) {
			logger.error("ERROR in upload speed"+e.getMessage());
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				client.disconnect();
			} catch (IOException e) {
				logger.error("ERROR in upload speed finally"+e.getMessage());
				e.printStackTrace();
			}
		}
		bw /= num;
		System.out.println("Final up: "+ bw);
		return bw;
	}

	/*
	 * private void doFtp() { FTPClient client = new FTPClient();
	 * FileInputStream fis = null;
	 * 
	 * try { client.connect("ftp.dlptest.com");
	 * client.login("dlpuser@dlptest.com", "3D6XZV9MKdhM5fF");
	 * 
	 * // // Create an InputStream of the file to be uploaded // String filename
	 * = "1MB.zip"; fis = new FileInputStream(filename);
	 * 
	 * // // Store file to server // client.storeFile(filename, fis);
	 * client.logout(); log("Done"); } catch (IOException e) {
	 * e.printStackTrace(); } finally { try { if (fis != null) { fis.close(); }
	 * client.disconnect(); } catch (IOException e) { e.printStackTrace(); } } }
	 */
}
