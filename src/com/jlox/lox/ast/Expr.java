package com.jlox.lox.ast;

abstract public class Expr {
  public interface Visitor<R> {
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitUnaryExpr(Unary expr);
    R visitTernaryExpr(Ternary expr);
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
