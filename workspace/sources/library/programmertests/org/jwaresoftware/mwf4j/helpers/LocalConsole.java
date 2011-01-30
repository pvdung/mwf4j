/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.io.BufferedReader;
import  java.io.IOException;
import  java.io.InputStreamReader;

/**
 * This class is needed b/c the "standard" Console implementation sucks
 * for IDE-based running. !@#$%@#!
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

public final class LocalConsole
{
    public static String readLine(String prompt)
    {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(prompt);
        System.out.flush();
        String input = null;
        try {
            input = stdin.readLine();
        } catch(IOException ioExc) {
            System.out.println(ioExc.getLocalizedMessage());
        }
        if (input == null) {
            return "";
        }
        return input;
    }
    
    private LocalConsole() { }
}


/* end-of-LocalConsole.java */
