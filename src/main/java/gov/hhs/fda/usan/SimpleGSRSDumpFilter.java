package gov.hhs.fda.usan;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.jayway.jsonpath.JsonPath;

public class SimpleGSRSDumpFilter {
	
	
	public static void main(String[] args) throws IOException {

		//Defining the input file
    	String gsrsDumpFileName = "files/dump-public.gsrs";
    	//Defining the output file
    	String gsrsDumpFileNameFilt = "files/dump-public.filtered.gsrs";
    	
    	//Defining the filter file
    	String keepListFile = "files/keeplist.txt";
    	
    	//MATCH CASE 0:
    	//UUID
    	
    	//MATCH CASE 1 (priority IDs):
    	//
    	// "FDA UNII"
    	// "EMA ID"
    	// 
    	
    	//MATCH CASE 2:
    	//
    	// INCHIKEY
    	//
    	
    	//MATCH CASE 3:
    	// "CAS"
    	// 
    	
    	
    	//A collection of UUIDs in memory
		Set<String> keepUUIDs = new HashSet<>();
		
		Set<String> foundUUIDs = new HashSet<>();
		
		//Read filter file, and put each UUID into memory in the Set
		
		try(InputStream fis=new FileInputStream(new File(keepListFile))){
			 new BufferedReader(new InputStreamReader(fis,
			          StandardCharsets.UTF_8))
			 .lines()
			 .forEach(l->{
				 keepUUIDs.add(l);
			 });
		}
		

		//Read through the input file and filter
    	try(	InputStream fis=new FileInputStream(new File(gsrsDumpFileName)); 
    			PrintWriter outputPrinter= new PrintWriter(new GZIPOutputStream(new FileOutputStream(new File(gsrsDumpFileNameFilt))))){
			 try(InputStream in = new GZIPInputStream(fis)) {
				 new BufferedReader(new InputStreamReader(in,
				          StandardCharsets.UTF_8))
				 .lines()
				 .filter(p->{
					 String json1=p.split("\t")[2];
					 String uuid = JsonPath.read(json1,"$.uuid");
					 if(keepUUIDs.contains(uuid)){
						 foundUUIDs.add(uuid);
						 System.out.println("FOUND:" + uuid);
						 return true;
					 }else{
						 return false;
					 }
				 })
				 .forEach(p->{
					 outputPrinter.println(p);
				 });
				 
			 }
			 
    	}
    	
    	keepUUIDs.stream()
    	.filter(u->!foundUUIDs.contains(u))
    	.forEach(u->{
    		System.out.println("NOT FOUND:" + u);
    	});
    	
    	System.out.println("DONE");
		
	}
}
