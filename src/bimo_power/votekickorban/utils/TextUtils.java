package bimo_power.votekickorban.utils;

import bimo_power.votekickorban.Main;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

//
//    public static String warning(String msg,boolean showSuffix) {
//        return msg;
//    }
//
//    public static String error(String msg,boolean showSuffix) {
//        return msg;
//    }
//
//    public static String primary(String msg,boolean showSuffix) {
//        return msg;
//    }
//
//    public static String success(String msg,boolean showSuffix) {
//        return msg;
//    }
//
//    public static String message(String msg,boolean showSuffix) {
////        return "§f[§6VoteKickOrBan§f] - §f" + msg;
//        return msg;
//    }
//
//    public static String wait(String msg) {
//        return msg;
//    }

    public static String suffix(String message, Map<String,String> maps){
        message = message.replaceAll("[#]","§");
        message = message.replaceAll("%n","\n");
//        message = message.replaceAll("$","\\$");

        if(maps != null){
            for(Map.Entry<String, String> map : maps.entrySet()){
                String regex = "\\{" + map.getKey() + "\\}";
                Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(message);
                message = matcher.find() ? message.replaceAll(regex,Matcher.quoteReplacement(map.getValue())) : message;
            }
        }
        return message;
    }

}
