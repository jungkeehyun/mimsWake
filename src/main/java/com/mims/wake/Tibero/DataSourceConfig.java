package com.mims.wake.Tibero;


import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DataSourceConfig {
	
 	@Bean
    //@ConfigurationProperties(prefix = "spring.datasource")//위에 application.properties에 앞부분
    public DataSource firstDataSource() {

        return DataSourceBuilder.create().build();
    }

    @Bean
    public SqlSessionFactory firstSqlSessionFactory(DataSource firstDataSource, ApplicationContext applicationContext) throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(firstDataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath*:mapper/*.xml"));//xml파일의 위치, src/main/resources아래에 위치
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * DAO 클래스에서 사용한다.
    **/
    @Bean
    public SqlSessionTemplate firstSqlSessionTemplate(SqlSessionFactory firstSqlSessionFactory) throws Exception {

        return new SqlSessionTemplate(firstSqlSessionFactory);
    }
}


