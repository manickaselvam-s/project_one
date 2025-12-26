package com.example.demo.service;

import com.example.demo.model.FileMetrics;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AnalyzerService {

    public FileMetrics analyze(String fileName, String content) {

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
            String trimmed = line.trim();

            if (trimmed.isBlank()) blankLines++;

            if (trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.endsWith("*/"))
                commentLines++;

            if (trimmed.startsWith("import")) importCount++;

            if (trimmed.matches(".*(String|int|double|float|boolean|char|long|var).*=.*;"))
                variableCount++;

            if (trimmed.contains("extends") || trimmed.contains("implements"))
                inheritanceCount++;

            if (trimmed.contains("private") || trimmed.contains("public") || trimmed.contains("protected"))
                encapsulationCount++;

            if (trimmed.contains("@Override"))
                overridingCount++;

            if (trimmed.matches(".*\\(.*\\).*\\{.*"))
                overloadingCount++;

            if (trimmed.contains("if") || trimmed.contains("for") || trimmed.contains("while") ||
                trimmed.contains("case") || trimmed.contains("&&") || trimmed.contains("||"))
                cyclomatic++;

            if (trimmed.contains("try")) tryCount++;
            if (trimmed.contains("catch")) catchCount++;
            if (trimmed.contains("finally")) finallyCount++;
        }

        // *Complexity Percentage Example: (Cyclomatic / Total Lines) * 100*
        double complexityPercentage = ((double) cyclomatic / totalLines) * 100;

        // Character wise analysis
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
}
