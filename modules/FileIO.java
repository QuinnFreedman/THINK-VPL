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

package modules;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import think.Executable;
import think.GraphEditor;
import think.Module;
import think.Variable;
import think.Variable.DataType;
import think.VariableData;

public class FileIO extends Module{
	private static boolean echo = true;

	public static void log(String string) {
		if(echo)
			Module.postToConsole(string);
	}
	
	@Override
	public void setup(){
		addFunction(new Set_Echo());
		addFunction(new Get_User_Home());
		addFunction(new Get_Working_Directory());
		addFunction(new Get_Line_Separator());
		addFunction(new Get_File_Separator());
		addFunction(new Get_Root_Directory());
		addFunction(new Make_Directory());
		addFunction(new Make_File());
		addFunction(new Read_File());
		addFunction(new Get_Files_In_Directory());
		addFunction(new File_Exists());
		addFunction(new Is_File());
		addFunction(new Is_Directory());
		addFunction(new Move_File());
		addFunction(new Delete_File());
	}
	
	public static class Set_Echo extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.BOOLEAN));
		}
		
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			
			echo = ((VariableData.Boolean) input[0]).value;
			
			return null;
		}
		
		public Set_Echo(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Set_Echo(){
			
		}
	}
	
	public static class Get_User_Home extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.STRING));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			return new VariableData.String(System.getProperty("user.home"));
		}
		
		public Get_User_Home(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Get_User_Home(){
			
		}
	}
	
	public static class Get_Working_Directory extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.STRING));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			return new VariableData.String(System.getProperty("user.dir"));
		}
		
		public Get_Working_Directory(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Get_Working_Directory(){
			
		}
	}
	
	public static class Get_Line_Separator extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.STRING));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			return new VariableData.String(System.getProperty("line.separator"));
		}
		
		public Get_Line_Separator(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Get_Line_Separator(){
			
		}
	}
	
	public static class Get_File_Separator extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.STRING));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			return new VariableData.String(System.getProperty("file.separator"));
		}
		
		public Get_File_Separator(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Get_File_Separator(){
			
		}
	}
	
	public static class Get_Root_Directory extends Executable{
		private static final long serialVersionUID = 1L;
		
		private static String root;
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.STRING));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			if(root == null){
				File file = new File(System.getProperty("user.dir"));
				while(file.getParent() != null){
					file = new File(file.getParent());
				}
				root = file.getAbsolutePath();
			}
			return new VariableData.String(root);
		}
		
		public Get_Root_Directory(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Get_Root_Directory(){
			
		}
	}
	
	public static class Make_Directory extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.STRING));
		}
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.BOOLEAN));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			log("Creating directory "+((VariableData.String) input[0]).value+"...");
			boolean success = new File(((VariableData.String) input[0]).value).mkdirs();
			log(success ? "Succeeded" : "FAILED");
			return new VariableData.Boolean(success);
		}
		
		public Make_Directory(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Make_Directory(){
			
		}
	}
	
	public static class Make_File extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.STRING,
					DataType.ARRAY));
		}
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC, DataType.BOOLEAN));
		}
		@Override
		public ArrayList<String> getInputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"File Name",
					"File Contents"
				));
		}
		
		@Override
		public ArrayList<String> getOutputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"Completed Successfully"
				));
		}
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			log("Creating file "+((VariableData.String) input[0]).value+"...");
			PrintWriter writer;
			try {
				writer = new PrintWriter(((VariableData.String) input[0]).value, "UTF-8");
				for(VariableData d : ((VariableData.Array) input[1]).value){
					if(d instanceof VariableData.String){
						writer.println(((VariableData.String) d).value);
					}else{
						writer.close();
						throw new Exception("ERROR: type missmatch in \"FunctionIO > Make_File\": expected an array of type String but got an array of a different type");
					}
				}
				writer.close();
				log("Succeeded");
			} catch (FileNotFoundException e) {
				log("FAILED: File not found: "+e.getMessage());
				return new VariableData.Boolean(false);
			} catch (UnsupportedEncodingException e) {
				throw e;
			}
			return new VariableData.Boolean(true);
		}
		
		public Make_File(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Make_File(){
			
		}
	}
	
	public static class Read_File extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.STRING));
		}
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.ARRAY));
		}
		@Override
		public ArrayList<String> getInputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"File Path"
				));
		}
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			String file = ((VariableData.String) input[0]).value;
			
			ArrayList<VariableData> lines = new ArrayList<VariableData>();
			
			log("Reading file "+file+"...");
			try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			    for(String line; (line = br.readLine()) != null; ) {
			        lines.add(new VariableData.String(line));
			    }
			}catch(Exception e){
				log("FAILED: "+e.getMessage());
				e.printStackTrace();
			}
			VariableData.Array array = new VariableData.Array(Variable.DataType.STRING);
			
			array.value = lines;
			return array;
		}
		
		public Read_File(Point pos, GraphEditor owner) {
			super(pos,owner);
			this.executeOnce = true;
		}
		Read_File(){
			
		}
	}
	
	public static class Get_Files_In_Directory extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.STRING));
		}
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.ARRAY));
		}
		@Override
		public ArrayList<String> getInputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"File Path"
				));
		}
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			String fileName = ((VariableData.String) input[0]).value;
			
			log("Looking for files in "+fileName);
			
			ArrayList<VariableData> files = new ArrayList<VariableData>();
			
			File file = new File(fileName);
			if(!file.exists()){
				log("FAILED: File "+fileName+" does not exist");
			}else if(!file.isDirectory()){
				log("FAILED: File "+fileName+" is not a directory");
			}else{
				for(String s : file.list()){
					files.add(new VariableData.String(s));
				}
			}
			
			VariableData.Array array = new VariableData.Array(Variable.DataType.STRING);
			array.value = files;
			return array;
		}
		
		public Get_Files_In_Directory(Point pos, GraphEditor owner) {
			super(pos,owner);
			this.executeOnce = true;
		}
		Get_Files_In_Directory(){
			
		}
	}

	public static class File_Exists extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.STRING));
		}
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.BOOLEAN));
		}
		@Override
		public ArrayList<String> getInputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"File Path"
				));
		}
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			String fileName = ((VariableData.String) input[0]).value;
			
			File file = new File(fileName);
			return new VariableData.Boolean(file.exists());
		}
		
		public File_Exists(Point pos, GraphEditor owner) {
			super(pos,owner);
			this.executeOnce = true;
		}
		File_Exists(){
			
		}
	}
	
	public static class Is_File extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.STRING));
		}
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.BOOLEAN));
		}
		@Override
		public ArrayList<String> getInputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"File Path"
				));
		}
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			String fileName = ((VariableData.String) input[0]).value;
			
			File file = new File(fileName);
			return new VariableData.Boolean(file.isFile());
		}
		
		public Is_File(Point pos, GraphEditor owner) {
			super(pos,owner);
			this.executeOnce = true;
		}
		Is_File(){
			
		}
	}
	
	public static class Is_Directory extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.OUT;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.STRING));
		}
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.BOOLEAN));
		}
		@Override
		public ArrayList<String> getInputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"File Path"
				));
		}
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			String fileName = ((VariableData.String) input[0]).value;
			
			File file = new File(fileName);
			return new VariableData.Boolean(file.isDirectory());
		}
		
		public Is_Directory(Point pos, GraphEditor owner) {
			super(pos,owner);
			this.executeOnce = true;
		}
		Is_Directory(){
			
		}
	}
	
	public static class Move_File extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.STRING,
					DataType.STRING));
		}
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC, DataType.BOOLEAN));
		}
		@Override
		public ArrayList<String> getInputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"File Path",
					"New File Path"
				));
		}
		
		@Override
		public ArrayList<String> getOutputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"Completed Successfully"
				));
		}
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			log("Renaming file "+((VariableData.String) input[0]).value+" to "+((VariableData.String) input[1]).value+"...");

			Boolean b = (new File(((VariableData.String) input[0]).value)).renameTo(new File(((VariableData.String) input[1]).value));
			
			log(b ? "Succeeded" : "FAILED");
			
			return new VariableData.Boolean(b);
		}
		
		public Move_File(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Move_File(){
			
		}
	}
	public static class Delete_File extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Mode getPrimairyMode(){return Mode.IN;};
		
		@Override
		public ArrayList<DataType> getInputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC,
					DataType.STRING));
		}
		@Override
		public ArrayList<DataType> getOutputs(){
			return new ArrayList<DataType>(Arrays.asList(
					DataType.GENERIC, DataType.BOOLEAN));
		}
		@Override
		public ArrayList<String> getInputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"File Path"
				));
		}
		
		@Override
		public ArrayList<String> getOutputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"Completed Successfully"
				));
		}
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			String fileName = ((VariableData.String) input[0]).value;
			
			log("Deleting file "+fileName+"...");
			
			File file = new File(fileName);
			
			if(!file.exists()){
				log("FAILED: file does not exist");
				return new VariableData.Boolean(false);
			}else if(!file.canWrite()){
				log("FAILED: do not have permission to write to this file");
				return new VariableData.Boolean(false);
			}
			
			Boolean b = file.delete();
			
			log(b ? "Succeeded" : "FAILED");
			
			return new VariableData.Boolean(b);
		}
		
		public Delete_File(Point pos, GraphEditor owner) {
			super(pos,owner);
		}
		Delete_File(){
			
		}
	}
}