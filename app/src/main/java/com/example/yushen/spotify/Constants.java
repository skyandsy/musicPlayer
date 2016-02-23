package com.example.yushen.spotify;

/**
 * Created by yushen on 2/17/16.
 * hhold interfaces
 */
public class Constants {
    public interface ACTION{
        public static String MAIN_ACTION = "com.example.yushen.spotify.action_main";
        public static String PREV_ACTION = "com.example.yushen.spotify.action_prev";
        public static String PLAY_ACTION = "com.example.yushen.spotify.action_play";
        public static String NEXT_ACTION = "com.example.yushen.spotify.action_next";
        public static String STARTFOREGROUND_ACTION = "com.example.yushen.spotify.action_startforeground";
        public static String STOPFOREGROUND_ACTION = "com.example.yushen.spotify.action_stopforeground";

    }

    public interface NOTIFICATION_ID{
        public static int FOREGROUND_SERVICE = 101;
    }

}
