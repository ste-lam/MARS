package mars.assembler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
Copyright (c) 2003-2012,  Pete Sanderson and Kenneth Vollmar

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
 * Class representing MIPS assembler directives.  If Java had enumerated types, these
 * would probably be implemented that way.  Each directive is represented by a unique object.
 * The directive name is indicative of the directive it represents.  For example, DATA
 * represents the MIPS .data directive.
 *
 * @author Pete Sanderson
 * @version August 2003
 **/
public enum Directives {
    DATA(".data", "Subsequent items stored in Data segment at next available address"),
    TEXT(".text", "Subsequent items (instructions) stored in Text segment at next available address"),
    WORD(".word", "Store the listed value(s) as 32 bit words on word boundary"),
    ASCII(".ascii", "Store the string in the Data segment but do not add null terminator"),
    ASCIIZ(".asciiz", "Store the string in the Data segment and add null terminator"),
    BYTE(".byte", "Store the listed value(s) as 8 bit bytes"),
    ALIGN(".align", "Align next data item on specified byte boundary (0=byte, 1=half, 2=word, 3=double)"),
    HALF(".half", "Store the listed value(s) as 16 bit halfwords on halfword boundary"),
    SPACE(".space", "Reserve the next specified number of bytes in Data segment"),
    DOUBLE(".double", "Store the listed value(s) as double precision floating point"),
    FLOAT(".float", "Store the listed value(s) as single precision floating point"),
    EXTERN(".extern", "Declare the listed label and byte length to be a global data field"),
    KDATA(".kdata", "Subsequent items stored in Kernel Data segment at next available address"),
    KTEXT(".ktext", "Subsequent items (instructions) stored in Kernel Text segment at next available address"),
    GLOBL(".globl", "Declare the listed label(s) as global to enable referencing from other files"),
    SET(".set", "Set assembler variables.  Currently ignored but included for SPIM compatability"),
    /*  EQV added by DPS 11 July 2012 */
    EQV(".eqv", "Substitute second operand for first. First operand is symbol, second operand is expression (like #define)"),
    /* MACRO and END_MACRO added by Mohammad Sekhavat Oct 2012 */
    MACRO(".macro", "Begin macro definition.  See .end_macro"),
    END_MACRO(".end_macro", "End macro definition.  See .macro"),
    /*  INCLUDE added by DPS 11 Jan 2013 */
    INCLUDE(".include", "Insert the contents of the specified file.  Put filename in quotes.");


    private final String descriptor;
    private final String description; // help text

    Directives(String name, String description) {
        this.descriptor = name;
        this.description = description;
    }

    public boolean matches(String str) {
        return descriptor.equalsIgnoreCase(str);
    }

    public boolean matchesPrefix(String str) {
        return str != null 
            && str.length() <= descriptor.length()
            && descriptor.regionMatches(true, 0, str, 0, str.length());
    }

    /**
     * Find Directive object, if any, which matches the given String.
     *
     * @param str A String containing candidate directive name (e.g. ".ascii")
     * @return If match is found, returns matching Directives object, else returns <code>null</code>.
     **/
    public static Directives matchDirective(String str) {
        for (Directives match: values()) {
            if (match.matches(str)) {
                return match;
            }
        }
        return null;
    }

    /**
     * Find Directive object, if any, which contains the given string as a prefix. For example,
     * ".a" will match ".ascii", ".asciiz" and ".align"
     *
     * @param str A String
     * @return If match is found, returns ArrayList of matching Directives objects, else returns <code>null</code>.
     **/
    public static List<Directives> prefixMatchDirectives(String str) {
        List<Directives> matches = Collections.emptyList();
        for (Directives match: values()) {
            if (match.matchesPrefix(str)) {
                if (matches.isEmpty()) {
                    matches = new ArrayList<>();
                }
                matches.add(match);
            }
        }
        return matches;
    }

    /**
     * Produces String-ified version of Directive object
     *
     * @return String representing Directive: its MIPS name
     **/
    public String toString() {
        return this.descriptor;
    }

    /**
     * Get name of this Directives object
     *
     * @return name of this MIPS directive as a String
     **/
    public String getName() {
        return this.descriptor;
    }

    /**
     * Get description of this Directives object
     *
     * @return description of this MIPS directive (for help purposes)
     **/
    public String getDescription() {
        return this.description;
    }

    /**
     * Lets you know whether given directive is for integer (WORD,HALF,BYTE).
     *
     * @param direct a MIPS directive
     * @return true if given directive is FLOAT or DOUBLE, false otherwise
     **/
    public static boolean isIntegerDirective(Directives direct) {
        return direct == Directives.WORD || direct == Directives.HALF || direct == Directives.BYTE;
    }

    /**
     * Lets you know whether given directive is for floating number (FLOAT,DOUBLE).
     *
     * @param direct a MIPS directive
     * @return true if given directive is FLOAT or DOUBLE, false otherwise.
     **/
    public static boolean isFloatingDirective(Directives direct) {
        return direct == Directives.FLOAT || direct == Directives.DOUBLE;
    }
}
