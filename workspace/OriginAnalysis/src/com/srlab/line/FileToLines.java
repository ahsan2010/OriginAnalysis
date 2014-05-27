package com.srlab.line;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileToLines {

		int line ;
	
	
	/**
	 * This function reads a file line basis and convert them to lines stored in a list
	 * it does not remove the blank lines or do any other formatting 
	 * @param filename the name of the file
	 * @return a list containing the lines where every list element represents a line
	 */
	public static ArrayList<String> convert(String body) {

			ArrayList<String> lines = new ArrayList<String>();
            String line = "";
            
            /* Line numbers start with 1. ArrayList index represent the line number. Since 
             * ArrayList index start with zero, so we add a dummy line add the beginning 
             */
          
            lines.add("IGNORE THIS LINE");
            try {
                    BufferedReader in = new BufferedReader(new StringReader(body));
                   
                    while ((line = in.readLine()) != null) {
                    	/* we remove multiple spaces with a single one */
                    	lines.add(line.replaceAll("\\s+"," ").trim());         
                    }
            } catch (IOException e) {
                    e.printStackTrace();
            }

            return lines;
    }
}