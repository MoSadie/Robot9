// 2016 competition robot code.
// Cleaned up and reorganized in preparation for 2016.
// For Robot "tba" built for FRC game "First Stronghold".

package Team4450.Robot9;

import java.io.IOException;
import java.util.Properties;

import Team4450.Lib.*;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file.
 */

public class Robot extends SampleRobot 
{
  static final String  	PROGRAM_NAME = "SWF9-1.28.01-01N";
  
  //Create CANTalon s for each motor controller
  final CANTalon left_front = new CANTalon(1);
  final CANTalon left_rear = new CANTalon(2);
  final CANTalon right_front = new CANTalon(3);
  final CANTalon right_rear = new CANTalon(4);
  
  // Motor CAN port assignments (1=front-left, 2=rear-left, 3=front-right, 4=rear-right)
  final RobotDrive      robotDrive = new RobotDrive(left_front,left_rear,right_front,right_rear);
  final Joystick        utilityStick = new Joystick(2);	// 0 old ds configuration
  final Joystick        leftStick = new Joystick(0);	// 1
  final Joystick        rightStick = new Joystick(1);	// 2
  final Joystick		launchPad = new Joystick(3);
  final Compressor		compressor = new Compressor(0);

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
  
  //Test GRIP Code
  private final static String[] GRIP_ARGS = new String[] {
	        "/usr/local/frc/JRE/bin/java", "-jar",
	        "/home/lvuser/grip.jar", "/home/lvuser/project.grip" };

  public final NetworkTable grip = NetworkTable.getTable("GRIP");

 
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

        robotDrive.stopMotor();
    
        robotDrive.setExpiration(0.1);
    
        ds = DriverStation.getInstance();

        // IP Camera object used for vision processing.
        //camera = AxisCamera.getInstance(CAMERA_IP);

        // Initialize CAN Talons and write status to log so we can verify
        // all the talons are connected.
        initializeCANTalon(left_front);
        initializeCANTalon(left_rear);
        initializeCANTalon(right_front);
        initializeCANTalon(right_rear);
        
        left_front.setInverted(true);
        right_front.setInverted(true);
        //robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        //robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
    
        //robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        //robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
      	
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
   		
   		/* Run GRIP in a new process */
        try {
            Runtime.getRuntime().exec(GRIP_ARGS);
        } catch (IOException e) {
            e.printStackTrace(Util.logPrintStream);
        }
            
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
		  SmartDashboard.putBoolean("FMS", ds.isFMSAttached());

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

  // Initialize and Log status indication from CANTalon. If we see an exception
  // or a talon has low voltage value, it did not get recognized by the RR on start up.
  
  public void initializeCANTalon(CANTalon talon)
  {
	  Util.consoleLog("talon init: %s   voltage=%.1f", talon.getDescription(), talon.getBusVoltage());

	  talon.clearStickyFaults();
	  talon.enableControl();
	  talon.changeControlMode(TalonControlMode.PercentVbus);
  }
}
