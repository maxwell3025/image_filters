package filters;

public class fouriertrans {
	public static double[] transform(double[] input) {
		int lengthin = input.length;
		double[] inputbuffer = input;
		double[] outputbuffer = new double[lengthin];
		for (int i = 0; i < lengthin; i++) {
			double currentvalue = 0;
			for (int n = 0; n < lengthin; n++) {
				currentvalue += inputbuffer[n] * Math.cos((Math.PI / lengthin) * (n + 0.5) * i);
			}
			if(Math.abs(currentvalue)>0.00000001 ){
				outputbuffer[i] = currentvalue;
			}else{
				outputbuffer[i] = 0;
			}
			
		}
		return outputbuffer;

	}

	public static void main(String[] args) {
		double[] stuff = new double[10];
		for (int i = 0; i < 10; i++) {
			stuff[i] = Math.cos(Math.PI * i * 0.5);
		}
		double[] out = transform(stuff);
		for (int i = 0; i < 10; i++) {
			System.out.println(out[i]);
		}

	}
}
