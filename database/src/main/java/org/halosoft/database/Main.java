package org.halosoft.database;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("os.name")); // Operating System Name
        sb.append(System.getProperty("os.version")); // Operating System Version
        sb.append(System.getProperty("os.arch")); // Operating System Architecture
        sb.append(System.getProperty("user.name")); // User Name
        sb.append(System.currentTimeMillis()); // Current Time in Milliseconds

        // Generate a UUID based on the string
        String a= UUID.nameUUIDFromBytes(sb.toString().getBytes()).toString();

        System.out.println(sb);
        System.out.println(a);
    }
}
