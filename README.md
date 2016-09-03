# dbunit and hypersonic database demonstration

This is an example demonstration of using the [dbUnit] [] framework in conjuction with a [hypersonic][] (HSQL) database , for the purposes of integration testing by utilizing a fake database, loaded with a schema and data that you can generate by extracting from actual databases.

## Overview


## Walkthrough 


## Extracting, loading and using real data 

### Extracting

In the ```DatabaseSetup``` class there is the method ```getData()``` that will connect to a database, extract table data and write it out to an xml file. Examining the method an explanation on how to use it is provided below:

```java
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
```
In order to utilize this you will need to provide the ```server```,```username``` and ```password``` values to connect to the database of your choice.  
You will also need to update the ```table_name``` value to specifcy the table you want to extract data from. If you want to extract from multiple tables you will need to repeat the command ```partialDataSet.addTable("table_name");``` for every table you want to extract data from. Note - this command will extract all the data in a table.
The data will then be written to a file called ```dataset.xml``` in the ```src/test/resources``` folder.

The database table descriptor (DTD) for the entire database is extracted via the ```getDTD()``` method:

```java
private static void getDTD() throws FileNotFoundException, IOException, SQLException, ClassNotFoundException, DatabaseUnitException
	{
		Connection jdbcConnection = DriverManager.getConnection(
				"server",
				"username", 
				"password");
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

		FlatDtdDataSet.write(connection.createDataSet(), new FileOutputStream("src/test/resources/test_database.dtd"));
	}
```
Similarly as before you will need to provide the ```server```,```username``` and ```password``` values to connect to the database of your choice. The DTD will then be written to a file called ```test_database.dtd``` in the ```src/test/resources``` folder.

### Loading

Loading the extracted data into the database happens automatically in the ```DatabaseSetup``` class by the ```databaseSetup()``` and ```getDataSet()``` methods. However, **the tables to insert the data to need to be created in the hypersonic database manually beforehand in this codebase**. As with the example you will need to write ```CREATE MEMORY TABLE PUBLIC.<TABLE_NAME>(COLUMNS)``` statements under the ```SET SCHEMA PUBLIC``` declaration of the hypersonic database, which is the file located at ```src/test/resources/test_database.script```


[dbUnit]: http://dbunit.sourceforge.net/
[hypersonic]: http://hsqldb.org/

