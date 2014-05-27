package com.marge.split;

import java.util.Comparator;

import javaxt.io.*;

public class FileComparartor implements Comparator<javaxt.io.File> {

		public int compare (javaxt.io.File file1, javaxt.io.File file2){
			return (file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase()));
		}
}
