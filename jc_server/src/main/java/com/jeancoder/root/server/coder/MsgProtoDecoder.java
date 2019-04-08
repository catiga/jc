package com.jeancoder.root.server.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;

public class MsgProtoDecoder extends ObjectDecoder {

	public MsgProtoDecoder(ClassResolver classResolver) {
		super(classResolver);
	}

	public MsgProtoDecoder(int maxObjectSize, ClassResolver classResolver) {
        super(maxObjectSize, classResolver);
    }
	
	@Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
//        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
//        if (frame == null) {
//            return null;
//        }
//
//        ObjectInputStream ois = new CompactObjectInputStream(new ByteBufInputStream(frame, true), classResolver);
//        try {
//            return ois.readObject();
//        } finally {
//            ois.close();
//        }
		try {
			return super.decode(ctx, in);
		} catch(Exception e) {
			throw e;
		}
    }
}
