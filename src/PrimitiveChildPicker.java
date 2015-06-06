import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PrimitiveChildPicker extends VObject{
		private static final long serialVersionUID = 1L;
		Variable parent;
		
		PrimitiveChildPicker(Variable parent, Point position, GraphEditor owner){
			super(owner);
			this.parent = parent;
			this.position = position;

			headerLabel = new JLabel();
			headerLabel.setOpaque(false);
			headerLabel.setBorder(BorderFactory.createEmptyBorder(4, 7, 2, 7));
			
			headerLabel.setText("Pick one...");
			
			this.body.setLayout(new BoxLayout(body, 1));
			this.body.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			body.add(headerLabel);
			
			/*for(Class<? extends VariableFunction> c : ((Variable) this.parent).functions){
				System.out.println((c.getClass()));
			}*/

			if(parent instanceof ContainsChildFunctions){ //TODO should be implied
				for(PrimitiveFunction f : ((ContainsChildFunctions) parent).getFunctions()){
					this.body.add(new MenuItem(f,this));
				}
			}
			
			this.setBounds(new Rectangle(position,new Dimension(100,this.getPreferredSize().height+5)));
		}
		
		private class MenuItem extends JLabel implements MouseListener{
			String name;
			PrimitiveChildPicker childPicker;
			private PrimitiveFunction f;
			MenuItem(PrimitiveFunction f, PrimitiveChildPicker primitiveChildPicker){
				super();
				this.f = f;
				String symbol;
				switch(f.getPrimairyMode()){
				case IN:
					symbol = "<";
					break;
				case OUT:
					symbol = ">";
					break;
				default:
					symbol = "-";
					break;
					
				}
				this.name = f.getSimpleName();
				this.childPicker = primitiveChildPicker;
				this.setBackground(Color.RED);
				this.setText(symbol+" "+name);
				this.addMouseListener(this);
				this.setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
				PrimitiveFunction pf = null;
				try {
					System.out.println(f.getClass());
					Constructor<?> constructor = f.getClass().getDeclaredConstructor(Point.class, Variable.class);
					pf = (PrimitiveFunction) constructor.newInstance(
							Node.getLocationOnPanel(this.childPicker, owner.getPanel()), 
							(Variable) this.childPicker.parent
						);
				} catch (InstantiationException e) {
					// Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}catch(Exception e){
					//pf = null;//= new VariableFunction(Node.getLocationOnPanel(this.childPicker), ((Variable) this.childPicker.parent).dataType, parentNode, (Variable) this.childPicker.parent,"error");
				}
				
				this.childPicker.delete();
				
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// Auto-generated method stub
				
			}
		}
		
	}
