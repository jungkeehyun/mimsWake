package com.mims.wake.server.outbound;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mims.wake.common.PushMessageDecoder;
import com.mims.wake.common.PushMessageEncoder;
import com.mims.wake.common.WebSocketFrameDecoder;
import com.mims.wake.common.WebSocketFrameEncoder;
import com.mims.wake.server.property.PushServiceProperty;
import com.mims.wake.server.queue.OutboundQueueManager;
import com.mims.wake.util.Base64Coder;
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
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * WebSocket 통신을 사용하는 Outbound Server 타입
 */
public class OutboundWebSocketServer extends OutboundServer {
	private static final Logger logger = LogManager.getLogger(OutboundWebSocketServer.class);

    private final PushServiceProperty property;					// Push Service property
    private final OutboundQueueManager outboundQueueManager;	// OutboundQueue 인스턴스 관리자
	// [+] SSL
	private final SslContext sslCtx1st = getCertificate();
	private final SSLContext sslCtx2nd = null;//createSSLContext2nd();
	// [-] SSL

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
            public void initChannel(SocketChannel socketChannel) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
                ChannelPipeline pipeline = socketChannel.pipeline();
				// [+] SSL
				if (sslCtx1st != null) {
					pipeline.addLast("ssl", sslCtx1st.newHandler(socketChannel.alloc()));
				} else if (sslCtx2nd != null) {
					SSLEngine engine = sslCtx2nd.createSSLEngine();
					engine.setEnabledCipherSuites(sslCtx2nd.getServerSocketFactory().getSupportedCipherSuites());
					engine.setUseClientMode(false);
					engine.setNeedClientAuth(false);
					pipeline.addLast("ssl", new SslHandler(engine));
                }
				// [-] SSL
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
		String certFile = path + subPath + "service.crt"; 		// 인증서 파일
		String keyFile = path + subPath + "service.pkcs8.key";	// 개인키 파일
		String caFile = path + subPath + "rootCA.pem";			// CA-Key

		File cert = new File(certFile);
		File privateKey = new File(keyFile);
		File caKey = new File(caFile);
		if (cert.exists() && privateKey.exists() && caKey.exists()) {
			try {
				return SslContextBuilder.forServer(cert, privateKey)
						.clientAuth(ClientAuth.REQUIRE)
						.trustManager(caKey)
						.build();
			} catch (SSLException e) {
				logger.error("[OutboundWebSocketServer: Error Initialization Certification!!]");
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
    
	private SSLContext createSSLContext() {
		try {
			String subPath = "certs" + commonUtil.pathToken();
			String path = commonUtil.getCurrentPath("");
			String jksFile = path + subPath + "clientcert.jks";
			
			String password = "123456";
			KeyStore ks = KeyStore.getInstance("JKS"); // "JKS"
			InputStream ksInputStream = new FileInputStream(jksFile);
			ks.load(ksInputStream, password.toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, password.toCharArray());

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), null, null);
			return sslContext;
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return null;
	}
	
	private SSLContext createSSLContext2nd() {
		try {
			String subPath = "certs" + commonUtil.pathToken();
			String path = commonUtil.getCurrentPath("");
			String jksFile = path + subPath + "clientcert.jks";
			String data = readFile(jksFile);
			String password = "123456";
			
			String algorithm = KeyManagerFactory.getDefaultAlgorithm();
			SSLContext serverContext = SSLContext.getInstance("TLSv1.2"); // JDK 7 버전부터 지원합니다.
			final KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new ByteArrayInputStream(Base64Coder.decode(data)), password.toCharArray());
			final KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
			kmf.init(ks, "123456".toCharArray());
			serverContext.init(kmf.getKeyManagers(), null, null);
			return serverContext;
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return null;
	}
	
	private String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}
}
