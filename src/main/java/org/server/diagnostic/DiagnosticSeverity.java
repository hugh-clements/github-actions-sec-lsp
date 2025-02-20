package org.server.diagnostic;

public enum DiagnosticSeverity {
    ERROR,
    WARNING,
    INFO,
    HINT;

    public static DiagnosticSeverity getSeverity(int i) {
        return switch (i) {
            case 0: yield DiagnosticSeverity.ERROR;
            case 1: yield DiagnosticSeverity.WARNING;
            case 2: yield DiagnosticSeverity.INFO;
            case 3: yield DiagnosticSeverity.HINT;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public static int getSeverityValue(DiagnosticSeverity severity) {
        return switch (severity) {
            case ERROR -> 0;
            case WARNING -> 1;
            case INFO -> 2;
            case HINT -> 3;
        };
    }
}



