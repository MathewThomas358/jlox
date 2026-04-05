package com.jlox.lox.ast;

import com.jlox.lox.Token;

public class Ternary extends Expr{
    public Ternary(Expr condition, Expr trueBranch, Expr falseBranch) {
      this.condition = condition;
      this.trueBranch = trueBranch;
      this.falseBranch = falseBranch;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitTernaryExpr(this);
    }

    public final Expr condition;
    public final Expr trueBranch;
    public final Expr falseBranch;
}
