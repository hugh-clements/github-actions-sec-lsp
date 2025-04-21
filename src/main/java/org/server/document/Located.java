package org.server.document;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.yaml.snakeyaml.nodes.Node;


/**
 * @param startRow
 * @param startCol
 * @param endRow
 * @param endCol
 * @param value
 * @param <T>
 */
public record Located<T>(int startRow, int startCol, int endRow, int endCol, T value) {

    /**
     * Method to create a Located<T> from and Object T
     * @param node node where the object is built from
     * @param object object to be located
     * @return Located object
     * @param <T> type of the object to be located
     */
    public static <T> Located<T> locate(Node node, T object) {
        return new Located<>(node.getStartMark().getLine(), node.getStartMark().getColumn(), node.getEndMark().getLine(), node.getEndMark().getColumn(), object);
    }

    /**
     * Method to take a located object and extract its range
     * @param located object to extract range from
     * @return Range of the inputted object
     */
    public static Range locatedToRange(Located<?> located) {
        return new Range(new Position(located.startRow, located.startCol), new Position(located.endRow, located.endCol));
    }
}

