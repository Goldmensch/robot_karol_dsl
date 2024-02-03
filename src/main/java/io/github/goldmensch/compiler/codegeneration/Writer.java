package io.github.goldmensch.compiler.codegeneration;

import java.util.ArrayList;
import java.util.List;

public class Writer {

    public static final String SPACE = " ";
    public static final String BREAK = "\n";
    private final StringBuilder builder = new StringBuilder();

    private final List<String> extraOnTop = new ArrayList<>();

    private final int depth = 0;

    public void addExtraOnTop(String s) {
        extraOnTop.add(s);
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
            builder.append(SPACE);
            builder.append(string);
        }
    }

}
