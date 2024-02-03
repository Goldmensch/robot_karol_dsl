package io.github.goldmensch.compiler.codegeneration;

import java.util.ArrayList;
import java.util.List;

public class Writer {

    public static final String SPACE = " ";
    public static final String BREAK = "\n";
    private final StringBuilder builder = new StringBuilder();

    private final List<String> extraOnTop = new ArrayList<>();

    public static final int INSERTION = 2;

    private int depth = 0;

    public void addExtraOnTop(String s) {
        extraOnTop.add(s);
    }

    public void addDepth() {
        depth += INSERTION;
    }

    public void subDepth() {
        depth -= INSERTION;
    }

    public void appendSection(Runnable runnable) {
        addDepth();
        runnable.run();
        subDepth();
    }

    public String code() {
        String result = "";
        for (String s : extraOnTop) {
            result += s + "\n";
        }
        return result + builder;
    }

    public void append(String... strings) {
        for (String string : strings) {
            if (!builder.isEmpty() && builder.charAt(builder.length() - 1) == '\n') {
                builder.append(" ".repeat(depth));
            }
            builder.append(string);
            if (!string.equals(BREAK)) {
                builder.append(SPACE);
            }
        }
    }

}
