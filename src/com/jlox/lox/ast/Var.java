package com.jlox.lox.ast;

import com.jlox.lox.Token;

public class Var extends Stmt{
    public Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    public final Token name;
    public final Expr initializer;
}
