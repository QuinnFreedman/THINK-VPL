package think;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveBundle implements Serializable{
	private static final long serialVersionUID = 5654849908756397929L;
	
	ArrayList<Blueprint> blueprints;
	
	SaveBundle(){
		blueprints = Main.blueprints;
	}
}
