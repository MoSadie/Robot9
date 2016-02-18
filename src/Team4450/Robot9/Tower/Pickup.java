package Team4450.Robot9.Tower;

import Team4450.Lib.*;
import Team4450.Robot9.*;
import edu.wpi.first.wpilibj.*;

public class Pickup {
	private FestoDA pickupPiston;
	private CANTalon belt;
	Pickup(Robot robot){
		pickupPiston = robot.towerControl.pickupPiston;
		belt = robot.towerControl.belt;
	}
	void PickupBall() {
		belt.set(-0.5);
		Timer.delay(0.5);
		pickupPiston.SetB(); //TODO Check This!!!
		belt.set(0);
	}
}
