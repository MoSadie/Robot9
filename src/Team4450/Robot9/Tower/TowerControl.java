package Team4450.Robot9.Tower;

import Team4450.Lib.*;
import Team4450.Robot9.*;
import edu.wpi.first.wpilibj.*;

public class TowerControl {
	public Shooter shoot;
	public Pickup pickup;
	FestoDA pickupPiston;
	CANTalon belt;
	Talon[] launchMotors;
	public TowerControl(Robot robot){
		pickupPiston = new FestoDA(6);
		belt = new CANTalon(7);
		if (robot.robotProperties.getProperty("RobotID").equals("comp")) {
			launchMotors[0] = new Talon(0);
			launchMotors[1] = new Talon(1);
		} else if (robot.robotProperties.getProperty("RobotID").equals("clone")) {
			launchMotors[0] = new Talon(7);
			launchMotors[1]= new Talon(8);
		}
		shoot = new Shooter(robot);
		pickup = new Pickup(robot);
	}
}
