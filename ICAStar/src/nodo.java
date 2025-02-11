
public class nodo {
	int x,y;
	double g,h,f;
	boolean accesible;
	nodo padre;
 
public nodo(int x,int y, boolean accesible) {
	this.x=x;
	this.y=y;
	
	this.accesible=accesible;
	this.g= Double.POSITIVE_INFINITY;
	this.h= 0;
	this.f= Double.POSITIVE_INFINITY;
	this.padre=null;
	}

}
