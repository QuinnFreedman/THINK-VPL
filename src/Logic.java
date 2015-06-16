import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

class Logic extends Executable{
	@Override
	public void resetActiveNode() {
		activeNode = 0;
	};

	private Logic getThis() {
		return this;
	}
	
	protected String getID(){
		return null;
	}
	
	Logic(Point pos, GraphEditor owner){
		super(owner);
		this.color = Color.DARK_GRAY;
		
		body.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		headerLabel = new JLabel();
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
		headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
		headerLabel.setText(getID());
		body.add(headerLabel,gbc);
		
		addOutputNode(new Node(Node.NodeType.SENDING, getThis(), Variable.DataType.BOOLEAN, true));
		 
		setBounds(new Rectangle(pos,getSize()));

	}
	
	static class Equals extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "=";
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
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.FLEX));
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.FLEX));
		}
		
	}
	
	static class LessThan extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "<";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() < inputs[1].getValueAsDouble()));
		}
		LessThan(Point p, GraphEditor owner) {
			super(p, owner);
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.NUMBER));
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.NUMBER));
		}
		
	}
	
	static class GreaterThan extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return ">";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() > inputs[1].getValueAsDouble()));
		}
		GreaterThan(Point p, GraphEditor owner) {
			super(p, owner);
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.NUMBER));
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.NUMBER));
		}
		
	}
	
	static class LessOrEqual extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "\u2264";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() <= inputs[1].getValueAsDouble()));
		}
		LessOrEqual(Point p, GraphEditor owner) {
			super(p, owner);
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.NUMBER));
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.NUMBER));
		}
		
	}
	
	static class GreaterOrEqual extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "\u2265";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean((inputs[0].getValueAsDouble() >= inputs[1].getValueAsDouble()));
		}
		GreaterOrEqual(Point p, GraphEditor owner) {
			super(p, owner);
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.NUMBER));
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.NUMBER));
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
		And(Point p, GraphEditor owner) {
			super(p, owner);
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.BOOLEAN));
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.BOOLEAN));
		}
		
	}
	
	static class Or extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "||";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean(((VariableData.Boolean) inputs[0]).value || ((VariableData.Boolean) inputs[1]).value);
		}
		Or(Point p, GraphEditor owner) {
			super(p, owner);
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.BOOLEAN));
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.BOOLEAN));
			SwingUtilities.invokeLater(new Runnable() {
		        @Override
		        public void run() {
		        	headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()-6));
		        }});
		}
		
	}
	
	static class Not extends Logic{
		private static final long serialVersionUID = 1L;
		
		@Override
		protected String getID(){
			return "!";
		}
		@Override
		public VariableData execute(VariableData[] inputs){
			return new VariableData.Boolean(!((VariableData.Boolean) inputs[0]).value);
		}
		Not(Point p, GraphEditor owner) {
			super(p, owner);
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.BOOLEAN));
			SwingUtilities.invokeLater(new Runnable() {
		        @Override
		        public void run() {
		        	headerLabel.setBorder(new EmptyBorder(new Insets(-8,-1,-1,-1)));
		        }});
		}
		
	}
	
	static class Branch extends Executable{
		private static final long serialVersionUID = 1L;
		
		private boolean isTrue;
		
		public boolean isTrue(){
			return isTrue;
		}
		
		private Branch getThis(){
			return this;
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			isTrue = ((VariableData.Boolean) inputs[0]).value;
			return null;
		}
		Branch(Point pos, GraphEditor owner) {
			super(owner);
			this.color = Color.BLACK;
			
			body.setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			headerLabel = new JLabel();
			//headerLabel.setFont(headerLabel.getFont().deriveFont(Font.PLAIN, headerLabel.getFont().getSize()+20));
			//headerLabel.setBorder(new EmptyBorder(new Insets(-10,-1,-1,-1)));
			headerLabel.setText("Branch");
			body.add(headerLabel,gbc);
			
			addInputNode(new Node(Node.NodeType.RECIEVING, getThis(), Variable.DataType.GENERIC, true));
			addInputNode(new Node(Node.NodeType.RECIEVING, getThis(), Variable.DataType.BOOLEAN));
			addOutputNode(new Node(Node.NodeType.SENDING, getThis(), Variable.DataType.GENERIC));
			addOutputNode(new Node(Node.NodeType.SENDING, getThis(), Variable.DataType.GENERIC));
			
			this.getInputNodes().get(1).setToolTipText("Condition");
			this.getOutputNodes().get(0).setToolTipText("If true...");
			this.getOutputNodes().get(1).setToolTipText("If false...");
			
			setBounds(new Rectangle(pos,getSize()));
					
		}
		
	}
	static class While extends Executable implements Repeater{
		private static final long serialVersionUID = 1L;
		
		private boolean isTrue;
		
		public boolean isContinue(){
			return isTrue;
		}
		
		private While getThis(){
			return this;
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			isTrue = ((VariableData.Boolean) inputs[0]).value;
			return null;
		}
		While(Point pos, GraphEditor owner) {
			super(owner);
			this.color = Color.BLACK;
			
			body.setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			headerLabel = new JLabel();
			headerLabel.setText("While");
			body.add(headerLabel,gbc);
			
			addInputNode(new Node(Node.NodeType.RECIEVING, getThis(), Variable.DataType.GENERIC, true));
			addInputNode(new Node(Node.NodeType.RECIEVING, getThis(), Variable.DataType.BOOLEAN));
			addOutputNode(new Node(Node.NodeType.SENDING, getThis(), Variable.DataType.GENERIC));
			addOutputNode(new Node(Node.NodeType.SENDING, getThis(), Variable.DataType.GENERIC));
			
			this.getInputNodes().get(1).setToolTipText("Condition");
			this.getOutputNodes().get(0).setToolTipText("Repeat while true...");
			this.getOutputNodes().get(1).setToolTipText("When false...");
			
			setBounds(new Rectangle(pos,getSize()));
			
		}
		
	}
	
	static class Sequence extends Executable implements Repeater{
		private static final long serialVersionUID = 1L;
		
		private boolean isTrue;
		public int activeOutNode = 0;
		
		public boolean isContinue(){
			return isTrue;
		}
		
		private Sequence getThis(){
			return this;
		}
		
		@Override
		public VariableData execute(VariableData[] inputs){
			return null;
		}
		Sequence(Point pos, GraphEditor owner) {
			super(owner);
			this.color = Color.BLACK;
			
			body.setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			headerLabel = new JLabel();
			headerLabel.setText("Sequence");
			body.add(headerLabel,gbc);
			
			addInputNode(new Node(Node.NodeType.RECIEVING, getThis(), Variable.DataType.GENERIC, true));
			addOutputNode(new ReplicatingNode(Node.NodeType.SENDING, getThis(), Variable.DataType.GENERIC));
			 
			setBounds(new Rectangle(pos,getSize()));
		}
		
		@Override
		public Dimension getSize(){
			return new Dimension(Math.max(60,this.getPreferredSize().width),
					30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
		}
		
		private class ReplicatingNode extends Node{
			private static final long serialVersionUID = 1L;

			@Override
			public void onConnect(){
				boolean allConnected = true;
				for(Node n : ((Executable) parentObject).getOutputNodes()){
					if(n.children.isEmpty()){
						allConnected = false;
						break;
					}
				}
				if(allConnected){
					System.out.println(parentObject.getPreferredSize().width);
					System.out.println(((Executable) parentObject).outputNodeHolder.getPreferredSize().width);
					
					((Executable) parentObject).addOutputNode(new ReplicatingNode(Node.NodeType.SENDING, ((Executable) parentObject), Variable.DataType.GENERIC));

					parentObject.setBounds(new Rectangle(parentObject.getLocation(),parentObject.getSize()));
					owner.getPanel().repaint();
					owner.getPanel().revalidate();
					
					
				}
			}
			
			ReplicatingNode(NodeType type, Executable parent, Variable.DataType dt) {
				super(type, parent, dt);
			}
		}
	}
	
	static class Wait extends Executable{
		private static final long serialVersionUID = 1L;
		
		@Override
		public VariableData execute(VariableData[] inputs){
			if(Debug.getRunMode() == Debug.RunMode.RUN){
				try {
					Thread.sleep((long)((VariableData.Integer) inputs[0]).value);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		Wait(Point pos, GraphEditor owner) {
			super(owner);
			this.color = Color.BLACK;
			
			body.setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			headerLabel = new JLabel();
			headerLabel.setText("Wait");
			body.add(headerLabel,gbc);
			
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.GENERIC, true));
			addInputNode(new Node(Node.NodeType.RECIEVING, this, Variable.DataType.INTEGER));
			getInputNodes().get(1).setToolTipText("Wait time (miliseconds)");
			addOutputNode(new Node(Node.NodeType.SENDING, this, Variable.DataType.GENERIC));
			 
			setBounds(new Rectangle(pos,getSize()));
		}
		
	}
}