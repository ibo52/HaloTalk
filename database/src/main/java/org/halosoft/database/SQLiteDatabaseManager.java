package org.halosoft.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.StandardOpenOption;

public class SQLiteDatabaseManager {

    public static enum OpenMode{

        CREATE(1),                  //creates file if not exists
        READ( CREATE.getNum()<<1 ),     //to open database file for query operations
        OVERWRITE( READ.getNum()<<1 );  //overwrites if a file exists, creates if not exists

        private final int numval;

        OpenMode(int val){
            this.numval=val;
        }

        public int getNum(){
            return this.numval;
        }
    }

    /*private final Connector connector;

    public SQLiteDatabaseManager(Connector c){

        connector=c;
    }*/

    /*
     * 1. creates a path by given path,
     * 2. creates sqlite database by given name(IMPORTANT: DELETES if such file exists)
     */
    public static SQLiteConnector createDatabase(String path, String dbName){

        SQLiteConnector c=null;
        try {
            c= SQLiteDatabaseManager.openDatabase(path, dbName, OpenMode.OVERWRITE);

        } catch (NoSuchFileException e) {
        }

        return c;
    }

    /*
     * 1. creates a path by given path,
     * 2. creates sqlite database by given name(IMPORTANT: DELETES if such file exists),
     * 3. executes the query given from file which is generally the table definitions or inserts
     */
    public static SQLiteConnector createDatabaseFromFile(String path, String dbName, Path sqlFile) throws NoSuchFileException, IOException{

        //execute database table queries on temporary, then move temporary to origin
        //to prevent other processes to access db before tables generated
        Path tempDB=Files.createTempFile("temporary", TalkDBProperties.DEFAULT_DB_FILE_EXTENSION);
        Path permanentDB=Paths.get(path, dbName);

        Files.deleteIfExists(permanentDB);
        Files.createDirectories(permanentDB.getParent());

        //System.out.println("permanent db created under: "+tempDB.toString());

        SQLiteConnector retval=SQLiteDatabaseManager.openDatabase(tempDB);//.createDatabase(path, dbName);

        retval.queryFromFile(sqlFile);

        try {
            retval.getConnection().close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        retval=null;

        Files.move(tempDB, permanentDB);

        Files.deleteIfExists(tempDB);

        retval=SQLiteDatabaseManager.openDatabase(permanentDB);

        return retval;
    }

    public static SQLiteConnector createDatabaseFromFile(Path dbPath,
            InputStream sqlFileAsStream)
            throws NoSuchFileException, IOException, FileAlreadyExistsException{

        //execute database table queries on temporary, then move temporary to origin
        //to prevent other processes to access db before tables generated
        Path tempDB=Files.createTempFile("temporary", TalkDBProperties.DEFAULT_DB_FILE_EXTENSION);
        Path permanentDB=dbPath;

        Files.deleteIfExists(permanentDB);
        Files.createDirectories(permanentDB.getParent());

        //System.out.println("permanent db created under: "+tempDB.toString());

        SQLiteConnector retval=SQLiteDatabaseManager.openDatabase(tempDB);//.createDatabase(path, dbName);

        retval.queryFromFile(sqlFileAsStream);

        try {
            retval.getConnection().close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        retval=null;

        Files.move(tempDB, permanentDB);

        Files.deleteIfExists(tempDB);

        retval=SQLiteDatabaseManager.openDatabase(permanentDB);

        return retval;
    }

    public static SQLiteConnector createDatabaseFromFile(String path, String dbName,
            InputStream sqlFileAsStream)
            throws NoSuchFileException, IOException, FileAlreadyExistsException {

        return SQLiteDatabaseManager.createDatabaseFromFile(Paths.get(path, dbName), sqlFileAsStream);
    }
    /**
     * Tries to open the given file if exists.
     * Throws exception if no such file or its path exists
     */
    public static SQLiteConnector openDatabase(String path, String dbName) throws NoSuchFileException{

        return SQLiteDatabaseManager.openDatabase(path, dbName, OpenMode.READ);
    }

    /**
     * Tries to open the given file if exists.
     * Throws exception if no such file or its path exists
     */
    public static SQLiteConnector openDatabase(Path dbfilepath) throws NoSuchFileException{

        return SQLiteDatabaseManager.openDatabase(dbfilepath.getParent().toString(), dbfilepath.getFileName().toString(), OpenMode.READ);
    }

    /**
     * Tries to open the given file either create or read modes;
     * Throws exception on READ mode if no such file or its path exists
     */
    public static SQLiteConnector openDatabase(String path, String dbName, OpenMode openMode) throws NoSuchFileException{

        SQLiteConnector retval=null;

        Path dbfile=Paths.get(path, dbName);


        switch (openMode) {

            case CREATE:
                try {

                    Files.createDirectories( Paths.get(path) );

                    retval=new SQLiteConnector(
                        dbfile.toString() );

                } catch (IOException e) {

                    System.err.println(String.format("[%s]: %s", SQLiteDatabaseManager.class.getName(), e.getMessage()));
                    e.printStackTrace();

                }
                break;

            case OVERWRITE:
                try {

                    Files.createDirectories( Paths.get(path) );
                    //truncate file to 0 byte:
                    //former invoke was Files.deleteIfExists(), which throws java.nio.file.FileSystemException on Windows
                    Files.write(dbfile, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);

                    retval=new SQLiteConnector(
                        dbfile.toString() );

                } catch (IOException e) {

                    System.err.println(String.format("[%s]: %s", SQLiteDatabaseManager.class.getName(), e.getMessage()));
                    e.printStackTrace();

                }
                break;

            case READ:
                if( !Files.exists(dbfile) ){
                    System.err.println(String.format("[%s]: %s -> %s", SQLiteDatabaseManager.class.getName(), "No such file exists: ", dbfile.toString() ));

                    throw new NoSuchFileException( dbfile.toString() );

                }else{
                    retval=new SQLiteConnector(
                        dbfile.toString() );
                }
                break;

            default:
                break;
        }

        return retval;
    }

    /**
     * Tries to open the given file.
     * Throws exception if no such file or its path exists
     */
    public static boolean databaseExists(Path path){

        return Files.exists(path);
    }
}
