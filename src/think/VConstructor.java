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

import java.awt.Point;

 class VConstructor extends UserFunc{
	private static final long serialVersionUID = 1L;

	VConstructor(Point pos, InstantiableBlueprint parent, GraphEditor owner) {
		super(pos, parent, owner);
		
	}
	
	@Override
	public VariableData execute(VariableData[] inputs){
		
		((InstantiableBlueprint) parentFunc).setWorkingInstance(new VariableData.Instance((InstantiableBlueprint) parentFunc, true));
		
		return null;
	}
	
}