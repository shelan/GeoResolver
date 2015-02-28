

1) What is Geo Resolver?

 Using Geo Resolver you can determine the location based proximity between phone numbers  offline without relying on internet
 
2) Configuration and Quick Start

Place **conf ** folder as the same highlevel hierarchy as the jar and following is a sample property file (geo.properties)

    #username for database
    username=root
    
    #password for database
    password=
    
    #driver for database
    driver=com.mysql.jdbc.Driver

	#URL for database
    url=jdbc:mysql://127.0.0.1:3306/geocode_db
    
    #Tab seperated geo data
    geoDataPath=data/geodata.tsv

	#Column seperated country data
    countryDataPath=data/country.csv

 - You need to place your data files (geo data and country data) inside
   a folder called **data**

.

 - Start your mysql server and run the Tables.sql (available in
   resources folder) to create required databases and schema.

eg:

    Run java -jar georesolver-1.0-SNAPSHOT-jar-with-dependencies.jar --populate-data

 

 - In the schema there are two tables for country codes (small) and geo
   location (over 10 million records). If you need to alter or
   regenerate them separately you can use following options.

for Geo data

    java -jar georesolver-1.0-SNAPSHOT-jar-with-dependencies.jar --populate-data --geo

for Country data

    java -jar georesolver-1.0-SNAPSHOT-jar-with-dependencies.jar --populate-data --country

3) Running the application

without any restrictions
   

     java -jar georesolver-1.0-SNAPSHOT-jar-with-dependencies.jar +33975180000 +441732600000 +14159690000

only for the same country

    java -jar georesolver-1.0-SNAPSHOT-jar-with-dependencies.jar --same-country-only +12018840000 +15148710000 +14159690000


