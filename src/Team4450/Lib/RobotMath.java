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
	
	/**
	 * Makes the Joysticks use a log curve output.
	 * @param joystickValue The value of the joystick to log-ify
	 * @return A double between -1 and 1.
	 */
	public static double logStick(double joystickValue){
		double base = 4;
		
		if (joystickValue >0) {
			joystickValue = RobotMath.log(base,joystickValue+1)+0.5;
		} else if (joystickValue < 0) {
			joystickValue = -RobotMath.log(base, -joystickValue+1)-0.5;
		}
		
		return joystickValue;
	}
}