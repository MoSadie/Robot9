// 2016 competition robot code.
// Cleaned up and reorganized in preparation for 2016.
// For Robot "USS Kelvin" built for FRC game "First Stronghold".

package Team4450.Robot9;

import java.io.IOException;
import java.util.Properties;
import Team4450.Robot9.Tower.*;
import Team4450.Lib.*;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file.
 */

public class Robot extends SampleRobot 
{
  static final String  	PROGRAM_NAME = "SWF9-3.24.16-02";

  // Motor CAN ID/PWM port assignments (1=left-front, 2=left-rear, 3=right-front, 4=right-rear)
  CANTalon				LFCanTalon, LRCanTalon, RFCanTalon, RRCanTalon, LSlaveCanTalon, RSlaveCanTalon;
  Talon					LFPwmTalon, LRPwmTalon, RFPwmTalon, RRPwmTalon;
  public SpeedController		belt;
  RobotDrive      		robotDrive;
  
  final Joystick        utilityStick = new Joystick(2);	// 0 old ds configuration
  final Joystick        leftStick = new Joystick(0);	// 1
  final Joystick        rightStick = new Joystick(1);	// 2
  final Joystick		launchPad = new Joystick(3);
  
  final Compressor		compressor = new Compressor(0);
  final Compressor		compressor1 = new Compressor(1);

  public Relay			headLight = new Relay(0, Relay.Direction.kForward);
  
  public Properties		robotProperties;
	
  //AxisCamera			camera = null;
  CameraServer			usbCameraServer = null;

  DriverStation         ds = null;
    
  DriverStation.Alliance	alliance;
  int                       location;
    
  Thread               	monitorBatteryThread, monitorDistanceThread, monitorCompressorThread;
  public CameraFeed		cameraThread;
    
  static final String  	CAMERA_IP = "10.44.50.11";
  static final int	   	USB_CAMERA = 2;
  static final int     	IP_CAMERA = 3;
  
  public TowerControl towerControl;
  public SendableChooser autoChoice;
 
  //public final NetworkTable grip = NetworkTable.getTable("GRIP");
  
  //public Process gripProcess;
  
  final AnalogGyro		gyro = new AnalogGyro(0); 
  public Robot() throws IOException
  {	
	// Set up our custom logger.
	  
	try
	{
		Util.CustomLogger.setup();
    }
    catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}
      
