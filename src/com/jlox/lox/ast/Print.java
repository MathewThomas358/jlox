package com.jlox.lox.ast;

import com.jlox.lox.Token;

public class Print extends Stmt{
    public Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    public final Expr expression;
}
