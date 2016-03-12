package Team4450.Robot9.Tower;

import Team4450.Lib.*;
import Team4450.Robot9.*;
import edu.wpi.first.wpilibj.*;

/**
 * This controls the 'tower' on the 2016 robot. This controls firing and picking up boulders.
 * @author Sean Flo
 *
 */
public class TowerControl extends Thread {
	public Shooter shoot;
	public Pickup pickup;
	public FestoDA pickupPiston;
	public SpeedController belt;
	public Talon launchMotor1;
	public Talon launchMotor2;
	public FestoDA shootPiston;
	public DigitalInput ballCheck;
	public boolean initDone;
	private Robot robot;
	public TowerControl(Robot robot){
		try {
		this.robot = robot;
		pickupPiston = new FestoDA(6);
		shootPiston = new FestoDA(4);
		Util.consoleLog("DigInput");
		ballCheck = new DigitalInput(0);
		Util.consoleLog("DigInput end");
		this.belt = robot.belt;
		} catch (Exception e) {e.printStackTrace(Util.logPrintStream);}
		
	}
	
	/**
	 * Initializes the TowerControl class, setting the belt and hood position to known states.
	 */
	public void run() {
		if (initDone==false) {
			initDone = true;
			pickupPiston.SetB();
			shootPiston.SetA();
			if (robot.isComp()) {
				robot.InitializeCANTalon((CANTalon) belt);
				launchMotor1 = new Talon(0);
				launchMotor2 = new Talon(1);
				Util.consoleLog("Launch Motors end");
			}
			else if (robot.isClone()) {
				launchMotor1 = new Talon(1);
				launchMotor2= new Talon(2);
			}
			shoot = new Shooter(robot);
			pickup = new Pickup(robot);
		}
	}
	
	void setBelt(String position) {
		if (position.toLowerCase() == "down") {
			pickupPiston.SetB();
		}
		if (position.toLowerCase() == "up") {
			pickupPiston.SetA();
		}
	}
}
