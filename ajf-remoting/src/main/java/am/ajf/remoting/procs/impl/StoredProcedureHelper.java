package am.ajf.remoting.procs.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.remoting.EditableMapper;
import am.ajf.remoting.Mapper;

public class StoredProcedureHelper {
		

	public static final String SP_RESULT_KEY = "$ajf$__RESULT__$ajf$";
	private static final Logger logger = LoggerFactory.getLogger(StoredProcedureHelper.class);

	public static Map<String, Object> callStoredProcedure(String jndi, String name, boolean isResultNull, boolean isResList, boolean isResWrapped, String resultWrappedName, boolean parametersAreNamed, Class<?> mapperClass, Class<?> resultType, int nbInParams, Object... params ) 
			throws SQLException, NamingException, InstantiationException, IllegalAccessException {		
		InitialContext ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup(jndi);
		Connection con = ds.getConnection();
		ResultSet rs = null;
		Mapper mapper = (Mapper) mapperClass.newInstance();
		
		//If we use the system mapper we need to specify the entity type for the instanciation
		//Users can define specifc mappers per entity, or they would need to use the same technique
		if (mapper instanceof EditableMapper) {
			EditableMapper subMapper = (EditableMapper) mapper;
			subMapper.setEntity(resultType);
		}
		
		/*
		DatabaseMetaData dbmd = con.getMetaData();
	    if (dbmd.supportsNamedParameters() == true) {
	    	System.out.println("NAMED PARAMETERS FOR CALLABLE STATEMENTS IS SUPPORTED");
        } else {
	    	System.out.println("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
	    }
	    */
		
		List<Object> result = new ArrayList<Object>();
		Map<String, Object> out = new HashMap<String, Object>();
		
		try {
			CallableStatement cstmt = buildStatement(name, nbInParams, parametersAreNamed, con, params);
			Map<String, Integer> paramsNameToIndex = prepareStatementParameters(name, nbInParams, cstmt, parametersAreNamed, params);
			
			//Manage the call + Resultset if exist
			if (isResultNull) {
				cstmt.execute();		
			} else {
				rs = cstmt.executeQuery();
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
			}
			
			//Manage out parameters (skip the couple <name, value> of IN params
			//in the "params" array)
			//if there is no out params or the IN params are not named, this will
			//be skiped						
			for (int i = nbInParams*2 ; i < params.length ; i = i + 2) {
				out.put((String)params[i], 
						cstmt.getObject(paramsNameToIndex.get(((String)params[i]).toUpperCase())));
			}
			
			
			cstmt.close();
		//Ensure the resources are closed properly without disrupting the Exceptions flow
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (con != null) {
				con.close();
			}
		}
		
		//Map the results to the output (Wrapped, Out params and List or no List)
		Object res;
		String resultNameInMap;
		
		//find the result name in map
		if (isResWrapped) {
			resultNameInMap = resultWrappedName;								
		} else {
			resultNameInMap = SP_RESULT_KEY;
		}
		
		//find the res to return
		if (isResList) {
			res =  result;
		} else {
			if (result.size() > 1) {
				throw new SQLException("More than one Object returned from the call to the stored procedure "+name+"while the return should be unique");
			}
			res = result.get(0);
		}	
		
		//We store the result
		out.put(resultNameInMap, res);
		
		return out;
	}

