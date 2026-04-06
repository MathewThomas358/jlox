package com.jlox.lox;

import java.util.List;

import com.jlox.lox.ast.Assign;
import com.jlox.lox.ast.Binary;
import com.jlox.lox.ast.Block;
import com.jlox.lox.ast.Expr;
import com.jlox.lox.ast.Expression;
import com.jlox.lox.ast.Grouping;
import com.jlox.lox.ast.Literal;
import com.jlox.lox.ast.Print;
import com.jlox.lox.ast.Stmt;
import com.jlox.lox.ast.Ternary;
import com.jlox.lox.ast.Unary;
import com.jlox.lox.ast.Var;
import com.jlox.lox.ast.Variable;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private Environment environment = new Environment();

    public void interpret(List<Stmt> statements) {
	try {
	    for (Stmt statement : statements) {
		execute(statement);
	    }
	} catch (RuntimeError error) {
	    Lox.runtimeError(error);
	}
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
	evaluate(stmt.expression);
	return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
	Object value = evaluate(stmt.expression);
	System.out.println(stringify(value));
	return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
	Object value = Environment.UNINITIALIZED;
	if (stmt.initializer != null) {
	    value = evaluate(stmt.initializer);
	}

	environment.define(stmt.name.lexeme, value);
	return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
	executeBlock(stmt.statements, new Environment(environment));
	return null;
    }

    @Override
    public Object visitAssignExpr(Assign expr) {
	Object value = evaluate(expr.value);
	environment.assign(expr.name, value);
	return value;
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
	return environment.get(expr.name);
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
	Object left = evaluate(expr.left);
	Object right = evaluate(expr.right);

	switch (expr.operator.type) {
	case BANG_EQUAL:
	    return !isEqual(left, right);
	case EQUAL_EQUAL:
	    return isEqual(left, right);
	case GREATER:
	    checkNumberOperands(expr.operator, left, right);
	    return (double) left > (double) right;
	case GREATER_EQUAL:
	    checkNumberOperands(expr.operator, left, right);
	    return (double) left >= (double) right;
	case LESS:
	    checkNumberOperands(expr.operator, left, right);
	    return (double) left < (double) right;
	case LESS_EQUAL:
	    checkNumberOperands(expr.operator, left, right);
	    return (double) left <= (double) right;
	case MINUS:
	    checkNumberOperands(expr.operator, left, right);
	    return (double) left - (double) right;
	case PLUS:
	    if (left instanceof Double && right instanceof Double) {
		return (double) left + (double) right;
	    }

	    if (left instanceof String ls && right instanceof String rs) {
		return ls + rs;
	    }

	    if (left instanceof String || right instanceof String) {
		return stringify(left) + stringify(right);
	    }
	    throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
	case SLASH:
	    checkNumberOperands(expr.operator, left, right);
	    checkDivisionByZero(expr.operator, right);
	    return (double) left / (double) right;
	case STAR:
	    checkNumberOperands(expr.operator, left, right);
	    return (double) left * (double) right;
	default:
	    break;
	}

	return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
	return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
	return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
	Object right = evaluate(expr.right);

	switch (expr.operator.type) {
	case BANG:
	    return !isTruthy(right);
	case MINUS:
	    checkNumberOperand(expr.operator, right);
	    return -(double) right;
	default:
	    return null;
	}
    }

    @Override
    public Object visitTernaryExpr(Ternary expr) {

	Object condition = evaluate(expr.condition);

	if (isTruthy(condition)) {
	    return evaluate(expr.trueBranch);
	}

	return evaluate(expr.falseBranch);
    }

    private void execute(Stmt stmt) {
	stmt.accept(this);
    }

    private Object evaluate(Expr expr) {
	return expr.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
	Environment previous = this.environment;
	try {
	    this.environment = environment;

	    for (Stmt statement : statements) {
		execute(statement);
	    }
	} finally {
	    this.environment = previous;
	}
    }

    private String stringify(Object object) {
	if (object == null)
	    return "nil";

	if (object instanceof Double) {
	    String text = object.toString();
	    if (text.endsWith(".0")) {
		text = text.substring(0, text.length() - 2);
	    }
	    return text;
	}

	return object.toString();
    }

    private boolean isTruthy(Object object) {
	if (object == null)
	    return false;
	if (object instanceof Boolean)
	    return (boolean) object;
	return true;
    }

    private boolean isEqual(Object a, Object b) {
	if (a == null && b == null)
	    return true;
	if (a == null)
	    return false;

	return a.equals(b);
    }

    private void checkNumberOperand(Token operator, Object operand) {
	if (operand instanceof Double)
	    return;
	throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
	if (left instanceof Double && right instanceof Double)
	    return;

	throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private void checkDivisionByZero(Token operator, Object denom) {
	if (denom instanceof Double doubleDenom && doubleDenom == 0)
	    throw new RuntimeError(operator, "Division by zero");
    }
}
