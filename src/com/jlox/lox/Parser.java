package com.jlox.lox;

import java.util.List;

import com.jlox.lox.ast.Binary;
import com.jlox.lox.ast.Expr;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
	this.tokens = tokens;
    }

    private Expr expression() {
	return equality();
    }

    private Expr equality() {

	Expr expr = comparison();

	while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
	    Token operator = previous();
	    Expr right = comparison();
	    expr = new Binary(expr, operator, right);
	}

	return expr;
    }
}
