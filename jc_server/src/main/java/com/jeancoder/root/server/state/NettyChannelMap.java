package com.jeancoder.root.server.state;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

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
