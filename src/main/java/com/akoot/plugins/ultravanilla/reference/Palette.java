package com.akoot.plugins.ultravanilla.reference;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Palette {

    private static Random random = new Random();

    public static final char[] rainbowseq = {'a', '3', '9', '5', 'd', 'c', '6', 'e'};
    public static String colorMatch = "(" + String.join("|", LegacyColors.listNames()) + "|#[0-9a-fA-F]{6}|[a-f0-9])";
    public static String MIX_SYMBOL = "+";

    public static final ChatColor NOUN = ChatColor.LIGHT_PURPLE;
    public static final ChatColor VERB = ChatColor.ITALIC;
    public static final ChatColor NUMBER = ChatColor.GOLD;
    public static final ChatColor OBJECT = ChatColor.AQUA;
    public static final ChatColor WRONG = ChatColor.RED;
    public static final ChatColor RIGHT = ChatColor.GREEN;
    public static final ChatColor TRUE = ChatColor.DARK_AQUA;
    public static final ChatColor FALSE = ChatColor.DARK_RED;

    public static String BOOLEAN(boolean bool) {
        return (bool ? Palette.TRUE : Palette.FALSE) + "" + bool;
    }

    public static String translate(String text) {

        // Rainbow text
        if (text.contains("&x")) {
            String toColor = getRegex("&x[^&]*", text);
            text = text.replace(toColor, rainbow(toColor.substring(2)));
        }

        // Random color text
        if (text.contains("&h")) {
            text = text.replace("&h", "" + ChatColor.values()[random.nextInt(ChatColor.values().length)]);
        }

        if (text.contains("&#")) {
            Pattern p = Pattern.compile("&(#[0-9a-fA-F]{6})");
            Matcher m = p.matcher(text);
            while (m.find()) {
                text = text.replace(m.group(), ChatColor.of(m.group(1)) + "");
            }
        }

        if (text.contains("&>")) {
            Pattern p = Pattern.compile("&>" + colorMatch + "\\" + MIX_SYMBOL + colorMatch + "([^&$]+)");
            Matcher m = p.matcher(text);
            while (m.find()) {
                text = text.replace(m.group(), gradient(m.group(3), m.group(1), m.group(2)));
            }
        }

        //TODO: read from config
        text = text
                .replace("$noun", NOUN + "")
                .replace("$verb", VERB + "")
                .replace("$number", NUMBER + "")
                .replace("$object", OBJECT + "")
                .replace("$wrong", WRONG + "")
                .replace("$right", RIGHT + "")
                .replace("$true", TRUE + "")
                .replace("$false", FALSE + "")
        ;

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static ChatColor getRandomColor() {
        return ChatColor.getByChar(rainbowseq[(int) (Math.random() * rainbowseq.length)]);
    }

    public static String gradient(String str, ChatColor color1, ChatColor color2) {
        return gradient(str, LegacyColors.getColor(color1), LegacyColors.getColor(color2));
    }

    public static String gradient(String str, String color1, String color2) {
        return gradient(str, LegacyColors.getColor(color1), LegacyColors.getColor(color2));
    }

    // implementation by lordpipe
    public static String gradient(String str, Color from, Color to) {
        StringBuilder sb = new StringBuilder();

        ColorSpace cie = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
        ColorSpace srgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        float[] cieFrom = cie.fromRGB(from.getRGBColorComponents(null));
        float[] cieTo = cie.fromRGB(to.getRGBColorComponents(null));

        for (int i = 0, l = str.length(); i < l; i++) {
            // do interpolation in CIE space
            float[] interpolatedCie = new float[] {
                    cieFrom[0] + (i * (1.0F / l)) * (cieTo[0] - cieFrom[0]),
                    cieFrom[1] + (i * (1.0F / l)) * (cieTo[1] - cieFrom[1]),
                    cieFrom[2] + (i * (1.0F / l)) * (cieTo[2] - cieFrom[2])
            };

            // we could just pass the CIE value directly into `new Color`, but it seems the ChatColor API expects the
            // conversion to sRGB to be pre-computed, so it fails
            float[] interpolatedSrgb = srgb.fromCIEXYZ(interpolatedCie);
            sb.append(ChatColor.of(new Color(interpolatedSrgb[0], interpolatedSrgb[1], interpolatedSrgb[2])));
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }

    public static String rainbow(String msg) {
        String rainbow = "";
        int i = random.nextInt(rainbowseq.length);
        for (char c : msg.toCharArray()) {
            if (i >= rainbowseq.length) {
                i = 0;
            }

            String ch = String.valueOf(c);
            if (c != ' ') {
                ch = "&" + rainbowseq[i] + ch;
                i++;
            }
            rainbow += ch;
        }
        return rainbow;
    }

    public static String getRegex(String regex, String data) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) data = matcher.group(0);
        return data;
    }
}
