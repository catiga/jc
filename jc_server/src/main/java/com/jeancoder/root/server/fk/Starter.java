package com.jeancoder.root.server.fk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.jeancoder.root.server.inet.JCServer;
import com.jeancoder.root.server.inet.ServerFactory;
import com.jeancoder.root.server.proto.conf.FkConf;
import com.jeancoder.root.server.proto.conf.ServerMod;

public class Starter {

	private static Logger logger = LoggerFactory.getLogger(Starter.class);
	
	final static String appConf = "ins.server.json";
	
	final static List<JCServer> iservers = new ArrayList<JCServer>();
	
	public static void main(String[] argc) {
		try {
			InputStream ins = Starter.class.getClassLoader().getResourceAsStream(appConf);
			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
			
			String lineContent = null;
			StringBuffer buff = new StringBuffer();
			while((lineContent = reader.readLine())!=null) {
				buff.append(lineContent);
			}
			Gson gson = new Gson();
			FkConf fk_con = gson.fromJson(buff.toString(), FkConf.class);
			
			for(ServerMod sm : fk_con.getServers()) {
				JCServer server = ServerFactory.generate(sm);
				iservers.add(server);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						server.start();
					}
				}).start();
			}
			while(true) {
				TimeUnit.MILLISECONDS.sleep(10000L);
				System.out.println(iservers);
			}
		} catch(Exception e) {
			logger.error("start error:", e);
		}
	}
}
