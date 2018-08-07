package com.coresmash;

public final class WorldSettings {
    private static int s_tileSize;

    private WorldSettings() {}

    public static void init() {
        s_tileSize = 70;
    }

    public static int getTileSize() {return s_tileSize;}

    public static int getWorldWidth() {
        return 1080;
    }

    public static int getWorldHeight() {
        return 1920;
    }

}