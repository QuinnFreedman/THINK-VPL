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

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import think.Node.NodeType;

class SaveFileIO{
	private static final String VERSION_ID = "0.1.3";
	
	static void write(File file) throws FileNotFoundException, IOException{
		Document document = new Document();
		Element root = new Element("document");
		root.setAttribute("version", VERSION_ID);
		for(Blueprint bp : Main.blueprints){
			
			Element blueprint = writeGraphEditor(bp);
			
			for(VFunction f : bp.getFunctions()){
				blueprint.addContent(writeGraphEditor(f.getEditor()));
			}
			
			root.addContent(blueprint);
		}
		document.setRootElement(root);
		XMLOutputter output = new XMLOutputter();
		output.setFormat(Format.getPrettyFormat());
		output.output(document, new FileOutputStream(file));
	}
	
	static Element writeGraphEditor(GraphEditor ge){
		Element element;
		
		if(ge instanceof InstantiableBlueprint){
			element = new Element("ibp");
			String s = "";
			for(int i = 1; i < ((InstantiableBlueprint) ge).getInput().size(); i++){
				s += ((InstantiableBlueprint) ge).getInput().get(i).toString()+",";
			}
			if(s.length() > 0)
				s = s.substring(0, s.length()-1);
			
			element.setAttribute("inputs", s);
			element.setAttribute("id", ((Blueprint) ge).getName());
		}else if(ge instanceof Blueprint){
			element = new Element("bp");
			element.setAttribute("id", ((Blueprint) ge).getName());
		}else if (ge instanceof FunctionEditor){
			element = new Element("func");
			element.setAttribute("id", ((VFunction) ((FunctionEditor) ge).getOverseer()).getID());
		}else{
			element = null;
			System.err.println("ERROR WRITING FILE");
		}
		
		//WRITE VARIABLES
		
		Element variables = new Element("variables");
		
		for(Variable v : ge.getVariables()){
			Element e = new Element("variable");
			
			e.setAttribute("type", v.getClass().getName());
			e.setAttribute("id", v.getID());
			if(!(v instanceof VInstance)){
				e.setAttribute("value", v.valueField.getText());
			}else{
				e.setAttribute("bp", ((VInstance) v).parentBlueprint.getName());
			}
			variables.addContent(e);
		}
		
		//WRITE OBJECTS
		
		Element objects = new Element("objects");
		
		for(VObject o : ge.getObjects()){
			if(!(o instanceof Executable))
				continue;
			
			String parentVar = "";
			String dataType = "";
			if((o instanceof PrimitiveFunction)){
				
				Variable parentVariable = ((PrimitiveFunction) o).getParentVariable().getOriginal();
				
				if(parentVariable.getOwner() == null){
					parentVar += "null"; // shouldn't happen
				}else if(parentVariable.getOwner() instanceof Blueprint){
					parentVar += ((Blueprint) parentVariable.getOwner()).getName();
				}else{
					//if function - shouldn't happen
				}
				
				parentVar += ":"+((PrimitiveFunction) o).getParentVariable().getID();
			}else if (o instanceof Constant){
				dataType = ((Constant) o).dt.toString();
			}else if(o instanceof Console.getStr){
				dataType = ((Console.getStr) o).getDataType().toString();
			}else if(o instanceof FunctionEditor.FunctionIO){
				FunctionEditor.FunctionIO io = ((FunctionEditor.FunctionIO) o);
				for(Node node : ((io.mode == FunctionEditor.FunctionIO.Mode.INPUT) ? io.getOutputNodes() : io.getInputNodes())){
					dataType += node.dataType.toString()+":";
				}
			}else if(o instanceof Cast){
				Cast cast = ((Cast) o);
				dataType += cast.getInput().toString()+":"
					+cast.getOutput().toString();
			}
			
			objects.addContent(
					new Element("object")
						.setAttribute("x", Integer.toString(o.getLocation().x))
						.setAttribute("y", Integer.toString(o.getLocation().y))
						.setAttribute("class", o.getClass().getName())
						.setAttribute("id", o.getUniqueID())
						.setAttribute("parentVar", parentVar)
						.setAttribute("dataType", dataType)
						.addContent((o instanceof Constant) ? ((Constant) o).getText() : null)
					);
		}
		
		//WRITE WIRES
		
		Element wires = new Element("wires");
		
		for(Curve c : ge.getCurves()){
			if(!c.isNode[0] || !c.isNode[0] || !(c.nodes[0].parentObject instanceof Executable)
					|| !(c.nodes[0].parentObject instanceof Executable))
				continue;
			
			wires.addContent(
					new Element("wire")
						.setAttribute("objSend", c.nodes[0].parentObject.getUniqueID())
						.setAttribute("nodeSend", Integer.toString(
								((c.nodes[0].type == NodeType.SENDING) ? ((Executable) c.nodes[0].parentObject).getOutputNodes() : ((Executable) c.nodes[0].parentObject).getInputNodes())
									.indexOf(c.nodes[0])))
						.setAttribute("objRecieve", c.nodes[1].parentObject.getUniqueID())
						.setAttribute("nodeRecieve", Integer.toString(
								((c.nodes[1].type == NodeType.SENDING) ? ((Executable) c.nodes[1].parentObject).getOutputNodes() : ((Executable) c.nodes[1].parentObject).getInputNodes())
									.indexOf(c.nodes[1])))
										
					);
		}
		
		element.addContent(variables);
		element.addContent(objects);
		element.addContent(wires);
		return element;
	}
	
