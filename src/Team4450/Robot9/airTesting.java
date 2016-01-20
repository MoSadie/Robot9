package Team4450.Robot9;

import Team4450.Lib.*;

public class airTesting {
	Robot robot;
	int port;
	boolean da;
	FestoDA fda;
	FestoSA fsa;
	
	public airTesting (Robot Robot, int Port, boolean PutHereIfDa)
	{
		robot = Robot;
		port = Port;
		da = PutHereIfDa;
		fda = new FestoDA(port);
	}
	
	public airTesting (Robot Robot, int Port)
	{
		robot = Robot;
		port = Port;
		da = false;
		fsa = new FestoSA(port);
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
