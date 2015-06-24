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

package think;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

public class ContextualPointer extends Executable{
	private static final long serialVersionUID = 1L;

	ContextualPointer(Point pos, GraphEditor owner){
		super(pos,owner);
		this.color = Main.colors.get(Variable.DataType.OBJECT);
		
		if(!(owner instanceof InstantiableBlueprint)){
			try{
				throw(new IllegalArgumentException());
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public ArrayList<Variable.DataType> getOutputs() {
		return new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.OBJECT));
	}
	
	@Override
	public String getSimpleName() {
		return "THIS";
	}
	
	@Override
	public VariableData execute(VariableData[] inputs) {
		VariableData.Instance output;
		
		if(owner instanceof InstantiableBlueprint){
			output = (VariableData.Instance) ((InstantiableBlueprint) owner).getWorkingInstance();
		}else{
			try{
				throw(new IllegalArgumentException());
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}
			output = null;
		}
		
		return output;	
	}
}