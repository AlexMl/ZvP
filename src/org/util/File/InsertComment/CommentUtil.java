package org.util.File.InsertComment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CommentUtil {
    
    /**
     * Use this to add <u>one</u> Comment into the file. For more Comments use {@link #addComment(option, comment)}!
     * 
     * @param file
     *        The file to write in
     * @param option
     *        The place where the comment will be inserted
     * @param comment
     *        The comment
     * @return {@link Boolean} - Success
     */
    @Deprecated
    public static boolean insertComment(File file, String option, String comment) {
	String insertion = comment.replace("#", "\n# ");
	insertion = "\n# " + insertion;
	
	try {
	    ArrayList<String> fileContent = new ArrayList<String>();
	    Scanner scan = new Scanner(file);
	    
	    while (scan.hasNextLine()) {
		String line = scan.nextLine();
		
		if (line.trim().split(":")[0].equalsIgnoreCase(option)) {
		    fileContent.add(insertion);
		}
		fileContent.add(line);
	    }
	    
	    PrintWriter writer = new PrintWriter(file);
	    for (String string : fileContent) {
		writer.write(string + "\n");
	    }
	    writer.flush();
	    writer.close();
	    scan.close();
	    return true;
	    
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	return false;
    }
    
    private List<CommentUtil.Comment> comments;
    private File file;
    
    public CommentUtil(File file) {
	this.file = file;
	this.comments = new ArrayList<CommentUtil.Comment>();
    }
    
    /**
     * Add a new Comment
     * 
     * @param option
     *        The place where the comment will be inserted
     * @param comment
     *        The comment
     */
    public void addComment(String option, String comment) {
	this.comments.add(new Comment(option, comment));
    }
    
    /**
     * Write all Comments at once into the file
     * 
     * @return {@link Boolean} Success
     */
    public boolean writeComments() {
	
	try {
	    ArrayList<String> fileContent = new ArrayList<String>();
	    Scanner scan = new Scanner(getFile());
	    
	    while (scan.hasNextLine()) {
		fileContent.add(scan.nextLine());
	    }
	    scan.close();
	    
	    for (int i = 0; i < fileContent.size(); i++) {
		String fileOption = fileContent.get(i).trim().split(":")[0];
		
		for (Comment comment : this.comments) {
		    String option = comment.getOption();
		    
		    if (fileOption.equalsIgnoreCase(option)) {
			fileContent.add(i, comment.getFormatedComment());
			i++;
		    }
		}
	    }
	    
	    PrintWriter writer = new PrintWriter(getFile());
	    for (String string : fileContent) {
		writer.write(string + "\n");
	    }
	    writer.flush();
	    writer.close();
	    
	    return true;
	    
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return false;
	
    }
    
    /**
     * @return the file used by {@link CommentUtil}
     */
    public File getFile() {
	return this.file;
    }
    
    private class Comment {
	
	private String option;
	private String comment;
	
	public Comment(String option, String comment) {
	    this.option = option;
	    this.comment = comment;
	}
	
	public String getOption() {
	    return this.option;
	}
	
	public String getComment() {
	    return this.comment;
	}
	
	public String getFormatedComment() {
	    return "\n# " + this.comment.replace("#", "\n# ");
	}
	
	@Override
	public String toString() {
	    return getClass().getSimpleName() + "[" + getOption() + ":" + getComment() + "]";
	}
    }
}
