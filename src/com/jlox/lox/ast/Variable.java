package com.jlox.lox.ast;

import com.jlox.lox.Token;

public class Variable extends Expr{
    public Variable(Token name) {
      this.name = name;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    public final Token name;
}
