package com.mims.wake;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

import com.mims.wake.server.Server;
import com.mims.wake.server.property.DBServiceProperty;
import com.mims.wake.server.property.PushBaseProperty;
import com.mims.wake.server.property.PushServiceProperty;
import com.mims.wake.server.property.UserProperty;

/**
 * Spring-Boot Start (with Main)
 * @author GenieInBed
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude= {DataSourceAutoConfiguration.class})
@ImportResource({"classpath*:application-config.xml"})
public class MimsWakeApplication implements CommandLineRunner  {

	private static final Logger logger = LogManager.getLogger(MimsWakeApplication.class);
	public static UserProperty userProperties;

	/**
	 * MIMS 항적 프로그램(Spring Boot) 시작.
	 * run() 에서 실제 프로그램 시작
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		logger.info("■□■□■MIM Wake Websocket Server Start■□■□■");
		SpringApplication.run(MimsWakeApplication.class, args);
	}
	
	/**
	 * CommandLineRunner 에 의해 자동 실행
	 *
	 * @param strings
	 * @throws Exception
	 */
	@Override
	public void run(String... strings) throws Exception {

		Server server = new Server();
		/*
		try (ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("application-config.xml")) {
            PushBaseProperty baseProperty = context.getBean(PushBaseProperty.class);
            Collection<PushServiceProperty> serviceProperties = context.getBeansOfType(PushServiceProperty.class).values(); */
		try {				
            // [YPK] get properties
			String propName = null;
			if(strings.length > 0)
				propName = strings[0];
			
			userProperties= new UserProperty();
			userProperties.readPrpoerties(propName);
            PushBaseProperty baseProperty = userProperties.getBaseProperty();
            if(baseProperty == null)
            	throw new Exception();
            Collection<PushServiceProperty> serviceProperties = userProperties.getServiceProperty();
            DBServiceProperty dBServiceProperty = userProperties.getDBProperty();

            // Push 서버 모듈 기동
            if(server.startupServer(false, baseProperty, serviceProperties, dBServiceProperty) == null) {
            	throw new Exception();
            }
            
            synchronized (MimsWakeApplication.class) {
            	MimsWakeApplication.class.wait();
            }

        } catch (Exception e) {
        	logger.error("startup failed", e);
        } finally {
            server.shutdownServer();
        }
	}
}