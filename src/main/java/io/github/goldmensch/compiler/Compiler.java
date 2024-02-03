package io.github.goldmensch.compiler;

import io.github.goldmensch.compiler.codegeneration.CodeGenerator;
import io.github.goldmensch.compiler.lexing.Scanner;
import io.github.goldmensch.compiler.lexing.Token;
import io.github.goldmensch.compiler.parsing.Parser;
import io.github.goldmensch.compiler.validation.AstValidator;

import java.util.List;

public class Compiler implements ErrorHandler {

    private final String source;

    private boolean error;

    public Compiler(String source) {
        this.source = source;
    }

    public static void main(String[] args) {
        String text = """
                 fun fast baueBecken {
                     12 times {
                         while !isWall() {
                             putBrick()
                             step()
                         }
                         turnLeft()
                     }
                 }
                
                 fun fast abbauenBecken {
                     12 times {
                         while !isWall() {
                             pickBrick()
                             step()
                         }
                         turnRight()
                     }
                 }
                
                 fun umdrehen {
                     turnLeft()
                     turnLeft()
                 }
                
                 fun schwimmen {
                     3 times { putBrick() }
                     step()
                     while !isBrick() {
                         3 times { putBrick() }
                         step()
                         umdrehen()
                         3 times { pickBrick() }
                         umdrehen()
                     }
                     step()
                     umdrehen()
                     3 times { pickBrick() }
                     umdrehen()
                 }
                
                 fun hauptteil {
                     baueBecken()
                     turnLeft()
                     2 times { step() }
                     turnRight()
                     schwimmen()
                     turnRight()
                     2 times { step() }
                     turnRight()
                    
                     abbauenBecken()
                   
                     while !isWall() {
                         step()
                     }
                     turnLeft() turnLeft()
                 }
                
                 main {
                     4 times {
                         hauptteil()
                     }
                 }
                """;
        var compiler = new Compiler(text);
        compiler.compile();
    }

    private void runStages() {
        var scanner = new Scanner(source, this);
        List<Token> tokens = scanner.scanTokens();
        if (error) return;

        var parser = new Parser(tokens, this);
        var ast = parser.parse();
        if (error) return;

        var validator = new AstValidator(ast, this);
        validator.validate();
        if (error) return;

        var codeGenerator = new CodeGenerator(ast);
        String generatedCode = codeGenerator.generate();
        if (error) return;
        System.out.println(generatedCode);
    }

    public void compile() {
        runStages();
        if (error) System.exit(64);
    }

    @Override
    public void error(int line, String msg, Object... args) {
        System.err.printf("Error at line %s: %s%n", line, msg.formatted(args));
        this.error = true;
    }
}
