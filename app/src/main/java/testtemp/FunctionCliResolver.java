package testtemp;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class FunctionCliResolver {
    private static final boolean isWindows = CommandUtils.isWindows();

    public static String resolveFunc() {
        return resolve().stream().findFirst().orElse(null);
    }

    public static List<String> resolve() {
        return resolveInner();
    }

    private static List<String> resolveInner() {
        // resolve command from $PATH
        final List<String> whichFuncDirs = resolveCommandPath("func");
        final Set<String> results = new HashSet<>();
        final Set<String> processedDirectories = new HashSet<>();
        for (final String dir : whichFuncDirs) {
            try {
                final File canonicalFile = new File(dir).getCanonicalFile();
                if (!canonicalFile.exists()) {
                    continue;
                }
                final String parentFolder = canonicalFile.getParentFile().getAbsolutePath();
                if (!processedDirectories.add(parentFolder)) {
                    // already processed
                    continue;
                }
                // when `func core tools` is manually installed and func is available at PATH
                // use canonical path to locate the real installation path
                String result = findFuncInFolder(parentFolder);
                if (result != null) {
                    results.add(result);
                }
                result = findFuncInAdditionalFolder(parentFolder);
                if (result != null) {
                    results.add(result);
                }
            } catch (IOException ignored) {
                // ignore
            }
        }
        Optional.ofNullable(findFuncInNpm()).ifPresent(results::add);
        return new ArrayList<>(results);
    }

    //@Nullable
    private static String findFuncInFolder(final String parentFolder) {
        if (new File(parentFolder, getFuncFileName()).exists()) {
            final File func = new File(parentFolder, getFuncFileName());
            if (func.exists()) {
                return Paths.get(func.getAbsolutePath()).normalize().toString();
            }
        }
        return null;
    }

    //@Nullable
    private static String findFuncInNpm() {
        try {
            final String output = StringUtils.trim(CommandUtils.exec("npm root --global"));
            final File path = new File(output, "azure-functions-core-tools/bin");
            if (FileUtils.isDirectory(path)) {
                return findFuncInFolder(path.getAbsolutePath());
            }
        } catch (IOException ignore) {
            // ignore
        }
        return null;
    }

    private static List<String> resolveCommandPath(String command) {
        final List<String> list = new ArrayList<>();
        try {

            final String output = CommandUtils.exec((isWindows ? "where " : "which ") + command);
            if (StringUtils.isBlank(output)) {
                return Collections.emptyList();
            }

            for (final String outputLine : output.split("[\\r\\n]")) {
                final File file = new File(StringUtils.trim(outputLine));
                if (!file.exists() || !file.isFile()) {
                    continue;
                }

                list.add(file.getAbsolutePath());
            }
        } catch (IOException ignored) {
            // ignore
        }
        return list;
    }

    private static String getFuncFileName() {
        return isWindows ? "func.exe" : "func";
    }

    private static String findFuncInAdditionalFolder(String funcParentFolder) {
        // from C:\ProgramData\chocolatey\bin\func.exe -> C:\ProgramData\chocolatey\lib\azure-functions-core-tools\tools\func.exe
        return Optional.ofNullable(findFuncInFolder(Paths.get(funcParentFolder, "../lib/azure-functions-core-tools/tools").toString()))
                // detect func installed by `brew install azure-functions-core-tools@3`
                // readlink /usr/local/bin/func =>
                //../Cellar/azure-functions-core-tools@3/3.0.3477/bin/func
                .orElseGet(() -> findFuncInFolder(Paths.get(funcParentFolder, "../bin").toString()));
    }


}
