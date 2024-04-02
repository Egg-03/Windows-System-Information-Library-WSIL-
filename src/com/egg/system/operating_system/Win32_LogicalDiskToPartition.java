package com.egg.system.operating_system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.egg.system.logger.ErrorLog;

//Associates a drive letter to a given partition
public class Win32_LogicalDiskToPartition {
	private static String classname = new Object() {}.getClass().getName();
	private Win32_LogicalDiskToPartition() {
		throw new IllegalStateException("Utility Class");
	}
	
	public static String getDriveLetter(String partitionID) throws IOException{
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		String command[] = {"powershell.exe", "/c", "Get-CimInstance -ClassName Win32_LogicalDiskToPartition | Where-Object {$_.Antecedent.DeviceID -eq '"+partitionID+"'} | Select-Object Dependent | Format-List"};
		
		Process process = Runtime.getRuntime().exec(command);
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String currentLine;
		String driveLetter = "N/A";
		while((currentLine=br.readLine())!=null)
			if(!currentLine.isBlank() || !currentLine.isEmpty())
				driveLetter = currentLine.substring(currentLine.indexOf("\"")+1, currentLine.lastIndexOf("\""));
		br.close();
		//getting error stream
		if(driveLetter.isEmpty()) {
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String errorLine;
			List<String> errorList = new ArrayList<>();
			
			while((errorLine=error.readLine())!=null)
				if(!errorLine.isBlank() || !errorLine.isEmpty())
					errorList.add(errorLine);
			
			error.close();
			ErrorLog errorLog = new ErrorLog();
			
			errorLog.log("\n"+classname+"-"+methodName+"\n"+errorList.toString()+"\n\n");
		}
		return driveLetter;
	}
}
