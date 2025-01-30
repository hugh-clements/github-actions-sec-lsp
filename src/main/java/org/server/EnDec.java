package org.server;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.google.gson.Gson;
import org.server.Message.Requests;

public class EnDec {

    private final Gson gson = new Gson();

    public void encodeMessage(Requests message, int id) {

    }

    public void decodeMessage(byte[] bytes) {
        var bb = ByteBuffer.wrap(bytes);
    }
}
