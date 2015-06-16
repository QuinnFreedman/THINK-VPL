import java.awt.RenderingHints;
import java.util.ArrayList;

interface GraphEditor{

	ArrayList<Variable> getVariables();

	void updateVars();
	
	DisplayPanel getPanel();

	ArrayList<VObject> getObjects();

	ArrayList<Curve> getCurves();
	
	void addNode(Node n);
	
	ArrayList<Node> getNodes();
}