	static void read(File f) 
			throws JDOMException, IOException, ClassNotFoundException, 
			NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, 
			IllegalArgumentException, InvocationTargetException
	{
		SAXBuilder reader = new SAXBuilder();
		Document document = reader.build(f);
		Element root = document.getRootElement();
		List<Element> bps1 = root.getChildren("bp");
		List<Element> bps2 = root.getChildren("ibp");
		
		ArrayList<Element> blueprints = new ArrayList<Element>();
		blueprints.addAll(bps1);
		blueprints.addAll(bps2);
		Collections.reverse(blueprints);
		
		//LOAD BLUEPRINTS
		for(Element bp : blueprints){
			Blueprint newBlueprint;
			if(bp.getName().equals("bp") && bp.getAttribute("id").getValue().equals("Main")){
				//do nothing
			}else{
				String id = bp.getAttributeValue("id");
				newBlueprint = new InstantiableBlueprint();
				newBlueprint.setName(id);
            	Main.blueprints.add(newBlueprint);
            	Main.tabbedPane.insertTab(id, null, newBlueprint.splitPane, null, 1);
            	((InstantiableBlueprint) newBlueprint).className.setText(id);
			}
		}
		
		//LOAD VARIABLES
		for(Element bp : blueprints){
			
			Blueprint owner = getBlueprintById(bp.getAttributeValue("id"));
			
			Element variables = bp.getChild("variables");
			List<Element> varList = variables.getChildren();
			for(Element var : varList){
				Out.println(" > "+var.getAttributeValue("id"));
				String className = var.getAttributeValue("type");
				Class varClass = ClassLoader.getSystemClassLoader().loadClass(className);
				Variable newVar;
				if(varClass == VInstance.class){
					Blueprint parentBlueprint = getBlueprintById(var.getAttributeValue("bp"));
					
					newVar = (Variable) varClass.getDeclaredConstructor(GraphEditor.class, Blueprint.class).newInstance(owner, parentBlueprint);//TODO
				}else{
					newVar = (Variable) varClass.getDeclaredConstructor(GraphEditor.class).newInstance(owner);
					newVar.valueField.setText(var.getAttributeValue("value"));
				}
				newVar.nameField.setText(var.getAttributeValue("id"));
				owner.getVariables().add(newVar);
			}
			owner.updateVars();
			owner.scrollVars.getViewport().setViewPosition(new Point(0,0));
			
		}
		
		//LOAD OBJECTS
		for(Element bp : blueprints){
			
			GraphEditor owner = getBlueprintById(bp.getAttributeValue("id"));
			
			ArrayList<Element> casts = new ArrayList<Element>();//TODO handle casts
			
			Element objects = bp.getChild("objects");
			List<Element> objList = objects.getChildren();
			for(Element obj : objList){
				String className = obj.getAttributeValue("class");
				Out.println(className);
				Class<? extends VObject> objClass= (Class<? extends VObject>) ClassLoader.getSystemClassLoader().loadClass(className);
				VObject newObj;
				Point p = new Point(Integer.parseInt(obj.getAttributeValue("x")), Integer.parseInt(obj.getAttribute("y").getValue()));
				if(objClass == EntryPoint.class){
					//do nothing
					continue;
				}else if(objClass == FunctionEditor.FunctionIO.class){
					String[] dataTypes = obj.getAttributeValue("dataType").split(":");
					
					for(String s : dataTypes){
						Variable.DataType dt = Variable.DataType.valueOf(s);
						if(dt == null){
							Out.println("!WARNING: data type is null");
							continue;
						}
						
						if(owner instanceof InstantiableBlueprint){
							
							if(obj.getAttributeValue("id").equals("0") && dt != Variable.DataType.GENERIC)
								((InstantiableBlueprint) owner).addInputNode(dt, (InstantiableBlueprint) owner);
							else
								continue;
							
						}else if(owner instanceof FunctionEditor){
							
							if(obj.getAttributeValue("id").equals("0"))
								((FunctionEditor) owner).addInputNode(dt);
							else
								((FunctionEditor) owner).addOutputNode(dt);
						}
					}
					
					continue;
				}else if(PrimitiveFunction.class.isAssignableFrom(objClass)){
					String[] parentVarStrs = obj.getAttributeValue("parentVar").split(":");
					Blueprint parentBlueprint = getBlueprintById(parentVarStrs[0]);
					Variable parentVar = getVariableById(parentBlueprint, parentVarStrs[1]);
					
					Constructor<?> constructor = objClass.getDeclaredConstructor(Point.class, Variable.class, GraphEditor.class);
					newObj = (VObject) constructor.newInstance(
							p, 
							parentVar,
							owner
						);
				}else if(objClass == Constant.class || objClass == Console.getStr.class){
					Constructor<?> constructor = objClass.getDeclaredConstructor(Point.class, Variable.DataType.class, GraphEditor.class);
					newObj = (VObject) constructor.newInstance(
							p,
							Variable.DataType.valueOf(obj.getAttributeValue("dataType")),
							owner
						);
					if(objClass == Constant.class){
						((Constant) newObj).editor.setText(obj.getText());
					}
				}else if(objClass == Cast.class){
					String[] dataType = obj.getAttributeValue("dataType").split(":");
					
					Constructor<?> constructor = objClass.getDeclaredConstructor(Point.class, Variable.DataType.class, Variable.DataType.class, GraphEditor.class);
					newObj = (Cast) constructor.newInstance(
							new Point(Integer.parseInt(obj.getAttributeValue("x")), Integer.parseInt(obj.getAttribute("y").getValue())),
							Variable.DataType.valueOf(dataType[0]),
									Variable.DataType.valueOf(dataType[1]),
							owner
						);
				}else{
					Constructor<?> constructor = objClass.getDeclaredConstructor(Point.class, GraphEditor.class);
					newObj = (VObject) constructor.newInstance(
							new Point(Integer.parseInt(obj.getAttributeValue("x")), Integer.parseInt(obj.getAttribute("y").getValue())),
							owner
						);
				}
				newObj.setUniqueID(obj.getAttributeValue("id"));
			}
			
		}
		
		//LOAD WIRES
		for(Element bp : blueprints){
			
			Blueprint owner = getBlueprintById(bp.getAttributeValue("id"));
			
			Element wires = bp.getChild("wires");
			List<Element> curveList = wires.getChildren();
			for(Element curve : curveList){
				VObject sendObj = getObjectById(owner, curve.getAttributeValue("objSend"));
				VObject recObj = getObjectById(owner, curve.getAttributeValue("objRecieve"));
				Executable sendEx;
				Executable recEx;
				if(!(sendObj instanceof Executable && recObj instanceof Executable)){
					Out.println("ERROR : ");
					Out.println("sendObj = "+sendObj);
					Out.println("recObj = "+recObj);
					continue;
				}else{
					sendEx = (Executable) sendObj;
					recEx = (Executable) recObj;
				}
				Node.connect(
						sendEx.getOutputNodes().get(Integer.parseInt(curve.getAttributeValue("nodeSend"))),
						recEx.getInputNodes().get(Integer.parseInt(curve.getAttributeValue("nodeRecieve")))
					);
			}
			
		}
	}
	
	private static Blueprint getBlueprintById(String name) {
		for(Blueprint bp : Main.blueprints){
			if(bp.getName().equals(name)){
				return bp;
			}
		}
		return null;
	}
	
	private static Variable getVariableById(Blueprint bp, String name) {
		for(Variable v : bp.getVariables()){;
			if(v.getID().equals(name)){
				return v;
			}
		}
		return null;
	}
	
	private static VObject getObjectById(GraphEditor bp, String UID){
		//Out.println("looking for "+UID+" in :");
		for(VObject o : bp.getObjects()){
			//Out.println(o.getUniqueID());
			if(o.getUniqueID().equals(UID)){
				return o;
			}
		}
	return null;
	}
	
}