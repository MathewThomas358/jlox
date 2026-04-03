package com.jlox.lox.ast;

import com.jlox.lox.Token;

public class Literal extends Expr{
    public Literal(Object value) {
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    final Object value;
}
