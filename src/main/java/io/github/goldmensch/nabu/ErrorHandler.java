package io.github.goldmensch.nabu;

public interface ErrorHandler {

    void error(int line, String msg, Object... args);
}