	/**
	 * Fill the prepare statements with IN and OUT datas.
	 * There is a couple (name, value) for each IN and OUT datas.
	 * IN datas need to be registered with setObject
	 * OUT datas need to be registerd with setObjects and registerOutParameter
	 * 
	 * @param nbInParams
	 * @param cstmt
	 * @param parametersAreNamed
	 * @param params
	 * @throws SQLException
	 */
	private static Map<String, Integer> prepareStatementParameters(String name, int nbInParams,
			CallableStatement cstmt, boolean parametersAreNamed, Object... params) throws SQLException {					    
		Map<String, Integer> nameToIndex = new HashMap<String, Integer>();
		
		//so we use register named params (we may not have OUT params)
		if (parametersAreNamed) {
			//Map the parameter column name to there index			 
			DatabaseMetaData dbmd = null;
			ResultSet rs = null;
			try {
				dbmd = cstmt.getConnection().getMetaData();		    
				rs = dbmd.getProcedureColumns(
			       null, null, "%"+name, null);
				while (rs.next()) {
					nameToIndex.put(rs.getString("COLUMN_NAME"), rs.getInt("ORDINAL_POSITION"));
				    //System.out.println("Column Name: '" + rs.getString("COLUMN_NAME") + "'");
				    //System.out.println("Column Type: '" + rs.getString("COLUMN_TYPE") + "'");
				    //System.out.println("Column DataType: '" + rs.getString("DATA_TYPE") + "'");
				    //System.out.println("Column TypeName: '" + rs.getString("TYPE_NAME") + "'");
				    //System.out.println("Parameter class name :"+cstmt.getParameterMetaData().getParameterClassName(1));
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
			
			// Add the parameter as indexed (from the nameToIndex table)
		    for (int i = 0 ; i < params.length ; i = i + 2) {
				//logger.debug(\"Setting parameter arg"+i+": \"+arg"+i+");\n");
		    	if (i < nbInParams*2) {
		    		cstmt.setObject(nameToIndex.get(((String)params[i]).toUpperCase().trim()), 
						params[i+1]);				
		    	} else {
					cstmt.registerOutParameter(nameToIndex.get(((String)params[i]).toUpperCase().trim()),
						java.sql.Types.OTHER);
				}
			} 
		//we dont have named params (always), so we only register indexed params
		} else {
			for (int i=0 ; i < params.length ; i++) {
				cstmt.setObject(i+1,params[i]);
			}
		}	
		return nameToIndex;
	}

	/**
	 * Build the the statement to call the stored procedure.
	 * This method deal with named parameters or ordinal parameters
	 * 
	 * @param name
	 * @param nbInParams
	 * @param parametersAreNamed
	 * @param con
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	private static CallableStatement buildStatement(String name,
			int nbInParams, boolean parametersAreNamed, Connection con, Object... params)
			throws SQLException {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("CALL ").append(name).append("(");
		
		if (parametersAreNamed) {
			/*
			buffer.append(generateInNames(nbInParams, params));
			if (nbInParams != 0 && nbInParams*2 < params.length) {
				buffer.append(",");
			}
			buffer.append(generateOutNames(nbInParams, params));
			*/
			buffer.append(generateQuestionMarks(params.length/2));
		} else {
			buffer.append(generateQuestionMarks(nbInParams));
		}
		buffer.append(")");
		logger.debug("calling stored procedure : "+buffer.toString());
		return con.prepareCall(buffer.toString());
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
	
	/**
	 * Generate the in params names.
	 * The params array contain the (names, value) couple so, we need
	 * to only get the odd values.
	 * 
	 * @param nbIn
	 * @param params
	 * @return
	 */
	private static String generateInNames(int nbIn, Object[] params) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0 ; i < nbIn*2 ; i = i + 2) {
			if (i==0) {
				buffer.append(":").append((String) params[i]);
			} else {
				buffer.append(",:").append((String) params[i]);
			}
		}
		return buffer.toString();
	}
	
	/**
	 * Generate the out params marker, starting on nbIn mark.
	 * Be careful because the out parameters actually start at nbIn*2, since
	 * the params array contain (name, value) for IN and (name, value) for OUT
	 * 
	 * @param nbIn
	 * @param params
	 * @return
	 */
	private static String generateOutNames(int nbIn, Object[] params) {
		StringBuffer buffer = new StringBuffer();
		for (int i=nbIn*2 ; i < params.length ; i = i + 2) {	
			if (i == nbIn*2) {
				buffer.append(":").append((String) params[i]);
			} else {
				buffer.append(",:").append((String) params[i]);
			}
		}
		return buffer.toString();
	}
}
