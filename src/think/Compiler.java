/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015 Quinn Freedman
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General  License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General  License for more details.
 *
 *  You should have received a copy of the GNU General  License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  For more information, visit the THINK VPL website or email the author at
 *  quinnfreedman@gmail.com
 * 
 */

package think;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import jdk.nashorn.internal.runtime.ScriptEnvironment.FunctionStatementBehavior;
import think.FunctionEditor.FunctionIO;
import think.FunctionEditor.FunctionIO.Mode;
import think.Variable.DataType;

class Compiler{
	
	private static final String ln = System.getProperty("line.separator");
	private static final String blockComment =	
			 "/**"												+ln
			+" *  Created with THINK version "+Main.VERSION_ID	+ln
			+" *  ThinkVPL.org"									+ln
			+" */"												+ln;

	private static int indent;
	private static ArrayList<String> imports = new ArrayList<String>();
	
	private static void addImport(String s){
		s = "import "+s+";";
		if(!imports.contains(s))
			imports.add(s);
	}
	
	static void compile() throws Exception{
		
		JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File(Main.SAVE_DIR));
		FileFilter filter = new FileNameExtensionFilter("Java File","java");
	    jfc.setFileFilter(filter);
		int result = jfc.showSaveDialog(Main.window);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = jfc.getSelectedFile();
		    Out.pln("Selected file: " + selectedFile.getAbsolutePath());
		    if(!selectedFile.getName().endsWith(".java")){
		    	selectedFile = new File(selectedFile.getAbsolutePath()+".java");
		    }
		    
		    Out.pln("Compiling");
		    
		    String[] simpleName = selectedFile.getName().replace(".", "-").split("-");
		    
			ArrayList<String> lines = getText(simpleName[0]);
			
			Out.pln("Finished Compiling");
			
