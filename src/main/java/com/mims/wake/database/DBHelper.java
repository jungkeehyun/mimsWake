package com.mims.wake.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.StringValue;
import com.mims.wake.server.property.DBServiceProperty;

public class DBHelper {
	
	private static final Logger logger = LogManager.getLogger(DBHelper.class);

    // 데이터베이스 접속에 필요한 정보 정의
//    private static String db_hostname;
//    private static int db_portnumber;
//    private static String db_database;
//	private static String db_charset;
//    private static String db_username;
//    private static String db_password;
	private static String url;
	private static String driverClassName;
	private static String username;
	private static String password;

    // 접속 처리를 위한 객체 선언
    private Connection conn = null;

    // === 싱글톤 객체 ===
    private static DBHelper current = null;

    public static DBHelper getInstance(DBServiceProperty dBServiceProperty) {
    	if (current == null) {
            current = new DBHelper();
            
            driverClassName = dBServiceProperty.getDriverClassName();
        	url = dBServiceProperty.getUrl();
        	username = dBServiceProperty.getUsername();
        	password = dBServiceProperty.getPassword();
        	
        }
    	
        return current;
    }
    
    public static DBHelper getInstance() {
    	if (current != null)
    		return current;
    	else
    		return null;
    }

    public void freeInstace() {
        current = null;
    }

    public DBHelper() {
    	
//    	try (InputStream input = new FileInputStream("config.properties")) {
//
//            Properties prop = new Properties();
//
//            // load a properties file
//            prop.load(input);
//
//            
//            // mysql
//            db_hostname = prop.getProperty("db.hostname");
//            db_portnumber = Integer.parseInt(prop.getProperty("db.portnumber"));
//            db_database = prop.getProperty("db.database");
//            db_charset =prop.getProperty("db.charset");
//            db_username = prop.getProperty("db.username");
//            db_password = prop.getProperty("db.password");
//            
//            
//            /*
//            // tibero db            
//            db_hostname = prop.getProperty("db.hostname");
//            db_portnumber = Integer.parseInt(prop.getProperty("db.portnumber"));
//            db_database = prop.getProperty("db.database");
//            db_username = prop.getProperty("db.username");
//            db_password = prop.getProperty("db.password");
//           	*/
//        } 
//    	catch (IOException ex) {
//            ex.printStackTrace();
//        }   	
    }

    // === 싱글톤 객체 ===
    public Connection open() {
    	
        if (conn == null) {
        	
            // 데이터베이스 접속 처리 
            // 데이터베이스 명을 포함한 URL을 만든다
        	
        	// mysql
            //String urlFormat = "jdbc:mysql://%s:%d/%s?charsetEncoding=%s&serverTimezone=UTC";
            //String url = String.format(urlFormat, db_hostname, db_portnumber, db_database); 

            // MySQL JDBC의 드라이버 클래스를 로딩해서 DriverManager 클래스에 등록한다.
            try {
            	logger.info("@@" + this.driverClassName);
            	logger.info("@@" + this.username);
            	logger.info("@@" + this.password);
            	logger.info("@@" + this.url);
            	
                Class.forName(this.driverClassName);
                conn = DriverManager.getConnection(this.url, this.username, this.password);
                logger.info("DATABASE Connect Success");
            } 
            catch (ClassNotFoundException e) {
            	logger.info(e.getMessage());
            } 
            catch (SQLException e) {
            	logger.info(e.getMessage());
            }
            

        	/*
            // tibero db
            String urlFormat = "jdbc:tibero:thin:@%s:%d:%s";
            String url = String.format(urlFormat, db_hostname, db_portnumber, db_database);

            // Tibero JDBC의 드라이버 클래스를 로딩해서 DriverManager 클래스에 등록한다.
            try {
                Class.forName("com.tmax.tibero.jdbc.TbDriver");
                conn = DriverManager.getConnection(url, db_username, db_password);
                //logger.info("DATABASE Connect Success");
            } 
            catch (ClassNotFoundException e) {
            	logger.info(e.getMessage());
            } 
            catch (SQLException e) {
            	logger.info(e.getMessage());
            }
			*/
        }
        return conn;
    }

    public void close() {
        if (conn != null) {
            // 데이터베이스 접속 해제 처리

            try {
                conn.close();
                logger.info("DATABASE Disconnect Success");
            } catch (SQLException e) {
            	logger.info(e.getMessage());
            }
            conn = null;
        }
    }
    
    public void insertClientConn(String clientIp, String groupId, String clientId) throws SQLException {
    	PreparedStatement pstmt = null;
    	
    	if (conn != null) {
    		String sql = "INSERT INTO clientConn(CONN_DTTM, CLIENT_CONN_IP, MODE, MSG_TYPE) VALUES (?,?,?,?)";
    		pstmt = conn.prepareStatement(sql);
    		
    		pstmt.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));	// CONN_DTTM
    		pstmt.setString(2, clientIp);	// CLIENT_CONN_IP
    		pstmt.setString(3, groupId);	// MODE
    		pstmt.setString(4, clientId);	// MSG_TYPE
    		
    		logger.info("insertClientConn [{}]", pstmt);
    		
    		int count = pstmt.executeUpdate();

			logger.info("insertClientConn Insert Count : {}", count);
    		
    	} else {
    		logger.info("insertClientConn [DB Connection error!!]");
    	}
    	
		

    }
    
    public void insert(String name, String no) throws SQLException {
    	
    	PreparedStatement pstmt = null;
    	
    	logger.info("@@@ INSERT start");
    	
    	
    	if (conn != null) {
    		
			// 3. SQL 쿼리 준비
			String sql = "INSERT INTO test VALUES (?,?)";
			pstmt = conn.prepareStatement(sql);
						
			// 4. 데이터 binding
			pstmt.setString(1, name);
			pstmt.setString(2, no);
						
			
			logger.info("@@@ INSERT " + sql);
			
			
			// 5. 쿼리 실행 및 결과 처리
			// SELECT와 달리 INSERT는 반환되는 데이터들이 없으므로
			// ResultSet 객체가 필요 없고, 바로 pstmt.executeUpdate()메서드를 호출하면 됩니다.
			// INSERT, UPDATE, DELETE 쿼리는 이와 같이 메서드를 호출하며
			int count = pstmt.executeUpdate();
			if( count == 0 ) {
				logger.info("데이터 입력 실패");
			}
			else {
				logger.info("데이터 입력 성공");
			}
	    }
    	
    	logger.info("@@@ INSERT end");
    }   

}