package com.flower.spirit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class YtDlpUtil {

	public static String exec(String url) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add("yt-dlp");
		command.add("--print-json");
		command.add("--skip-download");
		command.add(url);
//		System.setProperty("http.proxyHost", "192.168.12.13");
//		System.setProperty("http.proxyPort", "58089"); 
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
        	 stringBuilder.append(line);;
        }
        int exitCode = process.waitFor();
        System.out.println("Command executed with exit code: " + exitCode);
        String completeString = stringBuilder.toString();
		return completeString;
	}
	
	
}
