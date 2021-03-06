package de.jjohannes.gradle.moduledependencies.internal.utils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.jjohannes.gradle.moduledependencies.internal.utils.ModuleNamingUtil.sourceSetToModuleName;

public class ModuleInfo {

    public enum Directive {
        REQUIRES,
        REQUIRES_TRANSITIVE,
        REQUIRES_STATIC,
        REQUIRES_STATIC_TRANSITIVE;

        public String literal() {
            return toString().toLowerCase().replace("_", " ");
        }
    }

    private String moduleName;
    private final List<String> requires = new ArrayList<>();
    private final List<String> requiresTransitive = new ArrayList<>();
    private final List<String> requiresStatic = new ArrayList<>();
    private final List<String> requiresStaticTransitive = new ArrayList<>();

    public ModuleInfo(String moduleInfoFileContent) {
        boolean insideComment = false;
        for(String line: moduleInfoFileContent.split("\n")) {
            insideComment = parse(line, insideComment);
        }
    }

    public List<String> get(Directive directive) {
        if (directive == Directive.REQUIRES) {
            return requires;
        }
        if (directive == Directive.REQUIRES_TRANSITIVE) {
            return requiresTransitive;
        }
        if (directive == Directive.REQUIRES_STATIC) {
            return requiresStatic;
        }
        if (directive == Directive.REQUIRES_STATIC_TRANSITIVE) {
            return requiresStaticTransitive;
        }
        return Collections.emptyList();
    }

    @Nullable
    public String moduleNamePrefix(String projectName, String sourceSetName) {
        if (moduleName.equals(projectName)) {
            return "";
        }

        String projectPlusSourceSetName = sourceSetToModuleName(projectName, sourceSetName);
        if (moduleName.endsWith("." + projectPlusSourceSetName)) {
            return moduleName.substring(0, moduleName.length() - projectPlusSourceSetName.length() - 1);
        }
        if (moduleName.endsWith("." + projectName)) {
            return moduleName.substring(0, moduleName.length() - projectName.length() - 1);
        }
        return null;
    }

    /**
     * @return true, if we are inside a multi-line comment after this line
     */
    private boolean parse(String moduleLine, boolean insideComment) {
        if (insideComment) {
            return !moduleLine.contains("*/");
        }

        List<String> tokens = Arrays.asList(moduleLine.replace(";","").replace("{","").trim().split("\\s+"));
        if ("//".equals(tokens.get(0))) {
            return false;
        }

        if (tokens.contains("module")) {
            moduleName = tokens.get(tokens.size() - 1);
        }
        if (tokens.size() > 1 && tokens.get(0).equals("requires")) {
            if (tokens.size() > 3 && tokens.contains("static") && tokens.contains("transitive")) {
                requiresStaticTransitive.add(tokens.get(3));
            } else if (tokens.size() > 2 && tokens.contains("transitive")) {
                requiresTransitive.add(tokens.get(2));
            } else if (tokens.size() > 2 && tokens.contains("static")) {
                requiresStatic.add(tokens.get(2));
            } else {
                requires.add(tokens.get(1));
            }
        }
        return moduleLine.contains("/*");
    }
}
