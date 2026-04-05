package com.jlox.lox;

import java.util.List;
import java.util.function.Supplier;

import com.jlox.lox.ast.AstPrinter;
import com.jlox.lox.ast.AstPrinter.PrintMode;
import com.jlox.lox.ast.Binary;
import com.jlox.lox.ast.Expr;
import com.jlox.lox.ast.Grouping;
import com.jlox.lox.ast.Literal;
import com.jlox.lox.ast.Ternary;
import com.jlox.lox.ast.Unary;

/**
 * TODO: Add support for comma operators, write the grammer and implement
 * parsing <br>
 * TODO: Do the same for ternary operator
 */
public class Parser {

    private static class ParseError extends RuntimeException {
	private static final long serialVersionUID = -6292654238855445577L;
    }

    private final List<Token> tokens;
    private final boolean useErrorProdutions;
    private final boolean useCommaOperator;
    private final boolean useTernaryOperator;
    private int current = 0;

    /**
     * Expression Grammar <br>
     * <br>
     * expression → equality ; <br>
     * equality → comparison ( ( "!=" | "==" ) comparison )* ; <br>
     * comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )* ; <br>
     * term → factor ( ( "-" | "+" ) factor )* ; <br>
     * factor → unary ( ( "/" | "*" ) unary )* ; <br>
     * unary → ( "!" | "-" ) unary | primary ; <br>
     * primary → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
     * <br>
     * 
     * @param tokens List of token
     */
    Parser(List<Token> tokens) {
	this.tokens = tokens;
	this.useErrorProdutions = false;
	this.useCommaOperator = false;
	this.useTernaryOperator = false;
    }

    /**
     * * Error-augmented Expression Grammar with ternary and comma operators <br>
     * <br>
     * expression → comma; <br>
     * comma → ( ", ") ternary // Error production <br>
     * | ternary (( ", ") ternary)* <br>
     * ternary → equality ("?" equality ":" ternary)?<br>
     * equality → ( "!=" | "==" ) comparison // Error production<br>
     * | comparison ( ( "!=" | "==" ) comparison )* ; <br>
     * comparison → ( ">" | ">=" | "<" | "<=" ) term // Error production <br>
     * | term ( ( ">" | ">=" | "<" | "<=" ) term )* ; <br>
     * term → "+" factor // Error production <br>
     * | factor ( ( "-" | "+" ) factor )* ; <br>
     * factor → ( "/" | "*" ) unary // Error production <br>
     * | unary ( ( "/" | "*" ) unary )* ; <br>
     * unary → ( "!" | "-" ) unary | primary ; <br>
     * primary → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
     * <br>
     * 
     * @param token               List of token
     * @param useErrorProductions
     */
    Parser(List<Token> tokens, boolean useErrorProductions, boolean useCommaOperator, boolean useTernaryOperator) {
	this.tokens = tokens;
	this.useErrorProdutions = useErrorProductions;
	this.useCommaOperator = useCommaOperator;
	this.useTernaryOperator = useTernaryOperator;
    }

    Expr parse() {
	try {
	    return expression();
	} catch (ParseError error) {
	    return null;
	}
    }

    private Expr expression() {
	return comma();
    }

    private Expr comma() {

	if (useCommaOperator) {

	    /**
	     * This logic doesn't assign the value of the right most operand to the left
	     * hand side in case of assignments or give the right hand value as the overall
	     * value of a set of comma separated expressions. That has to be done in the
	     * interpreter phase. The discarding of all except the right most expression
	     * will be taken care at that phase.
	     * 
	     * TODO: Implement the right-hand expression evaluation i.e. the comma-seperated
	     * code block should evaluate all the constituents but the final value of the
	     * block should be the right most one. When the interpreter sees a comma, the
	     * left child should be ignored.<br>
	     * TODO: Handle function calls. Function calls should be handled separately.
	     */

	    noLhsCheck(useTernaryOperator ? this::ternary : this::equality, TokenType.COMMA);

	    Expr expr = useTernaryOperator ? ternary() : equality();
	    while (match(TokenType.COMMA)) {

		Token operator = previous();
		Expr right = useTernaryOperator ? ternary() : equality();
		expr = new Binary(expr, operator, right);
	    }

	    return expr;
	}

	if (useTernaryOperator) {
	    return ternary();
	}

	return equality();
    }

