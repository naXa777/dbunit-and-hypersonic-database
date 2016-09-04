package com.hakanozbay.example;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.ITable;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseSetupTest {

	private static final Logger log = LoggerFactory.getLogger(DatabaseSetupTest.class);
	
	private IDatabaseConnection connection;
	
	@Before
	public void setup() throws Exception
	{
		DatabaseSetup.databaseSetup();
		
		Connection jdbcConnection = DriverManager.getConnection(
				"jdbc:hsqldb:file:src/test/resources/test_database;ifexists=false;shutdown=true",
				"SA",
				"");
		
		connection = new DatabaseConnection(jdbcConnection);
	}
	
	@Test
	public void testDatabaseSetup() throws Exception 
	{
		
		ITable table = connection.createDataSet().getTable("Person");
		
		assertEquals(4,table.getRowCount());
		
		log.info("Data in row {}: {}",1,getRow(0, table).toString());
		log.info("Data in row {}: {}",2,getRow(1, table).toString());
		log.info("Data in row {}: {}",3,getRow(2, table).toString());
		log.info("Data in row {}: {}",4,getRow(3, table).toString());
		
	}

	private Map<String, Object> getRow(int rowNumber, ITable table) throws Exception
	{
		Map<String, Object> row = new HashMap<String, Object>(1);
		
		Column[] columns = table.getTableMetaData().getColumns();
		
		for (Column column : columns)
		row.put(column.getColumnName(), table.getValue(rowNumber, column.getColumnName()));
		
		return row;
	}
}
