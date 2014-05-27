/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.srlab.line;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author muhammad
 */
public class GnuDiff {
   
    public static ArrayList<Integer> runDiff(ArrayList<String> original, ArrayList<String> revised){
           
        ArrayList<Integer> delLineList = new ArrayList<Integer>();
        //First save them in a file //start Index is 0
        Diff diff = new Diff(original.toArray(),revised.toArray());
       
        //diff.no_discards = true;
        Diff.change script = diff.diff_2(false);
        
        while(script!=null){
         System.out.println("Line 0= "+script.line0);
         System.out.println("Line 1= "+script.line1);
         System.out.println("= "+script.deleted);
         System.out.println("= "+script.inserted);
      
           if(script.deleted!=0){
                int startLine = script.line0;
                int length = script.deleted;
                for(int i = startLine; i<=length+startLine-1;i++){
                    delLineList.add(i);
                }
            }
            script = script.link;
        }
        return delLineList;
    }
    
     public static ArrayList<Integer> runDiff(String original[], String revised[]){
           
        ArrayList<Integer> delLineList = new ArrayList<Integer>();
        //First save them in a file //start Index is 0
        Diff diff = new Diff(original,revised);
        Diff.change script = diff.diff_2(true);
        
        while(script!=null){
        
            if(script.deleted!=0){
                int startLine = script.line0;
                int length = script.deleted;
                for(int i = startLine; i<=length+startLine-1;i++){
                    delLineList.add(i);
                }
            }
             script = script.link;
        }
        return delLineList;
    }
    public static void main(String args[]){
            String test1[] = {
    "aaa","bbb","ccc","ddd","eee","fff","ggg","hhh","iii"
  };
      String test2[] = {
     "aaa","jjj","kkk","lll","bbb","ccc","hhh","iii","mmm","nnn","ppp"
  };  
      	ArrayList<String> original = new ArrayList();
		original.add("int bar");
		original.add("(char c) {");
                original.add("int b[];");
		original.add("foo(c,b);");
		original.add("if (size(b)>0 printf(\"D\");");
		original.add(" ");
		original.add("if (!b) {");
		original.add("printf(\"A\");");
		original.add("} else {");
		original.add("printf(\"C\");");
		original.add("printf(\"B\");");
		original.add("}");
		original.add("return 1");
		original.add("}");

		ArrayList<String> revised = new ArrayList();
		revised.add("if(x = 1){ x++}");
		revised.add("int bar(char c) {");
		revised.add("int b[]={1,2};");
		revised.add("b=foo(c,b);");
		revised.add(" ");
		revised.add("if (!b) {");
		revised.add("printf(\"B\");");
		revised.add("} else {");
		revised.add("printf(\"C\");");
		revised.add("printf(\"A\");");
		revised.add("}");
		revised.add("return 1");
		revised.add("}");
		
        ArrayList list = GnuDiff.runDiff(FileToLines.convert("/Users/parvez/Documents/powerOld.txt")
		, FileToLines.convert("/Users/parvez/Documents/powerNew.txt"));
        System.out.println(list);

    }
    
}
