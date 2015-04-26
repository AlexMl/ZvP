package org.util.File.InsertComment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class CommentUtil {
    
    /**
     * @param file
     *        The file to write in
     * @param option
     *        The place where the comment will be inserted
     * @param comment
     *        The comment
     * @return boolean - Success
     */
    public static boolean insertComment(File file, String option, String comment) {
	comment = comment.replace("#", "\n# ");
	comment = "\n# " + comment;
	
	try {
	    ArrayList<String> fileContent = new ArrayList<String>();
	    Scanner scan = new Scanner(file);
	    
	    while (scan.hasNextLine()) {
		String line = scan.nextLine();
		
		if (line.trim().split(":")[0].equalsIgnoreCase(option)) {
		    fileContent.add(comment);
		}
		fileContent.add(line);
	    }
	    
	    PrintWriter writer = new PrintWriter(file);
	    for (String string : fileContent) {
		writer.write(string + "\n");
	    }
	    writer.flush();
	    writer.close();
	    return true;
	    
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	return false;
    }
    
}
