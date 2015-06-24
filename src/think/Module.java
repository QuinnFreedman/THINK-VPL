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

import java.util.ArrayList;

public class Module{
	private ArrayList<Executable> functions = new ArrayList<Executable>();
	private String name;
	
	/**
	 * Gets all functions registered to the module.
	 * 
	 * @return the list of all child functions in the module
	 */
	protected ArrayList<Executable> getFunctions(){
		return functions;
	}
	
	/**
	 * Register a child function with the parent module.
	 * Every function must be registered in order to be accessed by the user.
	 * 
	 * @param e - the executable to be registered.
	 */
	protected void addFunction(Executable e){
		functions.add(e);
	}
	
	/**
	 * Specify the name of the module as it will appear on all child functions.
	 * 
	 * @param name - the name of the module.
	 */
	protected void setModuleName(String name){
		this.name = name;
	}
	
	/**
	 * 
	 * @return the name of the module.  If `name` is null, will return the name of the Module subclass
	 */
	public String getModuleName(){
		if(name != null){
			return name;
		}else{
			String[] strs = getClass().getName().replace('.',':').split(":");
			return strs[strs.length-1];
		}
	}
	
	/**
	 * setup() is called once when the module is added to the project;
	 * override it to perform whatever actions are necessary to setup the module.
	 * 
	 * Use instead of a constructor; as the core code changes, constructors may or may not be called.
	 * in setup(), register all child functions using Module.addFunction(Executable e);
	 */
	public void setup(){
		
	}
	
	/**
	 * run() is called every time the user's program is run.
	 * Override it to perform whatever actions are necessary to setup the module.
	 */
	public void run() {
		
	}
	
}