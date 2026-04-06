package com.jlox.lox.ast;

import java.util.List;

public class Block extends Stmt {
    public Block(List<Stmt> statements) {
	this.statements = statements;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
	return visitor.visitBlockStmt(this);
    }

    public final List<Stmt> statements;
}
