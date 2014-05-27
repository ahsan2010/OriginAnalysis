package com.marge.split;

import javaxt.io.*;
import javaxt.utils.Array;
import javaxt.utils.Date;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.*;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Comment;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

import java.util.*;import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


//import org.eclipse.core.
import java.io.File;
import java.io.FileFilter;



public class FileSearch {
	
	
	javaxt.io.File[] filesVersionOne;
	javaxt.io.File[] filesVersionTwo;   // Just container , they will contain all the java files...
	
	///home/amee/Documents/OriginAnalysis/TestVersion
	///home/amee/Documents/OriginAnalysis/TestVersion/
	String repFolder = "/home/amee/Documents/OriginAnalysis/TestVersion/";  // Set your Location of the Repository Folder
	String resultLocation = "/home/amee/Documents/do"; // Set the Result Location .. 
	
	
	BufferedWriter writerShort;  
	BufferedWriter writerLong;		// Writers for OutputFiles..
	
	Map <String,Integer> map1 = new HashMap<String,Integer>();
	Map <String,Integer> map2 = new HashMap<String,Integer>();
	
	List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods1 =
			    new HashMap<MethodDeclaration, ArrayList<MethodInvocation>>();
	HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods2 =
		    new HashMap<MethodDeclaration, ArrayList<MethodInvocation>>();
	HashMap<String, ArrayList<MethodDeclaration>> caller1 =
		    new HashMap<String, ArrayList<MethodDeclaration>>();
	HashMap<String, ArrayList<MethodDeclaration>> caller2 =
		    new HashMap<String, ArrayList<MethodDeclaration>>();
	
	int compMaxVersion = 1 ;
	boolean compMaxEditable = false ;   // Check Maximum number of Version you need to compare
	int total = 0;
	final int THvalue = 10 ;
	
	/*
	 * Mapping the files name with the java files for quickly access the java files
	 * 
	 */
	
	public void fillinMap(){
		
		int fileSize1 = filesVersionOne.length;
		int fileSize2 = filesVersionTwo.length;
		
		System.out.println("Version1  " + fileSize1 );    
		System.out.println("Version2 " + fileSize2 );
		
		
		for ( int i = 0 ; i < fileSize1 ; i++ ){
			map1.put(filesVersionOne[i].getName(), i);
		}
		for ( int i = 0 ; i < fileSize2 ; i++ ){
			map2.put(filesVersionTwo[i].getName(), i);
		}
	
	}
	
	/*
	 * 
	 * If there is no difference between two java files
	 * we eliminate them not looking for merge/split between
	 * these two files
	 * 
	 * 
	 */
	
