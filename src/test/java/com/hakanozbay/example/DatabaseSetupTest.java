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

public class DatabaseSetupTest {

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
		
		assertEquals(table.getRowCount(),4);
		
		System.out.println(getRow(0, table));
		System.out.println(getRow(1, table));
		System.out.println(getRow(2, table));
		System.out.println(getRow(3, table));
		
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
