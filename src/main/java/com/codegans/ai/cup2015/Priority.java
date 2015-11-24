package com.codegans.ai.cup2015;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 21.11.2015 22:19
 */
public interface Priority {
    int TOP = 1;
    int HIGH = TOP << 6;
    int NORMAL = HIGH << 2;
    int LOW = HIGH << 1;
    int NONE = Integer.MAX_VALUE;
}
