package Team4450.Robot9;

import Team4450.Lib.*;

public class airTesting {
	Robot robot;
	int port;
	boolean da;
	FestoDA fda;
	FestoSA fsa;
	
	public airTesting (Robot Robot, int Port, boolean Da)
	{
		robot = Robot;
		port = Port;
		da = Da;
		if (da == true) {
			fda = new FestoDA(port);
		}
		else if (da == false) {
			fsa = new FestoSA(port);
		}
		else {Util.consoleLog("airTest Error: Not valid da value: " + da);}
	}
	
	public void dispose() {
		if (fda != null) {fda.dispose();}
		if (fsa != null) {fsa.dispose();}
	}
	
	public void Open() {
		if (da) {
			fda.Open();
		} else if (da == false) {
			fsa.Open();
		}
	}
	
	public void Close() {
		if (da) {
			fda.Close();
		} else if (da == false) {
			fsa.Close();
		}
	}
}
