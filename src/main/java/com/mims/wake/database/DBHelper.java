package com.mims.wake.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mims.wake.server.kmtf.Field;
import com.mims.wake.server.kmtf.Set;
import com.mims.wake.server.property.DBServiceProperty;

public class DBHelper {
	
	private static final Logger logger = LogManager.getLogger(DBHelper.class);

    // 데이터베이스 접속에 필요한 정보 정의
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

    // === 싱글톤 객체 ===
    public Connection open() {
        if (conn == null) {
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

    public void insertAirWake(String modeId, Set set) throws SQLException {
    	PreparedStatement pstmt = null;
    	
    	if (conn != null) {
    		
    		String sql = "INSERT INTO";
    		if ("OPER".equals(modeId)) {
    			sql += " airWakeRe";
    		} else {
    			sql += " airWakeEx";
    		}
    		sql += "(ACFTNO, ACFTFNFDVCD, ALT, ACFT_CNTUNT, CLSGN"
    				+ ", TKOF_BASENO, SPD, KNDAP_NM, LTDLNGT_COORD"
    				+ ", AZ, REGR_DTTM)"
    				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    		
    		LinkedHashMap<Integer, Field> map = set.getFieldMap();
    		pstmt = conn.prepareStatement(sql);
    		
			for (Object key : map.keySet()) {
				Field field = map.get(key);
				
				switch (field.getName()) {
					case "ACFTNO" :
						pstmt.setString(1, field.getValue());
						break;
					case "ACFTFNFDVCD" :
						pstmt.setString(2, field.getValue());
						break;
					case "ALT" :
						pstmt.setFloat(3, Float.parseFloat(field.getValue()));
						break;
					case "ACFT_CNTUNT" :
						pstmt.setInt(4, Integer.parseInt(field.getValue()));
						break;
					case "CLSGN" :
						pstmt.setString(5, field.getValue());
						break;
					case "TKOF_BASENO" :
						pstmt.setString(6, field.getValue());
						break;
					case "SPD" :
						pstmt.setFloat(7, Float.parseFloat(field.getValue()));
						break;
					case "KNDAP_NM" :
						pstmt.setString(8, field.getValue());
						break;
					case "LTDLNGT_COORD" :
						pstmt.setString(9, field.getValue());
						break;
					case "AZ" :
						pstmt.setInt(10, Integer.parseInt(field.getValue()));
						break;
				}
				
				pstmt.setTimestamp(11, new java.sql.Timestamp(System.currentTimeMillis()));		// REGR_DTTM
			}
    		logger.info("insertAirWake [{}]", pstmt);
    		
    		int count = pstmt.executeUpdate();
			logger.info("insertAirWake Insert Count : {}", count);
			
    	} else {
    		logger.info("insertAirWake [DB Connection error!!]");
    	}
    }
    
    public void insertSeaWakeRe(Set set) throws SQLException {
    	PreparedStatement pstmt = null;
    	
    	if (conn != null) {
    		
    		String sql = "INSERT INTO seaWakeRe"
    				+ "(SEATIFFCD, IDNO, WRSHPNO, LOCTN, AZ"
    				+ ", SEATTGTMSCD, SEATNATCD, SPD, SEATTGTLRCCD, SEATTGTKNDCD"
    				+ ", EFTV_DTTM, TRACRLTDCD, REGR_DTTM)"
    				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
    		
    		LinkedHashMap<Integer, Field> map = set.getFieldMap();
    		pstmt = conn.prepareStatement(sql);
    		
			for (Object key : map.keySet()) {
				Field field = map.get(key);
				
				switch (field.getName()) {
					case "SEATIFFCD" :
						pstmt.setString(1, field.getValue());
						break;
					case "IDNO" :
						pstmt.setString(2, field.getValue());
						break;
					case "WRSHPNO" :
						pstmt.setString(3, field.getValue());
						break;
					case "LOCTN" :
						pstmt.setString(4, field.getValue());
						break;
					case "AZ" :
						pstmt.setInt(5, Integer.parseInt(field.getValue()));
						break;
					case "SEATTGTMSCD" :
						pstmt.setString(6, field.getValue());
						break;
					case "SEATNATCD" :
						pstmt.setString(7, field.getValue());
						break;
					case "SPD" :
						pstmt.setFloat(8, Float.parseFloat(field.getValue()));
						break;
					case "SEATTGTLRCCD" :
						pstmt.setString(9, field.getValue());
						break;
					case "SEATTGTKNDCD" :
						pstmt.setString(10, field.getValue());
						break;
					case "EFTV_DTTM" :
						pstmt.setString(11, field.getValue());
						break;
					case "TRACRLTDCD" :
						pstmt.setString(12, field.getValue());
						break;
				}
				
				pstmt.setTimestamp(13, new java.sql.Timestamp(System.currentTimeMillis()));		// REGR_DTTM
			}
    		logger.info("insertSeaWakeRe [{}]", pstmt);
    		
    		int count = pstmt.executeUpdate();
			logger.info("insertSeaWakeRe Insert Count : {}", count);
			
    	} else {
    		logger.info("insertSeaWakeRe [DB Connection error!!]");
    	}
    }
    
    public void insertSeaWakeEx(Set set) throws SQLException {
    	PreparedStatement pstmt = null;
    	
    	if (conn != null) {
    		
    		String sql = "INSERT INTO seaWakeEx"
    				+ "(SEATIFFCD, IDNO, WRSHPNO, LOCTN, AZ"
    				+ ", SPD, WARSBLCD, REGR_DTTM)"
    				+ " VALUES (?,?,?,?,?,?,?,?)";
    		
    		LinkedHashMap<Integer, Field> map = set.getFieldMap();
    		pstmt = conn.prepareStatement(sql);
    		
			for (Object key : map.keySet()) {
				Field field = map.get(key);
				
				switch (field.getName()) {
					case "SEATIFFCD" :
						pstmt.setString(1, field.getValue());
						break;
					case "IDNO" :
						pstmt.setString(2, field.getValue());
						break;
					case "WRSHPNO" :
						pstmt.setString(3, field.getValue());
						break;
					case "LOCTN" :
						pstmt.setString(4, field.getValue());
						break;
					case "AZ" :
						pstmt.setInt(5, Integer.parseInt(field.getValue()));
						break;
					case "SPD" :
						pstmt.setFloat(6, Float.parseFloat(field.getValue()));
						break;
					case "WARSBLCD" :
						pstmt.setString(7, field.getValue());
						break;
				}
				
				pstmt.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));		// REGR_DTTM
			}
    		logger.info("insertSeaWakeEx [{}]", pstmt);
    		
    		int count = pstmt.executeUpdate();
			logger.info("insertSeaWakeEx Insert Count : {}", count);
			
    	} else {
    		logger.info("insertSeaWakeEx [DB Connection error!!]");
    	}
    }

}