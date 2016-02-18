package Team4450.Robot9.Tower;

import Team4450.Lib.*;
import Team4450.Robot9.*;
import edu.wpi.first.wpilibj.*;

public class Shooter {
	private Talon[] launchMotors;
	private TowerControl towerControl;
	
	Shooter(Robot robot) {
		towerControl = robot.towerControl;
		launchMotors = towerControl.launchMotors;
	}
	
	public boolean fire(double speed) {
		if (Math.abs(speed) > 1) {
			Util.consoleLog("Speed not set correctly, expected between -1 and 1, got " + speed);
			return false;
		}
		towerControl.belt.set(0.5);
		launchMotors[0].set(speed);
		launchMotors[1].set(speed);
		Timer.delay(1); //TODO Check this number
		towerControl.belt.set(0);
		launchMotors[0].set(0);
		launchMotors[1].set(0);
		return true;
	}
}
