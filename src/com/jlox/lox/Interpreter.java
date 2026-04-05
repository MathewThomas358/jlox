package com.jlox.lox;

import com.jlox.lox.ast.Binary;
import com.jlox.lox.ast.Expr;
import com.jlox.lox.ast.Expr.Visitor;
import com.jlox.lox.ast.Grouping;
import com.jlox.lox.ast.Literal;
import com.jlox.lox.ast.Ternary;
import com.jlox.lox.ast.Unary;

public class Interpreter implements Visitor<Object> {

    public void interpret(Expr expression) {
	try {
	    Object value = evaluate(expression);
	    System.out.println(stringify(value));
	} catch (RuntimeError error) {
	    Lox.runtimeError(error);
	}
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

    private Object evaluate(Expr expr) {
	return expr.accept(this);
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
