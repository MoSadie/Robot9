package Team4450.Robot9.Tower;

import Team4450.Lib.*;
import Team4450.Robot9.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pickup {
	private Robot robot;
	private SpeedController belt;
	private TowerControl towerControl;
	Pickup(Robot robot){
		this.robot = robot;
		towerControl = robot.towerControl;
		if (towerControl.belt!=null) {
		belt = towerControl.belt;
		} else {
			Util.consoleLog("Belt is null in towerControl.");
			belt=null;
		}
	}
	public void pickupBall() {
		Util.consoleLog();
		towerControl.setBelt("down");
		toggleBelt(BeltStates.IN);
		while (robot.towerControl.ballCheck.get() == false) {
			Timer.delay(0.0050);
		}
		toggleBelt(BeltStates.STOP);
	}
	
	public void toggleBelt(BeltStates state) {
		if (state.equals(BeltStates.IN)) {
			SmartDashboard.putBoolean("PickupMotor", true);
			belt.set(BeltStates.IN.value);
		} else if (state.equals(BeltStates.STOP)) {
			SmartDashboard.putBoolean("PickupMotor", false);
			belt.set(BeltStates.STOP.value);
		} else if (state.equals(BeltStates.OUT)) {
			SmartDashboard.putBoolean("PickupMotor", true);
			belt.set(BeltStates.OUT.value);
		}
	}
	
	public enum BeltStates {
		IN (1),
		OUT (-1),
		STOP (0);
		
		public int value;
		
		private BeltStates (int value) {
			this.value = value;
		}
		
	}
}
