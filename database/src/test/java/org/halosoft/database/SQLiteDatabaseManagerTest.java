package org.halosoft.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.io.IOException;

import org.junit.Test;

public class SQLiteDatabaseManagerTest {

    //use temporary dir of OS for tests
    private final String path=System.getProperty("java.io.tmpdir");
    private final String dbname="test.db";
    private final String sqlFile="/tables.sql";
    @Test
    public void testCreateDatabase() {

        Connector c=SQLiteDatabaseManager.createDatabase(path, dbname);

        assertNotNull(c);
        assertTrue(Paths.get(path, dbname).toFile().exists() );

    }

    @Test
    public void testCreateDatabaseFromFile() {

        try {

            assertTrue(Paths.get(
                SQLiteDatabaseManager.class.getResource(sqlFile).toURI()
            ).toFile().exists());

            Connector c=SQLiteDatabaseManager.createDatabaseFromFile(path, dbname, Paths.get(
                SQLiteDatabaseManager.class.getResource(sqlFile).toURI()
            ));

            assertNotNull(c);
            assertTrue(Paths.get(path, dbname).toFile().exists() );

            QueryResultSet r=c.query(TalkDBProperties.selectFromSender("*"));

            assertNotNull(r);
            assertEquals(r.getNextRecord("ip"), "localhost");
            

            r=c.query(TalkDBProperties.selectFromMessageQueue("*"));
            
            assertNotNull(r);


        } catch (NoSuchFileException e) {

            e.printStackTrace();
        } catch (URISyntaxException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    @Test
    public void testDatabaseExists() {

        Connector c=SQLiteDatabaseManager.createDatabase(path, dbname);

        assertNotNull(c);

        assertTrue(SQLiteDatabaseManager.databaseExists(Paths.get(path,dbname)));
        assertFalse(SQLiteDatabaseManager.databaseExists(Paths.get(path,"nonexistend.db")));

    }

    @Test
    public void testOpenDatabase() {

    }
}
