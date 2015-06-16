import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

class FunctionEditor extends JFrame implements ActionListener, MouseListener, GraphEditor,ComponentListener{
	private static final long serialVersionUID = 1L;
	
	private VFunction parent;
	
	private JPopupMenu panelPopup;
	private DisplayPanel panel;
	private JPanel vars;
	private JPanel panelHolder;
	private JScrollPane scrollVars;
	private Point clickLocation;
	private ArrayList<Variable> variables = new ArrayList<Variable>();
	private ArrayList<VObject> objects = new ArrayList<VObject>();
	private ArrayList<Curve> curves = new ArrayList<Curve>();
	private ArrayList<Node> nodes = new ArrayList<Node>();
	
	private JButton addGenericInput;
	private JButton addGenericOutput;
	private FunctionIO inputObject;
	private FunctionIO outputObject;
	private JPanel addInputNode;
	private JPanel addOutputNode;
	private JScrollPane scrollPane;
	
	public FunctionIO getInputObject(){
		return inputObject;
	}
	public FunctionIO getOutputObject(){
		return outputObject;
	}
	public FunctionOverseer getOverseer(){
		return parent;
	}
	@Override
	public ArrayList<Variable> getVariables(){
		return variables;
	}
	@Override
	public ArrayList<VObject> getObjects(){
		return objects;
	}
	@Override
	public ArrayList<Curve> getCurves(){
		return curves;
	}
	@Override
	public ArrayList<Node> getNodes(){
		return nodes;
	}
	@Override
	public void addNode(Node n){
		nodes.add(n);
	}
	@Override
	public DisplayPanel getPanel(){
		return panel;
	}
	
