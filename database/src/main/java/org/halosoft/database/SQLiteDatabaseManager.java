package org.halosoft.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class SQLiteDatabaseManager {

    private final Connector connector;

    public SQLiteDatabaseManager(Connector c){

        connector=c;
    }

    /*
     * 1. creates a path by given path,
     * 2. creates sqlite database by given name(IMPORTANT: DELETES if such file exists)
     */
    public static SQLiteConnector createDatabase(String path, String dbName){

        SQLiteConnector retval=null;

        try {

            Files.createDirectories( Paths.get(path) );

            Files.deleteIfExists( Paths.get(path, dbName) );

            retval=new SQLiteConnector(
                Paths.get(path, dbName).toString() );

        } catch (IOException e) {
            
            System.err.println(String.format("[%s]: %s", SQLiteDatabaseManager.class.getName(), e.getMessage()));
            e.printStackTrace();
        }

        return retval;
    }

    /*
     * 1. creates a path by given path,
     * 2. creates sqlite database by given name(IMPORTANT: DELETES if such file exists),
     * 3. executes the query given from file which is generally the table definitions or inserts
     */
    public static SQLiteConnector createDatabaseFromFile(String path, String dbName, Path sqlFile) throws NoSuchFileException{
        
        SQLiteConnector retval=SQLiteDatabaseManager.createDatabase(path, dbName);

        retval.queryFromFile(sqlFile);


        return retval;   
    }

    /*
     * Tries to open the given file.
     * Throws exception if no such file or its path exists
     */
    public static SQLiteConnector openDatabase(String path, String dbName) throws NoSuchFileException{

        SQLiteConnector retval=null;

        Path dbRoot=Paths.get(path);

        if( !Files.exists( Paths.get(dbRoot.toString(), dbName) )){

            System.err.println(String.format("[%s]: %s -> %s", SQLiteDatabaseManager.class.getName(), "No such file exists: ",Paths.get(dbRoot.toString(), dbName).toString() ));

            throw new NoSuchFileException(Paths.get(dbRoot.toString(), dbName).toString());
        
        }

        retval=new SQLiteConnector(
            Paths.get(dbRoot.toString(), dbName).toString() );

        return retval;
    }
}
