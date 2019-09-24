package filters;

public class complexnumber {
double imag;
double real;
double abs;
	public complexnumber(double x,double y) {
		real=x;
		imag=y;
		abs=Math.sqrt(real*real+imag*imag);
	}
	public static complexnumber mult(complexnumber a, complexnumber b){
		return new complexnumber(a.real*b.real-a.imag*b.imag,a.real*b.imag+b.real*a.imag);
		
	}
	public static complexnumber pow(complexnumber c,int pow){
		complexnumber start = new complexnumber(pow(c.abs*2,(int)(pow*0.5)),0);
		complexnumber orig = new complexnumber(c.real/c.abs,c.imag/c.abs);
		for(int i = 0;i<pow;i++){
			start=mult(orig,start);
		}
		return start;
	}
	public static double pow(double in,int pow){
		double out = 1;
		for(int i = 0;i<pow;i++){
			out*=in;
		}
		return out;
	}

}
