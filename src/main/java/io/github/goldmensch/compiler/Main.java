package io.github.goldmensch.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("No script provided!");
            System.exit(64);
            return;

        }

        String source = Files.readString(Path.of(args[0]));
        var task = new Compiler(source);
        task.compile();
    }
}