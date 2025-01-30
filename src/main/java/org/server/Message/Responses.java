package org.server.Message;

public sealed interface Responses {

    record Result() implements Responses{}
    record Error() implements Responses{}
}
