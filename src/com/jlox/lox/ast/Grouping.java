package com.jlox.lox.ast;

import com.jlox.lox.Token;

public class Grouping extends Expr{
    public Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    public final Expr expression;
}
