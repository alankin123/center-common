package com.alankin.center.common.db.helper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 根据数据库表的元数据(metadata)创建Table对象
 * 
 * <pre>
 * getTable(sqlName) : 根据数据库表名,得到table对象
 * getAllTable() : 搜索数据库的所有表,并得到table对象列表
 * </pre>
 * 
 * @author lansongtao
 * @Date 2015年8月15日
 * @since 1.0
 */
@SuppressWarnings({ "unused", "rawtypes", "unchecked", "deprecation" })
public class TableFactory {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	DataSource dataSource;

	Connection connection;

	private DbHelper dbHelper = new DbHelper();
	private static TableFactory instance = null;

	public String getCatalog() {
		return null;
	}

	public String getSchema() {
		return null;
	}

	private TableFactory() {
	}

	public synchronized static TableFactory getInstance(DataSource dataSource) {
		if (instance == null) {
			instance = new TableFactory();
			instance.dataSource = dataSource;
		}
		return instance;
	}

	public Connection getConnection() {
		return getConnectionForCenter();
	}

	// public Connection getConnectionForCenter() {
	// if (connection == null) {
	// Properties props = new Properties();
	// try {
	// Class.forName(PropertiesDB.getInstans().getValue("jdbc.driver"));
	// String url = PropertiesDB.getInstans().getValue("jdbc.databaseurl");
	//
	// props.setProperty("user", PropertiesDB.getInstans().getValue("jdbc.databaseurl"));
	// props.setProperty("password", PropertiesDB.getInstans().getValue("jdbc.password"));
	// props.setProperty("remarks", "true"); // 设置可以获取remarks信息
	// props.setProperty("useInformationSchema", "true");// 设置可以获取tables remarks信息
	// connection = DriverManager.getConnection(url, props);
	//
	// } catch (SQLException | ClassNotFoundException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// return connection;
	// }

	public Connection getConnectionForCenter() {
		if (connection == null) {
			try {
				connection = dataSource.getConnection();
			} catch (SQLException e) {
			}
		}

		return connection;

	}

	public List<Table> getAllTables() throws SQLException {
		Connection conn = getConnectionForCenter();
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet rs = dbMetaData.getTables(getCatalog(), getSchema(), null, null);
		List<Table> tables = new ArrayList<Table>();

		rs.last();
		int count = rs.getRow();
		rs.beforeFirst();

		long start = System.currentTimeMillis();
		boolean flag = false;
		logger.info("表总数：{}", count);
		while (rs.next()) {
			tables.add(createTable(conn, rs));
			if (System.currentTimeMillis() - start > 5000 || (flag && rs.getRow() == count)) {
				flag = true;
				start = System.currentTimeMillis();
				logger.info("加载进度:{}%", rs.getRow() * 100 / count);
			}
		}

		connection.close();
		return tables;
	}

	private Table createTable(Connection conn, ResultSet rs) throws SQLException {
		String realTableName = null;
		try {
			ResultSetMetaData rsMetaData = rs.getMetaData();
			String schemaName = rs.getString("TABLE_SCHEM") == null ? "" : rs.getString("TABLE_SCHEM");
			realTableName = rs.getString("TABLE_NAME");
			String tableType = rs.getString("TABLE_TYPE");
			String remarks = rs.getString("REMARKS");
			if (remarks == null && dbHelper.isOracleDataBase()) {
				remarks = getOracleTableComments(realTableName);
			}

			Table table = new Table();
			table.setSqlName(realTableName);
			table.setRemarks(remarks);

			retriveTableColumns(table);

			// table.initExportedKeys(conn.getMetaData());
			// table.initImportedKeys(conn.getMetaData());
			// BeanHelper.copyProperties(table, TableOverrideValuesProvider.getTableOverrideValues(table.getSqlName()));
			return table;
		} catch (SQLException e) {
			throw new RuntimeException("create table object error,tableName:" + realTableName, e);
		}
	}

	private String getDatabaseStructureInfo() {
		ResultSet schemaRs = null;
		ResultSet catalogRs = null;
		String nl = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer(nl);
		// Let's give the user some feedback. The exception
		// is probably related to incorrect schema configuration.
		sb.append("Configured schema:").append(getSchema()).append(nl);
		sb.append("Configured catalog:").append(getCatalog()).append(nl);

		try {
			schemaRs = getMetaData().getSchemas();
			sb.append("Available schemas:").append(nl);
			while (schemaRs.next()) {
				sb.append("  ").append(schemaRs.getString("TABLE_SCHEM")).append(nl);
			}
		} catch (SQLException e2) {
			sb.append("  ?? Couldn't get schemas ??").append(nl);
		} finally {
			dbHelper.close(schemaRs, null);
		}

		try {
			catalogRs = getMetaData().getCatalogs();
			sb.append("Available catalogs:").append(nl);
			while (catalogRs.next()) {
				sb.append("  ").append(catalogRs.getString("TABLE_CAT")).append(nl);
			}
		} catch (SQLException e2) {
			sb.append("  ?? Couldn't get catalogs ??").append(nl);
		} finally {
			dbHelper.close(catalogRs, null);
		}
		return sb.toString();
	}

