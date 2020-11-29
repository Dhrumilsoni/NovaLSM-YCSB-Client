package com.yahoo.ycsb.db;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.yahoo.ycsb.db.ConfigurationUtil.Configurations;
import sun.misc.IOUtils;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class NovaLSMCoordinator {

	public static void change_cfg(List<String> servers, NovaClient client, int cfgId) throws InterruptedException {
		for (int i = servers.size() - 1; i >= 0; i--) {
			client.changeConfig(i);
			System.out.println("Change config for server " + i);
		}

		long start = System.currentTimeMillis();
		while (true) {
			boolean isAllReady = true;
			for (int i = 0; i < servers.size(); i++) {
				boolean isReady = client.queryConfigComplete(i);
				if (isReady) {
					System.out.println("Server " + i + " is ready");
				} else {
					isAllReady = false;
					System.out.println("Server " + i + " is not ready");
				}
			}
			if (isAllReady) {
				break;
			}
			Thread.sleep(10);
		}

		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println(cfgId + " Take to complete configuration change " + duration);
	}

	public static void main(String[] args) throws Exception {
		List<String> servers = Lists.newArrayList(args[0].split(","));
		String configFile = args[1];
		int port = Integer.parseInt(args[2]);
		Configurations configs = ConfigurationUtil.readConfig(configFile);

		ServerSocket server = null;
		Socket socket = null;

		server = new ServerSocket(port);
		socket = server.accept();
		System.out.println("Connected with client");
		int cur_cfg = 0;
//		int now = 0;
		DataInputStream in = new DataInputStream(socket.getInputStream());
		NovaClient client = new NovaClient(servers, true);

		String line = "";
		while (true) {
			try
			{
				line = in.readLine();

//				Scanner s = new Scanner(in); //.useDelimiter("\\A")
//				line = s.hasNext() ? s.next() : "";
				if(line.contains("change_cfg")){
					System.out.println("Changing config");
					change_cfg(servers, client, ++cur_cfg);
				}
				Thread.sleep(1000);
			}
			catch (Exception i) {
				System.out.println("Exception: "+i);
				break;
//				System.out.println(i);
			}
		}
//		for (int cfgId = 1; cfgId < configs.configs.size(); cfgId++) {
//			long startTime = configs.configs.get(cfgId).startTimeInSeconds;
//			while (true) {
//				if (now == startTime) {
//					break;
//				}
//				Thread.sleep(1000);
//				now++;
//			}
//
//			change_cfg(servers, client, cfgId);
//		}
	}
}
