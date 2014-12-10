package org.mihigh.cycling.app;

public class Utils {

  public static final String SESSION_ID = "JSESSIONID";


  public static int getSizeFromDP(int dpSize, float scale) {
        return (int) (dpSize * scale + 0.5f);
    }



}
