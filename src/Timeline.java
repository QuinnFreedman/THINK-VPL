import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class Timeline extends VObject{
	public ArrayList<Row> rows = new ArrayList<Row>();
	Timeline(Point p){
		super();
		this.color = Color.BLUE;
		this.setLocation(p);
		this.headerLabel.setText("Timeline");
		this.header.setLayout(new FlowLayout(FlowLayout.LEFT));
		body.setLayout(new BoxLayout(body,1));
		rows.add(new Row(this));
		rows.add(new Row(this));
		this.body.add(rows.get(0));
		this.body.add(rows.get(1));
		this.setSize(new Dimension(80,this.getPreferredSize().height));
		
		Main.panel.repaint();
		Main.panel.revalidate();
		Main.panel.add(this);
	}
	
	static class Row extends JPanel{
		public Node input;
		public Node output;
		private Timeline parent;
		
		Row(Timeline parent){
			this.parent = parent;
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			this.setLayout(new BorderLayout());
			this.setOpaque(false);
			input = new Node(Node.Direction.EAST, Node.NodeType.RECIEVING, this.parent, new ArrayList<Primative.DataType>());
			output = new Node(Node.Direction.WEST, Node.NodeType.SENDING, this.parent, new ArrayList<Primative.DataType>());
			this.add(output, BorderLayout.LINE_START);
			this.add(input, BorderLayout.LINE_END);
			this.setPreferredSize(new Dimension(this.getPreferredSize().width,25));
		}
	}
}