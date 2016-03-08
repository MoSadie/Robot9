package Team4450.Lib;

/**
 * Misc Math functions.
 * @author Sean
 */
public class RobotMath {
	
	/**
	 * Allows for custom base logarithms.
	 * @param b The base.
	 * @param a A value
	 * @return The logarithm base b of a.
	 */
	public static double log(double b, double a) {
		return Math.log(a)/Math.log(b);	
	}	
}