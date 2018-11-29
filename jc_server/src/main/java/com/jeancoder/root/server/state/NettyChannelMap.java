package com.jeancoder.root.server.state;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.jeancoder.root.server.proto.msg.ReplyMsg;
import com.jeancoder.root.server.proto.msg.ReplyServerBody;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

public class NettyChannelMap {

	private static Map<String, SocketChannel> map = new ConcurrentHashMap<String, SocketChannel>();
	
	public static Enumeration<String> clients() {
		Set<String> sets = map.keySet();
		Vector<String> vec = new Vector<>();
		if(sets!=null&&!sets.isEmpty()) {
			for(String s : sets) {
				vec.add(s);
			}
		}
		return vec.elements();
	}

	public static void add(String clientId, SocketChannel socketChannel) {
		try {
			map.put(clientId, socketChannel);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int index = 0;
				while(true) {
					try {
						TimeUnit.MILLISECONDS.sleep(1000l);
						if(!map.isEmpty()) {
							for(String client_id : map.keySet()) {
								SocketChannel channel = map.get(client_id);
								ReplyMsg msg = new ReplyMsg();
								ReplyServerBody body = new ReplyServerBody("center server reply:" + index++);
								msg.setBody(body);
								channel.writeAndFlush(msg);
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static Channel get(String clientId) {
		return map.get(clientId);
	}

	@SuppressWarnings("rawtypes")
	public static void remove(SocketChannel socketChannel) {
		for (Map.Entry entry : map.entrySet()) {
			if (entry.getValue() == socketChannel) {
				map.remove(entry.getKey());
			}
		}
	}

}
