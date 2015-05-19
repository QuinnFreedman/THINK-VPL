import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class Cast extends Executable{
	private static final long serialVersionUID = 1L;
	private Node sendingNode;
	private Node recievingNode;
	Cast(Point pos, Node sendingNode, Node recievingNode){
		super();
		setBounds(new Rectangle(pos,getSize()));
		this.addInputNode(new Node(Node.NodeType.RECIEVING, this, sendingNode.dataType));
		this.addOutputNode(new Node(Node.NodeType.SENDING, this, recievingNode.dataType));
	}
	
	@Override
	public Dimension getSize(){
		return new Dimension(Math.max(60,this.getPreferredSize().width),
				30+inputNodeHolder.getPreferredSize().height+outputNodeHolder.getPreferredSize().height);
	}
	
}