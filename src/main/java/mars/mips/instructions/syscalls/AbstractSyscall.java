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
 * Abstract class that a MIPS syscall system service may extend.  It provides
 * the service name; subclasses implement simulate().  Like any service, a
 * subclass must be a public class with a public no-argument constructor and must be
 * registered in <code>META-INF/services/mars.mips.instructions.syscalls.Syscall</code>
 * (see {@link Syscall}).  Its service number is assigned in
 * <code>kernel_syscalls.s</code>, not in the class.
 */
public abstract class AbstractSyscall implements Syscall {
    private String serviceName;

    /**
     * Constructor is provided so subclass may initialize instance variables.
     *
     * @param name service name which may be used for reference independent of number
     */
    public AbstractSyscall(String name) {
        serviceName = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return serviceName;
    }

    /**
     * {@inheritDoc}
     */
    public abstract void simulate(ProgramStatement statement) throws ProcessingException;
}