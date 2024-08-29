package org.halosoft.database;

public class TalkDBProperties {

    public static final String DEFAULT_STORAGE_PATH="/tmp/halotalk";//System.getProperty("user.home");
    public static final String DEFAULT_DB_FILE_EXTENSION=".sqlite";

    public static final String INSERT_INTO_MSGQUEUE="INSERT INTO MessageQueue(senderId, message)"
                    +"VALUES('%s', '%s')";

    public static final String SELECT_FROM_MSGQUEUE="SElECT %s FROM MessageQueue";

    public static final String SELECT_FROM_SENDER="SELECT %s FROM Sender";

    public static final String insertIntoMessageQueue(int senderId, String message){

        return String.format(INSERT_INTO_MSGQUEUE, senderId, message);
    }

    public static final String selectFromMessageQueue(String columns){

        return String.format(SELECT_FROM_MSGQUEUE, columns);
    }

    public static final String selectFromSender(String columns){

        return String.format(SELECT_FROM_SENDER, columns);
    }

    public static final String updateMessageQueue(String columnAndAssignment, String condition){

        return String.format("UPDATE MessageQueue SET %s WHERE %s", columnAndAssignment, condition);
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
