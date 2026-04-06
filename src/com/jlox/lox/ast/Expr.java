package com.jlox.lox.ast;

public abstract class Expr {
  public interface Visitor<R> {
    R visitAssignExpr(Assign expr);
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitUnaryExpr(Unary expr);
    R visitTernaryExpr(Ternary expr);
    R visitVariableExpr(Variable expr);
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
