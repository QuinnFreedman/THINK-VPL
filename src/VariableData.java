import java.util.ArrayList;
import java.util.HashMap;

class VariableData{
	
	public java.lang.String getValueAsString(){
		return null;
	}
	public double getValueAsDouble(){
		return 0;
	}
	public static boolean isNumber(VariableData v){
		if(v.getClass() == Double.class || v.getClass() == Float.class || v.getClass() == Integer.class || v.getClass() == Long.class)
			return true;
		else
			return false;
	}
	static class Integer extends VariableData{
		int value = 0;
		public Integer(int input) {
			this.value = input;
		}
		public Integer(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			if(value == java.lang.Integer.MAX_VALUE)
				return java.lang.Integer.toString(value)+" (INT MAX VALUE)";
			
			return java.lang.Integer.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Double extends VariableData{
		double value = 0;
		public Double(double input) {
			this.value = input;
		}
		public Double(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Double.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Float extends VariableData{
		float value = 0;
		public Float(float input) {
			this.value = input;
		}
		public Float(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Float.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Long extends VariableData{
		long value = 0;
		public Long(long input) {
			this.value = input;
		}
		public Long(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Long.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Short extends VariableData{
		short value = 0;
		public Short(short input) {
			this.value = input;
		}
		public Short(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Short.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Byte extends VariableData{
		byte value = 0;
		public Byte(byte input) {
			this.value = input;
		}
		public Byte(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Byte.toString(value);
		}
		@Override
		public double getValueAsDouble(){
			return value;
		}
	}
	static class Boolean extends VariableData{
		boolean value = false;
		public Boolean(boolean input) {
			this.value = input;
		}
		public Boolean(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Boolean.toString(value);
		}
	}
	static class String extends VariableData{
		java.lang.String value = "";
		public String(java.lang.String input) {
			this.value = input;
		}
		public String(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return value;
		}
	}
	static class Character extends VariableData{
		char value = ' ';
		public Character(char input) {
			this.value = input;
		}
		public Character(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Character.toString(value);
		}
	}
	static class Instance extends VariableData{
		ArrayList<VariableData> values;
		ArrayList<java.lang.String> names;
		private java.lang.String name;
		public InstantiableBlueprint parentBlueprint;
		
		public Instance(VInstance i) {
			values = new ArrayList<VariableData>();
			names = new ArrayList<java.lang.String>();
			for(Variable v : i.childVariables){
				values.add(v.varData);
				names.add(v.getID());
			}
			this.name = "DEBUG"+Math.random();
			this.parentBlueprint = (InstantiableBlueprint) i.parentBlueprint;
		}
		public Instance(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return "<"+name+">";
		}
		
		public VariableData getVariableDataByName(java.lang.String string){
			for(int i = 0; i < values.size(); i++){
				if(names.get(i).equals(string))
					return values.get(i);
			}
			return null;
		}
		
		public java.lang.String getJSON(){
			java.lang.String s = "{";
			
			for(int i = 0; i < values.size(); i++){
				java.lang.String n =  names.get(i);
				VariableData v = values.get(i);
				s += ("\n    \""+n+"\" : "+((v instanceof Instance) ? "\n"+((Instance) v).getJSON() : v.getValueAsString()));
			}
			
			s += "\n}";
			
			return s;
		}
	}
}