package org.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOManager {

    static Logger logger = LogManager.getLogger(IOManager.class.getName());

    private static void getMessage() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        scanner.nextByte();
        try {
            var header = reader.readLine();
            int length = Integer.parseInt(header.split(": ")[1].replaceAll("\\s+",""));
            char[] content = {};
            reader.read(content,0,length);
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void sendMessage() {

    }

    public static void main(String[] args) {
        logger.info("test");
        logger.fatal("test2");
        logger.warn("test3");
    }
}
