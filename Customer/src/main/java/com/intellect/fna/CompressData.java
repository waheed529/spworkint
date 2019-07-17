package com.intellect.fna;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CompressData {
	
public static String getCompressData(String result) throws IOException{
		String originalData=result;
		byte[] input = originalData.getBytes("UTF-8");		//encodes this String into a sequence of bytes using the platform's default charset, storing the result into a new byte array.Unicode Transformation Format (UTF).
		Deflater deflater = new Deflater();  
		deflater.setInput(input);  
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(input.length);   
		deflater.finish();  
		byte[] buffer = new byte[1024];   
		while (!deflater.finished()) {  
			int count = deflater.deflate(buffer); // returns the generated code... index  
			outputStream.write(buffer, 0, count);   
		   }  
		   outputStream.close();  
		 byte[] output = outputStream.toByteArray();  
		   JSONObject compressData = new JSONObject();
		   compressData.put("data",output);
		return compressData.toString();	
		
	}

}
