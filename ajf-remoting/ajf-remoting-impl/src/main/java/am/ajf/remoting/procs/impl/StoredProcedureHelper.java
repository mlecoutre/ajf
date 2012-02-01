package am.ajf.remoting.procs.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import am.ajf.remoting.EditableMapper;
import am.ajf.remoting.Mapper;

public class StoredProcedureHelper {
	
	public static Object callStoredProcedure(String jndi, String name, boolean isResultNull, boolean isResList, Class<? extends Mapper> mapperClass, Class<?> resultType, Object... params ) 
			throws SQLException, NamingException, InstantiationException, IllegalAccessException {		
		InitialContext ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup(jndi);
		Connection con = ds.getConnection();
		Mapper mapper = mapperClass.newInstance();
		
		//If we use the system mapper, so we need to specify the entity type for the instanciation
		if (mapper instanceof EditableMapper) {
			EditableMapper subMapper = (EditableMapper) mapper;
			subMapper.setEntity(resultType);
		}
		
		List<Object> result = new ArrayList<Object>();
		
		PreparedStatement pstmt = con.prepareCall("CALL "+name+"("+generateQuestionMarks(params.length)+")");
		
		for (int i = 0 ; i < params.length ; i++) {
			//logger.debug(\"Setting parameter arg"+i+": \"+arg"+i+");\n");
			pstmt.setObject(i+1,params[i]);
		}		
		if (isResultNull) {
			pstmt.execute();		
		} else {
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData metadata = rs.getMetaData();
			List<String> colNames = new ArrayList<String>();
			for (int i=0 ; i < metadata.getColumnCount() ; i++) {
				colNames.add(metadata.getColumnName(i+1).toLowerCase());
				//logger.debug("Adding col in cache : "+metadata.getColumnName(i+1).toLowerCase());
			}						
			
			while(rs.next()) {				
				Map<String, Object> item = new HashMap<String, Object>();
				for (String column : colNames) {
					item.put(column, rs.getObject(column));
				}
				result.add(mapper.map(item));
			}
			rs.close();
			pstmt.close();
		}		
		if (isResList) {
			return result;
		} else {
			if (result.size() > 1) {
				throw new SQLException("More than one Object returned from the call to the stored procedure : "+name);
			}
			return result.get(0);
		}
	
	}
	
	/**
	 * Generate question marks for a stored prcedure call
	 * 
	 * @param number
	 * @return
	 */
	private static String generateQuestionMarks(int number) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0 ; i < number ; i++) {
			if (i==0) {
				buffer.append("?");
			} else {
				buffer.append(",?");
			}
		}
		return buffer.toString();
	}

}
