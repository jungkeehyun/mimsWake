package com.mims.wake.server.property;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Service;

import com.mims.wake.util.commonUtil;

@Service("prop")
public class UserProperty {
	private static final Logger LOG = LoggerFactory.getLogger(UserProperty.class);
	
	private PushBaseProperty _baseProperty;
	private Collection<PushServiceProperty> _serviceProperties;
	private String _pathFile;

	public UserProperty() {
		String path = System.getProperty("user.dir");
		Vector<String> arrFile = commonUtil.getFileNames(path, "xml");
		if (!arrFile.isEmpty())
			_pathFile = arrFile.get(0);
	}

	public void readPrpoerties(String propName) {
		String name = propName;
		if(name == null || name.isEmpty())
			name = "default.properties";
		else {
			String pathFile = commonUtil.getCurrentPath("");
			pathFile += propName + ".properties";
			File file = new File(pathFile);
			if (file.exists() == false) {
				LOG.info("{} file not found.", pathFile);
				return;
			}
			name = pathFile;
		}
		
		FileReader resources = null;
		try {
			resources = new FileReader(name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Properties prop = new Properties();
		try {
			prop.load(resources);

			// base
			ServerType serverType = null;
			String type = prop.getProperty("server.type");
			switch (type) {
			case "TCPSOCKET":
				serverType = ServerType.TCPSOCKET;
				break;
			case "FILESOCKET":
				serverType = ServerType.FILESOCKET;
				break;
			}
			if (serverType == null) {
				resources.close();
				LOG.info("Base service property not found.");
				return;
			}
			_baseProperty = new PushBaseProperty();
			_baseProperty.setInboundServerType(serverType);
			_baseProperty.setInboundServerPort(Integer.parseInt(prop.getProperty("server.port")));
			_baseProperty.setInboundQueueCheckInterval(5);
			_baseProperty.setOutboundQueueCheckInterval(5);
			_baseProperty.setOutboundServerWsUri(prop.getProperty("server.host"));
			_baseProperty.setInboundPollingInterval(prop.getProperty("server.intv"));

			Map<Integer, PushServiceProperty> mapProp = new HashMap<Integer, PushServiceProperty>();

			// service
			String port = prop.getProperty("tcp.port");
			String addr = prop.getProperty("tcp.addr");
			if (port != null && !port.isEmpty()) {
				PushServiceProperty sprop = new PushServiceProperty();
				sprop.setServiceId(ServiceType.TCPSOCKET);
				sprop.setOutboundServerPort(Integer.parseInt(prop.getProperty("tcp.port")));
				sprop.setOutboundServerType(ServerType.TCPSOCKET);
				sprop.setOutboundServerWsUri(addr);
				sprop.setInboundQueueCapacity(10000);
				sprop.setOutboundQueueCapacity(10000);
				mapProp.put(1, sprop);
			}

			port = prop.getProperty("web.port");
			addr = prop.getProperty("web.addr");
			if (port != null && !port.isEmpty() && addr != null && !addr.isEmpty()) {
				PushServiceProperty sprop = new PushServiceProperty();
				sprop.setServiceId(ServiceType.WEBSOCKET);
				sprop.setOutboundServerPort(Integer.parseInt(prop.getProperty("web.port")));
				sprop.setOutboundServerType(ServerType.WEBSOCKET);
				sprop.setOutboundServerWsUri(addr);
				sprop.setInboundQueueCapacity(10000);
				sprop.setOutboundQueueCapacity(10000);
				mapProp.put(2, sprop);
			}

			port = prop.getProperty("file.port");
			addr = prop.getProperty("file.addr");
			if (port != null && !port.isEmpty() && addr != null && !addr.isEmpty()) {
				PushServiceProperty sprop = new PushServiceProperty();
				sprop.setServiceId(ServiceType.FILESOCKET);
				sprop.setOutboundServerPort(Integer.parseInt(prop.getProperty("file.port")));
				sprop.setOutboundServerType(ServerType.FILESOCKET);
				sprop.setOutboundServerWsUri(addr);
				sprop.setInboundQueueCapacity(10000);
				sprop.setOutboundQueueCapacity(10000);
				mapProp.put(3, sprop);
			}

			_serviceProperties = mapProp.values();
			resources.close();
			
			if(_serviceProperties.isEmpty())
				LOG.info("Push service properties not found.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// UNUSED
	public void loadProp() {
		if (_pathFile.isEmpty())
			return;

		try (AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:user-config02.xml")) {
			_baseProperty = ctx.getBean("baseProperty", PushBaseProperty.class);
			_serviceProperties = ctx.getBeansOfType(PushServiceProperty.class).values();
			_serviceProperties.forEach(prop -> {
				if (prop.getOutboundServerType() == null)
					_serviceProperties.remove(prop);
			});
			ctx.close();
		}
	}

	// UNUSED
	public void loadExam() {
		ConfigurableApplicationContext ctx = new GenericXmlApplicationContext();
		ConfigurableEnvironment env = ctx.getEnvironment();
		MutablePropertySources ps = env.getPropertySources();
		try {
			ps.addLast(new ResourcePropertySource("classpath:/user.properties"));

			System.out.println(env.getProperty("server.type"));
			System.out.println(env.getProperty("server.port"));
		} catch (Exception e) {
			// TODO: handle exception
		}

		GenericXmlApplicationContext gCtx = (GenericXmlApplicationContext) ctx;
		gCtx.load("classpath:user-config02.xml");
		gCtx.refresh();
		_baseProperty = gCtx.getBean("baseProperty", PushBaseProperty.class);
		System.out.println(_baseProperty.toString());
		gCtx.close();
		ctx.close();
	}

	public PushBaseProperty getBaseProperty() {
		return _baseProperty;
	}

	public Collection<PushServiceProperty> getServiceProperty() {
		return _serviceProperties;
	}
}
