import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class InstantiableBlueprint extends Blueprint implements FunctionOverseer, ComponentListener{
	
	private FunctionEditor.FunctionIO inputObject;
	private FunctionEditor.FunctionIO outputObject;
	private ArrayList<UserFunc> children;
	private UserFunc currentlyExecuting = null;
	private ArrayList<Variable.DataType> input = new ArrayList<Variable.DataType>();
	private ArrayList<Variable.DataType> output = new ArrayList<Variable.DataType>();
	
	JTextField className;
	
	InstantiableBlueprint(){
		JPanel holder = new JPanel(new BorderLayout(0,0));
		className = new JTextField();
		holder.add(className,BorderLayout.PAGE_START);
		holder.add(varsHeader,BorderLayout.CENTER);
		varsContainer.add(holder,BorderLayout.PAGE_START);
		
		className.addFocusListener(new SidebarItem.NoDuplicateNames(this,className));
		
		InstantiableBlueprint THIS = this;
		className.getDocument().addDocumentListener(new DocumentListener(){{}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update(className.getText());
				
			}
	
			@Override
			public void insertUpdate(DocumentEvent e) {
				update(className.getText());
				
			}
	
			@Override
			public void removeUpdate(DocumentEvent e) {
				update(className.getText());
				
			}
			private void update(String s){
				setName(s);
				Main.tabbedPane.setTitleAt(Main.blueprints.indexOf(THIS),Main.crop(s,20));
			}
		});
		
		JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		buttonHolder.add(new JLabel("Add input node:"));
		
		JButton button = new JButton("Integer");
		buttonHolder.add(button);
		
		button = new JButton("Double");
		buttonHolder.add(button);
		
		button = new JButton("Float");
		buttonHolder.add(button);
		
		button = new JButton("Boolean");
		buttonHolder.add(button);
		
		button = new JButton("String");
		buttonHolder.add(button);
		
		panelHolder.add(buttonHolder, BorderLayout.PAGE_START);
		
		inputObject = new FunctionEditor.FunctionIO(this, FunctionEditor.FunctionIO.Mode.INPUT);
		inputObject.addOutputNode(new Node(Node.NodeType.SENDING, inputObject, Variable.DataType.GENERIC));
		outputObject = new FunctionEditor.FunctionIO(this, FunctionEditor.FunctionIO.Mode.OUTPUT);
		outputObject.addInputNode(new Node(Node.NodeType.RECIEVING, outputObject, Variable.DataType.GENERIC));
		
		panel.addComponentListener(this);
	}
	
	
	@Override
	public FunctionEditor.FunctionIO getInputObject() {
		return inputObject;
	}

	@Override
	public FunctionEditor.FunctionIO getOutputObject() {
		return outputObject;
	}

	@Override
	public void addInput(Variable.DataType dataType){
		input.add(dataType);
		for(UserFunc f : children){
			f.addInputNode(new Node(Node.NodeType.RECIEVING,f,dataType, (dataType == Variable.DataType.GENERIC) ? true : false));
			f.inputNodeHolder.revalidate();
			f.inputNodeHolder.repaint();
		}
	}
	@Override
	public void addOutput(Variable.DataType dataType) {
		output.add(dataType);
		for(UserFunc f : children){
			f.addOutputNode(new Node(Node.NodeType.SENDING,f,dataType, (dataType == Variable.DataType.GENERIC) ? false : true));
			f.outputNodeHolder.revalidate();
			f.outputNodeHolder.repaint();
			f.setBounds(new Rectangle(f.getLocation(),f.getSize()));
			f.owner.getPanel().revalidate();
			f.owner.getPanel().repaint();
		}
	}
	
	@Override
	public void removeInput(int i){
		input.remove(i);
		for(UserFunc f : children){
			f.removeInputNode(f.getInputNodes().get(i));
		}
	}
	@Override
	public void removeOutput(int i){
		output.remove(i);
		for(UserFunc f : children){
			f.removeOutputNode(f.getOutputNodes().get(i));
		}
	}
	@Override
	public ArrayList<Variable.DataType> getInput() {
		return input;
	}
	@Override
	public ArrayList<Variable.DataType> getOutput() {
		return output;
	}
	@Override
	public void setCurrentlyExecuting(UserFunc f) {
		currentlyExecuting = f;
	}
	@Override
	public UserFunc getCurrentlyExecuting() {
		return currentlyExecuting;
	}

	@Override
	public void removeChild(UserFunc f){
		children.remove(f);
	}
	@Override
	public void addChild(UserFunc f){
		children.add(f);
	}
	@Override
	public void clearChildren(){
		while(children.size() > 0) {
			children.get(0).delete();
		}
	}
	@Override
	public boolean isStatic() {
		return true;
	}
	@Override
	public void componentResized(ComponentEvent e) {
		inputObject.setLocation((panel.getWidth()-inputObject.getWidth())/2,10);
		outputObject.setLocation((panel.getWidth()-outputObject.getWidth())/2,panel.getHeight()-48);
		
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		//Auto-generated method stub
	}
	
	@Override
	public void componentMoved(ComponentEvent arg0) {
		// Auto-generated method stub
	}
	
	@Override
	public void componentShown(ComponentEvent arg0) {
		// Auto-generated method stub
	}

	@Override
	public void setInput(ArrayList<Variable.DataType> dt) {
		input = dt;
	}

	@Override
	public void setOutput(ArrayList<Variable.DataType> dt) {
		output = dt;
	}


	@Override
	public boolean isEcexuteOnce() {
		System.err.println("Error: incomplete 207");
		return true;
	}

	@Override
	public GraphEditor getEditor() {
		return this;
	}
}