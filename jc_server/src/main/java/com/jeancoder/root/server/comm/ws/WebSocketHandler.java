package com.jeancoder.root.server.comm.ws;

import static com.jeancoder.root.io.line.HeaderNames.CONTENT_LENGTH;
import static com.jeancoder.root.io.line.HeaderNames.CONTENT_TYPE;
import static io.netty.buffer.Unpooled.copiedBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.http.ChannelWrapper;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.util.JackSonBeanMapper;
import com.jeancoder.root.env.ChannelContextWrapper;
import com.jeancoder.root.env.RunnerResult;
import com.jeancoder.root.exception.RunningException;
import com.jeancoder.root.io.http.JCHttpRequest;
import com.jeancoder.root.io.http.JCHttpResponse;
import com.jeancoder.root.io.socketx.BinaryData;
import com.jeancoder.root.io.socketx.DataBuf;
import com.jeancoder.root.io.socketx.StringData;
import com.jeancoder.root.io.socketx.WSRequest;
import com.jeancoder.root.manager.JCVMDelegator;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
	
	private HttpObject requestWrapper;
	
	public WebSocketHandler(HttpObject request) {
		this.requestWrapper = request;
	}
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if(msg instanceof WebSocketFrame) {
			JCVMDelegator.bindContext(ChannelContextWrapper.newone(ctx));
			messageReceived(ctx, msg);
		} else {
			ctx.channel().writeAndFlush("Unsupported Protocol");
		}

		
//        if (msg instanceof WebSocketFrame) {
//            System.out.println("This is a WebSocket frame");
//            System.out.println("Client Channel : " + ctx.channel());
//            if (msg instanceof BinaryWebSocketFrame) {
//                System.out.println("BinaryWebSocketFrame Received : ");
//                System.out.println(((BinaryWebSocketFrame) msg).content());
//                
//            } else if (msg instanceof TextWebSocketFrame) {
//                System.out.println("TextWebSocketFrame Received : ");
//                ctx.channel().writeAndFlush(new TextWebSocketFrame("Message recieved : " + ((TextWebSocketFrame) msg).text()));
//                System.out.println(((TextWebSocketFrame) msg).text());
//                
//            } else if (msg instanceof PingWebSocketFrame) {
//                System.out.println("PingWebSocketFrame Received : ");
//                System.out.println(((PingWebSocketFrame) msg).content());
//                
//            } else if (msg instanceof PongWebSocketFrame) {
//                System.out.println("PongWebSocketFrame Received : ");
//                System.out.println(((PongWebSocketFrame) msg).content());
//                
//            } else if (msg instanceof CloseWebSocketFrame) {
//                System.out.println("CloseWebSocketFrame Received : ");
//                System.out.println("ReasonText :" + ((CloseWebSocketFrame) msg).reasonText());
//                System.out.println("StatusCode : " + ((CloseWebSocketFrame) msg).statusCode());
//                ctx.channel().close();
//                
//            } else {
//                System.out.println("Unsupported WebSocketFrame");
//            }
//        }
    }
	
	protected void messageReceived(ChannelHandlerContext ctx, Object requestObj) {
		WebSocketFrame msg = (WebSocketFrame)requestObj;
		DataBuf real_data = null;
		if(msg instanceof TextWebSocketFrame) {
			real_data = new StringData<TextWebSocketFrame>((TextWebSocketFrame)msg);
		} else if(msg instanceof BinaryWebSocketFrame) {
			real_data = new BinaryData<BinaryWebSocketFrame>((BinaryWebSocketFrame)msg);
		}
		
		HttpRequest request = (HttpRequest) this.requestWrapper;
		InetSocketAddress remote = (InetSocketAddress)ctx.channel().remoteAddress();
		
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		WSRequest<DataBuf> stand_request = null; JCHttpResponse stand_response = null;
		try {
			stand_request = new WSRequest<DataBuf>((FullHttpRequest)request);
			stand_request.setRemoteHost(remote);
			stand_response = new JCHttpResponse(response);
			stand_request.setData(real_data);
			stand_request.setChannel(new ChannelWrapper(ctx.channel()));
			RunnerResult<Result> runner_result = JCVMDelegator.delegate().getVM().dispatch(stand_request, stand_response);
			Object ret_data = runner_result.getData();
			
			WebSocketFrame ret_frame = null;
			if(ret_data!=null) {
				if(ret_data instanceof String) {
					ret_frame = new TextWebSocketFrame(ret_data.toString());
				} else {
					//judge whether seria
					if(ret_data instanceof Serializable) {
						byte[] obj_bytes = this.toByteArray(ret_data);
						ByteBuf byte_buf = Unpooled.copiedBuffer(obj_bytes);
						ret_frame = new BinaryWebSocketFrame(byte_buf);
					} else {
						ret_frame = new TextWebSocketFrame(JackSonBeanMapper.toJson(ret_data));
					}
				}
			} else {
			}
			if(ret_frame!=null) {
				ctx.channel().writeAndFlush(ret_frame);
			}
			//ctx.channel().writeAndFlush(new TextWebSocketFrame(msg_content + ":" + new Random().nextInt()));
		} catch(Exception e) {
			logger.error("so should send msg by socket to center server:" + e.getMessage(), e);
			processHandlerException(e, stand_request, stand_response);
		} finally {
			JCVMDelegator.releaseContext();
		}
    }
	
	protected void processHandlerException(Throwable e, JCHttpRequest req, JCHttpResponse res) {
		StringBuffer error_buffer = new StringBuffer();
		error_buffer.append("VM ID:" + JCVMDelegator.delegate().delegatedId() + "\r\n\r\n");
		if(e instanceof RunningException) {
			RunningException rex = (RunningException)e;
			logger.error(rex.getApp() + "..." + rex.getPath() + "&&&" + rex.getRes());
			error_buffer.append("JCAPP CODE:" + rex.getApp() + "\r\n");
			error_buffer.append("JCAPP PATH:" + rex.getPath() + "\r\n");
			error_buffer.append("JCAPP RES:" + rex.getRes() + "\r\n\r\n");
		}
		error_buffer.append(e.getMessage() + "\r\n\r\n");
		for(StackTraceElement ste : e.getCause()==null?e.getStackTrace():e.getCause().getStackTrace()) {
			if(ste.getClassName().indexOf("io.netty.")>-1) {
				break;
			}
			error_buffer.append("	at " + ste.getClassName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")\r\n\r\n");
		}
		ByteBuf buf = copiedBuffer(error_buffer.toString().getBytes());
		FullHttpResponse new_response = null;
		if(res!=null&&res.delegateObj()!=null) {
			new_response = res.delegateObj().replace(buf);
		} else {
			new_response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
		}
		new_response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		new_response.headers().set(CONTENT_LENGTH, buf.readableBytes());
		new_response.setStatus(HttpResponseStatus.BAD_REQUEST);
		res.replaceDelegateObj(new_response);
	}
	
	public byte[] toByteArray (Object obj) {      
        byte[] bytes = null;      
        ByteArrayOutputStream bos = new ByteArrayOutputStream();      
        try {        
            ObjectOutputStream oos = new ObjectOutputStream(bos);         
            oos.writeObject(obj);        
            oos.flush();         
            bytes = bos.toByteArray ();      
            oos.close();         
            bos.close();        
        } catch (IOException ex) {        
            ex.printStackTrace();   
        }      
        return bytes;    
    }
}
