package io.github.goldmensch.compiler;

import io.github.goldmensch.compiler.codegeneration.CodeGenerator;
import io.github.goldmensch.compiler.lexing.Scanner;
import io.github.goldmensch.compiler.lexing.Token;
import io.github.goldmensch.compiler.parsing.Parser;
import io.github.goldmensch.compiler.validation.AstValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Compiler implements ErrorHandler {

    private final String source;

    private boolean error;

    public Compiler(String source) {
        this.source = source;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("No input file provided!");
            return;
        }
        Path filePath = Path.of(args[0]);
        String text = Files.readString(filePath);

        var compiler = new Compiler(text);
        String generatedCode = compiler.compile();

        if (args.length == 2 && args[1].equals("-c")) {
            System.out.println(generatedCode);
        } else {
            Path destPath = Path.of(filePath.getFileName() + ".kdp");
            Files.deleteIfExists(destPath);
            Files.writeString(destPath, generatedCode, StandardOpenOption.CREATE_NEW);
        }
    }

    private String runStages() {
        var scanner = new Scanner(source, this);
        List<Token> tokens = scanner.scanTokens();
        if (error) return null;

        var parser = new Parser(tokens, this);
        var ast = parser.parse();
        if (error) return null;

        var validator = new AstValidator(ast, this);
        validator.validate();
        if (error) return null;

        var codeGenerator = new CodeGenerator(ast);
        return codeGenerator.generate();
    }

    public String compile() {
        String generatedCode = runStages();
        if (error || generatedCode == null) System.exit(64);
        return generatedCode;
    }

    @Override
    public void error(int line, String msg, Object... args) {
        System.err.printf("Error at line %s: %s%n", line, msg.formatted(args));
        this.error = true;
    }
}
