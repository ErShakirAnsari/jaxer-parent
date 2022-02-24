
package in.jaxer.sdbms;

import in.jaxer.core.utilities.JUtilities;
import in.jaxer.core.utilities.JValidator;
import in.jaxer.sdbms.exceptions.JaxerSDBMSException;
import in.jaxer.sdbms.utils.NamedStatementUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Shakir Ansari
 */
@Log4j2
@ToString
public class NamedStatement implements AutoCloseable
{

	private PreparedStatement preparedStatement;

	private final Connection connection;

	private Map<String, List<Integer>> indexMap;

	private Map<String, Parameter> valueMap;

	private List<Parameter> parameters;

	private final String query;

	private String paramQuery;

	private String parsedQuery;

	private int autoGeneratedKeys;

	private final boolean isAutoGeneratedKeys;

	/**
	 *
	 * @param connection Database connection
	 * @param query      an SQL statement that may contain one or more '?' IN
	 */
	public NamedStatement(Connection connection, String query)
	{
		this.indexMap = new HashMap();
		this.query = query;
		this.connection = connection;
		this.isAutoGeneratedKeys = false;
	}

	/**
	 *
	 * @param connection        Database connection
	 * @param query             an SQL statement that may contain one or more '?' IN
	 *                          parameter placeholders
	 * @param autoGeneratedKeys a flag indicating whether auto-generated keys
	 *                          should be returned; one of
	 *                          <code>Statement.RETURN_GENERATED_KEYS</code> or
	 *                          <code>Statement.NO_GENERATED_KEYS</code>@throws SQLException
	 *
	 */
	public NamedStatement(Connection connection, String query, int autoGeneratedKeys)
	{
		this.indexMap = new HashMap();
		this.query = query;
		this.connection = connection;
		this.autoGeneratedKeys = autoGeneratedKeys;
		this.isAutoGeneratedKeys = true;
	}

	public boolean execute() throws SQLException
	{
		setValueMap();
		log.debug("execute: {}", preparedStatement.toString());
		return preparedStatement.execute();
	}

	public ResultSet getGeneratedKeys() throws SQLException
	{
		log.debug("getGeneratedKeys: {}", preparedStatement.toString());
		return preparedStatement.getGeneratedKeys();
	}

	public ResultSet executeQuery() throws SQLException
	{
		setValueMap();
		log.debug("executeQuery: {}", preparedStatement.toString());
		return preparedStatement.executeQuery();
	}

	public int executeUpdate() throws SQLException
	{
		setValueMap();
		log.debug("executeUpdate: {}", preparedStatement.toString());
		return preparedStatement.executeUpdate();
	}

	public long executeLargeUpdate() throws SQLException
	{
		setValueMap();
		log.debug("executeLargeUpdate: {}", preparedStatement.toString());
		return preparedStatement.executeLargeUpdate();
	}

	public void addBatch() throws SQLException
	{
		log.debug("addBatch: {}", preparedStatement.toString());
		preparedStatement.addBatch();
	}

	public int[] executeBatch() throws SQLException
	{
		setValueMap();
		log.debug("executeBatch: {}", preparedStatement.toString());
		return preparedStatement.executeBatch();
	}

	private void setValueMap() throws SQLException
	{
		log.debug("setValueMap");

		if (JValidator.isEmpty(valueMap))
		{
			valueMap = new HashMap<>();
		}

		if (JValidator.isNotEmpty(parameters))
		{
			paramQuery = NamedStatementUtils.setParameterListName(query, parameters);
			NamedStatementUtils.setParameterValue(valueMap, parameters);
		} else
		{
			paramQuery = query;
		}

		log.debug("\nnamedStatementParamList: {}\nvalueMap: {}", parameters, valueMap);

		// parsing query
		if (JValidator.isNotEmpty(valueMap))
		{
			indexMap = new HashMap<>();
			parsedQuery = NamedStatementUtils.queryParser(paramQuery, indexMap);
		} else
		{
			parsedQuery = paramQuery;
		}

		log.debug("\nquery: {}\nparamQuery: {}\nparsedQuery: {}\nindexMap: {}", query, paramQuery, parsedQuery, indexMap);

		// set values in PreparedStatement
		if (isAutoGeneratedKeys)
		{
			this.preparedStatement = this.connection.prepareStatement(parsedQuery, autoGeneratedKeys);
		} else
		{
			this.preparedStatement = this.connection.prepareStatement(parsedQuery);
		}

		Iterator<Map.Entry<String, Parameter>> iterator = valueMap.entrySet().iterator();
		while (iterator.hasNext())
		{
			Map.Entry<String, Parameter> entry = iterator.next();

			String name = entry.getKey();
			Parameter param = entry.getValue();

			List<Integer> indexes = indexMap.get(name);
			if (indexes == null)
			{
				throw new JaxerSDBMSException("Parameter [" + name + "] not found");
			}

			for (int index : indexes)
			{
				if (param.getValue() == null)
				{
					preparedStatement.setNull(index, java.sql.Types.JAVA_OBJECT);
				} else
				{
					preparedStatement.setObject(index, param.getValue());
				}
			}
		}

	}

	public void setParameter(String paramName, Object paramValue)
	{
		JValidator.requireNotEmpty(paramName, new JaxerSDBMSException("Parameter name cannot be empty"));

		if (paramValue == null)
		{
			log.debug("Parameter [{}] value is null", paramName);
		}

		if (JValidator.isEmpty(parameters))
		{
			parameters = new ArrayList<>();
		}

		parameters.add(new Parameter(paramName, paramValue));
	}

	public void setParameterList(String paramName, Collection collection)
	{
		JValidator.requireNotEmpty(paramName, new JaxerSDBMSException("Parameter name cannot be empty"));
		JValidator.requireNotEmpty(collection, new JaxerSDBMSException("Parameter List cannot be empty"));

		if (JValidator.isEmpty(parameters))
		{
			parameters = new ArrayList<>();
		}

		parameters.add(new Parameter(paramName, collection));
	}

	@Override
	public void close() throws SQLException
	{
		JUtilities.close(preparedStatement);
	}
}
