/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration.testutils;

import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class FileHelper
{
    public static File createFile(TemporaryFolder folder, String fileName, String[] lines) throws IOException {
        return createFile(folder, fileName, Arrays.asList(lines));
    }

    public static File createFile(TemporaryFolder folder, String fileName, Iterable<String> lines) throws IOException {
        File file = folder.newFile(fileName);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            for (String string : lines) {
                writer.write(string);
                writer.write("\n");
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return file;
    }
}
