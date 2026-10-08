package mars.mips.instructions.syscalls;

import mars.*;

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


/**
 * Interface for any MIPS syscall system service.
 * <p>
 * A service is discovered through the Java Service Provider Interface (SPI):
 * it must be a public class with a public no-argument constructor that implements
 * this interface, and it must be listed in the provider-configuration file
 * <code>META-INF/services/mars.mips.instructions.syscalls.Syscall</code>.
 * MARS loads all listed services upon startup and keeps them in its syscall list.
 * <p>
 * A service has no service number of its own. The number is assigned in the
 * kernel's syscall table (<code>kernel_syscalls.s</code>), which refers to the service
 * by its fully qualified class name. When the "syscall" instruction is executed
 * with the service number in register $v0, the kernel exception handler looks up the
 * service in that table and its simulate() method is invoked.
 * <p>
 * <b>Incompatible change:</b> earlier releases declared <code>getNumber()</code> and
 * <code>setNumber()</code> and detected services by scanning the syscalls directory.
 * Existing implementations must drop these methods, be registered as described above,
 * and be given a number in <code>kernel_syscalls.s</code>.
 */
public interface Syscall {
    /**
     * Return a name you have chosen for this syscall. The name is for reference
     * and display only and is independent of the service number.
     *
     * @return service name as a string
     */
    String getName();

    /**
     * Performs syscall function.  It will be invoked when the service is invoked
     * at simulation time.  Service is identified by value stored in $v0.
     *
     * @param statement ProgramStatement for this syscall statement.
     */
    void simulate(ProgramStatement statement)
            throws ProcessingException;
}