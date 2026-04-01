package com.jlox.lox.ast;

import com.jlox.lox.Token;

public class Grouping extends Expr{
    Grouping(Expr expression) {
      this.expression = expression;
    }

    final Expr expression;
}
