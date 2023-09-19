package dev.gether.getclans.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorFixer {


    static Pattern pattern = Pattern.compile("\\{#[a-fA-F0-9]{6}\\}");

    public static java.util.List<String> addColors(List<String> input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        for (int i = 0; i < input.size(); i++) {
            input.set(i, addColors(input.get(i)));
        }
        return input;
    }
    public static String addColors(String text)
    {
        Matcher matcher = pattern.matcher(text);

        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        int lastStart = 0;
        Color lastColor = null;
        while (matcher.find()) {
            result.append(text, lastEnd, matcher.start());
            lastEnd = matcher.end();

            String colorString = matcher.group().substring(1, matcher.group().length() - 1);
            Color color = Color.decode(colorString);

            if (matcher.group().charAt(1) == '/') {
                result.append(hsvGradient(text.substring(lastStart, matcher.start()), lastColor, color));
            } else {
                lastStart = matcher.end();
                lastColor = color;
            }
        }
        result.append(text, lastEnd, text.length());
        String x = org.bukkit.ChatColor.translateAlternateColorCodes('&', result.toString());

        return x;
    }
    public static String hsvGradient(String str, Color from, Color to) {
        final float[] hsvFrom = Color.RGBtoHSB(from.getRed(), from.getGreen(), from.getBlue(), null);
        final float[] hsvTo = Color.RGBtoHSB(to.getRed(), to.getGreen(), to.getBlue(), null);

        final double[] h = linear(hsvFrom[0], hsvTo[0], str.length());
        final double[] s = linear(hsvFrom[1], hsvTo[1], str.length());
        final double[] v = linear(hsvFrom[2], hsvTo[2], str.length());

        final StringBuilder builder = new StringBuilder();

        for (int i = 0 ; i < str.length(); i++) {
            builder.append(ChatColor.of(Color.getHSBColor((float) h[i], (float) s[i], (float) v[i]))).append(str.charAt(i));
        }
        return builder.toString();
    }
    private static double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }

}