    try
    {
    	Util.consoleLog(PROGRAM_NAME);
    
        ds = DriverStation.getInstance();

        // IP Camera object used for vision processing.
        //camera = AxisCamera.getInstance(CAMERA_IP);
        Util.consoleLog("%s %s", PROGRAM_NAME, "end");
    }
    catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}
  }
    
  public void robotInit()
  {
   	try
    {
   		Util.consoleLog();

   		LCD.clearAll();
   		LCD.printLine(1, "Mode: RobotInit");
      
   		// Read properties file from RoboRio "disk".
      
   		robotProperties = Util.readProperties();
      
   		SmartDashboard.putString("Program", PROGRAM_NAME);
   		
   		//SmartDashboard.putBoolean("CompressorEnabled", false);
   		SmartDashboard.putBoolean("CompressorEnabled", Boolean.parseBoolean(robotProperties.getProperty("CompressorEnabledByDefault")));

   		// Reset PDB sticky faults.
      
   		PowerDistributionPanel PDP = new PowerDistributionPanel();
   		PDP.clearStickyFaults();
   		
   		// Reset PCM sticky faults.
   		
   		compressor.clearAllPCMStickyFaults();
   		compressor1.clearAllPCMStickyFaults();

   		// Configure motor controllers and RobotDrive.
        // Competition robot uses CAN Talons clone uses PWM Talons.
   		
		if (robotProperties.getProperty("RobotId").equals("comp")) 
			InitializeCANTalonDrive();
		else
			InitializePWMTalonDrive();
		
        robotDrive.stopMotor();
    
        robotDrive.setExpiration(0.1);
        
        // Reverse motors so they all turn on the right direction to match "forward"
        // as we define it for the robot.
        robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
    
        robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
   		
   		// Set starting camera feed on driver station to USB-HW.
      
   		SmartDashboard.putNumber("CameraSelect", USB_CAMERA);

   		// Start usb camera feed server on roboRIO. usb camera name is set by roboRIO.
   		// If camera feed stops working, check roboRIO name assignment.
   		// Note this is not used if we do dual usb cameras, which are handled by the
   		// cameraFeed class. The function below is the standard WpiLib server which
   		// can be used for a single usb camera.
      
   		//StartUSBCameraServer("cam0");
      
   		// Start the battery, compressor, camera feed and distance monitoring Tasks.

   		monitorBatteryThread = new MonitorBattery(ds);
   		monitorBatteryThread.start();

   		monitorCompressorThread = new MonitorCompressor();
   		monitorCompressorThread.start();

   		// Start camera server using our class for dual usb cameras.
      
   		//cameraThread = new CameraFeed(this);
   		//cameraThread.start();
     
   		// Start thread to monitor distance sensor.
   		
   		//monitorDistanceThread = new MonitorDistanceMBX(this);
   		//monitorDistanceThread.start();
   		
   		towerControl = new TowerControl(this);
   		autoChoice = displayAutonomousOptions();
   		
   		
   		Util.consoleLog("end");
    }
    catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}
  }
    
  public void disabled()
  {
	  try
	  {
		  Util.consoleLog();

		  LCD.printLine(1, "Mode: Disabled");

		  // Reset driver station LEDs.

		  SmartDashboard.putBoolean("Disabled", true);
		  SmartDashboard.putBoolean("Auto Mode", false);
		  SmartDashboard.putBoolean("Teleop Mode", false);
		  SmartDashboard.putBoolean("PTO", false);
		  SmartDashboard.putBoolean("FMS", ds.isFMSAttached());
		  
		  Grip.stopGrip();
		  
		  Util.consoleLog("end");
	  }
	  catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}
  }
    
  public void autonomous() 
  {
      try
      {
    	  Util.consoleLog();

    	  LCD.clearAll();
    	  LCD.printLine(1, "Mode: Autonomous");
            
    	  SmartDashboard.putBoolean("Disabled", false);
    	  SmartDashboard.putBoolean("Auto Mode", true);
    	  if (towerControl.initDone == false) {
    		  towerControl.start();
    	  }
    	  // Make available the alliance (red/blue) and staring position as
    	  // set on the driver station or FMS.
        
    	  alliance = ds.getAlliance();
    	  location = ds.getLocation();

    	  // This code turns off the automatic compressor management if requested by DS.
    	  compressor.setClosedLoopControl(SmartDashboard.getBoolean("CompressorEnabled", true));
             
    	  // Start autonomous process contained in the MyAutonomous class.
        
    	  Autonomous autonomous = new Autonomous(this);
        
    	  autonomous.execute();
        
    	  autonomous.dispose();
    	  
    	  SmartDashboard.putBoolean("Auto Mode", false);
    	  Util.consoleLog("end");
      }
      catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}
  }

  public void operatorControl() 
  {
      try
      {
    	  Util.consoleLog();

    	  LCD.clearAll();
      	  LCD.printLine(1, "Mode: Teleop");
            
      	  SmartDashboard.putBoolean("Disabled", false);
      	  SmartDashboard.putBoolean("Teleop Mode", true);
        
      	  alliance = ds.getAlliance();
      	  location = ds.getLocation();
        
          Util.consoleLog("Alliance=%s, Location=%d, FMS=%b", alliance.name(), location, ds.isFMSAttached());
        
          // This code turns off the automatic compressor management if requested by DS.
          compressor.setClosedLoopControl(SmartDashboard.getBoolean("CompressorEnabled", true));
          
          //Print the Joysticks connected to the DS
     		Util.consoleLog("Joystick 0: " + leftStick.getName());
     		Util.consoleLog("Joystick 1: " + rightStick.getName());
     		Util.consoleLog("Joystick 2: " + utilityStick.getName());
     		Util.consoleLog("Joystick 3: " + launchPad.getName());
     		
     	  if (towerControl.initDone == false) {
     		  towerControl.start();
     	  }
     	  
          // Start operator control process contained in the MyTeleop class.
        
          Teleop teleOp = new Teleop(this);
        
          teleOp.OperatorControl();
        
          teleOp.dispose();
        	
          Util.consoleLog("end");
       }
       catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}
  }
    
  public void test() 
  {
  }

  // Start WpiLib usb camera server for single selected camera.
  
  public void StartUSBCameraServer(String cameraName)
  {
	  Util.consoleLog(cameraName);

	  usbCameraServer = CameraServer.getInstance();
      usbCameraServer.setQuality(30);
      usbCameraServer.startAutomaticCapture(cameraName);
  }

  private void InitializeCANTalonDrive()
  {
	  Util.consoleLog();

	  LFCanTalon = new CANTalon(1);
	  LRCanTalon = new CANTalon(2);
	  RFCanTalon = new CANTalon(3);
	  RRCanTalon = new CANTalon(4);
	  LSlaveCanTalon = new CANTalon(5);
	  RSlaveCanTalon = new CANTalon(6);
	  
	  robotDrive = new RobotDrive(LFCanTalon, LRCanTalon, RFCanTalon, RRCanTalon);
	  
	  belt = new CANTalon(7);

      // Initialize CAN Talons and write status to log so we can verify
      // all the talons are connected.
      InitializeCANTalon(LFCanTalon);
      InitializeCANTalon(LRCanTalon);
      InitializeCANTalon(RFCanTalon);
      InitializeCANTalon(RRCanTalon);
      InitializeCANTalon(LSlaveCanTalon);
      InitializeCANTalon(RSlaveCanTalon);
      InitializeCANTalon((CANTalon) belt);
      
      // Configure slave CAN Talons to follow the front L & R Talons.
      LSlaveCanTalon.changeControlMode(TalonControlMode.Follower);
      LSlaveCanTalon.set(LFCanTalon.getDeviceID());

      RSlaveCanTalon.changeControlMode(TalonControlMode.Follower);
      RSlaveCanTalon.set(RFCanTalon.getDeviceID());
}

  public void InitializePWMTalonDrive()
  {
	  Util.consoleLog();

	  LFPwmTalon = new Talon(3);
	  LRPwmTalon = new Talon(4);
	  RFPwmTalon = new Talon(5);
	  RRPwmTalon = new Talon(6);
	  
	  robotDrive = new RobotDrive(LFPwmTalon, LRPwmTalon, RFPwmTalon, RRPwmTalon);
	  
	  belt= new Talon(7);
	  if (belt == null) {Util.consoleLog("Belt is null in robot.");}
  }
  
