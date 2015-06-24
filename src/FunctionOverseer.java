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

public interface FunctionOverseer{
	public FunctionEditor.FunctionIO getInputObject();
	public FunctionEditor.FunctionIO getOutputObject();
	
	public void removeOutput(int i);
	public void removeInput(int i);
	public void addOutput(Variable.DataType dataType);
	public void addInput(Variable.DataType dataType);
	
	public ArrayList<Variable.DataType> getInput();
	public ArrayList<Variable.DataType> getOutput();
	
	public void removeChild(UserFunc f);
	public void addChild(UserFunc f);
	public void clearChildren();
	
	public void setCurrentlyExecuting(UserFunc f);
	public UserFunc getCurrentlyExecuting();
	
	public boolean isStatic();
	
	void setInput(ArrayList<Variable.DataType> dt);
	void setOutput(ArrayList<Variable.DataType> dt);
	public boolean isEcexuteOnce();
	
	public GraphEditor getEditor();
	
	public VariableData.Instance getWorkingInstance();
}