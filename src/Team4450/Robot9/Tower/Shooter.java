package Team4450.Robot9.Tower;

import Team4450.Lib.*;
import Team4450.Robot9.*;
import edu.wpi.first.wpilibj.*;

public class Shooter {
	private Talon launchMotor1;
	private Talon launchMotor2;
	private TowerControl towerControl;
	private FestoDA shootPiston;
	
	Shooter(Robot robot) {
		towerControl = robot.towerControl;
		launchMotor1 = towerControl.launchMotor1;
		launchMotor2 = towerControl.launchMotor2;
		shootPiston = towerControl.shootPiston;
	}
	
	/**
	 * This fires the launcher on the robot.
	 * @param speed The speed to fire the ball at. (Range: -1 to 1)
	 */
	public void fire(double speed) {
		if (Math.abs(speed) > 1) {
			Util.consoleLog("Speed not set correctly, expected between -1 and 1, got " + speed);
			return;
		}
		towerControl.pickupPiston.SetA();
		launchMotor1.set(speed);
		launchMotor2.set(speed);
		Timer.delay(2);
		towerControl.belt.set(1); //TODO Check this number
		Timer.delay(1); //TODO Check this number
		towerControl.belt.set(0);
		launchMotor1.set(0);
		launchMotor2.set(0);
	}
	/**
	 * This adjusts the angle of the shooter tube. Accepts the strings 'retract' or 'extend'
	 * @param Position The position to put the shooter hood in. Accepts 'retract' or 'extend'
	 */
	public void adjustAngle(String Position) {
		if (Position == "retract") {
			shootPiston.SetA();
			return;
		}
		if (Position == "extend") {
			shootPiston.SetB();
			return;
		}
	}
}
