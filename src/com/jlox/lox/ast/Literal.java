package com.jlox.lox.ast;

public class Literal extends Expr {
    Literal(Object value) {
	this.value = value;
    }

    final Object value;
}
