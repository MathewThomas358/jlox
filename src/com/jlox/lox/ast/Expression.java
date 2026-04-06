package com.jlox.lox.ast;

import com.jlox.lox.Token;

public class Expression extends Stmt{
    public Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    public final Expr expression;
}