	private DatabaseMetaData getMetaData() throws SQLException {
		return getConnection().getMetaData();
	}

	private void retriveTableColumns(Table table) throws SQLException {
		long start = System.currentTimeMillis();

		List primaryKeys = getTablePrimaryKeys(table);
		table.setPrimaryKeyColumns(primaryKeys);
		// table.setPrimaryKeyName(primaryKeys.get(0) + "");

		// get the indices and unique columns
		List indices = new LinkedList();
		// maps index names to a list of columns in the index
		Map uniqueIndices = new HashMap();
		// maps column names to the index name.
		Map uniqueColumns = new HashMap();
		ResultSet indexRs = null;

		// try {
		//
		// if (table.getOwnerSynonymName() != null) {
		// indexRs = getMetaData().getIndexInfo(getCatalog(), table.getOwnerSynonymName(), table.getSqlName(), false,
		// true);
		// } else {
		// logger.info("=====33333333333======" + (System.currentTimeMillis() - start));
		// indexRs = getMetaData().getIndexInfo(getCatalog(), getSchema(), table.getSqlName(), false, true);
		// logger.info("=====4444444444======" + (System.currentTimeMillis() - start));
		// }
		// while (indexRs.next()) {
		// String columnName = indexRs.getString("COLUMN_NAME");
		// if (columnName != null) {
		// indices.add(columnName);
		// }
		//
		// // now look for unique columns
		// String indexName = indexRs.getString("INDEX_NAME");
		// boolean nonUnique = indexRs.getBoolean("NON_UNIQUE");
		//
		// if (!nonUnique && columnName != null && indexName != null) {
		// List l = (List) uniqueColumns.get(indexName);
		// if (l == null) {
		// l = new ArrayList();
		// uniqueColumns.put(indexName, l);
		// }
		// l.add(columnName);
		// uniqueIndices.put(columnName, indexName);
		// }
		// }
		// } catch (Throwable t) {
		// // Bug #604761 Oracle getIndexInfo() needs major grants
		// // http://sourceforge.net/tracker/index.php?func=detail&aid=604761&group_id=36044&atid=415990
		// } finally {
		// dbHelper.close(indexRs, null);
		// }

		List columns = getTableColumns(table, primaryKeys, indices, uniqueIndices, uniqueColumns);

		for (Iterator i = columns.iterator(); i.hasNext();) {
			Column column = (Column) i.next();
			table.addColumn(column);
		}

		// In case none of the columns were primary keys, issue a warning.
		if (primaryKeys.size() == 0) {
		}
	}

	private List getTableColumns(Table table, List primaryKeys, List indices, Map uniqueIndices, Map uniqueColumns) throws SQLException {
		// get the columns
		List columns = new LinkedList();
		ResultSet columnRs = getColumnsResultSet(table);

		while (columnRs.next()) {
			int sqlType = columnRs.getInt("DATA_TYPE");
			String sqlTypeName = columnRs.getString("TYPE_NAME");
			String columnName = columnRs.getString("COLUMN_NAME");
			String columnDefaultValue = columnRs.getString("COLUMN_DEF");

			String remarks = columnRs.getString("REMARKS");
			if (remarks == null && dbHelper.isOracleDataBase()) {
				remarks = getOracleColumnComments(table.getSqlName(), columnName);
			}

			// if columnNoNulls or columnNullableUnknown assume "not nullable"
			boolean isNullable = (DatabaseMetaData.columnNullable == columnRs.getInt("NULLABLE"));
			int size = columnRs.getInt("COLUMN_SIZE");
			int decimalDigits = columnRs.getInt("DECIMAL_DIGITS");

			boolean isPk = primaryKeys.contains(columnName);
			boolean isIndexed = indices.contains(columnName);
			String uniqueIndex = (String) uniqueIndices.get(columnName);
			List columnsInUniqueIndex = null;
			if (uniqueIndex != null) {
				columnsInUniqueIndex = (List) uniqueColumns.get(uniqueIndex);
			}

			boolean isUnique = columnsInUniqueIndex != null && columnsInUniqueIndex.size() == 1;

			Column column = new Column(table, sqlType, sqlTypeName, columnName, size, decimalDigits, isPk, isNullable, isIndexed, isUnique, columnDefaultValue,
			        remarks);
			// BeanHelper.copyProperties(column, TableOverrideValuesProvider.getColumnOverrideValues(table, column));
			columns.add(column);
		}
		columnRs.close();
		return columns;
	}

