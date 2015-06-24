/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015  Quinn Freedman
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  For more information, visit the THINK VPL website or email the author at
 *  quinnfreedman@gmail.com
 * 
 */

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;

class VariableData{
	
	public java.lang.String getValueAsString(){
		return null;
	}
	public double getValueAsDouble(){
		return 0;
	}
	public static boolean isNumber(VariableData v){
		if(v.getClass() == Double.class || v.getClass() == Float.class || v.getClass() == Integer.class || v.getClass() == Long.class)
			return true;
		else
			return false;
	}
	public static VariableData clone(VariableData data){
		VariableData newData = null;
		if(data instanceof VariableData.Integer){
			newData = new VariableData.Integer(data);
		}else if(data instanceof VariableData.Double){
			newData = new VariableData.Double(data);
		}else if(data instanceof VariableData.Float){
			newData = new VariableData.Float(data);
		}else if(data instanceof VariableData.String){
			newData = new VariableData.String(data);
		}else if(data instanceof VariableData.Boolean){
			newData = new VariableData.Boolean(data);
		}else if(data instanceof VariableData.Instance){
			return data;
		}else{
			System.err.println("ERROR: clone type not supported");
		}
		return newData;
	}
	static class Integer extends VariableData{
		int value = 0;
		public Integer(VariableData variableData) {
			if(variableData instanceof VariableData.Integer){
				this.value = ((VariableData.Integer) variableData).value;
			}else{
				try{
					throw new InputMismatchException();
				}catch(InputMismatchException e){
					e.printStackTrace();
				}
			}
		}
		public Integer(int input) {
			this.value = input;
		}
		public Integer(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			if(value == java.lang.Integer.MAX_VALUE)
				return java.lang.Integer.toString(value)+" (INT MAX VALUE)";
			
			return java.lang.Integer.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Double extends VariableData{
		double value = 0;
		public Double(VariableData variableData) {
			if(variableData instanceof VariableData.Double){
				this.value = ((VariableData.Double) variableData).value;
			}else{
				try{
					throw new InputMismatchException();
				}catch(InputMismatchException e){
					e.printStackTrace();
				}
			}
		}
		public Double(double input) {
			this.value = input;
		}
		public Double(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Double.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Float extends VariableData{
		float value = 0;
		public Float(VariableData variableData) {
			if(variableData instanceof VariableData.Float){
				this.value = ((VariableData.Float) variableData).value;
			}else{
				try{
					throw new InputMismatchException();
				}catch(InputMismatchException e){
					e.printStackTrace();
				}
			}
		}
		public Float(float input) {
			this.value = input;
		}
		public Float(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Float.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Long extends VariableData{
		long value = 0;
		public Long(VariableData variableData) {
			if(variableData instanceof VariableData.Long){
				this.value = ((VariableData.Long) variableData).value;
			}else{
				try{
					throw new InputMismatchException();
				}catch(InputMismatchException e){
					e.printStackTrace();
				}
			}
		}
		public Long(long input) {
			this.value = input;
		}
		public Long(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Long.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Short extends VariableData{
		short value = 0;
		public Short(short input) {
			this.value = input;
		}
		public Short(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Short.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Byte extends VariableData{
		byte value = 0;
		public Byte(byte input) {
			this.value = input;
		}
		public Byte(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Byte.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Boolean extends VariableData{
		boolean value = false;
		public Boolean(VariableData variableData) {
			if(variableData instanceof VariableData.Boolean){
				this.value = ((VariableData.Boolean) variableData).value;
			}else{
				try{
					throw new InputMismatchException();
				}catch(InputMismatchException e){
					e.printStackTrace();
				}
			}
		}
		public Boolean(boolean input) {
			this.value = input;
		}
		public Boolean(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Boolean.toString(value);
		}
	}
	static class String extends VariableData{
		java.lang.String value = "";
		public String(VariableData variableData) {
			if(variableData instanceof VariableData.String){
				this.value = ((VariableData.String) variableData).value;
			}else{
				try{
					throw new InputMismatchException();
				}catch(InputMismatchException e){
					e.printStackTrace();
				}
			}
		}
		public String(java.lang.String input) {
			this.value = input;
		}
		public String(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return value;
		}
	}
	static class Character extends VariableData{
		char value = ' ';
		public Character(char input) {
			this.value = input;
		}
		public Character(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Character.toString(value);
		}
	}
	static class Instance extends VariableData{
		static final Random rng = new Random();
		
		ArrayList<VariableData> values;
		ArrayList<java.lang.String> names;
		private java.lang.String name;
		public InstantiableBlueprint parentBlueprint;
		
		public Instance(VInstance i) {
			values = new ArrayList<VariableData>();
			names = new ArrayList<java.lang.String>();
			for(Variable v : i.childVariables){
				values.add(v.varData);
				names.add(v.getID());
			}
			this.parentBlueprint = (InstantiableBlueprint) i.parentBlueprint;
			this.name = parentBlueprint.getName()+"::"+rng.nextInt(java.lang.Integer.MAX_VALUE);
		}
		public Instance(){
			
		}
		public Instance(InstantiableBlueprint bp) {
			values = new ArrayList<VariableData>();
			names = new ArrayList<java.lang.String>();
			for(Variable v : bp.getVariables()){
				values.add(v.varData);
				names.add(v.getID());
			}
			this.parentBlueprint = bp;
			this.name = parentBlueprint.getName()+"::"+rng.nextInt(java.lang.Integer.MAX_VALUE);
		}
		@Override
		public java.lang.String getValueAsString(){
			return "<"+name+">";
		}
		
		public VariableData getVariableDataByName(java.lang.String string){
			for(int i = 0; i < values.size(); i++){
				if(names.get(i).equals(string))
					return values.get(i);
			}
			return null;
		}
		
		public java.lang.String getJSON(){
			java.lang.String s = "{";
			
			for(int i = 0; i < values.size(); i++){
				java.lang.String n =  names.get(i);
				VariableData v = values.get(i);
				s += ("\n    \""+n+"\" : "+((v instanceof Instance) ? "\n"+((Instance) v).getJSON() : v.getValueAsString()));
			}
			
			s += "\n}";
			
			return s;
		}
	}
	static class Array extends VariableData{
		public Variable.DataType dataType;
		public ArrayList<VariableData> value;
		
		Array(Variable.DataType dataType, ArrayList<VariableData> value){
			this.dataType = dataType;
			this.value = value;
		}

		Array(Variable.DataType dataType) {
			this.dataType = dataType;
			this.value = new ArrayList<VariableData>();
		}

		public void add(VariableData data) {
			value.add(data);
		}
		
		@Override
		public java.lang.String getValueAsString(){
			java.lang.String s = "";
			for(VariableData v : value){
				s += (v.getValueAsString()+",");
			}
			if (s.length() > 0 && s.charAt(s.length()-1)==',') {
				s = s.substring(0, s.length()-1);
			}
			return s;
		}
	}
}