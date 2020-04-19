package com.mims.wake.server.outbound;

import java.io.File;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mims.wake.common.PushMessageDecoder;
import com.mims.wake.common.PushMessageEncoder;
import com.mims.wake.common.WebSocketFrameDecoder;
import com.mims.wake.common.WebSocketFrameEncoder;
import com.mims.wake.server.property.PushServiceProperty;
import com.mims.wake.server.queue.OutboundQueueManager;
import com.mims.wake.util.commonUtil;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * WebSocket 통신을 사용하는 Outbound Server 타입
 */
public class OutboundWebSocketServer extends OutboundServer {
	private static final Logger logger = LogManager.getLogger(OutboundWebSocketServer.class);

    private final PushServiceProperty property;					// Push Service property
    private final OutboundQueueManager outboundQueueManager;	// OutboundQueue 인스턴스 관리자
    private final SslContext sslCtx = getCertificate(); 		// SSL

    /**
     * constructor with parameters
     * @param property Push Service property
     * @param outboundQueueManager OutboundQueue 인스턴스 관리자
     */
    public OutboundWebSocketServer(PushServiceProperty property, OutboundQueueManager outboundQueueManager) {
        super(property);
        this.property = property;
        this.outboundQueueManager = outboundQueueManager;
    }

    /**
     * WebSocket 통신용 이벤트 핸들러를 설정하는 ChannelInitializer 인스턴스를 생성한다.<br>
     * @return ChannelInitializer 인스턴스
     * @see chess.push.server.outbound.OutboundServer#getChannelInitializer()
     */
    @Override
    protected ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel socketChannel) {
                ChannelPipeline pipeline = socketChannel.pipeline();
                if(sslCtx != null) { // SSL
                	pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));
                }
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(65536));
                pipeline.addLast(new WebSocketServerCompressionHandler());
                pipeline.addLast(new WebSocketServerProtocolHandler(property.getOutboundServerWsUri(), null, true));
                // 필요시 HTTP 요청 핸들러 설정
                // pipeline.addLast(???);
                pipeline.addLast(new WebSocketFrameDecoder(), new WebSocketFrameEncoder());
                pipeline.addLast(new PushMessageDecoder(), new PushMessageEncoder());
                pipeline.addLast(new OutboundServerHandler(property, outboundQueueManager));
            }
        };
    }

    /**
     * SSL 인증서 및 개인키 파일.<br>
     * @return SslContext 인스턴스
     * @see 
     */
    private SslContext getCertificate() {  
    	String subPath = "ssl" + commonUtil.pathToken();
		String path = commonUtil.getCurrentPath("");
		String certFile = path + subPath + "service.crt";
		String keyFile = path + subPath + "service.pkcs8.key";
		String caFile = path + subPath + "rootCA.pem";

		File cert = new File(certFile); // 인증서 파일
		File key = new File(keyFile); // 개인키 파일
		File ca = new File(caFile); 
		if (cert.exists() && key.exists() && ca.exists()) {
			try {
				return SslContextBuilder.forServer(cert, key)
						.clientAuth(ClientAuth.REQUIRE)
						.trustManager(ca)
						.build();
			} catch (SSLException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			logger.error("[OutboundWebSocketServer: Certification or Key file not found!!]");
			return null;
		}
    }
    
    private SslContext getSelfCertificate() {
		try {
			SelfSignedCertificate ssc = new SelfSignedCertificate(); // 자가 서명 인증서를 만드는 클래스
			try {
				return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
			} catch (SSLException e) {
				e.printStackTrace();
				return null;
			}
		} catch (CertificateException e1) {
			e1.printStackTrace();
			return null;
		}
    }
}