	FunctionEditor(VFunction parent){
		this.parent = parent;
		this.setTitle(parent.getID());
		this.setSize(800,500);
		this.setMinimumSize(new Dimension(555,325));
		//this.setLocationByPlatform(true);
		this.setLocationRelativeTo(null);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/*
		try {
			UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			// Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		panelHolder = new JPanel();
		panelHolder.setLayout(new BorderLayout());
		
		panel = new DisplayPanel(this);
		
		scrollPane = new JScrollPane(panelHolder);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		
		panelHolder.add(panel, BorderLayout.CENTER);
		
		//add input node
		
		addInputNode = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelHolder.add(addInputNode, BorderLayout.PAGE_START);
		
		addInputNode.add(new JLabel("Add input node:"));
		
		addGenericInput = new JButton("Empty");
		addGenericInput.setFocusable(false);
		ActionListener listener = new NodeAdderListener();
		addGenericInput.addActionListener(listener);
		addInputNode.add(addGenericInput);
		
		JButton addNode = new JButton("Integer");
		addNode.setFocusable(false);
		addNode.addActionListener(listener);
		addInputNode.add(addNode);
		
		/*addNode = new JButton("Float");
		addNode.setFocusable(false);
		addNode.addActionListener(listener);
		addInputNode.add(addNode);*/
		
		addNode = new JButton("Double");
		addNode.setFocusable(false);
		addNode.addActionListener(listener);
		addInputNode.add(addNode);
		
		addNode = new JButton("Boolean");
		addNode.setFocusable(false);
		addNode.addActionListener(listener);
		addInputNode.add(addNode);
		
		addNode = new JButton("String");
		addNode.setFocusable(false);
		addNode.addActionListener(listener);
		addInputNode.add(addNode);

		//add output node
		addOutputNode = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelHolder.add(addOutputNode, BorderLayout.PAGE_END);
		
		addOutputNode.add(new JLabel("Add output node:"));
		
		addGenericOutput = new JButton("Empty");
		addGenericOutput.setFocusable(false);
		addGenericOutput.addActionListener(listener);
		addGenericOutput.setEnabled(false);
		addOutputNode.add(addGenericOutput);
		
		addNode = new JButton("Integer");
		addNode.setFocusable(false);
		addNode.addActionListener(listener);
		addOutputNode.add(addNode);
		
		/*addNode = new JButton("Float");
		addNode.setFocusable(false);
		addNode.addActionListener(listener);
		addOutputNode.add(addNode);*/
		
		addNode = new JButton("Double");
		addNode.setFocusable(false);
		addNode.addActionListener(listener);
		addOutputNode.add(addNode);
		
		addNode = new JButton("Boolean");
		addNode.setFocusable(false);
		addNode.addActionListener(listener);
		addOutputNode.add(addNode);
		
		addNode = new JButton("String");
		addNode.setFocusable(false);
		addNode.addActionListener(listener);
		addOutputNode.add(addNode);
			
	//SIDEBAR
		
		Dimension minimumSize = new Dimension(100,100);
		
		JPanel varsContainer = new JPanel();
		
		varsContainer.setLayout(new BorderLayout(0, 0));
		
		JPanel varsHeader = new JPanel();
		varsContainer.add(varsHeader, BorderLayout.NORTH);
		varsHeader.setLayout(new BorderLayout(0, 0));
		
		JLabel lblVars = new JLabel("Variables");
		lblVars.setHorizontalAlignment(SwingConstants.CENTER);
		varsHeader.add(lblVars);
		
		JPanel varButtonHolder = new JPanel();
		varsHeader.add(varButtonHolder, BorderLayout.EAST);
		
		vars = new JPanel();
		scrollVars = new JScrollPane(vars);
		BoxLayout layout = new BoxLayout(vars, BoxLayout.Y_AXIS);
		vars.setLayout(layout);
		
		JButton addVar = new JButton("+");
		addVar.setPreferredSize(new Dimension(30,addVar.getPreferredSize().height));
		//addVar.setFocusable(false);
		varButtonHolder.add(addVar);
		FunctionEditor THIS = this;
		addVar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Variable v = new Variable(THIS);
				variables.add(0,v);
				updateVars();
				variables.get(0).fields.get(0).requestFocusInWindow();
				scrollVars.getViewport().setViewPosition(new Point(0,0));
			}
		});
		
		scrollVars.setMinimumSize(minimumSize);
		varsContainer.add(scrollVars);
		
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                varsContainer, scrollPane);
				splitPane.setDividerLocation(253);
		
		
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		this.setVisible(true);
		
		this.setFocusTraversalKeysEnabled(false);
		vars.setFocusTraversalKeysEnabled(false);
		
		//componentMover = new ComponentMover();
		//componentMover.setEdgeInsets(new Insets(10, 10, 10, 10));
		
		panelPopup = new JPopupMenu();
		panel.addMouseListener(this);
		
		JMenuItem popupAdd = new JMenuItem("Add");
		popupAdd.addActionListener(this);
		panelPopup.add(popupAdd);
		
		JMenuItem popupSubract = new JMenuItem("Subtract");
		popupSubract.addActionListener(this);
		panelPopup.add(popupSubract);
		
		JMenuItem popupMultiply = new JMenuItem("Multiply");
		popupMultiply.addActionListener(this);
		panelPopup.add(popupMultiply);
		
		JMenuItem popupDivide = new JMenuItem("Divide");
		popupDivide.addActionListener(this);
		panelPopup.add(popupDivide);
		
		JMenuItem popupRand = new JMenuItem("Random");
		popupRand.addActionListener(this);
		panelPopup.add(popupRand);
		
		JMenuItem popupRound = new JMenuItem("Round");
		popupRound.addActionListener(this);
		panelPopup.add(popupRound);
		
		JMenuItem popupConcat = new JMenuItem("Concatenate");
		popupConcat.addActionListener(this);
		panelPopup.add(popupConcat);
		
		panelPopup.addSeparator();
		
		JMenuItem popupLogic = new JMenuItem("Branch");
		popupLogic.addActionListener(this);
		panelPopup.add(popupLogic);
		
		JMenuItem popupEquals = new JMenuItem("Equals");
		popupEquals.addActionListener(this);
		panelPopup.add(popupEquals);
		
		JMenuItem popupLessThan = new JMenuItem("Is Less Than");
		popupLessThan.addActionListener(this);
		panelPopup.add(popupLessThan);
		
		JMenuItem popupGreaterThan = new JMenuItem("Is Greater Than");
		popupGreaterThan.addActionListener(this);
		panelPopup.add(popupGreaterThan);
		
		JMenuItem popupLessEqual = new JMenuItem("Is Less Than Or Equal To");
		popupLessEqual.addActionListener(this);
		panelPopup.add(popupLessEqual);
		
		JMenuItem popupGreaterEqual = new JMenuItem("Is Greater Than Or Equal To");
		popupGreaterEqual.addActionListener(this);
		panelPopup.add(popupGreaterEqual);
		
		JMenuItem popupItem = new JMenuItem("And");
		popupItem.addActionListener(this);
		panelPopup.add(popupItem);
		
		popupItem = new JMenuItem("Or");
		popupItem.addActionListener(this);
		panelPopup.add(popupItem);
		
		popupItem = new JMenuItem("Not");
		popupItem.addActionListener(this);
		panelPopup.add(popupItem);

		panelPopup.addSeparator();
		
		popupItem = new JMenuItem("While");
		popupItem.addActionListener(this);
		panelPopup.add(popupItem);
		
		popupItem = new JMenuItem("Sequence");
		popupItem.addActionListener(this);
		panelPopup.add(popupItem);
		
		panelPopup.addSeparator();
		
		JMenuItem popupLog = new JMenuItem("Log To Console");
		popupLog.addActionListener(this);
		panelPopup.add(popupLog);
		
		JMenuItem popupGet = new JMenuItem("Get String From Console");
		popupGet.addActionListener(this);
		panelPopup.add(popupGet);
		
		JMenuItem popupGetNum = new JMenuItem("Get Number From Console");
		popupGetNum.addActionListener(this);
		panelPopup.add(popupGetNum);
		
		/*JMenuItem popupMath = new JMenuItem("Math");
		popupMath.setEnabled(false);
		popupMath.addActionListener(THIS);
		panelPopup.add(popupMath);
		
		JMenuItem popupTimeline = new JMenuItem("Timeline");
		popupTimeline.addActionListener(THIS);
		popupTimeline.setEnabled(false);
		panelPopup.add(popupTimeline);
		
		JMenuItem popupBlueprint = new JMenuItem("Blueprint");
		popupBlueprint.addActionListener(THIS);
		popupBlueprint.setEnabled(false);
		panelPopup.add(popupBlueprint);*/

		this.panel.requestFocusInWindow();

		inputObject = new FunctionIO(this,FunctionIO.Mode.INPUT);
		outputObject = new FunctionIO(this,FunctionIO.Mode.OUTPUT);
		
		panel.addComponentListener(this);
	}
	public void updateVars(){
		vars.removeAll();
		for(Variable var : variables){
			vars.add(var);
		}
		vars.repaint();
		vars.revalidate();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		Point p;
		if(((JComponent) e.getSource()).getParent() == panelPopup){
			p = clickLocation;
			if(c == "Add"){
				new Arithmetic.Add(p, this);
			}else if(c == "Subtract"){
				new Arithmetic.Subtract(p, this);
			}else if(c == "Multiply"){
				new Arithmetic.Multiply(p, this);
			}else if(c == "Divide"){
				new Arithmetic.Divide(p, this);
			}else if(c == "Random"){
				new Arithmetic.Random(p, this);
			}else if(c == "Round"){
				new Arithmetic.Round(p, this);
			}else if(c == "Concatenate"){
				new Arithmetic.Concat(p, this);
			}else if(c == "Branch"){
				new Logic.Branch(p, this);
			}else if(c == "While"){
				new Logic.While(p, this);
			}else if(c == "Sequence"){
				new Logic.Sequence(p, this);
			}else if(c == "Equals"){
				new Logic.Equals(p, this);
			}else if(c == "Is Less Than"){
				new Logic.LessThan(p, this);
			}else if(c == "Is Greater Than"){
				new Logic.GreaterThan(p, this);
			}else if(c == "Is Less Than Or Equal To"){
				new Logic.LessOrEqual(p, this);
			}else if(c == "Is Greater Than Or Equal To"){
				new Logic.GreaterOrEqual(p, this);
			}else if(c == "And"){
				new Logic.And(p, this);
			}else if(c == "Or"){
				new Logic.Or(p, this);
			}else if(c == "Not"){
				new Logic.Not(p, this);
			}else if(c == "Log To Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				new Console.Log(p, this);
			}else if(c == "Get String From Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				new Console.getStr(p, Variable.DataType.STRING, this);
			}else if(c == "Get Number From Console"){
				if(Debug.console == null){
					Debug.console = new Console();
				}
				new Console.getStr(p, Variable.DataType.DOUBLE, this);
			}else{
				System.out.println("null Action:"+c);
			}
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		panel.requestFocusInWindow();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3){
			clickLocation = new Point(e.getX(), e.getY());
			panelPopup.show(panel, e.getX(), e.getY());
		}
	}
	
	static class FunctionIO extends Executable{
		public Mode mode;
		private Rectangle fillSize = null;
		
		public static enum Mode{
			OUTPUT,INPUT
		}
		@Override
		public void delete(){
			
		}
		@Override
		public void resetActiveNode(){
			if(this.getInputNodes() == null || this.getInputNodes().isEmpty() || 
					this.getInputNodes().get(0).dataType == Variable.DataType.GENERIC)
				this.activeNode = 1;
			else
				this.activeNode = 0;
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			if(inputs != null && inputs.length != 0)
				return inputs[0];
			else
				return null;
		}
		public FunctionOverseer getOverseer(){
			if(owner instanceof FunctionEditor)
				return ((FunctionEditor) owner).parent;
			else if(owner instanceof FunctionOverseer)
				return (FunctionOverseer) owner;
			return null;
		}
		FunctionIO(GraphEditor owner, Mode mode){
			super(owner);
			this.mode = mode;
			Main.componentMover.deregisterComponent(this);
			if(this.mode == Mode.INPUT){
				this.remove(inputNodeHolder);
				fillSize = new Rectangle(0,0,400,30);
				this.setSize(400,38);
				this.setLocation((owner.getPanel().getWidth()-this.getWidth())/2,10);
			}else{
				this.remove(outputNodeHolder);
				fillSize = new Rectangle(0,8,400,30);
				this.setSize(400,38);
				this.setLocation((owner.getPanel().getWidth()-this.getWidth())/2,owner.getPanel().getHeight()-48);
			}
			
		}
		
		@Override
		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if(selected){
				 Point2D start = new Point2D.Float(0, 0);
			     Point2D end = new Point2D.Float(this.getWidth()/2, 0);
			     float[] dist = {0.0f, 0.7f};
			     Color[] colors = {new Color(0,0,0,0), Color.YELLOW};
			     LinearGradientPaint p =
			         new LinearGradientPaint(start, end, dist, colors, MultipleGradientPaint.CycleMethod.REFLECT);
			    
				g2.setPaint(p);
				g2.setStroke(new BasicStroke(5));
				if(this.mode == Mode.OUTPUT){
					g2.draw(new Line2D.Double(5,5,this.getWidth(),5));
				}else{
					g2.draw(new Line2D.Double(0,30,this.getWidth(),30));
				}
			}
			GradientPaint gradient = new GradientPaint(
					fillSize.x, 
					fillSize.y, 
					((mode == Mode.INPUT) ? new Color(0,0,0,0) : Color.BLACK),
					fillSize.x,
					fillSize.height,
					((mode == Mode.INPUT) ? Color.BLACK : new Color(0,0,0,0)));
			
			g2.setPaint(gradient);
		    g2.fill(new Rectangle2D.Double(fillSize.x,fillSize.y,fillSize.width,fillSize.height));
		    g2.setPaint(Color.black);
		}
	}
	class removeableNode extends Node{
		Node THIS = this;
		
		public removeableNode(NodeType type, VObject parentObj, Variable.DataType dt, boolean b) {
			super(type, parentObj, dt, b);
			this.addMouseListener(new Deleter());
			
		}
		
		private class Deleter implements MouseListener{

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					if(THIS.type == NodeType.SENDING){
						if(THIS.dataType == Variable.DataType.GENERIC){
							parent.removeOutput(0);
							outputObject.removeInputNode(outputObject.getInputNodes().get(0));
							while(outputObject.getInputNodes().size() > 1){
								parent.removeOutput(1);
								outputObject.removeInputNode(outputObject.getInputNodes().get(1));
							}
							if(outputObject.getInputNodes().size() == 0){
								enableOutput(true);
							}else{
								enableOutput(false);
							}
						}
						parent.removeInput(((Executable) THIS.parentObject).getOutputNodes().indexOf(THIS));
						((Executable) THIS.parentObject).removeOutputNode(THIS);
						if(inputObject.getOutputNodes().isEmpty())
							addGenericInput.setEnabled(true);
					}else{
						if(THIS.dataType == Variable.DataType.GENERIC){
							parent.removeInput(0);
							inputObject.removeOutputNode(inputObject.getOutputNodes().get(0));
							parent.removeOutput(0);
							outputObject.removeInputNode(outputObject.getInputNodes().get(0));
							while(outputObject.getInputNodes().size() > 1){
								parent.removeOutput(1);
								outputObject.removeInputNode(outputObject.getInputNodes().get(1));
							}
							if(outputObject.getInputNodes().size() == 0){
								enableOutput(true);
							}else{
								enableOutput(false);
							}
						}else{
							parent.removeOutput(((Executable) THIS.parentObject).getInputNodes().indexOf(THIS));
							((Executable) THIS.parentObject).removeInputNode(THIS);
						}
						if(inputObject.getOutputNodes().isEmpty() && outputObject.getInputNodes().isEmpty()){
							addGenericInput.setEnabled(true);
						}
						if(outputObject.getInputNodes().isEmpty() || outputObject.getInputNodes().get(0).dataType == Variable.DataType.GENERIC){
							enableOutput(true);
						}
						if(outputObject.getInputNodes().size() <= 1){
							parent.setExecuteOnce(false);
							System.out.println("setExecuteOnce (false)");
						}
					}
					
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// Auto-generated method stub
				
			}
		}
		
	}
	class NodeAdderListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String s = ((JButton) e.getSource()).getText();
			Variable.DataType dataType;
			if(s.equals("Empty")){
				dataType = Variable.DataType.GENERIC;
				if(parent.getOutput().isEmpty()){
					//enableOutput(false);
					outputObject.addInputNode(new removeableNode(Node.NodeType.RECIEVING, outputObject, Variable.DataType.GENERIC,true));
					parent.addOutput(dataType);
				}
			}else if(s.equals("Integer")){
				dataType = Variable.DataType.INTEGER;
			}else if(s.equals("Float")){
				dataType = Variable.DataType.FLOAT;
			}else if(s.equals("Double")){
				dataType = Variable.DataType.DOUBLE;
			}else if(s.equals("Boolean")){
				dataType = Variable.DataType.BOOLEAN;
			}else if(s.equals("String")){
				dataType = Variable.DataType.STRING;
			}else{
				dataType = null;
			}
			if(((JButton) e.getSource()).getParent() == addInputNode){
				addGenericInput.setEnabled(false);
				inputObject.addOutputNode(new removeableNode(Node.NodeType.SENDING, inputObject, dataType, (dataType == Variable.DataType.GENERIC) ? false : true));
				inputObject.revalidate();
				inputObject.repaint();
				parent.addInput(dataType);
			}else{
				addGenericInput.setEnabled(false);
				outputObject.addInputNode(new removeableNode(Node.NodeType.RECIEVING, outputObject, dataType, (dataType == Variable.DataType.GENERIC) ? true : false));
				outputObject.revalidate();
				outputObject.repaint();
				parent.addOutput(dataType);
				if(outputObject.getInputNodes().size() == 1 && outputObject.getInputNodes().get(0).dataType != Variable.DataType.GENERIC){
					enableOutput(false);
				}
				if(outputObject.getInputNodes().size() > 1){
					parent.setExecuteOnce(true);
					System.out.println("setExecuteOnce (true)");
				}
			}
			
			repaintAll();
		}
	}
	private void repaintAll(){
		for(Blueprint c : Main.blueprints){
			c.getPanel().repaint();
			c.getPanel().revalidate();
			for(VFunction f : c.getFunctions()){
				if(f != null && f.editor != null){
					f.editor.getPanel().repaint();
				}
				else if(f == null){
					System.out.println("!WARNING: f is null");
				}else if (f.editor == null){
					System.out.println("!WARNING: f.editor is null");
				}
			}
		}
	}
	private void enableOutput(boolean b){
		for(Component component : addOutputNode.getComponents()){
			if(component instanceof JButton && ((JButton) component).getText() != "Empty"){
				((JButton) component).setEnabled(b);
			}
		}
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		// Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent e) {
		// Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent e) {
		inputObject.setLocation((panel.getWidth()-inputObject.getWidth())/2,10);
		outputObject.setLocation((panel.getWidth()-outputObject.getWidth())/2,panel.getHeight()-48);
		
	}
	@Override
	public void componentShown(ComponentEvent e) {
		// Auto-generated method stub
		
	}
}