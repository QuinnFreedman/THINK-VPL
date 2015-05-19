class VariableData{
	static class Integer extends VariableData{
		int value = 0;
		public Integer(int input) {
			this.value = input;
		}
		public Integer(){
			
		}
	}
	static class Double extends VariableData{
		double value = 0;
		public Double(double input) {
			this.value = input;
		}
		public Double(){
			
		}
	}
	static class Float extends VariableData{
		float value = 0;
		public Float(float input) {
			this.value = input;
		}
		public Float(){
			
		}
	}
	static class Long extends VariableData{
		long value = 0;
		public Long(long input) {
			this.value = input;
		}
		public Long(){
			
		}
	}
	static class Short extends VariableData{
		short value = 0;
		public Short(short input) {
			this.value = input;
		}
		public Short(){
			
		}
	}
	static class Byte extends VariableData{
		byte value = 0;
		public Byte(byte input) {
			this.value = input;
		}
		public Byte(){
			
		}
	}
	static class Boolean extends VariableData{
		boolean value = false;
		public Boolean(boolean input) {
			this.value = input;
		}
		public Boolean(){
			
		}
	}
	static class String extends VariableData{
		java.lang.String value = "";
		public String(java.lang.String input) {
			this.value = input;
		}
		public String(){
			
		}
	}
	static class Charecter extends VariableData{
		char value = ' ';
		public Charecter(char input) {
			this.value = input;
		}
		public Charecter(){
			
		}
	}
}