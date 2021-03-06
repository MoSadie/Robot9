
package Team4450.Robot9;

import java.lang.Math;
import Team4450.Lib.RobotMath;
import Team4450.Robot9.Tower.Pickup.BeltStates;
import Team4450.Lib.*;
import Team4450.Lib.FestoDA.PCMids;
import Team4450.Lib.JoyStick.*;
import Team4450.Lib.LaunchPad.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.networktables.*;

class Teleop
{
	private final Robot 		robot;
	private JoyStick			rightStick, leftStick, utilityStick;
	private LaunchPad			launchPad;
	private final FestoDA		shifterValve, ptoValve, rampValve;
	private boolean				ptoMode = false;
	//private final RevDigitBoard	revBoard = new RevDigitBoard();
	//private final DigitalInput	hallEffectSensor = new DigitalInput(0);
	
	// Constructor.
	
	Teleop(Robot robot)
	{
		Util.consoleLog();

		this.robot = robot;
		
		shifterValve = new FestoDA(2);
		ptoValve = new FestoDA(0);
		
		rampValve = new FestoDA(PCMids.PCM_ONE, 0);
	}

	// Free all objects that need it.
	
	void dispose()
	{
		Util.consoleLog();
		//airTest.dispose();
		if (leftStick != null) leftStick.dispose();
		if (rightStick != null) rightStick.dispose();
		if (utilityStick != null) utilityStick.dispose();
		if (launchPad != null) launchPad.dispose();
		if (shifterValve != null) shifterValve.dispose();
		if (ptoValve != null) ptoValve.dispose();
		if (rampValve != null) rampValve.dispose();
		//if (revBoard != null) revBoard.dispose();
		//if (hallEffectSensor != null) hallEffectSensor.free();
	}

