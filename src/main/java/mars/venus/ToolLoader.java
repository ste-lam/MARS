package mars.venus;

import mars.tools.MarsTool;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

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
 * This class provides functionality to bring external Mars tools into the Mars
 * system by adding them to its Tools menu.  This permits anyone with knowledge
 * of the Mars public interfaces, in particular of the Memory and Register
 * classes, to write applications which can interact with a MIPS program
 * executing under Mars.  The execution is of course simulated.  The
 * private method for loading tool classes is adapted from Bret Barker's
 * GameServer class from the book "Developing Games In Java".
 *
 * @author Pete Sanderson with help from Bret Barker
 * @version August 2005
 */

public class ToolLoader {
    private static final String TOOLS_MENU_NAME = "Tools";
    
    private static final List<MarsTool> list = new ArrayList<>();
    static {
        ServiceLoader.load(MarsTool.class).forEach(list::add);
    }

    private ToolLoader() {
    }

    /**
     * Called in VenusUI to build its Tools menu.  A qualifying tool
     * must be a class in the Tools package that implements MarsTool, must be compiled
     * into a .class file, and its .class file must be added to SPI mars.tools.MarsTool
     *
     * @return a Tools JMenu
     */
    static public JMenu buildToolsMenu() {
        JMenu menu = new JMenu(TOOLS_MENU_NAME);
        menu.setMnemonic(KeyEvent.VK_T);
        list.forEach(tool -> {
            menu.add(tool.getName()).addActionListener(e -> tool.action());
        });
        menu.setVisible(menu.getItemCount() >= 1);

        return menu;
    }
}