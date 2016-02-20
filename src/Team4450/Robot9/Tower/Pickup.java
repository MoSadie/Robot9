package Team4450.Robot9.Tower;

import Team4450.Lib.*;
import Team4450.Robot9.*;
import edu.wpi.first.wpilibj.*;

public class Pickup {
	private Robot robot;
	private FestoDA pickupPiston;
	private CANTalon belt;
	private TowerControl towerControl;
	Pickup(Robot robot){
		this.robot = robot;
		towerControl = robot.towerControl;
		pickupPiston = towerControl.pickupPiston;
		belt = towerControl.belt;
	}
	void PickupBall() {
		Util.consoleLog();
		towerControl.setBelt("down");
		belt.set(-0.75);
		while (robot.towerControl.ballCheck.get() == false) {
			Timer.delay(0.0050);
		}
		belt.set(0);
	}
	
}
