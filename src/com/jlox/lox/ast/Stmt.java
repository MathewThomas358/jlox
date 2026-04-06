package com.jlox.lox.ast;

public abstract class Stmt {
  public interface Visitor<R> {
    R visitExpressionStmt(Expression stmt);
    R visitPrintStmt(Print stmt);
    R visitVarStmt(Var stmt);
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
