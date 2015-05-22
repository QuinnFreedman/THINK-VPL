class VariableData{
	
	public java.lang.String getValueAsString(){
		return null;
	}
	public double getValueAsDouble(){
		return 0;
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
	static class Charecter extends VariableData{
		char value = ' ';
		public Charecter(char input) {
			this.value = input;
		}
		public Charecter(){
			
		}
		@Override
		public java.lang.String getValueAsString(){
			return java.lang.Character.toString(value);
		}
	}
}