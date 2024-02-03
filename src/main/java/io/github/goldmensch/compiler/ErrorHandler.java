package io.github.goldmensch.compiler;

public interface ErrorHandler {

    void error(int line, String msg, Object... args);
}
