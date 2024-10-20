package org.halosoft.database;

import java.lang.System.Logger;
import java.nio.file.Paths;

public class TalkDBProperties {
    public static final Logger logger=System.getLogger(TalkDBProperties.class.getName());

    public static final String DEFAULT_STORAGE_PATH=Paths.get(System.getProperty("java.io.tmpdir"),"halotalk").toString();
    public static final String DEFAULT_DB_FILE_EXTENSION=".sqlite";

    public static final String INSERT_INTO_MSGQUEUE="INSERT INTO MessageQueue(senderId, message, completed)"
                    +"VALUES('%s', '%s', '%s')";

    public static final String SELECT_FROM_MSGQUEUE="SElECT %s FROM MessageQueue";

    public static final String SELECT_FROM_SENDER="SELECT %s FROM Sender";

    public static final String insertIntoMessageQueue(int senderId, String message){

        return String.format(INSERT_INTO_MSGQUEUE, senderId, message, 1);
    }

    public static final String insertIntoMessageQueue(int senderId, String message, int completed){

        return String.format(INSERT_INTO_MSGQUEUE, senderId, message, completed & 0x01);
    }

    public static final String selectFromMessageQueue(String columns){

        return String.format(SELECT_FROM_MSGQUEUE, columns);
    }

    public static final String selectFromSender(String columns){

        return String.format(SELECT_FROM_SENDER, columns);
    }

    public static final String insertIntoSender(String ip){

        //do not add same again(although col 'ip' is unique, we are also preventing error by typing this)
        return String.format("INSERT INTO Sender(ip) SELECT '%s' "
        +"WHERE NOT EXISTS(SELECT id FROM Sender WHERE ip='%s')", ip, ip);
    }

    public static final String updateMessageQueue(String columnAndAssignment, String condition){

        return String.format("UPDATE MessageQueue SET %s WHERE %s", columnAndAssignment, condition);
    }

    /**
     *
     * @param limit the count of messages to be returned by database
     * @param offset the record skip step. Skipping will be made by multiplying offset with limit.
     * @return wanted query as a string
     *
     */
    public static final String getMessages(int limit, int offset){

        return String.format(SELECT_FROM_MSGQUEUE, "*").concat(" LIMIT "+limit+" OFFSET "+offset );
    }

    public static final String getMessagesDesc(int limit, int offset){

        return String.format(SELECT_FROM_MSGQUEUE, "*").concat(" ORDER BY id DESC LIMIT "+limit+" OFFSET "+offset );
    }

    public static final String getUnreadMessages(){

        return String.format(SELECT_FROM_MSGQUEUE, "*").concat(" WHERE completed=0 AND senderId=1");
    }

    public static final String getUnsentMessages(){

        return String.format(SELECT_FROM_MSGQUEUE, "*").concat(" WHERE completed=0 AND senderId=0");
    }

    public static final String nameTheDB(String name){

        return name+DEFAULT_DB_FILE_EXTENSION;
    }
}
