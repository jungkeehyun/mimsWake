package com.mims.wake.server.property;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;

/**
 * 개별 Push 서비스 속성 정의
 */
@ComponentScan
public class DBServiceProperty {

	@Value("driverClassName")
    private String driverClassName;
	
	@Value("url")
    private String url;
    
	@Value("username")
    private String username;
    
	@Value("password")
    private String password;

    public String getDriverClassName() {
        return driverClassName;
    }
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void getPassword(String password) {
        this.password = password;
    }
	
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append("[")
               .append("serviceId=").append(driverClassName)
               .append(", inboundQueueCapacity=").append(url)
               .append(", outboundQueueCapacity=").append(username)
               .append(", outboundServerPort=").append(password)
               .append("]");
        return builder.toString();
    }
}
