
package Team4450.Lib;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * Interface class for Festo Double Action pneumatic valve.
 * Double action is a sliding valve. Calling open applies power to the open side momentarily causing
 * the valve to move to the open position. Power is then turned off and the valve
 * stays where it is (open). Calling close applies power to the close side momentarily causing the
 * valve to move to the closed position. Power is then turned off and the valve
 * stays where it is (closed).
 * 
 * Open and Close are arbitrary definitions, they are actually defined by the physical robot
 * valve piping/wiring and what you want the cylinder to do. Typically you would pipe the "open" side
 * of the valve to extend a cylinder and close to retract. For VEX valves, our convention is to wire
 * the A side to the first port and pipe to open or extend the cylinder and B side to close or retract.
 * Again, these are conventions and the reality is what you design your valve to cylinder piping to be
 * and the wiring to the corresponding sides of the valve to the PCM ports.
 */

public class FestoDA
{
	private final Solenoid	valveOpenSide, valveCloseSide;

	public double           solenoidSlideTime;

	/**
	 * @param port PCM port wired to open/A side of valve. Close/B side is wired to PCM next port.
	 */

	public FestoDA(int port)
	{
	  	Util.consoleLog("port=%d", port);

		valveOpenSide = new Solenoid(port);
		valveCloseSide = new Solenoid(port + 1);
    
		solenoidSlideTime = .05;
    
		//Close();
	}
	
	/**
	 * @param port1 PCM port wired to open/A side of valve.
	 * @param port2 Close/B side is wired to PCM, usually the next port.
	 */
	public FestoDA(int port1, int port2) {
		Util.consoleLog("Port1=%d Port2=%d", port1, port2);
		
		valveOpenSide = new Solenoid(port1);
		valveCloseSide = new Solenoid(port2);
		
		solenoidSlideTime = .05;
		
		//Close();
	}
	
	/**
	 * @param port PCM port wired to open/A side of valve.
	 * @param moduleNumber The CAN Device ID that the PCM is on.
	 */
	public FestoDA(PCMids moduleNumber, int port) {
		Util.consoleLog("Port=%d ModuleNumber=%d", port, moduleNumber.value);
		
		valveOpenSide = new Solenoid(moduleNumber.value, port);
		valveCloseSide = new Solenoid(moduleNumber.value, port+1);
		
		solenoidSlideTime = .05;
		
		//Close();
	}
	
	/**
	 * @param port1 PCM port wired to open/A side of valve.
	 * @param port2 Close/B side is wired to PCM, usually the next port.
	 * @param moduleNumber The CAN Device ID that the PCM is on.
	 */
	public FestoDA(PCMids moduleNumber,int port1, int port2) {
		Util.consoleLog("Port1=%d Port2=%d", port1, port2);
		
		valveOpenSide = new Solenoid(moduleNumber.value, port1);
		valveCloseSide = new Solenoid(moduleNumber.value, port2);
		
		solenoidSlideTime = .05;
		
		//Close();
	}
	
	public void dispose()
	{
		Util.consoleLog();
		
		valveOpenSide.free();
		valveCloseSide.free();
	}

	/**
	 * Open the valve (pressurize port).
	 */
	public void Open()
	{
		Util.consoleLog();
    
		valveCloseSide.set(false);
    
		valveOpenSide.set(true);
		Timer.delay(solenoidSlideTime);
		valveOpenSide.set(false);
	}
	
	/**
	 * Pressurize the A side of the valve.
	 */
	public void SetA()
	{
		Util.consoleLog();
		
		Open();
	}

	/**
	 * Close the valve (pressurize port+1).
	 */
	public void Close()
	{
		Util.consoleLog();
    
		valveOpenSide.set(false);
    
		valveCloseSide.set(true);
		Timer.delay(solenoidSlideTime);
		valveCloseSide.set(false);
	}
	
	/**
	 * Pressurize the B side of the valve.
	 */
	public void SetB()
	{
		Util.consoleLog();
		
		Close();
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