package Team4450.Robot9.Tower;

import Team4450.Lib.*;
import Team4450.Robot9.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
	private Robot robot;
	private Talon launchMotor1;
	private Talon launchMotor2;
	private TowerControl towerControl;
	private FestoDA hoodPiston;
	
	Shooter(Robot robot) {
		try {
		this.robot = robot;
		towerControl = robot.towerControl;
		launchMotor1 = towerControl.launchMotor1;
		launchMotor2 = towerControl.launchMotor2;
		hoodPiston = towerControl.shootPiston;
		} catch (Exception e) {e.printStackTrace(Util.logPrintStream);}
	}
	
	/**
	 * Allows you to manually control when the shooter motors spins up.
	 * @param onOrOff Sets the motor On (True) or Off (False)
	 */
	public void manualFire(boolean onOrOff) {
		if (onOrOff) {
			SmartDashboard.putBoolean("ShooterMotor", true);
			launchMotor1.set(1);
			launchMotor2.set(1);
		} else if (!onOrOff) {
			SmartDashboard.putBoolean("ShooterMotor", false);
			launchMotor1.set(0);
			launchMotor2.set(0);
			robot.headLight.set(Relay.Value.kOff);
		}
	}
	
	
	/**
	 * This fires the launcher on the robot.
	 */
	public void fire() {
		towerControl.pickupPiston.SetA();
		towerControl.belt.set(1); //TODO Check this number
		Timer.delay(1); //TODO Check this number
		towerControl.belt.set(0);
		manualFire(false);
	}
	/**
	 * This adjusts the angle of the shooter tube. Accepts the strings 'retract' or 'extend'
	 * @param Position The position to put the shooter hood in. Accepts 'retract' or 'extend'
	 */
	public void adjustAngle(String Position) {
		if (Position == "retract") {
			hoodPiston.SetA();
			return;
		}
		if (Position == "extend") {
			hoodPiston.SetB();
			return;
		}
	}
}
