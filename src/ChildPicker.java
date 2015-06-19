import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ChildPicker extends VObject{
	private static final long serialVersionUID = 1L;
	Node parentNode;
	ArrayList<MenuItem> items;
	String searchKey = "";
	JPanel itemHolder;
	ChildPicker(Node parentNode, Point position, GraphEditor owner){
		super(owner);
		this.parentNode = parentNode;
		
		owner.getPanel().setComponentZOrder(this, 0);
		
		Node connectingNode = new Node((parentNode.type == Node.NodeType.SENDING) ? Node.NodeType.RECIEVING : Node.NodeType.SENDING,
				this,
				parentNode.dataType,
				false){
					private static final long serialVersionUID = 1L;
					
					@Override
					public Dimension getPreferredSize(){
						return new Dimension(0,0);
					}
					@Override
					public void mousePressed(MouseEvent e){
						
					}
					@Override
					public void mouseReleased(MouseEvent e){
						
					}
					@Override
					public void mouseDragged(MouseEvent e){
						
					}
					@Override
					public void mouseClicked(MouseEvent e){
						
					}
					@Override
					public void onDisconnect(){
						this.parentObject.delete();
					}
				{}};
		connectingNode.facing = Node.Direction.WEST;
		
		JPanel nodeBorder = new JPanel();
		nodeBorder.setOpaque(false);
		nodeBorder.setBorder(BorderFactory.createEmptyBorder(7,-10,0,0));
		nodeBorder.add(connectingNode);
		
		headerLabel = new JLabel();
		headerLabel.setOpaque(false);
		headerLabel.setBorder(BorderFactory.createEmptyBorder(4, 7, 2, 7));
		
		headerLabel.setText("Pick one...");
		
		JPanel header = new JPanel(new BorderLayout(0,0));
		header.add(nodeBorder, BorderLayout.LINE_START);
		header.add(headerLabel, BorderLayout.CENTER);
		header.setOpaque(false);
		
		this.body.setLayout(new BoxLayout(body, 1));
		this.body.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		body.add(header);
		
		Node.castOrConnect(parentNode, connectingNode);
		
		JTextField search = new JTextField();
		search.setOpaque(false);
		search.getDocument().addDocumentListener(new DocumentListener(){{}
		
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				searchKey = search.getText();
				resetItems();
			}
	
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				searchKey = search.getText();
				resetItems();
			}
	
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				searchKey = search.getText();
				resetItems();
				
		}});
		search.addActionListener(new ActionListener(){{}

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(itemHolder != null && itemHolder.getComponentCount() != 0)
					((MenuItem) itemHolder.getComponent(0)).createObject();
				
			}
		});
		body.add(search);
		search.requestFocusInWindow();
		
		itemHolder = new JPanel();
		itemHolder.setLayout(new BoxLayout(itemHolder, 1));
		itemHolder.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		itemHolder.setOpaque(false);
		JScrollPane itemScrollPane = new JScrollPane(itemHolder);
		itemScrollPane.setOpaque(false);
		body.add(itemScrollPane);
		
		items = new ArrayList<MenuItem>();
		
		for(Blueprint bp : Main.blueprints){
			for(Variable v : bp.getVariables()){
				for(PrimitiveFunction f : ((ContainsChildFunctions) v).getFunctions()){
					considerAdding(f);
				}
			}
			for(VFunction f : bp.getFunctions()){
				considerAdding(f);
			}
		}
		
		if(owner instanceof FunctionEditor){
			for(Variable v : owner.getVariables()){
				for(PrimitiveFunction f : ((ContainsChildFunctions) v).getFunctions()){
					considerAdding(f);
				}
			}
			
		}
		
		/*for(Variable v : owner.getVariables()){
			for(PrimitiveFunction f : ((ContainsChildFunctions) v).getFunctions()){
				considerAdding(f);
			}
			if(v instanceof VInstance){
				for(Variable v2 : ((VInstance) v).childVariables){
					for(PrimitiveFunction f2 : ((ContainsChildFunctions) v2).getFunctions()){
						considerAdding(f2);
					}
				}
				for(VFunction f3 : ((VInstance) v).childFunctions){
					considerAdding(f3);
				}
			}
		}
		if(owner instanceof Blueprint){
			for(VFunction f : ((Blueprint) owner).getFunctions()){
				considerAdding(f);
			}
			
		}else if(owner instanceof FunctionEditor){
			for(VFunction f : ((Blueprint) ((VFunction) ((FunctionEditor) owner).getOverseer()).getOwner()).getFunctions()){
				considerAdding(f);
			}
			for(Variable v : ((Blueprint) ((VFunction) ((FunctionEditor) owner).getOverseer()).getOwner()).getVariables()){
				for(PrimitiveFunction f2 : ((ContainsChildFunctions) v).getFunctions()){
					considerAdding(f2);
				}
				if(v instanceof VInstance){
					for(Variable v2 : ((VInstance) v).childVariables){
						for(PrimitiveFunction f3 : ((ContainsChildFunctions) v2).getFunctions()){
							considerAdding(f3);
						}
					}
				}
			}
		}
		
		if(owner != Main.mainBP){
			for(Variable v : Main.mainBP.getVariables()){
				for(PrimitiveFunction f : ((ContainsChildFunctions) v).getFunctions()){
					considerAdding(f);
				}
				if(v instanceof VInstance){
					for(Variable v2 : ((VInstance) v).childVariables){
						for(PrimitiveFunction f2 : ((ContainsChildFunctions) v2).getFunctions()){
							considerAdding(f2);
						}
					}
					for(VFunction f3 : ((VInstance) v).childFunctions){
						considerAdding(f3);
					}
				}
			}
			for(VFunction f : Main.mainBP.getFunctions()){
				considerAdding(f);
			}
			
		}*/
		
		for(Module m : Main.modules){
			for(Executable e : m.functions){
				considerAdding(e);
			}
		}
		
		if(this.owner instanceof InstantiableBlueprint){
			items.add(new MenuItem(ContextualPointer.class,"THIS",this));
		}
		
		if(parentNode.type == Node.NodeType.SENDING && parentNode.dataType.isNumber()){
			items.add(new MenuItem(Logic.GreaterThan.class,"Is Greater Than (>)",this));
			items.add(new MenuItem(Logic.LessThan.class,"Is Less Than (<)",this));
			items.add(new MenuItem(Logic.GreaterOrEqual.class,"Is Greater Than Or Equal To (\u2265)",this));
			items.add(new MenuItem(Logic.LessOrEqual.class,"Is Less Than Or Equal To (\u2264)",this));
		}
		
		if((parentNode.type == Node.NodeType.SENDING && parentNode.dataType.isNumber()) ||
		(parentNode.type == Node.NodeType.RECIEVING && (parentNode.dataType.isNumber() || parentNode.dataType == Variable.DataType.STRING))){

			items.add(new MenuItem(Arithmetic.Add.class,"Add (+)",this));
			items.add(new MenuItem(Arithmetic.Subtract.class,"Subtract (-)",this));
			items.add(new MenuItem(Arithmetic.Multiply.class,"Multiply (\u00D7)",this));
			items.add(new MenuItem(Arithmetic.Divide.class,"Divide (\u00F7)",this));
			items.add(new MenuItem(Arithmetic.Round.class,"Round (\u2248)",this));
		}
		if(parentNode.type == Node.NodeType.RECIEVING && (parentNode.dataType.isNumber() || parentNode.dataType == Variable.DataType.STRING)){
			
			items.add(new MenuItem(Arithmetic.Random.class,"Random (?)",this));
		}
		
		if(parentNode.type == Node.NodeType.SENDING){
			items.add(new MenuItem(Console.Log_To_Console.class,"Log To Console",this));
			if(parentNode.dataType != Variable.DataType.GENERIC){
				items.add(new MenuItem(Logic.Equals.class,"Equals (=)",this));
			}
			if(parentNode.dataType == Variable.DataType.BOOLEAN){
				items.add(new MenuItem(Logic.And.class,"And (&)",this));
				items.add(new MenuItem(Logic.Or.class,"Or (||)",this));
				items.add(new MenuItem(Logic.Not.class,"Not (!)",this));
				items.add(new MenuItem(Logic.While.class,"While...",this));
				items.add(new MenuItem(Logic.Branch.class,"Branch",this));
			}else if(parentNode.dataType == Variable.DataType.OBJECT){
				items.add(new MenuItem(VInstance.Get_Name.class,"Get Object Name",this));
				items.add(new MenuItem(VInstance.Get_JSON.class,"Get Object JSON",this));
			}
		}else if(parentNode.type == Node.NodeType.RECIEVING){
			if(parentNode.dataType != Variable.DataType.GENERIC){
				if(parentNode.dataType == Variable.DataType.NUMBER || parentNode.dataType == Variable.DataType.FLEX){
					items.add(new MenuItem(Constant.class,"Constant Float",this,Variable.DataType.FLOAT));
					items.add(new MenuItem(Constant.class,"Constant Integer",this,Variable.DataType.INTEGER));
					items.add(new MenuItem(Constant.class,"Constant Double",this,Variable.DataType.DOUBLE));
					if(parentNode.dataType == Variable.DataType.FLEX){
						items.add(new MenuItem(Constant.class,"Constant Boolean",this,Variable.DataType.BOOLEAN));
						items.add(new MenuItem(Constant.class,"Constant String",this,Variable.DataType.STRING));
					}
				}else{
					String str = parentNode.dataType.toString().toLowerCase();
					str = str.substring(0,1).toUpperCase() + str.substring(1);
					items.add(new MenuItem(Constant.class,"Constant "+str,this,parentNode.dataType));
				}
			}
			if(parentNode.dataType == Variable.DataType.BOOLEAN){
				items.add(new MenuItem(Logic.And.class,"And (&)",this));
				items.add(new MenuItem(Logic.Or.class,"Or (||)",this));
				items.add(new MenuItem(Logic.Not.class,"Not (!)",this));
				items.add(new MenuItem(Logic.Equals.class,"Equals (=)",this));
				items.add(new MenuItem(Logic.GreaterThan.class,"Is Greater Than (>)",this));
				items.add(new MenuItem(Logic.LessThan.class,"Is Less Than (<)",this));
				items.add(new MenuItem(Logic.GreaterOrEqual.class,"Is Greater Than Or Equal To (\u2265)",this));
				items.add(new MenuItem(Logic.LessOrEqual.class,"Is Less Than Or Equal To (\u2264)",this));
			}else if(parentNode.dataType == Variable.DataType.NUMBER || parentNode.dataType.isNumber()){
				items.add(new MenuItem(Console.getStr.class,"Get Number From Console",this,Variable.DataType.DOUBLE));
			}else if(parentNode.dataType == Variable.DataType.STRING){
				items.add(new MenuItem(Console.getStr.class,"Get String From Console",this,Variable.DataType.STRING));
			}
		}
		
		if(parentNode.dataType == Variable.DataType.GENERIC){
			items.add(new MenuItem(Logic.Branch.class,"Branch",this));
			items.add(new MenuItem(Logic.While.class,"While...",this));
			items.add(new MenuItem(Logic.Sequence.class,"Sequence",this));
			items.add(new MenuItem(Logic.Wait.class,"Wait",this));
			for(Blueprint bp : Main.blueprints){
				if(bp instanceof InstantiableBlueprint){
					items.add(new MenuItem(((InstantiableBlueprint) bp),this));
				}
			}
		}else if(parentNode.dataType == Variable.DataType.STRING){
			items.add(new MenuItem(Arithmetic.Concat.class,"Concatinate",this));
			items.add(new MenuItem(VInstance.Get_Name.class,"Get Object Name",this));
			items.add(new MenuItem(VInstance.Get_JSON.class,"Get Object JSON",this));
		}
		
		Collections.sort(items, new MenuItemComparator());
		
		resetItems();
		
		this.setBounds(new Rectangle(position,new Dimension(Math.min(Math.max(this.getPreferredSize().width,100), 400),Math.min(this.getPreferredSize().height+5,200))));
	}
	
	private void considerAdding(Executable f){
		if((parentNode.type == Node.NodeType.SENDING && f.getInputs() != null && couldConnect(parentNode.dataType,f.getInputs())) ||
				(parentNode.type == Node.NodeType.RECIEVING && f.getOutputs() != null && couldConnect(f.getOutputs(),parentNode.dataType))	
					){
			this.items.add(new MenuItem(f,this));
		}
	}
	
	private void considerAdding(VFunction f){
		if(couldConnect(parentNode.dataType,f.getInput()) || couldConnect(f.getOutput(),parentNode.dataType)){
			this.items.add(new MenuItem(f,this));
		}
	}
	
	private void resetItems(){
		itemHolder.removeAll();
		for(MenuItem i : items){
			if(containsSearchTerms(i.name.toLowerCase(),searchKey.toLowerCase())){
				itemHolder.add(i);
			}
		}
		itemHolder.revalidate();
		itemHolder.repaint();
	}
	private static boolean containsSearchTerms(String text, String key){
		if(key.equals(""))
			return true;
		String[] keys = key.split(" ");
		for(String s : keys){
			if(!text.contains(s))
				return false;
		}
		
		return true;
	}
	private static boolean couldConnect(ArrayList<Variable.DataType> a, Variable.DataType b){
		System.out.print("Could Connect ("+a+", "+b+") = ");
		if(a.contains(b)){
			System.out.println("true - contains");
			return true;
		}else{
			for(Variable.DataType d : a){
				if(Cast.isCastable(d, b)){
					System.out.println("true - castable");
					return true;
				}else if((d == Variable.DataType.NUMBER && b.isNumber()) ||
						(b == Variable.DataType.NUMBER && d.isNumber()) ||
						(d == Variable.DataType.FLEX && !b.isNumber()) ||
						(b == Variable.DataType.FLEX && !d.isNumber())
						){
					return true;
				}
			}
			System.out.println("false");
			return false;
		}
	}
	private static boolean couldConnect(Variable.DataType a, ArrayList<Variable.DataType> b){
		System.out.print("Could Connect ("+a+", "+b+") = ");
		if(b.contains(a)){
			System.out.println("true - contains");
			return true;
		}else{
			for(Variable.DataType d : b){
				if(Cast.isCastable(a, d)){
					System.out.println("true - castable");
					return true;
				}else if((a == Variable.DataType.NUMBER && d.isNumber()) ||
						(d == Variable.DataType.NUMBER && a.isNumber()) ||
						(a == Variable.DataType.FLEX && d != Variable.DataType.NUMBER && d != Variable.DataType.GENERIC) ||
						(d == Variable.DataType.FLEX && a != Variable.DataType.NUMBER && a != Variable.DataType.GENERIC)
						){
					return true;
				}
			}
			System.out.println("false");
			return false;
		}
	}
	
	private class MenuItem extends JLabel implements MouseListener{
		public String name;
		ChildPicker childPicker;
		private Executable f;
		private Class<? extends Executable> c;
		private VFunction vf;
		Variable.DataType dataType;
		private InstantiableBlueprint bp;
		
		MenuItem(Executable f, ChildPicker childPicker){
			super();
			this.f = f;
			
			this.name = "";
			if(f instanceof PrimitiveFunction){
				this.name += (f.getSimpleName()+" (");
				if(((PrimitiveFunction) f).getParentVar().getOwner() != childPicker.owner){
					if(((PrimitiveFunction) f).getParentVar().getOwner() instanceof Blueprint){
						this.name += ((Blueprint) (((PrimitiveFunction) f).getParentVar().getOwner())).getName()+" > ";
					}
				}
				this.name += ((((PrimitiveFunction) f).getParentVar().getFullName())+")");
			}else{
				this.name = "";
				if(f.owner != childPicker.owner){
					if(f.owner instanceof Blueprint){
						this.name += ((Blueprint) f.owner).getName()+" > ";
					}
				}
				if(f instanceof UserFunc && !((UserFunc) f).getParentVar().isStatic()){
					this.name += ((VFunction) ((UserFunc) f).getParentVar()).getFullName()+" > ";
				}
				this.name += f.getSimpleName();
			}
			this.setOpaque(false);
			this.childPicker = childPicker;
			this.setText(name);
			this.addMouseListener(this);
			this.setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		
		MenuItem(Class<? extends Executable> c, String name, ChildPicker childPicker){
			super();
			this.c = c;

			this.setOpaque(false);
			this.name = name;
			this.childPicker = childPicker;
			this.setText(name);
			this.addMouseListener(this);
			this.setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		
		MenuItem(Class<? extends Executable> class1, String string,
				ChildPicker childPicker2, Variable.DataType dataType) {
			this(class1,string,childPicker2);
			this.dataType = dataType;
		}
		
		MenuItem(VFunction function, ChildPicker childPicker){
			this.vf = function;
			
			String s = "";
			
			if(function.getOwner() != childPicker.owner){
				s += (((Blueprint) function.getOwner()).getName()+" > ");
			}
			s += vf.getFullName();
			
			this.name = s;
			this.setOpaque(false);
			this.childPicker = childPicker;
			this.setText(name);
			this.addMouseListener(this);
			this.setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		
		MenuItem(InstantiableBlueprint function, ChildPicker childPicker){
			this.bp = function;
			
			String s = "New "+function.getName();
			
			this.name = s;
			this.setOpaque(false);
			this.childPicker = childPicker;
			this.setText(name);
			this.addMouseListener(this);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// Auto-generated method stub
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			Font font = this.getFont();
			Map attributes = font.getAttributes();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			this.setFont(font.deriveFont(attributes));
			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			Font font = this.getFont();
			Map attributes = font.getAttributes();
			attributes.put(TextAttribute.UNDERLINE, -1);
			this.setFont(font.deriveFont(attributes));
			
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			createObject();
			
		}
		
		public void createObject(){
			Executable ex = null;
			try {
				Point pos = Node.getLocationOnPanel(this.childPicker, owner.getPanel());
				if(f instanceof PrimitiveFunction){
					Constructor<?> constructor = f.getClass().getDeclaredConstructor(Point.class, Variable.class, GraphEditor.class);
					ex = (PrimitiveFunction) constructor.newInstance(
							pos, 
							((PrimitiveFunction) f).getParentVar(),
							owner
						);
				}else if(f != null){
					Constructor<? extends Executable> constructor = f.getClass().getDeclaredConstructor(Point.class, GraphEditor.class);
					ex = constructor.newInstance(
							pos,
							owner
						);
				}else if(c != null){
					if(c == Constant.class || c == Console.getStr.class){
						Constructor<?> constructor = c.getDeclaredConstructor(Point.class, Variable.DataType.class, GraphEditor.class);
						ex = (Executable) constructor.newInstance(
								pos,
								dataType,
								owner
							);
					}else{
						Constructor<?> constructor = c.getDeclaredConstructor(Point.class, GraphEditor.class);
						ex = (Executable) constructor.newInstance(
								pos, 
								owner
							);
					}
				}else if(vf != null){
					ex = new UserFunc(pos, vf.getOriginal(), owner);
				}else if(bp != null){
					ex = new VConstructor(pos, bp, owner);
				}
				if(parentNode.type == Node.NodeType.SENDING){
					System.out.println(ex.getInputNodes());
					for(Node n : ex.getInputNodes()){
						System.out.println(Node.canConnect(parentNode, n));
						if(Node.canConnect(parentNode, n)){
							Node.castOrConnect(parentNode, n);
							break;
						}
					}
				}else if(parentNode.type == Node.NodeType.RECIEVING){
					for(Node n : ex.getOutputNodes()){
						if(Node.canConnect(n, parentNode)){
							Node.castOrConnect(n, parentNode);
							break;
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace(System.err);
			}
			
			this.childPicker.delete();
		}
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// Auto-generated method stub
			
		}
	}
	class MenuItemComparator implements Comparator<MenuItem> {
	    @Override
	    public int compare(MenuItem o1, MenuItem o2) {
	    	if(o1.name.contains("Constant ") && !o2.name.contains("Constant ")){
	    		return -1;
	    	}
	    	if(o2.name.contains("Constant ") && !o1.name.contains("Constant ")){
	    		return 1;
	    	}
	        return o1.name.compareTo(o2.name);
	    }
	}
}
