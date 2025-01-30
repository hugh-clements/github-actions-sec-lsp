package org.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class IOManager {


    private static void getMessage() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            var header = reader.readLine();
            int length = Integer.parseInt(header.split(": ")[1].replaceAll("\\s+",""));

        } catch (Exception e) {

        }


    }


    private void sendMessage() {

    }
}