			Out.pln("Creating file "+selectedFile.toString()+"...");
			PrintWriter writer;
			try {
				writer = new PrintWriter(selectedFile, "UTF-8");
				writer.println(blockComment);
				
				for(String s : imports){
					writer.println(s);
				}
				writer.println();
				
				for(String s : lines){
					writer.println(s);
				}
				writer.close();
				Out.pln("Succeeded");
			} catch (FileNotFoundException e) {
				Out.pln("FAILED: File not found: "+e.getMessage());
				Main.error("Failed to compile: File not found: "+e.getMessage());
			} catch (UnsupportedEncodingException e) {
				throw e;
			}
		    
		}
		
	}

	private static ArrayList<String> getText(String name) throws Exception {
		ArrayList<String> lines = new ArrayList<String>();
		indent = 0;
		
		if(Main.blueprints.size() > 1)
			throw new Exception("Compiling does not handle multi-class programs yet.  I'm working on it, I promise.");
		
		//CLASS
		lines.add("public abstract class "+name+" {");
		
		indent++;
		
		//CLASS VARIABLES
		for(Variable v : Main.mainBP.getVariables()){
			lines.add(getIndent()+"static "+getVariableDeclaration(v)+";");
		}
		
		//MAIN
		lines.add(getIndent()+"public static void main(String args[]) {");
		
		lines.addAll(getContinuousWireText(Main.entryPoint.getOutputNodes().get(0)));
		
		//lines.add(getIndent()+"}");
		
		//FUNCTION DECLARATIONS
		lines.addAll(getFunctionDeclarations(Main.mainBP));
		
		indent--;
		
		//END CLASS
		lines.add("}");
		return lines;
	}
	
	private static ArrayList<String> getInstantiableBlueprintDeclaration(InstantiableBlueprint bp)
			throws Exception{
		ArrayList<String> lines = new ArrayList<String>();
		
		//CLASS
		lines.add("public abstract class "+bp.getName()+" {");
		
		indent++;
		
		//CLASS VARIABLES
		for(Variable v : Main.mainBP.getVariables()){
			lines.add(getIndent()+getVariableDeclaration(v)+";");
		}
		
		//CONSTRUCTOR
		lines.add(getIndent()+"public "+bp.getID()+"(){}");
		
		lines.add(getIndent());
		
		//seudoconstructor
		{
			String declarationLine = getIndent()+bp.getID()+" init(";
			
			int id = 0;
			for(Variable.DataType dataType : bp.getOutput()){
				if(dataType != Variable.DataType.GENERIC)
					declarationLine += getJavaName(dataType)+" arg"+id;
			}
			
			declarationLine += "){";
			
			lines.add(declarationLine);
			
		}
		ArrayList<String> initFunctionDefn = getContinuousWireText(bp.getInputObject().getOutputNodes().get(0));
		for(String s : initFunctionDefn){
			s = s.replaceAll("return;", "return this;");
		}
		lines.addAll(initFunctionDefn);
		//TODO retun obj
		lines.add(getIndent()+"}");
		
		
		//FUNCTION DECLARATIONS
		lines.addAll(getFunctionDeclarations(bp));
		
		
		indent--;
		
		//END CLASS
		lines.add("}");
		return lines;
	}
	
	private static String getVariableDeclaration(Variable v){
		if(v instanceof VArray){
			addImport("java.util.ArrayList"); //TODO eliminate duplicates more efficiently
			String type = getJavaObjectName(((VArray) v).dataType);
			String declaration = "ArrayList<"+type+"> "+v.getID()+" = new ArrayList<"+type+">(";
			String initialization = null;
			if(v.valueField.getText().isEmpty()){
				initialization = "";
			}else{
				try {
					v.resetVariableData();
					for(VariableData data : ((VariableData.Array) v.varData).value){
						initialization += data.getValueAsString()+", ";
					}
					if(!initialization.isEmpty())
						initialization = initialization.substring(0, initialization.length()-2);
				} catch(Exception e){
					Main.warn("Unable to parse the value of Array \""+v.getID()+"\"");
					initialization = v.valueField.getText();
				}
			}
			if(!initialization.isEmpty()){
				addImport("java.util.Arrays");
				declaration += "Arrays.asList("+initialization+")";
			}
			declaration += ")";
			return declaration;
		}else if(v instanceof VInstance){
			String type = ((VInstance) v).parentBlueprint.getName();
			return type+" "+v.getID()+" = new "+type+"()";
		}else{
			String quote = ((v.dataType == Variable.DataType.STRING) ? "\"" : ((v.dataType == Variable.DataType.CHARACTER) ? "\'" : ""));
			return getJavaName(v.dataType)+" "+v.getID()+" = "+quote+v.valueField.getText()+quote;
		}
	}
	
	private static ArrayList<String> getFunctionDeclarations(Blueprint bp) throws Exception{
		ArrayList<String> lines = new ArrayList<String>();
		
		for(VFunction function : bp.getFunctions()){
			
			String declarationLine = getIndent()+(bp instanceof InstantiableBlueprint ? "" : "static ");
			
			assert function.getInput().size() >= 1;
			assert function.getOutput().size() == 1;
			
			declarationLine += getJavaName(function.getOutput().get(0))
					+" "+function.getID()+" (";
			int id = 0;
			for(Variable.DataType dataType : function.getOutput()){
				if(dataType != Variable.DataType.GENERIC)
					declarationLine += getJavaName(dataType)+" arg"+id;
			}
			
			declarationLine += ") {";
			
			lines.add(declarationLine);
			
			indent++;
			
			for(Variable v : function.editor.getVariables()){
				lines.add(getIndent()+getVariableDeclaration(v)+";");
			}
			
			lines.add(getIndent());
			
			indent--;
			
			assert function.getInputObject().getOutputNodes().size() >= 1;
			
			lines.addAll(getContinuousWireText(function.getInputObject().getOutputNodes().get(0)));
		}
		
		return lines;
	}
	
	private static ArrayList<String> getContinuousWireText(Node n) throws Exception{
		return getContinuousWireText(n, true, true);
	}
	
	private static ArrayList<String> getContinuousWireText(Node node, boolean indented, boolean closed) throws Exception{
		Out.pln("**********************getContinuousWireText*********************");
		if(indented)
			indent++;
		ArrayList<String> lines = new ArrayList<String>();
		
		Node current = node.children.isEmpty() ? null : node.children.get(0);
		
		while(current != null){
			String line = getFunctionCall(current,false);
			Executable currentObj = ((Executable) current.parentObject);
			if(line != null){
				Out.pln("__currentObj == "+currentObj);
				if(!(line.endsWith("}") || line.endsWith(ln))){
					line += ";";
					if( currentObj.getOutputNodes().size() > 1 ) {
						line = getJavaName(currentObj.getOutputNodes().get(1).dataType)+" "
								+currentObj.getCompilerId()+" = "+line;
					}
				}else if(line.endsWith("}"+ln)){
					line = line.substring(0, line.length() - ln.length());
				}
				line = getIndent()+line;
				lines.add(line);
				Out.pln("__line == "+line);
				if(line.endsWith("}") || line.endsWith(ln)){
					break;
				}
			}
			
			Executable next = getNext(currentObj);
			Out.pln("__next == "+next);
			if(next == null)
				break;
			Out.pln(" > "+next);
			Out.pln(" > SIZE: "+next.getOutputNodes().size());
			
			if(next instanceof FunctionEditor.FunctionIO && ((FunctionEditor.FunctionIO) next).mode == Mode.OUTPUT)
				current = next.getInputNodes().get(0);
			else
				current = next.getOutputNodes().get(0);
		}
		
		if(indented)
			indent --;
			
		if(closed)
			lines.add(getIndent()+"}");
		
		return lines;
	}
	
	private static String getFunctionCall(Node n) throws Exception {
		return getFunctionCall(n,true);
	}
	
	private static String getFunctionCall(Node node, boolean isCalledAsArguement) throws Exception {
		Executable ex = (Executable) node.parentObject;
		
		if(ex instanceof Rerout){
			if(isCalledAsArguement){
				return getFunctionCall(((Rerout) ex).inputNode.parents.get(0));
			}else{
				return null;
			}
		}
		
		if(isCalledAsArguement && ex.getOutputNodes().size() > 1 &&
				!(ex instanceof FlowControl.For || ex instanceof FlowControl.AdvancedFor
						|| ex instanceof FunctionEditor.FunctionIO)){
			return ex.getCompilerId();
		}
		
		String output = null;
		
		if(ex instanceof Constant){
			String quote = "";
			String suffix = "";
			switch (((Constant) ex).dt) {
			case ARRAY:
				throw new Exception("Think does not support constant arrays/lists yet");
			case BYTE:
				break;
			case CHARACTER:
				quote = "\'";
				break;
			case DOUBLE:
				suffix = "d";
				break;
			case FLOAT:
				suffix = "f";
				break;
			case INTEGER:
				break;
			case LONG:
				suffix = "L";
				break;
			case STRING:
				quote = "\"";
				break;
			default:
				break;
			
			}
			
			output = quote+((Constant) ex).editor.getText()+suffix+quote;
		}else if(ex instanceof PrimitiveFunction){
			String id = ((PrimitiveFunction) ex).parentVariable.getID();
			Node input1 = null;
			if(!ex.getInputNodes().isEmpty()){
				boolean hasGeneric = (ex.getInputNodes().get(0).dataType == DataType.GENERIC);
				if(!hasGeneric)
					input1 = ex.getInputNodes().get(0).parents.get(0);
				else if(ex.getInputNodes().size() > 1)
					input1 = ex.getInputNodes().get(1).parents.get(0);
			}
			//Basic
			if(ex.getClass().getSimpleName().equals("Get")
					&& ex.getClass() != VArray.Get.class){
				output = id;
			}else if(ex.getClass().getSimpleName().equals("Set")
					&& ex.getClass() != VArray.Set.class){
				output = id+" = "+getFunctionCall(input1);
				
			}else if(ex.getClass().getSimpleName().equals("Multiply_By")){
				output = id+" *= "+getFunctionCall(input1);
				
			}else if(ex.getClass().getSimpleName().equals("Add_To")){
				output = id+" += "+getFunctionCall(input1);
				
			}
			// Integer
			else if(ex.getClass() == VInt.Increment.class){
				output = id+"++";
			}
			//Boolean
			else if(ex.getClass() == VBoolean.Toggle.class){
				output = "!"+id;
			}
			//String
			else if(ex.getClass() == VString.Append.class){
				output = id+" += "+getFunctionCall(input1);
				
			}else if(ex.getClass() == VString.Get_Length.class){
				output = id+".length()";
				
			}else if(ex.getClass() == VString.Get_Char_At.class){
				output = "Character.toString("+id+".charAt("+getFunctionCall(input1)+"))";
				
			}else if(ex.getClass() == VString.Replace.class){
				output = id+" = "+id+".replaceAll("+getFunctionCall(input1)+", "+ex.getInputNodes().get(2).parents.get(0)+")";
				
			}else if(ex.getClass() == VString.Split.class){
				addImport("java.util.Arrays");
				output = "new ArrayList<String>(Arrays.asList("+id+".split("+getFunctionCall(input1)+")))";
				
			}
			//ARRAYS
			else if(ex.getClass() == VArray.Add.class){
				output = id+".add("+getFunctionCall(input1)+")";
				
			}else if(ex.getClass() == VArray.Get.class){
				output = id+".get("+getFunctionCall(input1)+")";
			
			}else if(ex.getClass() == VArray.Get_Array.class){
				output = id;
			
			}else if(ex.getClass() == VArray.Get_Length.class){
				output = id+".size()";
				
			}else if(ex.getClass() == VArray.Set_Array.class){
				output = id+" = "+getFunctionCall(input1);
				
			}else if(ex.getClass() == VArray.Remove.class){
				output = id+".remove("+getFunctionCall(input1)+")";
				
			}else if(ex.getClass() == VArray.Set.class){
				output = id+".set("+getFunctionCall(input1)+","
						+getFunctionCall(ex.getInputNodes().get(2).parents.get(0))+")";
				
			}else if(ex.getClass() == VArray.To_String.class){
				output = id+".toString()";
				
			}else if(ex.getClass() == VArray.Clear.class){
				output = id+".clear()";
				
			}
		}else if(ex instanceof Binop){
			if(ex instanceof Logic.Not){
				output = "!("+getFunctionCall(ex.getInputNodes().get(0).parents.get(0))+")";
			}else{
				Node input1 = ex.getInputNodes().get(0).parents.get(0);
				Node input2 = ex.getInputNodes().get(1).parents.get(0);
				
				String str1 = getFunctionCall(input1);
				String str2 = getFunctionCall(input2);
				
				Out.pln("binop");
				Out.pln("str1 = "+str1);
				Out.pln("str2 = "+str2);
				
				if(str1.contains(" "))
					str1 = "("+str1+")";
				if(str2.contains(" "))
					str2 = "("+str2+")";
				
				output = str1+" "+((Binop) ex).getJavaBinop()+" "+str2;
			}
			
		}else if(ex instanceof Cast){
			
			String functionCall = getFunctionCall(ex.getInputNodes().get(0).parents.get(0));
			if(functionCall.contains(" "))
				functionCall = "("+functionCall+")";
			
			if(((Cast) ex).getOutput() == DataType.STRING){
				if(((Cast) ex).getInput() == DataType.OBJECT || ((Cast) ex).getInput() == DataType.ARRAY){
					output = functionCall+".toString()";
				}else{
					output = "String.valueOf("+functionCall+")";
				}
			}else{
				output = "("+getJavaName(((Cast) ex).getOutput())+") "+functionCall;
			}
		}else if(ex instanceof FlowControl.Branch){
			output = "if ("
					+getFunctionCall(ex.getInputNodes().get(1).parents.get(0))
					+") {"+ln;
			for(String s : getContinuousWireText(ex.getOutputNodes().get(0), true, false)){
				output += s+ln;
			}
			ArrayList<String> lines2 = getContinuousWireText(ex.getOutputNodes().get(1));
			if(lines2.size() > 1){
				output += (getIndent()+"} else {"+ln);
			}
			for(String s : lines2){
				output += s+ln;
			}
		}else if(ex instanceof FlowControl.For){
			if(isCalledAsArguement){
				output = "i";
			}else{
				output = "for (int i = 0; i < "//TODO add index property to each for object
						+getFunctionCall(ex.getInputNodes().get(1).parents.get(0))
						+"; i++) {"+ln;
				for(String s : getContinuousWireText(ex.getOutputNodes().get(0))){
					output += s+ln;
				}
				//output += getIndent()+"}"+ln;
				ArrayList<String> lines2 = getContinuousWireText(ex.getOutputNodes().get(2), false, false);
				for(String s : lines2){
					output += s+ln;
				}
			}
		}else if(ex instanceof FlowControl.AdvancedFor){
			throw new Exception("Compiling does not support advanced for loops yet");
		}else if(ex instanceof FlowControl.Sequence){
			output = "//SEQUENCE"+ln
					+getIndent()+"{";
			for(Node n : ex.getOutputNodes()){
				for(String s : getContinuousWireText(n)){
					output += s+ln;
				}
			}
			
		}else if(ex instanceof FlowControl.While){
			output = "while ("
					+getFunctionCall(ex.getInputNodes().get(1).parents.get(0))
					+") {"+ln;
			for(String s : getContinuousWireText(ex.getOutputNodes().get(0))){
				output += s+ln;
			}
			//output += getIndent()+"}"+ln;
			ArrayList<String> lines2 = getContinuousWireText(ex.getOutputNodes().get(1), false, false);
			for(String s : lines2){
				output += s+ln;
			}
		}else if(ex instanceof FlowControl.Wait){
			output = getIndent()+"try {"+ln;
			indent++;
			output += getIndent()+"Thread.sleep((long)("
				+ getFunctionCall(ex.getInputNodes().get(1).parents.get(0))
				+ ");"+ln;
			indent--;
			output += "} catch (InterruptedException e) {e.printStackTrace();}";
		}else if(ex instanceof Console.getStr){
			addImport("java.util.Scanner");
			if(((Console.getStr) ex).getDataType() == Variable.DataType.DOUBLE){
				output = "(new Scanner(System.in)).nextDouble() /*TODO: anon scanner is never closed*/";
			}else{
				output = "(new Scanner(System.in)).nextLine() /*TODO: anon scanner is never closed*/";
			}
		}else if(ex instanceof FunctionIO){
			if(((FunctionIO) ex).mode == FunctionIO.Mode.INPUT){
				assert isCalledAsArguement;
				
				int i = 0;
				
				for(Node n : ex.getOutputNodes()){
					if(n.dataType == Variable.DataType.GENERIC)
						continue;
					if(node == n)
						output = "arg"+i;
					i++;
				}
			}else{
				boolean hasGeneric = (ex.getInputNodes().get(0).dataType == Variable.DataType.GENERIC);//contains(Variable.DataType.GENERIC);
				//assert ex.getInputNodes().size() <= (hasGeneric ? 2 : 1); 
				if(!(ex.getInputNodes().size() <= (hasGeneric ? 2 : 1)))
					throw new Exception("Compiling does not handle multi-output functions yet ("+((FunctionIO) ex).getOverseer().getID()+")");
				//TODO handle multi-output funcs
				
				assert !isCalledAsArguement;
				
				output = "return";
				
				if(ex.getInputNodes().size() >= (hasGeneric ? 2 : 1)){
					output += " "+getFunctionCall(
							ex.getInputNodes().get((hasGeneric ? 1 : 0)).parents.get(0)
						);
				}
				
			}
			
		}else{
			
			if(ex instanceof JavaKeyword){
				output = ((JavaKeyword) ex).getJavaKeyword();
			}else if(ex instanceof UserFunc){
				FunctionOverseer overseer = ((UserFunc) ex).getParentVar();
				if(overseer instanceof VFunction)
					output = ((VFunction) overseer).getID();
				else
					output = ((InstantiableBlueprint) overseer).getName();
			}else{
				output = ex.getSimpleName().replace(" > ", ".").replace(" ", "_");
			}
			output += "(";
			
			for(Node n : ex.getInputNodes()){
				if(!n.parents.isEmpty() && n.dataType != Variable.DataType.GENERIC){
					output += getFunctionCall(n.parents.get(0))+", ";
				}
			}
			
			if(output.length() >= 2 && output.charAt(output.length()-1) == ' '){
				output = output.substring(0, output.length()-2);
			}
			
			output += ")";
		}
		
		if(!isCalledAsArguement)
			Out.pln(output);
		return output;
	}

	private static Executable getNext(Executable current) {
		if(!current.getOutputNodes().isEmpty()){
			Node outputNode = current.getOutputNodes().get(0);
			if(outputNode.dataType == Variable.DataType.GENERIC && !outputNode.children.isEmpty()){
				return (Executable) outputNode.children.get(0).parentObject;
			}
		}
		return null;
	}

	private static String getJavaName(DataType dt){
		switch(dt){
		case CHARACTER:
			return "char";
		case INTEGER:
			return "int";
		case STRING:
			return "String";
		case ARRAY:
			addImport("java.util.ArrayList");
			return "ArrayList";//TOOD <?>
		case GENERIC:
			return "void";
		default:
			return dt.toString().toLowerCase();
		}
	}
	private static String getJavaObjectName(DataType dataType) {
		String str = dataType.toString().toLowerCase();
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	private static String getIndent(){
		String output = "";
		for(int i = 0; i < indent; i++){
			output += "    ";
		}
		
		return output;
	}
	
}