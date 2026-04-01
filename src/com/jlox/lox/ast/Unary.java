package com.jlox.lox.ast;

import com.jlox.lox.Token;

public class Unary extends Expr{
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    final Token operator;
    final Expr right;
}
