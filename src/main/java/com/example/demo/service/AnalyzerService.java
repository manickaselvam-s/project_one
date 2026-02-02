package com.example.demo.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.FileMetrics;

@Service
public class AnalyzerService {

    public FileMetrics analyze(String fileName, String content) {

        String ext = getExtension(fileName);
        List<String> lines = Arrays.asList(content.split("\n"));

        int totalLines = lines.size();
        int blankLines = 0;
        int commentLines = 0;
        int importCount = 0;
        int variableCount = 0;
        int tryCount = 0, catchCount = 0, finallyCount = 0;

        int inheritanceCount = 0;
        int encapsulationCount = 0;
        int overridingCount = 0;
        int overloadingCount = 0;

        int cyclomatic = 1;

        for (String line : lines) {
            String t = line.trim();

            if (t.isBlank()) blankLines++;

            // ---------- COMMENTS ----------
            if (isComment(t, ext)) commentLines++;

            // ---------- IMPORTS ----------
            if (isImport(t, ext)) importCount++;

            // ---------- VARIABLES ----------
            if (isVariable(t, ext)) variableCount++;

            // ---------- JAVA OOPS ONLY ----------
            if (ext.equals("java")) {
                if (t.contains("extends") || t.contains("implements"))
                    inheritanceCount++;

                if (t.contains("private") || t.contains("public") || t.contains("protected"))
                    encapsulationCount++;

                if (t.contains("@Override"))
                    overridingCount++;

                if (t.matches(".*\\(.*\\).*\\{.*"))
                    overloadingCount++;
            }

            // ---------- CYCLOMATIC ----------
            if (t.matches(".*\\b(if|for|while|case|elif|else if)\\b.*")
                    || t.contains("&&") || t.contains("||"))
                cyclomatic++;

            // ---------- TRY / CATCH ----------
            if (t.contains("try")) tryCount++;
            if (t.contains("catch") || t.contains("except")) catchCount++;
            if (t.contains("finally")) finallyCount++;
        }

        double complexityPercentage = ((double) cyclomatic / totalLines) * 100;

        // ---------- CHARACTER ANALYSIS ----------
        int vowels = 0, consonants = 0, special = 0;
        for (char c : content.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                if ("AEIOUaeiou".indexOf(c) >= 0) vowels++;
                else consonants++;
            } else if (!Character.isWhitespace(c) && !Character.isDigit(c)) {
                special++;
            }
        }

        return new FileMetrics(
                fileName,
                totalLines,
                blankLines,
                commentLines,
                importCount,
                variableCount,
                inheritanceCount,
                encapsulationCount,
                overridingCount,
                overloadingCount,
                cyclomatic,
                complexityPercentage,
                tryCount,
                catchCount,
                finallyCount,
                vowels,
                consonants,
                special,
                content.length()
        );
    }

    // ================= HELPERS =================

    private String getExtension(String name) {
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }

    private boolean isComment(String l, String e) {
        return switch (e) {
            case "java", "js", "php" ->
                    l.startsWith("//") || l.startsWith("/*") || l.startsWith("*");
            case "py" ->
                    l.startsWith("#");
            default -> false;
        };
    }

    private boolean isImport(String l, String e) {
        return switch (e) {
            case "java" -> l.startsWith("import");
            case "py" -> l.startsWith("import") || l.startsWith("from");
            case "js" -> l.startsWith("import") || l.contains("require(");
            case "php" -> l.startsWith("use");
            default -> false;
        };
    }

    private boolean isVariable(String l, String e) {
        return switch (e) {
            case "java" ->
                    l.matches(".*(String|int|double|float|boolean|char|long|var).*=");
            case "py" ->
                    l.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s*=.*");
            case "js" ->
                    l.matches(".*(let|var|const)\\s+.*=");
            case "php" ->
                    l.matches("\\$[a-zA-Z_][a-zA-Z0-9_]*\\s*=.*");
            default -> false;
        };
    }
}
