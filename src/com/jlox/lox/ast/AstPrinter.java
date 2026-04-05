package com.jlox.lox.ast;

import com.jlox.lox.Token;
import com.jlox.lox.TokenType;
import com.jlox.lox.ast.Expr.Visitor;

public class AstPrinter implements Visitor<String> {

    private final PrintMode printMode;

    public AstPrinter() {
	this.printMode = PrintMode.NORMAL;
    }

    public AstPrinter(PrintMode printMode) {
	this.printMode = printMode;
    }

    public String print(Expr expr) {
	return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
	return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
	if (printMode == PrintMode.RPN)
	    return expr.expression.accept(this);
	return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
	if (expr.value == null)
	    return "nil";
	return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
	return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, Expr... exprs) {
	switch (printMode) {
	case RPN:
	    return paranthesizeRpn(name, exprs);
	default:
	    return paranthesizeNormal(name, exprs);
	}
    }

    private String paranthesizeNormal(String name, Expr... exprs) {
	StringBuilder builder = new StringBuilder();

	builder.append("(").append(name);
	for (Expr expr : exprs) {
	    builder.append(" ");
	    builder.append(expr.accept(this));
	}
	builder.append(")");

	return builder.toString();
    }

    private String paranthesizeRpn(String name, Expr... exprs) {
	StringBuilder builder = new StringBuilder();

	for (Expr expr : exprs) {
	    builder.append(" ");
	    builder.append(expr.accept(this));
	}
	builder.append(" ").append(name);

	return builder.toString();
    }

    public static void main(String[] args) {
	Expr expression = new Binary(new Unary(new Token(TokenType.MINUS, "-", null, 1), new Literal(123)),
		new Token(TokenType.STAR, "*", null, 1), new Grouping(new Literal(45.67)));

	// @formatter:off
	Expr arithmeticTestExpr = new Binary(
                        		new Grouping(
                        			new Binary(
                        				new Literal(1), 
                        				new Token(TokenType.PLUS, "+", null, 1), 
                        				new Literal(2)
                        			)
                        		), 
                        		new Token(TokenType.STAR, "*", null, 1),
                        		new Grouping(
                        			new Binary(
                        				new Literal(4), 
                        				new Token(TokenType.MINUS, "-", null, 1), 
                        				new Literal(3)
                        			)
                        		)
                        	  );
	// @formatter:on

	System.out.println(new AstPrinter().print(expression));
	System.out.println(new AstPrinter(PrintMode.RPN).print(arithmeticTestExpr));
    }

    public enum PrintMode {
	NORMAL, RPN
    };
}