	private ResultSet getColumnsResultSet(Table table) throws SQLException {
		ResultSet columnRs = null;
		if (table.getOwnerSynonymName() != null) {
			columnRs = getMetaData().getColumns(getCatalog(), table.getOwnerSynonymName(), table.getSqlName(), null);
		} else {
			columnRs = getMetaData().getColumns(getCatalog(), getSchema(), table.getSqlName(), null);
		}
		return columnRs;
	}

	private List<String> getTablePrimaryKeys(Table table) throws SQLException {
		// get the primary keys
		List primaryKeys = new LinkedList();
		ResultSet primaryKeyRs = null;
		if (table.getOwnerSynonymName() != null) {
			primaryKeyRs = getMetaData().getPrimaryKeys(getCatalog(), table.getOwnerSynonymName(), table.getSqlName());
		} else {
			primaryKeyRs = getMetaData().getPrimaryKeys(getCatalog(), getSchema(), table.getSqlName());
		}
		while (primaryKeyRs.next()) {
			String columnName = primaryKeyRs.getString("COLUMN_NAME");
			primaryKeys.add(columnName);
		}
		primaryKeyRs.close();
		return primaryKeys;
	}

	private String getOracleTableComments(String table) {
		String sql = "SELECT comments FROM user_tab_comments WHERE table_name='" + table + "'";
		return dbHelper.queryForString(sql);
	}

	private String getOracleColumnComments(String table, String column) {
		String sql = "SELECT comments FROM user_col_comments WHERE table_name='" + table + "' AND column_name = '" + column + "'";
		return dbHelper.queryForString(sql);
	}

	// /** 得到表的自定义配置信息 */
	// public static class TableOverrideValuesProvider {
	//
	// private static Map getTableOverrideValues(String tableSqlName) {
	// NodeData nd = getTableConfigXmlNodeData(tableSqlName);
	// if (nd == null) {
	// return new HashMap();
	// }
	// return nd == null ? new HashMap() : nd.attributes;
	// }
	//
	// private static Map getColumnOverrideValues(Table table, Column column) {
	// NodeData root = getTableConfigXmlNodeData(table.getSqlName());
	// if (root != null) {
	// for (NodeData item : root.childs) {
	// if (item.nodeName.equals("column")) {
	// if (column.getSqlName().equalsIgnoreCase(item.attributes.get("sqlName"))) {
	// return item.attributes;
	// }
	// }
	// }
	// }
	// return new HashMap();
	// }
	//
	// private static NodeData getTableConfigXmlNodeData(String tableSqlName) {
	// NodeData nd = getTableConfigXmlNodeData0(tableSqlName);
	// if (nd == null) {
	// nd = getTableConfigXmlNodeData0(tableSqlName.toLowerCase());
	// if (nd == null) {
	// nd = getTableConfigXmlNodeData0(tableSqlName.toUpperCase());
	// }
	// }
	// return nd;
	// }
	//
	// private static NodeData getTableConfigXmlNodeData0(String tableSqlName) {
	// try {
	// File file = FileHelper.getFileByClassLoader("generator_config/table/" + tableSqlName + ".xml");
	// GLogger.trace("getTableConfigXml() load nodeData by tableSqlName:" + tableSqlName + ".xml");
	// return new XMLHelper().parseXML(file);
	// } catch (Exception e) {// ignore
	// GLogger.trace("not found config xml for table:" + tableSqlName + ", exception:" + e);
	// return null;
	// }
	// }
	// }

	class DbHelper {
		public void close(ResultSet rs, PreparedStatement ps, Statement... statements) {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				for (Statement s : statements) {
					s.close();
				}
			} catch (Exception e) {
			}
		}

		public boolean isOracleDataBase() {
			try {
				return DatabaseMetaDataUtils.isOracleDataBase(getMetaData());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		public String queryForString(String sql) {
			Statement s = null;
			ResultSet rs = null;
			try {
				s = getConnection().createStatement();
				rs = s.executeQuery(sql);
				if (rs.next()) {
					return rs.getString(1);
				}
				return null;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			} finally {
				close(rs, null, s);
			}
		}
	}

	public static class DatabaseMetaDataUtils {
		public static boolean isOracleDataBase(DatabaseMetaData metadata) {
			try {
				boolean ret = false;
				ret = (metadata.getDatabaseProductName().toLowerCase().indexOf("oracle") != -1);
				return ret;
			} catch (SQLException s) {
				return false;
				// throw new RuntimeException(s);
			}
		}

		public static boolean isHsqlDataBase(DatabaseMetaData metadata) {
			try {
				boolean ret = false;
				ret = (metadata.getDatabaseProductName().toLowerCase().indexOf("hsql") != -1);
				return ret;
			} catch (SQLException s) {
				return false;
				// throw new RuntimeException(s);
			}
		}
	}
}
