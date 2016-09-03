# dbunit and hypersonic database demonstration

This is an example demonstration of using the [dbUnit] [] framework in conjuction with a [hypersonic][] (HSQL) database, for the purposes of integration and workflow testing by utilizing a fake database, loaded with a schema and data that you can generate by extracting from real databases.

## Overview


## Walkthrough of demonstration
For the purposes of demonstrating a working example, I have predefined `dataset.xml` and `test_database.dtd` files. Therefore the database setup code will not perform data extraction, it will only perform data loading.

### Schema and setup of demonstration
The demonstration involves a database table called `Person` with 4 columns: `firstname`, `lastname`, `gender`, `age`. 

The database table descriptor (DTD) in `test_database.dtd` is defined as:
```dtd
<!ELEMENT dataset (
Person*)>
 
 
<!ELEMENT Person EMPTY>
<!ATTLIST Person
    firstname CDATA #REQUIRED
    lastname CDATA #REQUIRED
    gender CDATA #REQUIRED
    age CDATA #REQUIRED
> 
```

The example dataset to be loaded for this table as defined in `dataset.xml`:

```xml
<dataset>
<Person firstname="John" lastname="Smith" gender="M" age="35"/>
<Person firstname="Samantha" lastname="Johnson" gender="F" age="27"/>
<Person firstname="Thomas" lastname="Dempsey" gender="M" age="63"/>
<Person firstname="Jane" lastname="Peters" gender="F" age="41"/>
</dataset>
```

The database table is declared in the `test_database.script`:

```sql
CREATE MEMORY TABLE PUBLIC.PERSON(FIRSTNAME VARCHAR(255) NOT NULL,LASTNAME VARCHAR(255) NOT NULL,GENDER VARCHAR(1) NOT NULL,AGE INTEGER NOT NULL)
```
### Running the demonstration
Run the `DatbaseSetupTest` as a JUnit test. The sequence of events in this workflow:
- load the dataset.xml and test_database.dtd into the test_database.script before the test.
- the test will connect to the database to extract the `Person` table and verify the number of rows in the table
- The test will then retrieve and log the data of every row

There will be log messages produced throughout the execution of the entire end to end workflow to illustrate the actions of dbUnit and HSQL. 
Within the log messages, the data for each row of the table will be visible and the generated log messages for these will be viewable as:

```
[main] INFO com.hakanozbay.example.DatabaseSetupTest - Data in row 1: {GENDER=M, FIRSTNAME=John, LASTNAME=Smith, AGE=35}
[main] INFO com.hakanozbay.example.DatabaseSetupTest - Data in row 2: {GENDER=F, FIRSTNAME=Samantha, LASTNAME=Johnson, AGE=27}
[main] INFO com.hakanozbay.example.DatabaseSetupTest - Data in row 3: {GENDER=M, FIRSTNAME=Thomas, LASTNAME=Dempsey, AGE=63}
[main] INFO com.hakanozbay.example.DatabaseSetupTest - Data in row 4: {GENDER=F, FIRSTNAME=Jane, LASTNAME=Peters, AGE=41}
```

## Extracting, loading and using real data 

### Extracting

The data extracting part of the datbase setup will only be executed if there is no dataset.xml or test_database.dtd existing in the `src/test/resources` folder. Ensure this before proceeding with the guidelines below. 

In the `DatabaseSetup` class there is the method `getData()` that will connect to a database, extract table data and write it out to an xml file. Examining the method an explanation on how to use it is provided below:

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
In order to utilize this you will need to provide the `server`,`username` and `password` values to connect to the database of your choice.  
You will also need to update the `table_name` value to specifcy the table you want to extract data from. If you want to extract from multiple tables you will need to repeat the command `partialDataSet.addTable("table_name");` for every table you want to extract data from. Note - this command will extract all the data in a table.
The data will then be written to a file called `dataset.xml` in the `src/test/resources` folder.

The database table descriptor (DTD) for the entire database is extracted via the `getDTD()` method:

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
Similarly as before you will need to provide the `server`,`username` and `password` values to connect to the database of your choice. The DTD will then be written to a file called `test_database.dtd` in the `src/test/resources` folder.

### Loading

Loading the extracted data into the database happens automatically in the `DatabaseSetup` class by the `databaseSetup()` and `getDataSet()` methods. However, **the tables to insert the data to need to be created in the hypersonic database manually beforehand in this codebase**. As with the example you will need to write `CREATE MEMORY TABLE PUBLIC.<TABLE_NAME>(COLUMNS)` statements under the `SET SCHEMA PUBLIC` declaration of the hypersonic database, which is the file located at `src/test/resources/test_database.script`


[dbUnit]: http://dbunit.sourceforge.net/
[hypersonic]: http://hsqldb.org/