    private Expr ternary() {

	if (useTernaryOperator) {
	    Expr expr = equality();

	    if (match(TokenType.QUESTION_MARK)) {

		Expr exprTrue = expression();
		consume(TokenType.COLON, "Missing ternary operator");
		Expr exprFalse = ternary();

		return new Ternary(expr, exprTrue, exprFalse);
	    }

	    return expr;
	}

	return equality();
    }

    private Expr equality() {

	noLhsCheck(this::comparison, TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL);

	Expr expr = comparison();

	while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {

	    Token operator = previous();
	    Expr right = comparison();
	    expr = new Binary(expr, operator, right);
	}

	return expr;
    }

    private Expr comparison() {

	noLhsCheck(this::term, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL);

	Expr expr = term();

	while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
	    Token operator = previous();
	    Expr right = term();
	    expr = new Binary(expr, operator, right);
	}

	return expr;
    }

    private Expr term() {

	noLhsCheck(this::factor, TokenType.PLUS);

	Expr expr = factor();

	while (match(TokenType.MINUS, TokenType.PLUS)) {
	    Token operator = previous();
	    Expr right = factor();
	    expr = new Binary(expr, operator, right);
	}

	return expr;
    }

    private Expr factor() {

	noLhsCheck(this::unary, TokenType.SLASH, TokenType.STAR);

	Expr expr = unary();

	while (match(TokenType.SLASH, TokenType.STAR)) {
	    Token operator = previous();
	    Expr right = unary();
	    expr = new Binary(expr, operator, right);
	}

	return expr;
    }

    /**
     * unary → ( "!" | "-" ) unary | primary
     */
    private Expr unary() {

	if (match(TokenType.BANG, TokenType.MINUS)) {
	    Token operator = previous();
	    Expr right = unary();
	    return new Unary(operator, right);
	}

	return primary();
    }

    /**
     * primary → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")"
     */
    private Expr primary() {
	if (match(TokenType.FALSE))
	    return new Literal(false);
	if (match(TokenType.TRUE))
	    return new Literal(true);
	if (match(TokenType.NIL))
	    return new Literal(null);

	if (match(TokenType.NUMBER, TokenType.STRING)) {
	    return new Literal(previous().literal);
	}

	if (match(TokenType.LEFT_PAREN)) {
	    Expr expr = expression();
	    consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
	    return new Grouping(expr);
	}

	throw error(peek(), "Expect expression");
    }

    private Token consume(TokenType type, String message) {
	if (check(type))
	    return advance();

	throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
	Lox.error(token, message);
	return new ParseError();
    }

    private void synchronize() {
	advance();

	while (!isAtEnd()) {
	    if (previous().type == TokenType.SEMICOLON) {
		return;
	    }

	    switch (peek().type) {
	    case CLASS:
	    case FUN:
	    case VAR:
	    case FOR:
	    case IF:
	    case WHILE:
	    case PRINT:
	    case RETURN: // NOSONAR
		return;
	    default:
		break;
	    }

	    advance();
	}
    }

    private boolean match(TokenType... types) {
	for (TokenType type : types) {
	    if (check(type)) {
		advance();
		return true;
	    }
	}
	return false;
    }

    private boolean check(TokenType type) {
	if (isAtEnd())
	    return false;
	return peek().type == type;
    }

    private Token advance() {
	if (!isAtEnd())
	    current++;
	return previous();
    }

    private boolean isAtEnd() {
	return peek().type == TokenType.EOF;
    }

    private Token peek() {
	return tokens.get(current);
    }

    private Token previous() {
	return tokens.get(current - 1);
    }

    private void noLhsCheck(Supplier<Expr> production, TokenType... types) {

	if (useErrorProdutions && match(types)) {
	    error(previous(), "No left-hand operand found");
	    Expr right = production.get(); // This consumes the right hand operand
	    System.out.println("Discarded rh: " + new AstPrinter(PrintMode.RPN).print(right));

	    throw new ParseError();
	}
    }

}
