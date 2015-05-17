import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class VMath extends Function{
	
	SpecialEditorPane valueField;
	ArrayList<Node> additionalInputNodes = new ArrayList<Node>();
	JPanel nodeHolder;
	VMath(Point p){
		super();
		this.color = Color.ORANGE;
		this.setLocation(p);
		this.setSize(new Dimension(160,60));
		this.headerLabel.setText("Math");
		this.header.setLayout(new FlowLayout(FlowLayout.LEFT));
		body.setLayout(new BorderLayout());
		valueField = new SpecialEditorPane();
		valueField.setPreferredSize(new Dimension(90,18));
		valueField.setOpaque(false);
	//	valueField.getDocument().addDocumentListener(this);
		valueField.setBorder(Variable.bodyPadding);
		this.body.add(valueField,BorderLayout.CENTER);
		
		nodeHolder = new JPanel();
		nodeHolder.setLayout(new BoxLayout(nodeHolder, BoxLayout.PAGE_AXIS));
		nodeHolder.setOpaque(false);
		this.body.add(nodeHolder,BorderLayout.LINE_START);
		
		this.inputNode = new FlexNode(Node.Direction.WEST,Node.NodeType.RECIEVING,this);
		inputNode.setBorder(Variable.bodyPadding);
		inputNode.canHaveMultipleInputs = false;
		Main.nodes.add(inputNode);
		nodeHolder.add(inputNode);
		
		this.outputNode = new Node(Node.Direction.EAST,Node.NodeType.SENDING,this,new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.INTEGER)));
		outputNode.setBorder(Variable.bodyPadding);
		outputNode.canHaveMultipleInputs = false;
		Main.nodes.add(outputNode);
		this.body.add(outputNode,BorderLayout.LINE_END);
		
		this.valueField.setText("a");
		((AbstractDocument) valueField.getDocument()).setDocumentFilter(new SpecialEditorPane.NoBreakDocumentFilter());
		
		
		Main.panel.repaint();
		Main.panel.revalidate();
		Main.panel.add(this);
	}

	static class FlexNode extends Node{
		static final ArrayList<Variable.DataType> generic = new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.GENERIC));
		FlexNode(Direction dir, NodeType type, VObject parentObj) {
			super(dir, type, parentObj, generic);
		}
		
		@Override
		public void onConnect(){
			Node newNode = new FlexNode(Node.Direction.WEST,Node.NodeType.RECIEVING,this.parentObject);
			VMath parent = ((VMath) this.parentObject);
			parent.additionalInputNodes.add(newNode);
			newNode.setBorder(Variable.bodyPadding);
			newNode.canHaveMultipleInputs = false;
			Main.nodes.add(newNode);
			parent.nodeHolder.add(newNode);
			parent.setSize(new Dimension(parent.getSize().width,Math.max(parent.getSize().height,parent.getPreferredSize().height)));
			
			setNodeType:{
			for(Node n : parent.additionalInputNodes){
				if(n.dataType.contains(Variable.DataType.DOUBLE) ||  parent.inputNode.dataType.contains(Variable.DataType.DOUBLE)){
					parent.outputNode.dataType = new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.DOUBLE));
					break setNodeType;
				}
			}
			for(Node n : parent.additionalInputNodes){
				if(n.dataType.contains(Variable.DataType.FLOAT) ||  parent.inputNode.dataType.contains(Variable.DataType.FLOAT)){
					parent.outputNode.dataType = new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.FLOAT));
					break setNodeType;
				}
			}
			parent.outputNode.dataType = new ArrayList<Variable.DataType>(Arrays.asList(Variable.DataType.INTEGER));
			}
			
			Main.panel.repaint();
			Main.panel.revalidate();
		}
		@Override
		public void onDisconnect(){
			this.dataType = generic;
		}
	}

}