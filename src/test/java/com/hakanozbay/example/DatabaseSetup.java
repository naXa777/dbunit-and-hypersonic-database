package com.hakanozbay.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

public class DatabaseSetup {

	static IDatabaseTester databaseTester;

	public static void databaseSetup() throws Exception
	{
		if (databaseTester == null)
			databaseConnectionCreation();
		
		databaseTester.getConnection().getConnection().prepareStatement("TRUNCATE SCHEMA PUBLIC AND COMMIT").execute();
		DatabaseOperation.CLEAN_INSERT.execute(databaseTester.getConnection(), getDataSet());
	}

	private static void databaseConnectionCreation() throws Exception
	{
		databaseTester = new JdbcDatabaseTester
				("org.hsqldb.jdbcDriver",
						"jdbc:hsqldb:file:src/test/resources/test_database;ifexists=false;shutdown=true",
						"SA",
						"",
						"PUBLIC");
		
	}

	private static IDataSet getDataSet() throws Exception {
		if (!new File("src/test/resources/dataset.xml").exists())
			getData();

		if (!new File("src/test/resources/test_database.dtd").exists())
			getDTD();

		return new FlatXmlDataSetBuilder().setDtdMetadata(true)
				.setMetaDataSetFromDtd(new FileInputStream("src/test/resources/test_database.dtd")).build(new FileInputStream("src/test/resources/dataset.xml"));
	}


	private static void getDTD() throws FileNotFoundException, IOException, SQLException, ClassNotFoundException, DatabaseUnitException
	{
		Connection jdbcConnection = DriverManager.getConnection(
				"server",
				"username", 
				"password");
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

		FlatDtdDataSet.write(connection.createDataSet(), new FileOutputStream("src/test/resources/test_database.dtd"));
	}


	private static void getData() throws FileNotFoundException, IOException, SQLException, ClassNotFoundException, DatabaseUnitException
	{
		Connection jdbcConnection = DriverManager.getConnection(
				"server",
				"username",
				"password");
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

		QueryDataSet partialDataSet = new QueryDataSet(connection);
		partialDataSet.addTable("table_name");
		FlatXmlDataSet.write(partialDataSet, new FileOutputStream("src/test/resources/dataset.xml"));
	}

}
