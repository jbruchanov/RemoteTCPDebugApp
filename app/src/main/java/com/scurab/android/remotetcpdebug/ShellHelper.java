package com.scurab.android.remotetcpdebug;
import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JBruchanov on 25/10/2016.
 */

public class ShellHelper {

    public static String test() {
        return executeSafe("su", "mkdir /sdcard/testdir");
    }

    public static String executeSafe(String cmd, String... cmds) {
        String result = null;
        try {
            result = execute(cmd, cmds);
        } catch (Throwable e) {
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            result = e.getMessage() + "\n" + stack.toString();
        }

        return result;
    }

    public static String execute(String cmd, String... cmds) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(cmd);
        DataOutputStream dos = new DataOutputStream(p.getOutputStream());
        for (String c : cmds) {
            dos.writeBytes(c + "\n");
        }
        dos.flush();
        dos.close();
        p.waitFor();
        InputStream inputStream = p.getInputStream();
        String result = IOUtils.toString(inputStream);
        if (result == null || result.isEmpty()) {
            result = "No output detected.";
        }
        return result;
    }

    public static List<String> getIpAddresses() {
        String consoleOutput = executeSafe("ifconfig");
        List<String> result = new ArrayList<>();
        if (consoleOutput != null && consoleOutput.length() > 0) {
            String[] lines = consoleOutput.split("\\n");
            Pattern compile = Pattern.compile("inet addr:(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
            for (String line : lines) {
                Matcher matcher = compile.matcher(line);
                if (matcher.find()) {
                    result.add(matcher.group(1));
                }
            }
        }

        if (result.size() == 0) {
            consoleOutput = executeSafe("netcfg");
            if (consoleOutput != null && consoleOutput.length() > 0) {
                String[] lines = consoleOutput.split("\\n");
                Pattern compile = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
                Set<String> found = new HashSet<>();
                for (String line : lines) {
                    Matcher matcher = compile.matcher(line);
                    if (matcher.find()) {
                        found.add(matcher.group(1));
                    }
                }
                result.addAll(found);
            }
        }
        result.remove("127.0.0.1");
        result.remove("0.0.0.0");
        return result;
    }
}
