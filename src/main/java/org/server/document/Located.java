package org.server.document;

/**
 * Used to store location information for any node with in the data structure
 * @param row
 * @param col
 * @param value of the node of type <T>
 * @param <T> The type of the node in the data structure
 */
public record Located<T>(int row, int col, T value) { }
