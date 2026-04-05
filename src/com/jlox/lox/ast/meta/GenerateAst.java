package com.jlox.lox.ast.meta;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {

    public static void main(String[] args) throws IOException {
	String outputDir = "src/com/jlox/lox/ast/";

	List<String> types = Arrays.asList("Binary   : Expr left, Token operator, Expr right",
		"Grouping : Expr expression", "Literal  : Object value", "Unary    : Token operator, Expr right",
		"Ternary    : Expr condition, Expr trueBranch, Expr falseBranch");

	defineBaseClass(outputDir, "Expr", types);
	defineAst(outputDir, "Expr", types);
    }

    private static void defineBaseClass(String outputDir, String baseName, List<String> types) throws IOException {

	String path = outputDir + "/" + baseName + ".java";

	File file = new File(path);
	file.getParentFile().mkdirs();

	try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
	    writer.println("package com.jlox.lox.ast;");
	    writer.println();
	    writer.println("abstract public class " + baseName + " {");
	    defineVisitor(writer, baseName, types);
	    writer.println();
	    writer.println("  public abstract <R> R accept(Visitor<R> visitor);");
	    writer.println("}");
	}
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
	for (String type : types) {

	    String className = type.split(":")[0].trim();
	    String path = outputDir + "/" + className + ".java";

	    File file = new File(path);
	    file.getParentFile().mkdirs();

	    try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {

		writer.println("package com.jlox.lox.ast;");
		writer.println();
		writer.println("import com.jlox.lox.Token;");
		writer.println();
		writer.println("public class " + className + " extends " + baseName + "{");

		String fields = type.split(":")[1].trim();
		defineType(writer, baseName, className, fields);

		writer.println("}");
		writer.close();
	    }
	}
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
	// Constructor.
	writer.println("    public " + className + "(" + fieldList + ") {");

	// Store parameters in fields.
	String[] fields = fieldList.split(", ");
	for (String field : fields) {
	    String name = field.split(" ")[1];
	    writer.println("      this." + name + " = " + name + ";");
	}

	writer.println("    }");

	writer.println();
	writer.println("    @Override");
	writer.println("    public <R> R accept(Visitor<R> visitor) {");
	writer.println("      return visitor.visit" + className + baseName + "(this);");
	writer.println("    }");

	// Fields.
	writer.println();
	for (String field : fields) {
	    writer.println("    final " + field + ";");
	}
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
	writer.println("  public interface Visitor<R> {");

	for (String type : types) {
	    String typeName = type.split(":")[0].trim();
	    writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
	}

	writer.println("  }");
    }
}
