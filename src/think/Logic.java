/**
 * 
 *  THINK VPL is a visual programming language and integrated development environment for that language
 *  Copyright (C) 2015  Quinn Freedman
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

class Logic extends Executable{
	private static final long serialVersionUID = 1L;
	
	
	protected String getID(){
		return null;
	}
	
	@Override
	public ArrayList<Variable.DataType> getOutputs(){
		return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.BOOLEAN));
	}
	
	Logic(Point pos, GraphEditor owner){
		super(pos, owner);
		this.color = Color.DARK_GRAY;
		
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
		headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
		headerLabel.setText(getID());

	}
	
	Logic() {
		
	}
	
	static class Equals extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "=";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.FLEX,
					Variable.DataType.FLEX));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			if(VariableData.isNumber(inputs[0]) && VariableData.isNumber(inputs[0]))
				return new VariableData.Boolean((inputs[0].getValueAsDouble() == inputs[1].getValueAsDouble()));
			else
				return new VariableData.Boolean((inputs[0].getValueAsString().equals(inputs[1].getValueAsString())));
		}
		Equals(Point p, GraphEditor owner) {
			super(p, owner);
		}

		Equals() {
			
		}
		
	}
	
	static class Less_Than extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "<";
		}
		@Override
		public String getMenuName() {
			return"Less Than (<)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.NUMBER,
					Variable.DataType.NUMBER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() < inputs[1].getValueAsDouble()));
		}
		Less_Than(Point p, GraphEditor owner) {
			super(p, owner);
		}
		Less_Than(){
			
		}
	}
	
	static class Greater_Than extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return ">";
		}
		@Override
		public String getMenuName() {
			return"Greater Than (>)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.NUMBER,
					Variable.DataType.NUMBER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() > inputs[1].getValueAsDouble()));
		}
		Greater_Than(Point p, GraphEditor owner) {
			super(p, owner);
		}
		Greater_Than(){
			
		}
		
	}
	
	static class Less_Than_Or_Equal_To extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "\u2264";
		}
		@Override
		public String getMenuName() {
			return"Greater Than Or Equal To (\u2264)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.NUMBER,
					Variable.DataType.NUMBER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() <= inputs[1].getValueAsDouble()));
		}
		Less_Than_Or_Equal_To(Point p, GraphEditor owner) {
			super(p, owner);
		}
		Less_Than_Or_Equal_To(){
			
		}
		
	}
	
	static class Greater_Than_Or_Equal_To extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "\u2265";
		}
		@Override
		public String getMenuName() {
			return"Less Than Or Equal To (\u2265)";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.NUMBER,
					Variable.DataType.NUMBER));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() >= inputs[1].getValueAsDouble()));
		}
		Greater_Than_Or_Equal_To(Point p, GraphEditor owner) {
			super(p, owner);
		}
		Greater_Than_Or_Equal_To(){
			
		}
		
	}
	
	static class And extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "&";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean(((VariableData.Boolean) inputs[0]).value && ((VariableData.Boolean) inputs[1]).value);
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.BOOLEAN,
					Variable.DataType.BOOLEAN));
		}
		
		And(Point p, GraphEditor owner) {
			super(p, owner);
		}
		public And() {
			
		}
		
	}
	
	static class Or extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "||";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.BOOLEAN,
					Variable.DataType.BOOLEAN));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean(((VariableData.Boolean) inputs[0]).value || ((VariableData.Boolean) inputs[1]).value);
		}
		Or(Point p, GraphEditor owner) {
			super(p, owner);
			SwingUtilities.invokeLater(new Runnable() {
		        @Override
		        public void run() {
		        	headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()-6));
		        }});
		}

		Or() {
			
		}
		
	}
	
	static class Not extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "!";
		}
		
		@Override
		public ArrayList<Variable.DataType> getInputs(){
			return new ArrayList<Variable.DataType>(Arrays.asList(
					Variable.DataType.BOOLEAN));
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean(!((VariableData.Boolean) inputs[0]).value);
		}
		Not(Point p, GraphEditor owner) {
			super(p, owner);
			SwingUtilities.invokeLater(new Runnable() {
		        @Override
		        public void run() {
		        	headerLabel.setBorder(new EmptyBorder(new Insets(-8,-1,-1,-1)));
		        }});
		}

		public Not() {
			
		}
		
	}
	
}