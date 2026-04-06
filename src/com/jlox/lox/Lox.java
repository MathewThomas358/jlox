package com.jlox.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.jlox.lox.ast.Stmt;

/**
 * Lox Interpreter in Java
 * 
 * Additional Tweaks: <br>
 * 1. Added a AST Printer option to print in RPN <br>
 * 2. <br>
 * <br>
 * Potential Improvements:<br>
 * TODO: Add C-style block comments. <br>
 * TODO: Reading user input. <br>
 * TODO: Add more numeric data-types <br>
 * TODO: Add LALR parsing support <br>
 * TODO: Implement the same in Rust <br>
 * TODO: Implement a LLVM IR <br>
 */
public class Lox {

    // This is static so that we use the same interpreter when running a REPL
    // session. Global variables should persist across REPL sessions.
    private static final Interpreter INTERPRETER = new Interpreter();

    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
	if (args.length > 1) {
	    System.out.println("Usage: jlox [script]");
	    System.exit(64);
	} else if (args.length == 1) {
	    runFile(args[0]);
	} else {
	    runPrompt();
	}
    }

    private static void runFile(String path) throws IOException {
	if (hadError)
	    System.exit(65);
	if (hadRuntimeError)
	    System.exit(70);

	byte[] bytes = Files.readAllBytes(Paths.get(path));
	run(new String(bytes, Charset.defaultCharset()));
    }

    private static void runPrompt() throws IOException {
	InputStreamReader input = new InputStreamReader(System.in);
	BufferedReader reader = new BufferedReader(input);

	for (;;) {
	    System.out.print("> ");
	    String line = reader.readLine();
	    if (line == null) {
		break;
	    }
	    run(line);
	    hadError = false;
	}
    }

    private static void run(String source) {
	Scanner scanner = new Scanner(source);
	List<Token> tokens = scanner.scanTokens();

	Parser parser = new Parser(tokens, true, false, true);
	List<Stmt> statements = parser.parse();

	if (hadError)
	    return;

	INTERPRETER.interpret(statements);
    }

    static void error(int line, String message) {
	report(line, "", message);
    }

    private static void report(int line, String where, String message) {
	System.err.println("[line " + line + "] Error" + where + ": " + message);
	hadError = true;
    }

    static void error(Token token, String message) {
	if (token.type == TokenType.EOF) {
	    report(token.line, " at end", message);
	} else {
	    report(token.line, " at '" + token.lexeme + "'", message);
	}
    }

    static void runtimeError(RuntimeError error) {
	System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
	hadRuntimeError = true;
    }
}