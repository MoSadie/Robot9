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
	public CANTalon belt;
	public Talon launchMotor1;
	public Talon launchMotor2;
	public FestoDA shootPiston;
	public DigitalInput ballCheck;
	public boolean initDone;
	public TowerControl(Robot robot){
		pickupPiston = new FestoDA(6);
		belt = new CANTalon(7);
		robot.InitializeCANTalon(belt);
		shootPiston = new FestoDA(4);
		Util.consoleLog("FLARE!!");
		//if (robot.isComp()) {
			launchMotor1 = new Talon(0);
			launchMotor2 = new Talon(1);
		Util.consoleLog("LANDED");
		//} else if (robot.isClone()) {
		//	launchMotor1 = new Talon(7);
		//	launchMotor2= new Talon(8);
		//}
		ballCheck = new DigitalInput(0);
		shoot = new Shooter(robot);
		pickup = new Pickup(robot);
		
	}
	
	/**
	 * Initializes the TowerControl class, setting the belt and hood position to known states.
	 */
	public void run() {
		if (initDone==false) {
			initDone = true;
			pickupPiston.SetB();
			shootPiston.SetA();
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
