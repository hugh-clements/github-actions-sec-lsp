package org.server.Message;

public sealed interface Requests {

    //TODO: Each Method type received gets its own record with its properties stored as params
    record test() implements Requests {}
}
