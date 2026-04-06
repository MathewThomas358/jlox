package com.jlox.lox.ast;

import com.jlox.lox.Token;

public class Assign extends Expr{
    public Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    public final Token name;
    public final Expr value;
}
