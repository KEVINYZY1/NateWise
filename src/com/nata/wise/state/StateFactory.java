package com.nata.wise.state;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.NodeList;

import com.nata.wise.cmdtool.GetAdb;


public class StateFactory {

	private static XMLParser mXmlParser = new XMLParser();

	/**
	 * create current state
	 * 
	 * @param serial
	 * @param out
	 * @return state
	 */
	public static State createState(String serial) {

		PkgAct lPkgAct = GetAdb.getCurrentPkgAct(serial);
		if (lPkgAct == null) {
			lPkgAct = new PkgAct("Unknow", "Unknow");
			return new State(lPkgAct, null);
		}

		File out = new File("dump");
		out = new File(out, serial);

		out.mkdirs();
		if (!out.exists()) {
			System.err.println("error: out file not exist!");
			return new State(new PkgAct("Unknow", "Unknow"), null);
		}

		File xmlDumpFile = new File(out, "dump.xml");
		if (!xmlDumpFile.exists())
			try {
				xmlDumpFile.createNewFile();
			} catch (IOException e) {
				System.err.println("dump.xml cannot create!");
				e.printStackTrace();
			}

		// start get UI
		int dumpCount = 0;
		NodeList nodeList = null;
		while (nodeList == null) {
			boolean ret=DumpUI.dumpThisDevice(serial, xmlDumpFile);
			if(ret)
				nodeList = mXmlParser.startParser(xmlDumpFile);
			dumpCount++;
			if (dumpCount > 3) {
				System.err.println("Dump error!");
				break;
			}
		}

		State lState = new State(lPkgAct, nodeList);

		return lState;
	}

	public static void main(String[] args) {
		String adb = "/Users/Tianchi/Tool/sdk/platform-tools/adb";
		GetAdb.setAdbFile(adb);
		File outFile = new File("/Users/Tianchi/AppTest/dump");
		if (!outFile.exists())
			outFile.mkdirs();

		State mState = createState("0093e1a0ce9a2fd0");
		System.out.println(mState.toString());
	}
}
