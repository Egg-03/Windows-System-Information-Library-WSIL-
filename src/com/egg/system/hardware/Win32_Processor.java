package com.egg.system.hardware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.egg.system.logger.ErrorLog;

public class Win32_Processor{
	private static String classname = new Object() {}.getClass().getName();
	private Win32_Processor() {
		throw new IllegalStateException("Utility Class");
	}
	
	public static List<String> getDeviceIDList() throws IOException {
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		List<String> deviceIDList = new ArrayList<>();
		String[] command = {"powershell.exe", "/c", "Get-CimInstance -ClassName Win32_Processor | Select-Object DeviceID | Format-List"};
		Process process = Runtime.getRuntime().exec(command);
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
		String currentLine;
		String value = "";
		while((currentLine=br.readLine())!=null)
			if(!currentLine.isBlank() || !currentLine.isEmpty())
				if(currentLine.contains(" : "))
					deviceIDList.add(value =currentLine);
				else {
					int lastIndex = deviceIDList.size()-1;
					deviceIDList.set(lastIndex, deviceIDList.get(lastIndex).concat(value));
				}
			
		br.close();
		
		//getting error stream
		if(deviceIDList.isEmpty()) {
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
		
		//strip the property_name and keep only the property value
		for(int i=0 ; i<deviceIDList.size(); i++) {
			deviceIDList.set(i, deviceIDList.get(i).substring(deviceIDList.get(i).indexOf(":")+1).strip());
		}
		
		return deviceIDList;
	}
	
	public static Map<String, String> getCurrentProcessor(String deviceID) throws IOException {
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		String[] command = {"powershell.exe", "/c", "Get-CimInstance -ClassName Win32_Processor | Where-Object {$_.DeviceID -eq '"+deviceID+"'} | Select-Object Name, NumberOfCores, ThreadCount, Manufacturer, AddressWidth, L2CacheSize, L3CacheSize, MaxClockSpeed, ExtClock, SocketDesignation, Version, Caption, Family, Stepping, VirtualizationFirmwareEnabled, ProcessorID | Format-List"};
		
		Process process = Runtime.getRuntime().exec(command);
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		String currentLine;
		Map<String, String> propertyValues = new LinkedHashMap<>();
		
		while((currentLine=br.readLine())!=null)
			if(!currentLine.isBlank() || !currentLine.isEmpty()) {
				String key = "";
				String value = "";
				if(currentLine.contains(" : "))
					propertyValues.put(key=currentLine.substring(0, currentLine.indexOf(":")).strip(), value =currentLine.substring(currentLine.indexOf(":")+1).strip());
				else
					propertyValues.replace(key, value.concat(currentLine.strip()));
			}
		br.close();
		
		//getting error stream
		if(propertyValues.isEmpty()) {
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
		return propertyValues;
	}
}