/** Initialize and Log status indication from CANTalon. If we see an exception
* or a talon has low voltage value, it did not get recognized by the RR on start up.
* @param talon A CANTalon object to initialize
**/
  public void InitializeCANTalon(CANTalon talon)
  {
	  Util.consoleLog("talon init: %s   voltage=%.1f", talon.getDescription(), talon.getBusVoltage());

	  talon.clearStickyFaults();
	  talon.enableControl();
	  talon.changeControlMode(TalonControlMode.PercentVbus);
  }
  public boolean isComp(){
	  try {
	  return robotProperties.getProperty("RobotId").equals("comp");
	  }
	  catch (Exception e) {e.printStackTrace(Util.logPrintStream);}
	  return false;
  }
  public boolean isClone(){
	  try {
	  return robotProperties.getProperty("RobotId").equals("clone");
	  }
	  catch (Exception e) {e.printStackTrace(Util.logPrintStream);}
	  return false;
  }
  public SendableChooser displayAutonomousOptions() {
	  Util.consoleLog();
	  SendableChooser autoChoice = new SendableChooser();
	  autoChoice.addDefault("Do Nothing", 0);
	  autoChoice.addObject("Drive Forward to defense and stop", 1);
	  autoChoice.addObject("Cross Rough Terrain", 2);
	  autoChoice.addObject("Cross Rock Wall", 3);
	  autoChoice.addObject("Shoot from SpyBot Position", 4);
	  autoChoice.addObject("Test Gyro", 5);
	  SmartDashboard.putData("SmartDashboard", autoChoice);
	  return autoChoice;
  }
}
