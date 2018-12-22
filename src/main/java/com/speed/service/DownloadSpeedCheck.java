package com.speed.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DownloadSpeedCheck {
	private static final Logger logger = LoggerFactory.getLogger(DownloadSpeedCheck.class);
	public float calculateDownloadRate(int waitTime, BufferedInputStream in) throws Exception {
		int bufferSize = 512;
		byte[] data = new byte[bufferSize]; // buffer

		int count = 0;
		long startedAt = System.currentTimeMillis();
		long stoppedAt;
		float rate;
		while (((stoppedAt = System.currentTimeMillis()) - startedAt) < waitTime) {
			if (in.read(data, 0, bufferSize) != -1) {
				count++;
			} else {
				logger.info("Finished");
				return Float.NaN;
			}
		}

		long diff = stoppedAt - startedAt;
		logger.info("Count: " + count + "  Diff : " + diff);
		rate = 1000 * (((float) count * bufferSize * 8 / diff)) / (1024 * 1024);// rate
																				// in
																				// Mbps
		return rate;
	}

	public float calculateAverageDownloadRate() throws Exception {
		int times[] = { 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700,
				1800, 1900, 2000 };
		float bw = 0, curBw;
		int i = 0, len = times.length, num = 0;
		BufferedInputStream in = null;
		logger.info("starting download speed");
		try {
			in = new BufferedInputStream(new URL("ftp://speedtest.tele2.net/1MB.zip").openStream());
			while (i < len) {
				curBw = calculateDownloadRate(times[i++], in);
				if (!Float.isNaN(curBw)) {
					if (curBw > 0) {
						num++;
						bw += curBw;
						logger.info("Current rate : " + Float.toString(curBw));
					} else {
						logger.info("Skipping--");						
					}
				} else {
					break;
				}
			}
		} catch (IOException e) {
			logger.error("ERROR in download speed"+e.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				logger.error("ERROR in download speed finally"+e.getMessage());
			}
		}
		bw /= num;
		logger.info("Final : " + bw);
		return bw;
	}
}
