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
		addFunction(new Get_User_Home());
		addFunction(new Get_Working_Directory());
		addFunction(new Get_Line_Separator());
		addFunction(new Get_File_Separator());
		addFunction(new Get_Root_Directory());
		addFunction(new Make_Directory());
		addFunction(new Make_File());
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
					DataType.GENERIC));
		}
		
		@Override
		public VariableData execute(VariableData[] input){
			log("Creating directory "+((VariableData.String) input[0]).value+"...");
			log(new File(((VariableData.String) input[0]).value).mkdirs() ? "succeeded" : "failed");
			return null;
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
					DataType.GENERIC));
		}
		@Override
		public ArrayList<String> getInputTooltips() {
			return new ArrayList<String>(Arrays.asList(
					"File Name",
					"File Contents"
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
			} catch (FileNotFoundException e) {
				log("FAILED: File not found: "+e.getMessage());
				return null;
			} catch (UnsupportedEncodingException e) {
				throw e;
			}
			return null;
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
					"File Name"
				));
		}
		
		@Override
		public VariableData execute(VariableData[] input) throws Exception{
			String file = ((VariableData.String) input[0]).value;
			
			ArrayList<String> lines = new ArrayList<String>();
			
			log("Reading file "+file+"...");
			try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			    for(String line; (line = br.readLine()) != null; ) {
			        lines.add(line);
			    }
			}catch(Exception e){
				log("FAILED: "+e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
		
		public Read_File(Point pos, GraphEditor owner) {
			super(pos,owner);
			this.executeOnce = true;
		}
		Read_File(){
			
		}
	}

}