package com.mims.wake.database;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mims.wake.server.Server;

@Repository
public class commonDAO {
	
	private static final Logger logger = LogManager.getLogger(commonDAO.class);
	
	@Autowired
	@Qualifier("firstSqlSessionTemplate")//config파일의 sqlsession 매소드 이름
	private SqlSession sqlSession;
	
	public String getCurrentDataTime() {
		return sqlSession.selectOne("mapper.mapper.getCurrentDateTime");
	}
	    
	public Object selectObject(String mapperId) throws Exception {
	    return this.sqlSession.selectOne(mapperId);
    }
		
	public Object selectObject(String mapperId, Object parameter) throws Exception {
		return this.sqlSession.selectOne(mapperId, parameter);
	}
	
	// insert example 
	public void insertTest() {
		Map<String, Object> param = new HashMap<>(); 
		param.put("connectUserId", "1222"); 
		param.put("testnum", "hung");
		 
		logger.info(param.toString());
		logger.info(sqlSession.toString());
		int result = sqlSession.insert("mapper.mapper.insertTest", param);
		logger.info("@@@@@@");
		sqlSession.commit();
	}
	
	// insert example 
	public void insertPlayer() {
		Map<String, Object> param = new HashMap<>(); 
		param.put("age", 41); 
		param.put("name", "황장군");
		 
		int result = sqlSession.insert("mapper.mapper.insertPlayer", param); // 1
		sqlSession.commit();
	}
	
	// update example
	public void updatePlayer() {
		Map<String, Object> param = new HashMap<>(); 
		param.put("id", 444);
		param.put("age", 41); 
		param.put("name", "황장군");
		 
		int result = sqlSession.update("mapper.mapper.insertPlayer", param); // 2
		sqlSession.commit();
	}
	
	// delete example 
	public void deletePlayer() {
		Map<String, Object> param = new HashMap<>(); 
		param.put("name", "황장군");
		 
		int result = sqlSession.delete("test.deletePlayerWhere", param);  // 3
		sqlSession.commit();
	}
}
