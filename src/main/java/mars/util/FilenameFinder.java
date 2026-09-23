   package mars.util;
   import java.io.File;
   import java.util.ArrayList;
   import java.util.StringTokenizer;

   import javax.swing.filechooser.FileFilter;
	
	/*
Copyright (c) 2003-2008,  Pete Sanderson and Kenneth Vollmar

Developed by Pete Sanderson (psanderson@otterbein.edu)
and Kenneth Vollmar (kenvollmar@missouristate.edu)

Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the 
"Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, 
distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be 
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
 */
	
/**
 * Utility class to perform necessary file-related search
 * operations.  One is to find file names in JAR file,
 * another is to find names of files in given directory
 * of normal file system.
 *
 * @author Pete Sanderson
 * @version October 2006
  */
    public class FilenameFinder
   {
      private static final boolean NO_DIRECTORIES = false;
      public static String MATCH_ALL_EXTENSIONS = "*"; 
   
   
   /**
    * Locate files and return list of file names.  Given a known directory path,
    * it will locate it and build list of all names of files in that directory 
    * having the given file extension.  If file extenion is null or empty, all
    * filenames are returned. Returned list contains absolute filename paths.
    * @param directoryPath Search will be confined to this directory.  
    * @param fileExtension Only files with this extension will be added to the list.
    * Do NOT include "." in extension.
    * If null or empty string, all files are added.
    * @return array list of matching file names (absolute path).  If none, list is empty. 
    */
       public static ArrayList getFilenameList(String directoryPath, String fileExtension) {
         fileExtension = checkFileExtension(fileExtension);
         ArrayList filenameList = new ArrayList();
         File directory = new File(directoryPath);
         if (directory.isDirectory()) {
            File[] allFiles = directory.listFiles();
            FileFilter filter = getFileFilter(fileExtension, "", NO_DIRECTORIES);
            for (int i=0; i<allFiles.length; i++) {
               if (filter.accept(allFiles[i])) {
                  filenameList.add(allFiles[i].getAbsolutePath());
               }
            }
         }
         return filenameList;
      }
   
   
   /**
    * Locate files and return list of file names.  Given a known directory path,
    * it will locate it and build list of all names of files in that directory 
    * having the given file extension.  If file extenion is null or empty, all
    * filenames are returned. Returned list contains absolute filename paths.
    * @param directoryPath Search will be confined to this directory.  
    * @param fileExtensions ArrayList of Strings containing file extensions.
    * Only files with an extension in this list will be added 
    * to the list.  Do NOT include the "." in extensions.  If Arraylist or 
    * extension null or empty, all files are added.
    * @return array list of matching file names (absolute path).  If none, list is empty. 
    */
       public static ArrayList getFilenameList(String directoryPath, ArrayList fileExtensions) {
         ArrayList filenameList = new ArrayList();
         String fileExtension;
         if (fileExtensions==null || fileExtensions.size()==0) {
            filenameList = getFilenameList(directoryPath,"");
         } 
         else {
            for (int i=0; i<fileExtensions.size(); i++) {
               fileExtension = checkFileExtension((String)fileExtensions.get(i));
               filenameList.addAll(getFilenameList(directoryPath, fileExtension));
            }
         }
         return filenameList;
      }
   
   /**
    * Return list of file names.  Given a list of file names, it will return the list 
    * of all having the given file extension.  If file extenion is null or empty, all
    * filenames are returned.  Returned list contains absolute filename paths.
    * @param nameList ArrayList of String containing file names.  
    * @param fileExtension Only files with this extension will be added to the list.
    * If null or empty string, all files are added.  Do NOT include "." in extension.
    * @return array list of matching file names (absolute path).  If none, list is empty. 
    */
       public static ArrayList getFilenameList(ArrayList nameList, String fileExtension) {
         fileExtension = checkFileExtension(fileExtension);
         ArrayList filenameList = new ArrayList();
         FileFilter filter = getFileFilter(fileExtension, "", NO_DIRECTORIES);
         for (int i=0; i<nameList.size(); i++) {
            File file = new File((String)nameList.get(i));
            if (filter.accept(file)) {
               filenameList.add(file.getAbsolutePath());
            }				
         }
         return filenameList;
      }
   
   	/**
   	 *  Get the filename extension of the specified File.
   	 *  @param file the File object representing the file of interest
   	 *  @return The filename extension (everything that follows 
   	 *  last '.' in filename) or null if none.
   	 */
   	 // Source code from Sun Microsystems "The Java Tutorials : How To Use File Choosers"
       private static String getExtension(File file) {
         String ext = null; 
         String s = file.getName(); 
         int i = s.lastIndexOf('.'); 
         if (i > 0 && i < s.length() - 1) { 
            ext = s.substring(i+1).toLowerCase(); 
         } 
         return ext; 
      }
   	
   	/**
   	 *  Get a FileFilter that will filter files based on the given list of filename extensions.
   	 *  @param extensions ArrayList of Strings, each string is acceptable filename extension.
   	 *  @param description String containing description to be added in parentheses after list of extensions.
   	 *  @param acceptDirectories boolean value true if directories are accepted by the filter, false otherwise.
   	 *  @return a FileFilter object that accepts files with given extensions, and directories if so indicated.
   	 */
   	 
       public static FileFilter getFileFilter(ArrayList extensions, String description, boolean acceptDirectories) {
         return new MarsFileFilter(extensions, description, acceptDirectories);
      }
   	 
   	/**
   	 *  Get a FileFilter that will filter files based on the given filename extension.
   	 *  @param extension String containing acceptable filename extension.
   	 *  @param description String containing description to be added in parentheses after list of extensions.
   	 *  @param acceptDirectories boolean value true if directories are accepted by the filter, false otherwise.
   	 *  @return a FileFilter object that accepts files with given extensions, and directories if so indicated.
   	 */
   	 
       private static FileFilter getFileFilter(String extension, String description, boolean acceptDirectories) {
         ArrayList extensions = new ArrayList();
         extensions.add(extension);
         return new MarsFileFilter(extensions, description, acceptDirectories);
      }
   	
   	// make sure file extension, if it is real, does not start with '.' -- remove it.
       private static String checkFileExtension(String fileExtension) {
         return (fileExtension==null || fileExtension.length()==0 || !fileExtension.startsWith("."))
                ? fileExtension
            	 : fileExtension.substring(1);
      }
   	
   	
   	///////////////////////////////////////////////////////////////////////////
   	//  FileFilter subclass to be instantiated by the getFileFilter method above.
   	//  This extends javax.swing.filechooser.FileFilter
   	
       private static class MarsFileFilter extends FileFilter {
      
         private ArrayList extensions;
         private String fullDescription;
         private boolean acceptDirectories;
        
          private MarsFileFilter(ArrayList extensions, String description, boolean acceptDirectories) {
            this.extensions = extensions;
            this.fullDescription = buildFullDescription(description, extensions);
            this.acceptDirectories = acceptDirectories;
         }
      	
      	// User provides descriptive phrase to be parenthesized.
      	// We will attach it to description of the extensions.  For example, if the extensions
      	// given are s and asm and the description is "Assembler Programs" the full description
      	// generated here will be "Assembler Programs (*.s; *.asm)"
          private String buildFullDescription(String description, ArrayList extensions) {
            String result = (description == null) ? "" : description;
            if (extensions.size() > 0) {
               result += "  (";
            }
            for (int i=0; i<extensions.size(); i++) {
               String extension = (String) extensions.get(i);
               if (extension != null && extension.length() > 0) {
                  result += ((i==0)?"":"; ")+"*"+((extension.charAt(0)=='.')? "" : ".")+extension;
               }
            }
            if (extensions.size() > 0) {
               result += ")";
            }
            return result;
         }
      	
      	// required by the abstract superclass
          public String getDescription() {
            return this.fullDescription;
         }
      	
      	// required by the abstract superclass.
          public boolean accept(File file) {
            if (file.isDirectory()) { 
               return acceptDirectories; 
            } 
            String fileExtension = getExtension(file); 
            if (fileExtension != null) { 
               for (int i=0; i<extensions.size(); i++) {
                  String extension = checkFileExtension((String)extensions.get(i));
                  if (extension.equals(MATCH_ALL_EXTENSIONS) || 
                      fileExtension.equals(extension)) {
                     return true;
                  }	
               }
            }
            return false;
         }
      	
      } // MarsFileFilter class
   		
   } // FilenameFinder class

