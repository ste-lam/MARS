   package mars.mips.instructions;
   import mars.mips.instructions.syscalls.*;
   import mars.*;
	import mars.util.*;
   import java.util.*;

/*
Copyright (c) 2003-2006,  Pete Sanderson and Kenneth Vollmar

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

   	
    /****************************************************************************/
    /* This class provides functionality to bring external Syscall definitions
     * into MARS.  This permits anyone with knowledge of the Mars public interfaces, 
     * in particular of the Memory and Register classes, to write custom MIPS syscall
     * functions. This is adapted from the ToolLoader class, which is in turn adapted
     * from Bret Barker's GameServer class from the book "Developing Games In Java".
     */

    public class SyscallLoader {
      
      private static final String CLASS_PREFIX = "mars.mips.instructions.syscalls.";
      private static final String SYSCALLS_DIRECTORY_PATH = "mars/mips/instructions/syscalls";
      private static final String CLASS_EXTENSION = "class";
   	
   /*
      *  Dynamically loads Syscalls into an ArrayList.  This method is adapted from
      *  the loadGameControllers() method in Bret Barker's GameServer class.
      *  Barker (bret@hypefiend.com) is co-author of the book "Developing Games
      *  in Java".  Also see the "loadMarsTools()" method from ToolLoader class.
      */
   static public ArrayList<Syscall> loadSyscalls() {
         ArrayList<Syscall> syscallList = new ArrayList<>();
         // grab all class files in the same directory as Syscall
         ArrayList candidates = FilenameFinder.getFilenameList(SyscallLoader.class.getClassLoader(),
                                              SYSCALLS_DIRECTORY_PATH, CLASS_EXTENSION);
		   Set<String> syscalls = new HashSet<>(candidates);
         for (String file : syscalls) {
               try {
                  // grab the class, make sure it implements Syscall, instantiate, add to list
                  String syscallClassName = CLASS_PREFIX+file.substring(0, file.indexOf(CLASS_EXTENSION)-1);
                  Class<?> clas = Class.forName(syscallClassName);
                  if (!Syscall.class.isAssignableFrom(clas) || Syscall.class.equals(clas) || AbstractSyscall.class.equals(clas)) {
                     continue;
                  }
                  Syscall syscall = (Syscall) clas.newInstance();
                  syscallList.add(syscall);
               } 
                   catch (Exception e) {
                     System.err.println("Error instantiating Syscall from file " + file + ": "+e);
                     System.exit(0);
                  }
         }
         return syscallList;
      }
   }