	void OperatorControl()
	{
		double	rightY, leftY;
        
        // Motor safety turned off during initialization.
        robot.robotDrive.setSafetyEnabled(false);

		Util.consoleLog();
		
		LCD.printLine(1, "Mode: OperatorControl");
		LCD.printLine(2, "All=%s, Start=%d, FMS=%b", robot.alliance.name(), robot.location, robot.ds.isFMSAttached());
		
		// Initial setting of air valves.

		shifterLow();
		ptoDisable();
	
		
		rampValve.SetA();
		
		// Configure LaunchPad and Joystick event handlers.
		
		launchPad = new LaunchPad(robot.launchPad, LaunchPadControlIDs.BUTTON_BLACK, this);
		LaunchPadControl lpControl = launchPad.AddControl(LaunchPadControlIDs.ROCKER_LEFT_FRONT);
		lpControl.controlType = LaunchPadControlTypes.SWITCH;
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_YELLOW);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_BLUE);
		launchPad.AddControl(LaunchPadControlIDs.BUTTON_GREEN);
        launchPad.addLaunchPadEventListener(new LaunchPadListener());
        launchPad.Start();

		leftStick = new JoyStick(robot.leftStick, "LeftStick", JoyStickButtonIDs.TOP_LEFT, this);
        //leftStick.addJoyStickEventListener(new LeftStickListener());
        //leftStick.Start();
        
		rightStick = new JoyStick(robot.rightStick, "RightStick", JoyStickButtonIDs.TOP_LEFT, this);
        rightStick.AddButton(JoyStickButtonIDs.TRIGGER);
		rightStick.addJoyStickEventListener(new RightStickListener());
        rightStick.Start();
        
		utilityStick = new JoyStick(robot.utilityStick, "UtilityStick", JoyStickButtonIDs.TOP_LEFT, this);
		utilityStick.AddButton(JoyStickButtonIDs.TRIGGER);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_RIGHT);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_MIDDLE);
		utilityStick.AddButton(JoyStickButtonIDs.TOP_BACK);
        utilityStick.addJoyStickEventListener(new UtilityStickListener());
        utilityStick.Start();
        
        //Prototyping Cannon Testing Code
        //airTest = new airTesting(this.robot,1,true); //Robot, port, is DA
        
        
        // Motor safety turned on.
        robot.robotDrive.setSafetyEnabled(true);
        
		// Driving loop runs until teleop is over.

		while (robot.isEnabled() && robot.isOperatorControl())
		{
			// Get joystick deflection and feed to robot drive object.
			// using calls to our JoyStick class.

			if (ptoMode)
			{
				rightY = utilityStick.GetY();
				leftY = rightY;
			} 
			else
			{
    			rightY = rightStick.GetY();		// fwd/back right
    			leftY = leftStick.GetY();		// fwd/back left
    			
    			// This corrects stick alignment error when trying to drive straight. 
    			//if (Math.abs(rightY - leftY) < 0.2) rightY = leftY;
    			
    			if (rightY >0) {
    				rightY = RobotMath.log(4,rightY+1)+0.5;
    			} else if (rightY < 0) {
    				rightY = -RobotMath.log(4, -rightY+1)-0.5;
    			}
    			if (leftY >0) {
    				leftY = RobotMath.log(4,leftY+1)+0.5;
    			} else if (leftY < 0) {
    				leftY = -RobotMath.log(4, -leftY+1)-0.5;
    			}
			}

			LCD.printLine(4, "leftY=%.4f  rightY=%.4f", leftY, rightY);

			
			// Set motors.

			robot.robotDrive.tankDrive(leftY, rightY);

			// End of driving loop.
			
			Timer.delay(.020);	// wait 20ms for update from driver station.
		}
		
		// End of teleop mode.
		
		Util.consoleLog("end");
	}

	// Transmission control functions.
	
	void shifterLow()
	{
		Util.consoleLog();
		
		shifterValve.SetA();

		SmartDashboard.putBoolean("Low", true);
		SmartDashboard.putBoolean("High", false);
	}

	void shifterHigh()
	{
		Util.consoleLog();
		
		shifterValve.SetB();

		SmartDashboard.putBoolean("Low", false);
		SmartDashboard.putBoolean("High", true);
	}

	void ptoDisable()
	{
		Util.consoleLog();
		
		ptoMode = false;
		
		ptoValve.SetA();

		SmartDashboard.putBoolean("PTO", false);
	}

	void ptoEnable()
	{
		Util.consoleLog();
		
		ptoValve.SetB();

		ptoMode = true;
		
		SmartDashboard.putBoolean("PTO", true);
	}

	// Handle LaunchPad control events.
	
	public class LaunchPadListener implements LaunchPadEventListener 
	{
	    public void ButtonDown(LaunchPadEvent launchPadEvent) 
	    {
			Util.consoleLog("%s, latchedState=%b", launchPadEvent.control.id.name(),  launchPadEvent.control.latchedState);
			
			// Change which USB camera is being served by the RoboRio when using dual usb cameras.
			
			if (launchPadEvent.control.id.equals(LaunchPad.LaunchPadControlIDs.BUTTON_BLACK))
				if (launchPadEvent.control.latchedState)
					robot.cameraThread.ChangeCamera(robot.cameraThread.cam2);
				else
					robot.cameraThread.ChangeCamera(robot.cameraThread.cam1);
	
			if (launchPadEvent.control.id == LaunchPadControlIDs.BUTTON_BLUE)
			{
				if (launchPadEvent.control.latchedState)
    				shifterHigh();
    			else
    				shifterLow();
			}

			if (launchPadEvent.control.id == LaunchPadControlIDs.BUTTON_YELLOW)
			{
				if (launchPadEvent.control.latchedState)
				{
					shifterLow();
					ptoEnable();
				}
    			else
    				ptoDisable();
			}
			
			if (launchPadEvent.control.id.equals(LaunchPadControlIDs.BUTTON_GREEN)) {
				if (launchPadEvent.control.latchedState) 
					rampValve.SetB();
				else
					rampValve.SetA();
			}
			/* if (launchPadEvent.control.id == LaunchPadControlIDs.BUTTON_BLUE) {
				//Get published values from GRIP using NetworkTables
		        for (double centerX : grip.getNumberArray("myContoursReport/centerX", new double[0])) {
		            System.out.println("Got contour with x=" + centerX);
		        }
			} */
	    }
	    
	    public void ButtonUp(LaunchPadEvent launchPadEvent) 
	    {
	    	//Util.consoleLog("%s, latchedState=%b", launchPadEvent.control.name(),  launchPadEvent.control.latchedState);
	    }

	    public void SwitchChange(LaunchPadEvent launchPadEvent) 
	    {
	    	Util.consoleLog("%s", launchPadEvent.control.id.name());

	    	// Change which USB camera is being served by the RoboRio when using dual usb cameras.
			
			if (launchPadEvent.control.id.equals(LaunchPadControlIDs.BUTTON_FOUR))
				if (launchPadEvent.control.latchedState)
					robot.cameraThread.ChangeCamera(robot.cameraThread.cam2);
				else
					robot.cameraThread.ChangeCamera(robot.cameraThread.cam1);
	    }
	}

	// Handle Right JoyStick Button events.
	
	private class RightStickListener implements JoyStickEventListener 
	{
	    public void ButtonDown(JoyStickEvent joyStickEvent) 
	    {
			Util.consoleLog("%s, latchedState=%b", joyStickEvent.button.id.name(),  joyStickEvent.button.latchedState);
			
			if (joyStickEvent.button.id.equals(JoyStickButtonIDs.TRIGGER))
				if (joyStickEvent.button.latchedState) {
					robot.headLight.set(Relay.Value.kOn);
					SmartDashboard.putBoolean("Light", true);
				}
				else {
					robot.headLight.set(Relay.Value.kOff);
					SmartDashboard.putBoolean("Light", false);
				}
			
					
			// Change which USB camera is being served by the RoboRio when using dual usb cameras.
			
			if (joyStickEvent.button.id.equals(JoyStickButtonIDs.TOP_LEFT))
				if (joyStickEvent.button.latchedState)
					((CameraFeed) robot.cameraThread).ChangeCamera(((CameraFeed) robot.cameraThread).cam2);
				else
					((CameraFeed) robot.cameraThread).ChangeCamera(((CameraFeed) robot.cameraThread).cam1);			
	    }

	    public void ButtonUp(JoyStickEvent joyStickEvent) 
	    {
	    	//Util.consoleLog("%s", joyStickEvent.button.name());
	    }
	}

	// Handle Left JoyStick Button events.
	
	@SuppressWarnings("unused")
	private class LeftStickListener implements JoyStickEventListener 
	{
	    public void ButtonDown(JoyStickEvent joyStickEvent) 
	    {
			Util.consoleLog("%s, latchedState=%b", joyStickEvent.button.id.name(),  joyStickEvent.button.latchedState);
			
	    }

	    public void ButtonUp(JoyStickEvent joyStickEvent) 
	    {
	    	//Util.consoleLog("%s", joyStickEvent.button.name());
	    }
	}

	// Handle Utility JoyStick Button events.
	
	private class UtilityStickListener implements JoyStickEventListener 
	{
	    public void ButtonDown(JoyStickEvent joyStickEvent) 
	    {
			Util.consoleLog("%s, latchedState=%b", joyStickEvent.button.id.name(),  joyStickEvent.button.latchedState);
			
			//Changes State of the piston
			
			if (joyStickEvent.button.id.equals(JoyStickButtonIDs.TRIGGER))
				robot.towerControl.shoot.fire();
			
			if (joyStickEvent.button.id.equals(JoyStickButtonIDs.TOP_RIGHT))
				if (joyStickEvent.button.latchedState)
					robot.towerControl.pickup.pickupBall();
				else
					robot.towerControl.belt.set(0);
			
			if (joyStickEvent.button.id.equals(JoyStickButtonIDs.TOP_MIDDLE))
				if (joyStickEvent.button.latchedState)
					robot.towerControl.pickup.toggleBelt(BeltStates.IN);
				else
					robot.towerControl.pickup.toggleBelt(BeltStates.STOP);
			
			if (joyStickEvent.button.id.equals(JoyStickButtonIDs.TOP_BACK))
				if (joyStickEvent.button.latchedState)
					robot.towerControl.pickup.toggleBelt(BeltStates.OUT);
				else
					robot.towerControl.pickup.toggleBelt(BeltStates.STOP);
			
			
			if (joyStickEvent.button.id.equals(JoyStickButtonIDs.TOP_LEFT))
				if (joyStickEvent.button.latchedState)
					robot.towerControl.shoot.manualFire(true);
				else
					robot.towerControl.shoot.manualFire(false);
	    }

	    public void ButtonUp(JoyStickEvent joyStickEvent) 
	    {
	    	//Util.consoleLog("%s", joyStickEvent.button.id.name());
	    }
	}
}
