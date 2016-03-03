package com.androiddev.andrewbrown.munchkintracker;

/**
 * TODO: javadoc
 *
 * @author andrew.brown@laterooms.com
 */
public class Player {

    public final String name;
    public int level = 1;

    public Player (String name) {
        this.name = name;
    }

    public String getLevel() {
        return Integer.toString(level);
    }

    public void addLevel() {
        level++;
    }

    public void removeLevel() {
        if (level>1) {
            level--;
        }
    }

    public void reset() {
        level = 1;
    }
}
