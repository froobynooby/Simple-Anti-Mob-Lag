package com.froobworld.saml.config;

import com.froobworld.saml.Saml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigUpdater {
    private static final Pattern SECTION_HEADER_PATTERN = Pattern.compile("\\[[a-z_]+\\]");
    private static final String VERSION_PREFIX = ConfigKeys.VERSION + ": ";
    private static final String SECTION_BEGIN_PREFIX = "# section-begin: ";
    private static final String SECTION_END_PREFIX = "# section-end";

    public static boolean update(File configFile, InputStream patchInputStream, int versionFrom) {
        HashMap<String, List<String>> sectionedNewLines = new HashMap<>();
        List<String> unsectionedNewLines = new ArrayList<>();

        try(BufferedReader patchReader = new BufferedReader(new InputStreamReader(patchInputStream))) {
            String currentSection = null;
            for(String nextLine = patchReader.readLine(); nextLine != null; nextLine = patchReader.readLine()) {
                if(SECTION_HEADER_PATTERN.matcher(nextLine).matches()) {
                    currentSection = nextLine.replace("[", "").replace("]", "");
                    continue;
                }
                if(currentSection != null) {
                    sectionedNewLines.putIfAbsent(currentSection, new ArrayList<>());
                    sectionedNewLines.get(currentSection).add(nextLine);
                } else {
                    unsectionedNewLines.add(nextLine);
                }
            }
        } catch (IOException e) {
            Saml.logger().severe("Failed to read config patch.");
            e.printStackTrace();
            return false;
        }

        List<String> combinedLines = new ArrayList<>();

        try(BufferedReader configReader = new BufferedReader(new FileReader(configFile))) {
            String currentSection = null;
            for(String nextLine = configReader.readLine(); nextLine != null; nextLine = configReader.readLine()) {
                if(nextLine.startsWith(VERSION_PREFIX)) {
                    combinedLines.add(VERSION_PREFIX + (versionFrom + 1));
                    continue;
                }
                if(nextLine.startsWith(SECTION_BEGIN_PREFIX)) {
                    combinedLines.add(nextLine);
                    currentSection = nextLine.replace(SECTION_BEGIN_PREFIX, "");
                    continue;
                }
                if(nextLine.startsWith(SECTION_END_PREFIX)) {
                    if(currentSection != null) {
                        if (sectionedNewLines.containsKey(currentSection)) {
                            combinedLines.add("# begin-config-update: " + versionFrom + " -> " + (versionFrom + 1));
                            combinedLines.add("");

                            combinedLines.addAll(sectionedNewLines.get(currentSection));

                            combinedLines.add("# end-config-update");
                            combinedLines.add("");

                            sectionedNewLines.remove(currentSection);
                        }
                    } else {
                        Saml.logger().warning("Reached section end marker without a current section.");
                    }
                    combinedLines.add(SECTION_END_PREFIX);
                    continue;
                }
                combinedLines.add(nextLine);
            }
            if(!sectionedNewLines.isEmpty() || !unsectionedNewLines.isEmpty()) {
                combinedLines.add("");
                combinedLines.add("# begin-config-update: " + versionFrom + " -> " + (versionFrom + 1));
                combinedLines.add("");

                for(List<String> newLines : sectionedNewLines.values()) {
                    combinedLines.addAll(newLines);
                }
                if(!unsectionedNewLines.isEmpty()) {
                    combinedLines.addAll(unsectionedNewLines);
                }

                combinedLines.add("# end-config-update");
            }

        } catch (IOException e) {
            Saml.logger().severe("Failed to read existing config file.");
            e.printStackTrace();
            return false;
        }

        try(PrintWriter writer = new PrintWriter(new FileWriter(configFile))) {
            for(String line : combinedLines) {
                writer.println(line);
            }
        } catch (IOException e) {
            Saml.logger().severe("Failed to write patch to config file.");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
