
package Team4450.Lib;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Interface class for Festo Single Action pneumatic valve.
 * Single action opens against a spring. When you call open power is applied and
 * opens the valve against the spring and remains on. When close is called, power
 * is turned off and the spring closes the valve.
 */

public class FestoSA
{
	private final Solenoid valveOpenSide;

	/**
	 * @param port The port on the PCM the solenoid is wired to.
	 */
	public FestoSA(int port)
	{
	  	Util.consoleLog("port=%d", port); 

		valveOpenSide = new Solenoid(port);
	}
	
	/**
	 * @param port The port on the PCM the solenoid is wired to.
	 */
	public FestoSA(int port, PCMids moduleNumber)
	{
	  	Util.consoleLog("port=%d ModuleNumber=%d", port, moduleNumber.value); 

		valveOpenSide = new Solenoid(moduleNumber.value, port);
	}

	public void dispose()
	{
		Util.consoleLog();
		
		Close();
		
		valveOpenSide.free();
	}

	/**
	 * Open the valve.
	 */
	public void Open()
	{
		Util.consoleLog();
    
		valveOpenSide.set(true);
	}

	/**
	 * Close the valve.
	 */
	public void Close()
	{
		Util.consoleLog();
    
		valveOpenSide.set(false);
	}
	
	public enum PCMids {
		PCM_ZERO (0),
		PCM_ONE (1);

		public int value;
		
		private PCMids (int value) {
			this.value = value;
		}
		
	}
}