	boolean checkDiff(String path1,String path2){
		
		try {
			
			Process p = Runtime.getRuntime().exec("diff -q "+path1+" "+path2);
			 BufferedReader stdInput = new BufferedReader(new 
	                 InputStreamReader(p.getInputStream()));
			 String output = "";
					 output += stdInput.readLine();
			 if(output.isEmpty()) return false;  // No difference	
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		return true;
	}
	
	/*
	 * Taking two file versions with similar name files for investigating 
	 * merging and splitting with given ThreshHold Value
	 * 
	 */
	
	public void compareFiles(Element item,Document doc){		
		for ( javaxt.io.File f : filesVersionOne){
			if(map2.containsKey(f.getName())){
				int index = (int) map2.get(f.getName());
				if(checkDiff(f.getPath()+f.getName(),filesVersionTwo[index].getPath()+filesVersionTwo[index].getName())){
					
					try{
						
						//System.out.println(f.getName());
						
						Element className = doc.createElement("Class");
						
						CompareMethods cm = new CompareMethods(f,filesVersionTwo[index],writerShort,writerLong,className,doc,THvalue);
						cm.extractMethodyFromAST();
						cm.CalculateMatchLines();
						boolean match = cm.calculateSimilarity();
						if (match){
							
							
							className.setAttribute("LocationV1", f.getPath()+f.getName());
							className.setAttribute("LocationV2", filesVersionTwo[index].getPath()+filesVersionTwo[index].getName());
							className.setAttribute("ClassName", f.getName());
							item.appendChild(className);
						}
						
						
					}catch (Exception e ){
						e.printStackTrace();
					}
					
					
				}
			}
		}
	}
	
	/*	
	 *  readRepository() :
	 * 
	 *  Given the location of the release repository or revision repository
	 *  the following function reads the repository and and sorts out the
	 * 	the java files and then mapping the files with the name for 
	 * 	quick access and then call the main compare files between two 
	 *  releases or revisions
	 *  
	 *  if 'compMaxEditable' is true that means predefined number of revisions need to be
	 *  checked   otherwise it will check ( totalNumber of revision - 1 ) checking from the
	 *  repository location
	 *  
	 * 	
	 */	
	
	public void readRepository() {

		String curRevision;
		String nextRevision;

		try {

			ArrayList<String> versionFolders = new ArrayList<String>();
			String st[] = new String[1000];
			repFolder = repFolder.replace("\\", "/");
			resultLocation = resultLocation.replace("\\", "/");
			File dir = new File(repFolder); // Enter the location of your
											// Repository
			File[] subDirs = dir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});
			for (File subDir : subDirs) {
				versionFolders.add(subDir.getName()); // Collecting all the
														// sub-directory of the
														// versions .. (Total
														// Version Folders . .
														// .)
			}

			Collections.sort(versionFolders, new Comparator<String>() {
				@Override
				public int compare(String a, String b) {
					return (a.compareTo(b)); // Sorting the versions with name
				}
			});

			for (String s : versionFolders) {
				System.out.println("Version " + s);
			}

			int ind = 0;
			for (String s : versionFolders) {
				st[ind] = "";
				st[ind++] += s;
			}

			compMaxVersion = compMaxEditable ? Math.min(compMaxVersion,
					versionFolders.size() - 1) : versionFolders.size() - 1;

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("MergeSplit");
			doc.appendChild(rootElement);

			File outFileShort = new File(resultLocation + "result.txt"); // Output
																			// File
			File outFileLong = new File(resultLocation + "result_data.txt"); // Output
																				// with
																				// Table
																				// of
																				// Metrics
																				// ..

			outFileShort.createNewFile();
			outFileLong.createNewFile();

			System.out.println(outFileLong.getAbsolutePath());

			writerShort = new BufferedWriter(new FileWriter(
					outFileShort.getAbsoluteFile()));
			writerLong = new BufferedWriter(new FileWriter(
					outFileLong.getAbsoluteFile())); // Creating Buffer Writer
														// for storing the
														// result

			System.out.println("The Program Starts Running");

			for (int i = 0; i < compMaxVersion; i++) {

				String dir1 = repFolder + st[i];
				String dir2 = repFolder + st[i + 1];

				curRevision = st[i];
				nextRevision = st[i + 1];
				
				
				Element item = doc.createElement("Release");
				rootElement.setAttribute("ThreshHold", THvalue+"");
				rootElement.appendChild(item);
				item.setAttribute("Version", curRevision + " Vs " + nextRevision);
				
				
				
				writerLong.write("-----Ver " + curRevision + "  Ver "
						+ nextRevision + "-----" + "\n\n");
				writerShort.write("-----Ver " + curRevision + "  Ver "
						+ nextRevision + "-----" + "\n\n");

				// Filtering the java file from the versions directory ...

				javaxt.io.Directory directory = new javaxt.io.Directory(dir1);
				javaxt.io.Directory directory2 = new javaxt.io.Directory(dir2);
				filesVersionOne = directory.getFiles(".java", true);
				filesVersionTwo = directory2.getFiles(".java", true);

				fillinMap(); // Mapping all the java files for faster access ..
								// .
				compareFiles(item,doc); // Compare the files between the two versions..
								// .

				System.out.println("Finished Version Compare " + st[i] + " "
						+ st[i + 1]);

			}
			
			writerLong.close();
			writerShort.close();
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(resultLocation+"result.xml"));
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Reading Finished");
	}
	
	public static void main ( String arg[ ])  {
			
		FileSearch  program = new FileSearch();
		program.readRepository();
	
	} 
}
