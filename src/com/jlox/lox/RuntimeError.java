package com.jlox.lox;

public class RuntimeError extends RuntimeException {
    private static final long serialVersionUID = -6032243375728829698L;
    public final Token token;

    public RuntimeError(Token token, String message) {
	super(message);
	this.token = token;
    }

}
