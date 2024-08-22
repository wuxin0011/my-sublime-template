import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




/******************************************************** use ********************************************************

 1 config
 root dir : DEFAULT_ROOTS

 2 run:

 LCUtil.run(ClassType,methodName,compareFile,booleanShap,Strict)


 ClassType: must
 methodName : run class method name,if is build class,place use "__ConstructorClass__" flag
 compareFile: read file,can absolute path, default root in.txt
 booleanShap: long text line parse,default false
 Strict: if compare content not need ,default false



 3 example:

 LCUtil.run(Hello.class, "checkRecord", "solution.txt");
 LCUtil.run(Hello.class, "checkRecord", "solution.txt"，true,false);
 LCUtil.run(Hello.class, "__ConstructorClass__", "solution.txt"，true,false);


 4  use testcase-code parse

    LCUtil.run(Hello.class, "checkRecord", "__TEST_CASE__");


    @testcase-start

    3
    4
    6

    2
    7
    15

    @testcase-end

 ********************************************************************************************************************


*/
 /**
 * @author: wuxin0011
 * @Description:
 */
@SuppressWarnings("unchecked")
public class _LCUtil {
    // must config src dir name !!!
    public static final String[] DEFAULT_ROOTS = {""};
    public static final String CLASS_FLAG = "__ConstructorClass__";
    public static final String DEFAULT_METHOD_NAME = "null";
    public static final String VOID_OR_ARGS = "null";
    public static final String DEFAULT_READ_FILE = "in.txt";
    public static final boolean DEFAULT_SUPPORT_LONG_CONTENT = false;
    public static final boolean IS_STRICT_EQUAL = true;
    public static final String TEST_CASE_FLAG = "__TEST_CASE__";
    public static final String TEST_CASE_START = "@testcase-start";
    public static final String TEST_CASE_END = "@testcase-end";

    private static String ROOT_DIR = "null";

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
    }

    static {
        getProjectRootDir();
    }

    public static String getProjectRootDir() {
        if (!(ROOT_DIR == null || "null".equals(ROOT_DIR))) {
            return ROOT_DIR;
        }
        if (DEFAULT_ROOTS == null) {
            throw new RuntimeException("not null");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(File.separator);
        for (String defaultRoot : DEFAULT_ROOTS) {
            sb.append(defaultRoot);
            sb.append(File.separator);
        }
        ROOT_DIR = sb.toString();
        return ROOT_DIR;
    }


    public static <T> void testUtil(Class<T> c) {
        run(c, DEFAULT_METHOD_NAME, DEFAULT_READ_FILE);
    }


    public static <T> void run(Class<T> c, String fileName, boolean openLongContent) {
        run(c, DEFAULT_METHOD_NAME, fileName, openLongContent);
    }


    public static <T> void run(Class<T> c, boolean openLongContent) {
        run(c, DEFAULT_METHOD_NAME, DEFAULT_READ_FILE, openLongContent);
    }


    public static <T> void run(Class<T> c, String methodName) {
        run(c, methodName, DEFAULT_READ_FILE);
    }

    public static <T> void run(Class<T> c, String methodName, String fileName) {
        run(c, methodName, fileName, DEFAULT_SUPPORT_LONG_CONTENT);
    }

    public static <T> void run(Class<T> c, String methodName, String fileName, boolean openLongContent) {
        run(c, methodName, fileName, openLongContent, IS_STRICT_EQUAL);
    }

    public static <T> void run(Class<T> src, String methodName, String fileName, boolean openLongContent, boolean isStrict) {
        check(src, methodName, fileName);
        boolean find = false;
        try {
            List<String> inputList = null;
            if(TEST_CASE_FLAG.equals(fileName)) {
                inputList = readTestCase(src);
            }else {
                inputList = readFile(src, fileName, openLongContent);
            }
            if (inputList == null || inputList.size() == 0) {
                System.exit(0);
            }
            if (CLASS_FLAG.equals(methodName)) {
                find = true;
                handlerConstructorValid(src, inputList, methodName, isStrict);
            } else {
                Object obj = ReflectUtils.initObjcect(src, null);
                Method method = findMethodName(src, methodName);
                if (method != null) {
                    find = true;
                    startValid(obj, method, inputList, isStrict, true);
                }
            }
            if (!find) {
                System.err.println("check methodName ,not found " + methodName + " method !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void handlerConstructorValid(Class<?> src, List<String> inputList, String methodName, boolean isStrict) {
        String[] names = null, args = null, expect = null;
        boolean isTest = false;
        final String className = src.getSimpleName();
        Constructor constructor = src.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Method[] declaredMethods = src.getDeclaredMethods();
        List<String> result = null;
        Map<String, Method> map = new HashMap<>();
        for (Method method : declaredMethods) {
            method.setAccessible(true);
            map.put(method.getName(), method);
        }
        int t = 0;
        int compareTimes = 0;
        TestData testData = new TestData(src);
        int[] testGroup = testData == null || testData.testCaseGroup == null ? new int[]{1, 0x3fffff} : testData.testCaseGroup;
        boolean isTestCase = false;
        List<String> errorTimes = new ArrayList<>();
        for (String s : inputList) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            t++;
            if (t % 3 == 1) {
                names = ReflectUtils.oneStringArray(s);
                for (int i = 0; i < names.length; i++) {
                    names[i] = StringUtils.ingoreString(names[i]);
                }
            } else if (t % 3 == 2) {
                args = ReflectUtils.parseConstrunctorClassString(s);
            } else if (t % 3 == 0) {
                expect = ReflectUtils.parseConstrunctorClassString(s);
            }
            if (t % 3 == 0) {
                Object obj = null;
                int a = 0, b = 0, deep = 0;
                if (args.length != expect.length || args.length != names.length || expect.length != names.length) {
                    throw new RuntimeException("result not mathch palce check");
                }
                compareTimes++;
                isTestCase = testGroup == null ? true : testGroup[0] <= compareTimes && compareTimes <= testGroup[1];
                if (!isTestCase) {
                    continue;
                }
                for (int index = 0; index < names.length; index++) {
                    String name = names[index];
                    if (StringUtils.isEmpty(name)) {
                        continue;
                    }
                    if (name.equals(className)) {
                        try {
                            result = new ArrayList<>();
                            ReflectUtils.handlerConstructorMethodInput(args[index], result);
                            obj = handlerConstructorMethod(constructor, result, src);
                        } catch (Exception e) {
                            e.printStackTrace();
                            obj = null;
                        }
                    } else {
                        Objects.requireNonNull(obj, "obj is null");
                        Method method = map.get(name);
                        if (!map.containsKey(name)) {
                            throw new RuntimeException("not find method " + name + ", place check your format !");
                        }
                        result = new ArrayList<>();
                        ReflectUtils.handlerConstructorMethodInput(args[index], result, method);
                        ReflectUtils.handlerConstructorMethodOutput(expect[index], result, method);
                        boolean isOk = startValid(obj, map.get(name), result, isStrict, false);
                        if (!isOk) {
                            String errorInfo = "Run CompareTimes :  " + compareTimes + "\nCall Method      :  " + name + "\nArgs Index       :  " + index + "\nArgs             :  " + args[index];
                            errorTimes.add(errorInfo + "\n");
                        }
                    }
                }
                args = names = expect = null;
                result = null;
            }
        }
        if (testData != null && !StringUtils.isEmpty(testData.info)) {
            System.out.println(testData.info);
        }
        if (errorTimes.size() == 0) {
            System.out.println("Accepted!");
        } else {
            for (String errorInfo : errorTimes) {
                System.out.println(errorInfo);
            }
        }
    }

    public static Object handlerConstructorMethod(Constructor<?> constructor, List<String> inputList, Class<?> aclass) {
        Object obj = null;
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length == 0) {
            try {
                obj = constructor.newInstance();
                return obj;
            } catch (Exception e) {
                return null;
            }
        }
        Object[] args = null;

        int size = inputList.size();
        String read = null;
        for (int idx = 0; idx < size; ) {
            args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length && idx < size; i++, idx++) {
                while (idx < size && ((read = inputList.get(idx)) == null || read.length() == 0)) {
                    idx++;
                }
                if (idx == size) {
                    break;
                }
                if (idx != size && (read == null || read.length() == 0)) {
                    throw new RuntimeException("result not match place check your ans !");
                }
                args[i] = ReflectUtils.parseArg(aclass, constructor.getName(), parameterTypes[i], read, i, parameterTypes.length);
                read = null;
            }

            try {
                obj = constructor.newInstance(args);
            } catch (Exception e) {
                e.printStackTrace();
                obj = null;
            }
        }
        return obj;


    }

    public static boolean startValid(Object obj, Method method, List<String> inputList, boolean isStrict, boolean newObj) {
        Objects.requireNonNull(obj, "obj is null");
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> srcClass = obj.getClass();
        Class<?> origin = ReflectUtils.loadOrigin(obj.getClass());
        Object[] args = null;
        String returnName = method.getReturnType().getSimpleName();
        final String tempRetrunName = returnName;
        int size = inputList.size();
        String read = null;
        TestData testData = newObj ? new TestData(method, origin, srcClass) : null;
        int[] testCaseInfo = testData == null || testData.testCaseGroup == null ? new int[]{1, 0x3f3f3f} : testData.testCaseGroup;
        int typeId = -2;
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        boolean isConstrunctorClass = !origin.getSimpleName().equals(obj.getClass().getSimpleName());
        List<Integer> errorTimes = new ArrayList<>();
        int exceptionTime = -1;
        int compareTimes = 1;
        boolean isStartTest = false;
        for (int idx = 0; idx < size; ) {
            if (compareTimes > testCaseInfo[1]) {
                break;
            }
            isStartTest = testCaseInfo[0] <= compareTimes && compareTimes <= testCaseInfo[1];
            if (isStartTest) {
                if (newObj && !isStatic) {
                    obj = ReflectUtils.initObjcect(srcClass, null);
                    Objects.requireNonNull(obj, "obj is null");
                }
            }
            boolean isFill = false;
            if (parameterTypes == null || parameterTypes.length == 0) {
                while (idx < size && ((read = inputList.get(idx)) == null)) {
                    idx++;
                }
                if (VOID_OR_ARGS.equals(read) || read.length() == 0) {
                    isFill = true;
                    read = null;
                    idx++;
                } else {
                    throw new RuntimeException("NO args fill, should null");
                }
            } else {
                args = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length && idx < size; i++, idx++) {
                    while (idx < size && ((read = inputList.get(idx)) == null || read.length() == 0)) {
                        idx++;
                    }
                    if (idx == size) {
                        break;
                    }
                    if (idx != size && (read == null || read.length() == 0)) {
                        throw new RuntimeException("result not match place check your ans !");
                    }
                    isFill = true;
                    if (isStartTest) {
                        args[i] = ReflectUtils.parseArg(origin, method.getName(), parameterTypes[i], read, i, parameterTypes.length);
                    }
                    read = null;
                }

            }
            if (idx >= size) {
                if (isFill) {
                    System.out.println("place check result match");
                    errorTimes.add(compareTimes);
                }
                break;
            }
            Object result = null;
            try {
                if (isStartTest) {
                    if (parameterTypes == null || parameterTypes.length == 0) {
                        result = method.invoke(isStatic ? null : obj);
                    } else {
                        result = method.invoke(isStatic ? null : obj, args);
                    }
                }
                while (idx < inputList.size() && ((read = inputList.get(idx)) == null || read.length() == 0)) {
                    idx++;
                }
                if (read == null) {
                    if ("string".equalsIgnoreCase(returnName)) {
                        read = "";
                    } else {
                        throw new RuntimeException("result not match place check your ans !");
                    }
                }
                if (!isStartTest) {
                    returnName = tempRetrunName;
                    idx++;
                    compareTimes++;
                    continue;
                }
                if ("void".equalsIgnoreCase(returnName) && result == null) {
                    if (VOID_OR_ARGS.equals(read)) {
                        idx++;
                        compareTimes++;
                        continue;
                    }
                    if (args != null && args.length > 0) {
                        if (typeId == -2) {
                            typeId = ReflectUtils.handlerVoidReturnType(parameterTypes);
                        }
                        if (typeId == -1) {
                            throw new RuntimeException("unkonwn compare type");
                        }
                        returnName = parameterTypes[typeId].getSimpleName();
                        result = args[typeId];
                    }
                }

                Object expect = ReflectUtils.parseArg(origin, method.getName(), returnName, read, -1, -1);
                if (expect != null && !TestUtils.valid(result, expect, returnName, isStrict, true)) {
                    if (newObj) {
                        System.out.println("compare " + compareTimes + " is Error , Run Method Name : " + method.getName() + "\n");
                    }
                    errorTimes.add(compareTimes);
                }
                args = null;
                read = null;
            } catch (Exception e) {
                e.printStackTrace();
                exceptionTime = compareTimes;
                break;
            }
            returnName = tempRetrunName;
            idx++;
            compareTimes++;
        }
        if (newObj && !StringUtils.isEmpty(testData.info)) {
            System.out.println(testData.info);
        }
        if (errorTimes.size() == 0 && exceptionTime == -1 && newObj) {
            System.out.println("Accepted!");
        } else {
            if (exceptionTime != -1) {
                System.out.println("exception times :" + exceptionTime);
            }
        }
        return errorTimes.size() == 0 && exceptionTime == -1;
    }

    public static Method findMethodName(Class<?> src, String methodName) {
        boolean find = false;
        Method[] methods = src.getDeclaredMethods();
        List<String> names = new ArrayList<>();
        for (Method method : methods) {
            names.add(method.getName());
        }
        if (names.size() > 0 && DEFAULT_METHOD_NAME.equals(methodName) || "main".equals(methodName)) {
            for (String name : names) {
                if (name.equals("main") || name.startsWith("lambda$")) {
                    continue;
                }
                methodName = name;
            }
        }
        for (Method method : methods) {
            if (!method.getName().equals(methodName)) {
                continue;
            }
            if ("main".equals(method.getName())) {
                continue;
            }
            find = true;
            method.setAccessible(true);
            return method;
        }
        return null;
    }


    public static List<String> readFile(Class<?> c, String filename, boolean openLongContent) {
        return readFile(buildAbsolutePath(ReflectUtils.loadOrigin(c)), filename, openLongContent);
    }

    public static List<String> readFile(String path, String fileName, boolean openLongContent) {
        if(TEST_CASE_FLAG.equals(fileName)) {
            System.out.println("path = " + path + ",fileName = " + fileName);
        }else {
           if(isAbsolutePath(fileName) && (System.getProperty("os.name").contains("window") || System.getProperty("os.name").contains("Window"))) {
                if(fileName.contains(".java")) {
                    String[] fileNames = fileName.split("/");
                    String className = "";
                    String readFileName = "";
                    for(String name : fileNames) {
                        if(name.contains(".java")) {
                            className = name.replace(".java","");
                        }
                    }
                    fileName = System.getProperty("user.dir") + "/../in/" + className + "/in.txt";
                }else if((fileName.startsWith("\\") ||fileName.startsWith("/")) && !fileName.contains(":")  ) {
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0;i < fileName.length();i++) {
                        if( i == 0) {
                            continue;
                        }
                        sb.append(fileName.charAt(i));
                        if(i == 1) {
                            sb.append(":");
                        }
                    }
                    fileName = sb.toString();
                }
        }
        }
        
        File file = new File(isAbsolutePath(fileName) ? fileName : (path + fileName));

        
        if (!file.exists()) {
            // System.out.println(file.getAbsolutePath() + " not found!");
            createFile(file.getAbsolutePath());
            if(file.exists()) {
                System.out.println("create file path : "  + file.getAbsolutePath());
            }
            return null;
        }
        BufferedReader breder = null;
        BufferedInputStream bis = null;
        List<String> ans = new ArrayList<>();
        try {
            if (openLongContent) {
                return parseShpInfo(file);
            } else {
                breder = new BufferedReader(new FileReader(file));
                String t = null;
                while ((t = breder.readLine()) != null) {
                    ans.add(t);
                }
            }
        } catch (Exception e) {
            System.err.println("parse failed " + e.getMessage());
        } finally {
            close(breder);
            close(bis);
        }
        return ans;
    }

    public static List<String> parseShpInfo(File file) {
        if (file == null || !file.exists()) {
            if (file != null) System.out.println(file.getName() + " is null ,place check exist !");
            return null;
        }
        List<String> ans = new ArrayList<>();
        BufferedInputStream bis = null;
        try {
            byte[] buff = new byte[1024 * 1024];
            StringBuilder sb = new StringBuilder();
            bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
            while ((bis.read(buff)) != -1) {
                sb.append(new String(buff));
            }
            String input = sb.toString();
            Pattern pattern = Pattern.compile("#([^#]+)#", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(input);
            boolean find = false;
            while (matcher.find()) {
                ans.add(matcher.group(1));
                find = true;
            }
            if (!find) {
                System.out.println("find error place use #content# package your content ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(bis);
        }
        return ans;
    }

    public static <T> String buildAbsolutePath(Class<T> c) {
        return buildAbsolutePath() + getPackagePath(c) + File.separator;
    }

    public static String buildAbsolutePath() {
        return buildAbsolutePath(getProjectRootDir());
    }

    public static String buildAbsolutePath(String baseDir) {
        String wordDir = getWorkDir();
        return wordDir + baseDir;
    }

    public static <T> String getPackagePath(Class<T> c) {
        Package pkg = c.getPackage();
        if (pkg == null) return "";
        String info = pkg.getName();
        info = info.replace("package ", "");
        char[] cs = info.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] == '.') {
                cs[i] = File.separator.charAt(0);
            }
        }
        return new String(cs);
    }

    public static String getWorkDir() {
        String dir = System.getProperty("user.dir");
        if (dir == null || "null".equals(dir) || dir.length() == 0) {
            dir = new File("").getAbsolutePath();
        }
        return dir;
    }

    public static void check(Class<?> src, String methodName, String fileName) {
        Objects.requireNonNull(src, "className not null");
        Objects.requireNonNull(methodName, "methodName not null");
        Objects.requireNonNull(fileName, "fileName  not null");
    }

    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private static final String defaultContent = "List<String>";

    public static String findListReturnTypeMethod(Class<?> c, String name, String returnType, int idx, int argsSize) {
        if (c == null) {
            System.out.println("error t is null");
            return defaultContent;
        }
        c = ReflectUtils.loadOrigin(c);
        String path = buildAbsolutePath(c) + c.getSimpleName() + ".java";
        File file = new File(path);
        if (!file.exists()) {
            System.out.println(file.getAbsolutePath() + "Not found !");
            return defaultContent;
        }
        BufferedReader bis = null;
        String s = null;
        String matchMethodName = name + "(";
        try {
            bis = new BufferedReader(new FileReader(file));
            while ((s = bis.readLine()) != null) {
                if (s.contains(matchMethodName)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(bis);
        }
        if (s == null) {
            System.out.println("not find + " + name + " method");
            return defaultContent;
        }
        StringBuilder sb = new StringBuilder();
        Stack<Character> sk = new Stack<>();
        int st = -1;
        if (idx == -1) {
            st = s.indexOf(returnType);
            if (st == -1) {
                throw new RuntimeException("not find " + returnType + " as return type !");
            }
            st += returnType.length();
            sb.append(returnType);
            for (int i = st; i < s.length(); i++) {
                char cr = s.charAt(i);
                if (sk.isEmpty() && cr == ' ') break;
                if (cr == '[') {
                    sk.push(cr);
                } else if (cr == ']') {
                    if (!sk.isEmpty()) {
                        sk.pop();
                    }
                }
                sb.append(cr);
            }
            return sb.toString();
        } else {
            st = s.indexOf(matchMethodName);
            if (st == -1) throw new RuntimeException("not find methodName");
            st += name.length();
            List<String> argsList = new ArrayList<>();
            for (int i = st; i < s.length(); i++) {
                char chr = s.charAt(i);
                if (chr == ' ') {
                    if (sb != null && sb.toString().length() != 0 && !"".equals(sb.toString())) {
                        argsList.add(sb.toString());
                        sb = null;
                    }
                    continue;
                }
                if (chr == '(') {
                    sb = new StringBuilder();
                    sk.push(chr);
                } else if (chr == ')') {
                    if (sb != null) {
                        argsList.add(sb.toString());
                        sb = null;
                    }
                    if (!sk.isEmpty()) {
                        sk.pop();
                    }
                    if (sk.isEmpty()) break;
                } else if (chr == ',') {
                    sb = new StringBuilder();
                } else {
                    if (sb != null) {
                        sb.append(chr);
                    }
                }
            }
            if (idx >= argsList.size()) throw new RuntimeException("not find " + returnType);
            if (argsSize != argsList.size()) throw new RuntimeException("args size not match place check!");
            return argsList.get(idx);
        }
    }

    public static String wrapperAbsolutePath(Class<?> c, String dir) {
        Objects.requireNonNull(dir, "dir Not allow null");
        return isAbsolutePath(dir) ? dir : buildAbsolutePath(c) + dir;
    }

    public static boolean isAbsolutePath(String dir) {
        if (dir == null || dir.length() == 0) {
            return false;
        }

        return dir.charAt(0) == File.separator.charAt(0) || dir.charAt(0) == '/' || dir.length() >= 2 && dir.charAt(1) == ':';
    }

    public static String readContent(Class<?> c, String path) {
        path = wrapperAbsolutePath(c, path);
        return readContent(path);
    }

    public static String readContent(String path) {
        return readContent(new File(path));
    }

    public static String readContent(File file) {
        BufferedInputStream bis = null;
        StringBuilder sb = null;
        try {
            if (!file.exists()) {
                return "";
            }
            bis = new BufferedInputStream(new FileInputStream(file));
            sb = new StringBuilder();
            byte[] buf = new byte[1024 * 1024];
            int len = -1;
            while ((len = bis.read(buf)) != -1) {
                sb.append(new String(buf, 0, len));
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            close(bis);
        }
    }


    public static List<String> readTestCase(Class<?> src) {
        try{
            Class<?> origin = ReflectUtils.loadOrigin(src);
            String newFileName = buildAbsolutePath(origin) + "../" + origin.getSimpleName()+".java";
            File file = new File(newFileName);
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String s = null;
            List<String> result = new ArrayList<>();
            int st = -1;
            while((s = bf.readLine())!=null) {
                if(s.contains(TEST_CASE_END)) {
                    break;
                }
                if(s.contains(TEST_CASE_START)) {
                    st = 0;
                    continue;
                }
                if(st != -1) {
                    result.add(s);
                }
            }
            bf.close();
            return result;
        }catch(Exception e){
            return null;
        }
    }

    public static void writeContent(Class<?> c, String url, String content) {
        url = wrapperAbsolutePath(c, url);
        writeContent(url, content);
    }

    public static void writeContent(String url, String content) {
        writeContent(new File(url), content);
    }

    public static void writeContent(File file, String content) {
        BufferedOutputStream bos = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(content.getBytes(StandardCharsets.UTF_8));
            bos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(bos);
        }
    }

    public static File createFile(Class<?> c, String filename) {
        filename = wrapperAbsolutePath(c, filename);
        return createFile(filename);
    }

    public static File createFile(String fileName) {
        try {
            File file = new File(fileName);
            if (file == null) {
                return null;
            }
            String parent = file.getParent();
            if (parent == null) {
                return file;
            }
            File parentFile = new File(parent);
            if (!parentFile.exists()) parentFile.mkdirs();
            if (!file.exists()) {
                file.createNewFile();
                return file;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class StringUtils {
        public static boolean isEmpty(String s) {
            return s == null || s.length() == 0;
        }

        public static boolean strictIsEmpty(String s) {
            return s == null || s.length() == 0 || "null".equals(s) || "#".equals(s);
        }

        public static boolean isIgnore(char c) {
            return c == '\r' || c == '\n' || c == '\t' || c == '\b' || c == '\f' || c == '\0' || c == '\\' || c == ' ' || c == '\'' || c == '\"';
        }

        public static boolean isIgnore(char c, char st) {
            if (st == '#' && isIgnoreStrict(c)) {
                return true;
            }
            return st != '#' && isIgnore(c);
        }

        public static boolean isIgnoreStrict(char c) {
            return isIgnore(c) || isIgnoreStrict(c, '#');
        }

        public static boolean isIgnoreStrict(char c, char... chars) {
            if (isIgnore(c)) {
                return true;
            }
            for (char c1 : chars) {
                if (c1 == c) return true;
            }
            return false;
        }

        public static String ingoreString(String input) {
            if (input == null || input.length() == 0) {
                return input;
            }
            input = input.replace("&quot;", "");
            char[] charArray = input.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : charArray) {
                if (StringUtils.isIgnoreStrict(c)) continue;
                sb.append(c);
            }
            return sb.toString();
        }
    }

    public static class ReflectUtils {
        public static Object parseArg(Class<?> origin, String methodName, Class<?> src, String input, int idx, int argsSize) {
            return parseArg(origin, methodName, src.getSimpleName(), input, idx, argsSize);
        }

        public static Object parseArg(Class<?> src, String methodName, String type, String input, int idx, int argsSize) {
            if (input == null || "".equals(input) || input.length() == 0) {
                System.out.println("read content is null");
                return null;
            }
            if ("void".equals(type)) {
                System.out.println("void type not support place check type !");
                return null;
            }
            input = toString(input);
            try {
                switch (type) {
                    case "int":
                    case "Integer":
                        return Integer.parseInt(input);
                    case "long":
                    case "Long":
                        return Long.parseLong(input);
                    case "boolean":
                    case "Boolean":
                        return input.contains("t");
                    case "boolean[]":
                    case "Boolean[]":
                        return oneBooleanArray(input);
                    case "boolean[][]":
                    case "Boolean[][]":
                        return doubleBooleanArray(input);
                    case "double":
                        return parseDouble(input);
                    case "double[]":
                        return oneDoubleArray(input);
                    case "double[][]":
                        return doubleDoubleArray(input);
                    case "long[]":
                    case "Long[]":
                        return oneLongArray(input);
                    case "long[][]":
                    case "Long[][]":
                        return doubleLongArray(input);
                    case "float":
                        return Float.parseFloat(input);
                    case "int[]":
                    case "Integer[]":
                        return oneIntArray(input);
                    case "int[][]":
                    case "Integer[][]":
                        return doubleIntArray(input);
                    case "int[][][]":
                    case "Integer[][][]":
                        return threeIntArray(input);
                    case "char":
                    case "Character":
                        return input.toCharArray()[0];
                    case "char[]":
                    case "Character[]":
                        return oneCharArray(input);
                    case "char[][]":
                    case "Character[][]":
                        return doubleCharArray(input);
                    case "char[][][]":
                    case "Character[][][]":
                        return threeCharArray(input);
                    case "string":
                    case "String":
                        return toString(input);
                    case "string[]":
                    case "String[]":
                        return oneStringArray(input);
                    case "string[][]":
                    case "String[][]":
                        return doubleStringArray(input);
                    case "string[][][]":
                    case "String[][][]":
                        return threeStringArray(input);
                    case "TreeNode":
                        return TreeNode.widthBuildTreeNode(oneStringArray(input));
                    case "TreeNode[]":
                        return createListTreeNodeArray(input);
                    case "ListNode":
                        return ListNode.createListNode(oneIntArray(input));
                    case "ListNode[]":
                        return createListNodeArray(input);
                    case "List":
                    case "ArrayList":
                        return toList(src, methodName, type, input, idx, argsSize);
                    default:
                        System.out.println(type + " not implement ,place implement!");
                        return null;
                }
            } catch (NumberFormatException e) {
                errorInfo(type);
                return null;
            }
        }

        private static TreeNode[] createListTreeNodeArray(String input) {
            List<List<String>> lists = parseDoubleString(input);
            TreeNode[] nodes = new TreeNode[lists.size()];
            for (int i = 0; i < lists.size(); i++) {
                try {
                    int size = lists.get(i).size();
                    String[] ss = new String[size];
                    for (int k = 0; k < size; k++) {
                        ss[k] = lists.get(i).get(i);
                    }
                    nodes[i] = TreeNode.widthBuildTreeNode(ss);
                } catch (Exception e) {
                    nodes[i] = null;
                }
            }
            return nodes;
        }

        public static ListNode[] createListNodeArray(String input) {
            List<List<Integer>> ls = parseListDoubleInteger(input);
            int n = ls.size();
            ListNode[] nodes = new ListNode[n];
            for (int i = 0; i < n; i++) {
                try {
                    if (ls.get(i) == null) continue;
                    int[] array = ls.get(i).stream().mapToInt(Integer::intValue).toArray();
                    nodes[i] = ListNode.createListNode(array);
                } catch (Exception e) {
                    nodes[i] = null;
                }
            }
            return nodes;
        }

        private static long[][] doubleLongArray(String input) {
            List<List<Long>> longList = parseDoubleLongList(input);
            long[][] longs = new long[longList.size()][];
            for (int i = 0; i < longList.size(); i++) {
                longs[i] = new long[longList.get(i).size()];
                for (int j = 0; j < longList.get(i).size(); j++) {
                    longs[i][j] = longList.get(i).get(j);
                }
            }
            return longs;
        }

        private static List<List<Long>> parseDoubleLongList(String input) {
            List<List<String>> list = parseDoubleString(input);
            List<List<Long>> longs = new ArrayList<>();
            for (List<String> one : list) {
                List<Long> temp = new ArrayList<>();
                for (String s : one) {
                    temp.add(Long.parseLong(s));
                }
                longs.add(temp);
            }
            return longs;
        }

        private static double[][] doubleDoubleArray(String input) {
            List<List<Double>> doubleDoubleList = parseDoubleDoubleList(input);
            double[][] doubles = new double[doubleDoubleList.size()][];
            for (int i = 0; i < doubleDoubleList.size(); i++) {
                doubles[i] = new double[doubleDoubleList.get(i).size()];
                for (int j = 0; j < doubleDoubleList.get(i).size(); j++) {
                    doubles[i][j] = doubleDoubleList.get(i).get(j);
                }
            }
            return doubles;
        }

        private static Object oneLongArray(String input) {
            List<Long> ls = parseListLong(input);
            long[] res = new long[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                res[i] = ls.get(i);
            }
            return res;
        }

        private static List<Long> parseListLong(String input) {
            List<String> strings = parseListString(input);
            List<Long> res = new ArrayList<>();
            for (String s : strings) {
                try {
                    res.add(Long.parseLong(s));
                } catch (Exception e) {
                }
            }
            return res;
        }

        public static Object toList(Class<?> t, String methodName, String type, String input, int idx, int argsSize) {
            String listType = findListReturnTypeMethod(t, methodName, type, idx, argsSize);
            String originType = listType;
            if (listType.contains("ArrayList")) {
                listType = listType.replace("ArrayList", "List");
            }
            switch (listType) {
                case "List<TreeNode>":
                    return parseListTreeNode(input);
                case "List<String>":
                    return parseListString(input);
                case "List<List<String>>":
                    return parseDoubleString(input);
                case "List<List<List<String>>>":
                    return parseThreeString(input);
                case "List<Integer>":
                    return parseListInteger(input);
                case "List<Long>":
                    return parseListLong(input);
                case "List<Boolean>":
                    return parseListBoolean(input);
                case "List<List<Boolean>>":
                    return parseDoubleBoolean(input);
                case "List<Double>":
                    return parseDoubleList(input);
                case "List<List<Double>>":
                    return parseDoubleDoubleList(input);
                case "List<List<Integer>>":
                    return parseListDoubleInteger(input);
                case "List<List<List<Integer>>>":
                    return parseListThreeInteger(input);
                case "List<Character>":
                    return parseListChar(input);
                case "List<List<Character>>":
                    return parseListDoubleChar(input);
                case "List<List<List<Character>>>":
                    return parseThreeCharArray(input);
                default:
                    System.err.println("NOT implement " + originType + ",place implement this ,default convert string list");
                    return parseListString(input);
            }
        }

        private static List<TreeNode> parseListTreeNode(String input) {
            List<TreeNode> treeNodes = new ArrayList<>();
            String[][] strings = doubleStringArray(input);
            for (int i = 0; i < strings.length; i++) {
                TreeNode treeNode = TreeNode.widthBuildTreeNode(strings[i]);
                treeNodes.add(treeNode);
            }
            return treeNodes;
        }

        public static String toString(String input) {
            if (input == null || input.length() == 0) {
                return "";
            }
            char[] charArray = input.toCharArray();
            StringBuilder sb = new StringBuilder();
            char st = input.charAt(0);
            for (char c : charArray) {
                if (StringUtils.isIgnore(c, st)) continue;
                sb.append(c);
            }
            return sb.toString();
        }

        public static boolean[] oneBooleanArray(String input) {
            List<Boolean> ls = parseListBoolean(input);
            boolean[] ans = new boolean[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                ans[i] = ls.get(i);
            }
            return ans;
        }

        public static boolean[][] doubleBooleanArray(String input) {
            List<List<Boolean>> ls = parseDoubleBoolean(input);
            boolean[][] ans = new boolean[ls.size()][];
            for (int i = 0; i < ls.size(); i++) {
                ans[i] = new boolean[ls.get(i).size()];
                for (int j = 0; j < ls.get(i).size(); j++) {
                    ans[i][j] = ls.get(i).get(j);
                }
            }
            return ans;
        }

        private static List<List<Boolean>> parseDoubleBoolean(String input) {
            List<List<String>> doubles = parseDoubleString(input);
            List<List<Boolean>> ans = new ArrayList<>();
            int m = doubles.size(), n = doubles.get(0).size();
            for (int i = 0; i < m; i++) {
                ArrayList<Boolean> temp = new ArrayList<>();
                for (int j = 0; j < n; j++) {
                    String s = doubles.get(i).get(j);
                    if (s.contains("t")) {
                        temp.add(true);
                    } else {
                        temp.add(false);
                    }
                }
                ans.add(temp);
            }
            return ans;
        }

        private static List<Boolean> parseListBoolean(String input) {
            List<String> strings = parseListString(input);
            List<Boolean> ans = new ArrayList<>();
            for (String s : strings) {
                if (s.contains("t")) ans.add(true);
                else ans.add(false);
            }
            return ans;
        }

        public static double[] oneDoubleArray(String input) {
            List<Double> ls = parseDoubleList(input);
            double[] ans = new double[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                ans[i] = ls.get(i);
            }
            return ans;
        }

        public static List<Double> parseDoubleList(String input) {
            List<String> list = parseListString(input);
            ArrayList<Double> doubles = new ArrayList<>();
            for (String s : list) {
                try {
                    doubles.add(parseDouble(s));
                } catch (NumberFormatException e) {
                }
            }
            return doubles;
        }

        private static List<List<Double>> parseDoubleDoubleList(String input) {
            List<List<String>> list = parseDoubleString(input);
            List<List<Double>> doubles = new ArrayList<>();
            for (List<String> one : list) {
                List<Double> temp = new ArrayList<>();
                for (String s : one) {
                    temp.add(parseDouble(s));
                }
                doubles.add(temp);
            }
            return doubles;
        }

        public static int[] oneIntArray(String input) {
            if ("[]".equals(input) || "{}".equals(input)) {
                return new int[]{};
            }
            List<Integer> ls = parseListInteger(input);
            if (ls.size() == 0) {
                return new int[]{};
            }
            int[] ans = new int[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                ans[i] = ls.get(i);
            }
            return ans;
        }

        public static int[][] doubleIntArray(String input) {
            if ("[]".equals(input) || "[[]]".equals(input) || "{}".equals(input) || "{{}}".equals(input)) {
                return new int[][]{};
            }
            List<List<Integer>> ls = parseListDoubleInteger(input);
            if (ls == null || ls.size() == 0) {
                return new int[][]{};
            }
            int row = ls.size();
            int[][] ans = new int[row][];
            for (int i = 0; i < row; i++) {
                ans[i] = new int[ls.get(i).size()];
                for (int j = 0; j < ls.get(i).size(); j++) {
                    ans[i][j] = ls.get(i).get(j);
                }
            }
            return ans;
        }

        public static int[][][] threeIntArray(String input) {
            List<List<List<Integer>>> ls = parseListThreeInteger(input);
            int[][][] result = new int[ls.size()][][];
            for (int i = 0; i < ls.size(); i++) {
                List<List<Integer>> d = ls.get(i);
                result[i] = new int[d.size()][];
                for (int j = 0; j < d.size(); j++) {
                    List<Integer> t = d.get(j);
                    result[i][j] = new int[t.size()];
                    for (int k = 0; k < t.size(); k++) {
                        result[i][j][k] = t.get(k);
                    }
                }
            }
            return result;
        }

        public static char[] oneCharArray(String input) {
            List<Character> ls = parseListChar(input);
            char[] cs = new char[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                cs[i] = ls.get(i);
            }
            return cs;
        }

        public static char[][] doubleCharArray(String input) {
            List<List<Character>> ls = parseListDoubleChar(input);
            int row = ls.size();
            char[][] cs = new char[row][];
            for (int i = 0; i < row; i++) {
                List<Character> cc = ls.get(i);
                cs[i] = new char[cc.size()];
                for (int j = 0; j < cc.size(); j++) {
                    cs[i][j] = cc.get(j);
                }
            }
            return cs;
        }

        public static char[][][] threeCharArray(String input) {
            List<List<List<Character>>> lists = parseThreeCharArray(input);
            int row = lists.size();
            char[][][] ans = new char[row][][];
            for (int i = 0; i < row; i++) {
                ans[i] = new char[lists.get(i).size()][];
                for (int j = 0; j < lists.get(i).size(); j++) {
                    ans[i][j] = new char[lists.get(i).get(j).size()];
                    for (int z = 0; z < lists.get(i).get(j).size(); z++) {
                        ans[i][j][z] = lists.get(i).get(j).get(z);
                    }
                }
            }
            return ans;
        }

        public static List<Integer> parseListInteger(String input) {
            List<String> strings = parseListString(input);
            ArrayList<Integer> ans = new ArrayList<>();
            if ("[]".equals(input) || "{}".equals(input)) {
                return ans;
            }
            for (String s : strings) {
                try {
                    ans.add(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                }
            }
            return ans;
        }

        public static List<List<Integer>> parseListDoubleInteger(String input) {
            List<List<Integer>> ls = new ArrayList<>();
            if ("[]".equals(input) || "[[]]".equals(input) || "{}".equals(input) || "{{}}".equals(input)) {
                return new ArrayList<>();
            }
            List<List<String>> lists = parseDoubleString(input);
            for (List<String> row : lists) {
                if (row == null) {
                    continue;
                }
                List<Integer> temp = new ArrayList<>();
                for (String s : row) {
                    if (s == null) continue;
                    try {
                        temp.add(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                    }
                }
                ls.add(temp);
            }
            return ls;
        }

        public static List<List<List<Integer>>> parseListThreeInteger(String input) {
            List<List<List<Integer>>> ans = new ArrayList<>();
            List<List<List<String>>> lists = parseThreeString(input);
            for (List<List<String>> list : lists) {
                List<List<Integer>> d = new ArrayList<>();
                if (list == null) continue;
                for (List<String> strings : list) {
                    if (strings == null) continue;
                    List<Integer> t = new ArrayList<>();
                    for (String string : strings) {
                        if (string == null) continue;
                        try {
                            t.add(Integer.parseInt(string));
                        } catch (NumberFormatException e) {
                        }
                    }
                    d.add(t);
                }
                ans.add(d);
            }
            return ans;
        }


        public static List<Character> parseListChar(String input) {
            List<Character> ls = new ArrayList<>();
            List<String> strings = parseListString(input);
            for (String s : strings) {
                ls.add(s.charAt(0));
            }
            return ls;
        }

        public static List<List<Character>> parseListDoubleChar(String input) {
            List<List<String>> lists = parseDoubleString(input);
            List<List<Character>> ls = new ArrayList<>();
            List<Character> temp = null;
            for (List<String> row : lists) {
                temp = new ArrayList<>();
                for (String s : row) {
                    temp.add(s.charAt(0));
                }
                ls.add(temp);
            }
            return ls;
        }

        public static List<List<List<Character>>> parseThreeCharArray(String input) {
            List<List<List<Character>>> ans = new ArrayList<>();
            List<List<List<String>>> lists = parseThreeString(input);
            for (List<List<String>> list : lists) {
                List<List<Character>> d = new ArrayList<>();
                if (list == null) continue;
                for (List<String> strings : list) {
                    if (strings == null) continue;
                    List<Character> t = new ArrayList<>();
                    for (String string : strings) {
                        if (string == null) continue;
                        t.add(string.charAt(0));
                    }
                    d.add(t);
                }
                ans.add(d);
            }
            return ans;
        }

        public static String[] oneStringArray(String input) {
            List<String> ls = parseListString(input);
            String[] ans = new String[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                ans[i] = ls.get(i);
            }
            return ans;
        }

        public static String[][] doubleStringArray(String input) {
            List<List<String>> lists = parseDoubleString(input);
            int row = lists.size();
            String[][] ans = new String[row][];
            for (int i = 0; i < row; i++) {
                ans[i] = new String[lists.get(i).size()];
                for (int j = 0; j < lists.get(i).size(); j++) {
                    ans[i][j] = lists.get(i).get(j);
                }
            }
            return ans;
        }

        public static String[][][] threeStringArray(String input) {
            List<List<List<String>>> lists = parseThreeString(input);
            int row = lists.size();
            String[][][] ans = new String[row][][];
            for (int i = 0; i < row; i++) {
                ans[i] = new String[lists.get(i).size()][];
                for (int j = 0; j < lists.get(i).size(); j++) {
                    ans[i][j] = new String[lists.get(i).get(j).size()];
                    for (int z = 0; z < lists.get(i).get(j).size(); z++) {
                        ans[i][j][z] = lists.get(i).get(j).get(z);
                    }
                }
            }
            return ans;
        }

        public static List<String> parseListString(String input) {
            List<String> ls = new ArrayList<>();
            char[] flag = getFlag(input);
            char startFlag = flag[0];
            char endFlag = flag[1];
            char interruptFlag = flag[2];
            if (!input.contains(String.valueOf(startFlag)) && !input.contains(String.valueOf(endFlag))) {
                ls.add(input);
                return ls;
            }
            String nullStr = new String(new char[]{startFlag, endFlag});
            if (nullStr.equals(input)) return ls;
            StringBuilder sb = null;
            char[] cs = input.toCharArray();
            for (char c : cs) {
                if (c == startFlag) {
                    sb = new StringBuilder();
                    continue;
                }
                if (sb == null) break;
                if (c == endFlag) {
                    ls.add(sb.toString());
                    break;
                } else if (c == interruptFlag) {
                    ls.add(sb.toString());
                    sb = new StringBuilder();
                } else {
                    sb.append(c);
                }
            }
            return ls;
        }

        public static List<List<String>> parseDoubleString(String input) {
            StringBuilder sb = null;
            char[] flag = getFlag(input);
            char startFlag = flag[0];
            char endFlag = flag[1];
            char interruptFlag = flag[2];
            List<List<String>> ls = new ArrayList<>();
            List<String> temp = null;
            Stack<Character> sk = new Stack<>();
            char[] cs = input.toCharArray();
            for (int i = 0; i < cs.length; i++) {
                char c = cs[i];
                if (c == startFlag) {
                    sk.push(c);
                    if (sk.size() == 2) temp = new ArrayList<>();
                } else if (c == endFlag) {
                    if (!sk.isEmpty()) {
                        sk.pop();
                    }
                    if (sk != null && !sk.isEmpty() && temp != null) {
                        if (sb != null) {
                            temp.add(sb.toString());
                        }
                        ls.add(temp);
                    }
                    sb = null;
                    temp = null;
                } else if (c == interruptFlag) {
                    if (temp != null && sb != null) {
                        temp.add(sb.toString());
                        sb = new StringBuilder();
                    }
                } else {
                    if (sb == null) {
                        sb = new StringBuilder();
                    }
                    sb.append(c);
                }
            }
            return ls;
        }

        public static List<List<List<String>>> parseThreeString(String input) {
            List<List<List<String>>> ans = new ArrayList<>();
            char[] charArray = input.toCharArray();
            StringBuilder sb = new StringBuilder();
            boolean f1 = false, f2 = false, f3 = false;
            List<List<String>> d = new ArrayList<>();
            List<String> t = new ArrayList<>();
            Stack<Character> sk = new Stack<>();
            char[] flag = getFlag(input);
            char startFlag = flag[0];
            char endFlag = flag[1];
            char interruptFlag = flag[2];
            char stChar = input.charAt(0);
            for (char c : charArray) {
                if (StringUtils.isIgnore(c, stChar)) continue;
                if (c == startFlag) {
                    sk.push(c);
                    if (sk.size() == 2) {
                        d = new ArrayList<>();
                    } else if (sk.size() == 3) {
                        t = new ArrayList<>();
                    }
                } else if (c == endFlag) {
                    if (!sk.isEmpty()) {
                        sk.pop();
                    }
                    if (sk.size() == 1) {
                        ans.add(d);
                    } else if (sk.size() == 2) {
                        if (sb != null) {
                            t.add(sb.toString());
                            sb = null;
                        }
                        d.add(t);
                    }
                } else if (c == interruptFlag) {
                    if (sb != null) {
                        t.add(sb.toString());
                    }
                    sb = null;
                } else {
                    if (sb == null) {
                        sb = new StringBuilder();
                    }
                    sb.append(c);
                }
            }
            return ans;
        }

        public static void errorInfo(String argType) {
            switch (argType) {
                case "boolean":
                    System.out.println("place input a valid boolean content , example true ");
                    break;
                case "long":
                    System.out.println("place input a valid long number , example 100000000 ");
                    break;
                case "double":
                    System.out.println("place input a valid double number , example 100.0");
                    break;
                case "float":
                    System.out.println("place input a valid float number , example 1.1");
                    break;
                case "int":
                    System.out.println("place input a valid number , example 100,-1 , 0");
                    break;
                case "int[]":
                case "ListNode":
                    System.out.println("place input this format int[] ,example [1,5,4,2,9,9,9]");
                    break;
                case "int[][]":
                    System.out.println("place input this format int[][] ,example [[1,2,-1],[4,-1,6],[7,8,9]]");
                    break;
                case "char":
                    System.out.println("lace input this format char example a ");
                    break;
                case "char[]":
                    System.out.println("place input this format char[] ,example [\"8\",\"3\",\".\",\".\",\"7\",\".\",\".\",\".\",\".\"]");
                    break;
                case "char[][]":
                    System.out.println("place input this format char[][] ,example [[\"8\",\"3\",\".\",\".\",\"7\",\".\",\".\",\".\",\".\"],[\".\",\".\",\".\",\".\",\"8\",\".\",\".\",\"7\",\"9\"]]\n");
                    break;
                case "List":
                case "string[]":
                    System.out.println("place input this format List<String> ,example [\"abbb\",\"ba\",\"aa\"] if content is List<Integer> [1,5,4,2,9,9,9]");
                    break;
                case "string[][]":
                    System.out.println("place input this format string[][],example [[\"abbb\",\"ba\",\"aa\"],[\"ee\",\"dd\",\"cc\"]");
                    break;
                case "TreeNode":
                    System.out.println("place input this format TreeNode,example [3,9,20,null,null,15,7]");
                    break;
                default:
                    System.out.println("unknown support type");
                    break;
            }
        }

        public static char[] getFlag(String input) {
            char st = '\0';
            for (int i = 0; i < input.length(); i++) {
                if ((st = input.charAt(i)) != ' ' && st != '\0') {
                    break;
                }
            }
            char[] flag = new char[4];
            flag[3] = 'Y';
            if (st == '{') {
                flag[0] = '{';
                flag[1] = '}';
                flag[2] = ',';
            } else if (st == '[') {
                flag[0] = '[';
                flag[1] = ']';
                flag[2] = ',';
            } else {
                flag[3] = 'N';

            }
            return flag;
        }

        public static String[] parseConstrunctorClassString(String s) {
            int st = 0, ed = s.length() - 1;
            char start = 0;
            char end = 0;
            char inter = 0;
            char[] flag = ReflectUtils.getFlag(s);
            if (flag[3] == 'Y') {
                start = flag[0];
                end = flag[1];
                inter = flag[2];
                while (st < s.length() && s.charAt(st) != start) {
                    st++;
                }
                while (ed >= 0 && s.charAt(ed) != end) {
                    ed--;
                }
            } else {
                start = '[';
                end = ']';
                inter = ',';
                ed = s.length();
                st = -1;
            }
            int deep = 0;
            List<String> ans = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (int i = st + 1; i < ed; i++) {
                char c = s.charAt(i);
                if (c == start) {
                    deep++;
                    if (sb == null) sb = new StringBuilder();
                    sb.append(c);
                } else if (c == end) {
                    deep--;
                    if (sb != null) {
                        sb.append(c);
                    }
                    if (deep == 0) {
                        ans.add(sb.toString());
                        sb = null;
                    }
                } else if (c == inter) {
                    if (sb != null) {
                        if (deep == 0) {
                            ans.add(sb.toString());
                            sb = null;
                        } else {
                            sb.append(c);
                        }
                    } else {
                        sb = new StringBuilder();
                    }
                } else {
                    if (sb == null) {
                        sb = new StringBuilder();
                    }
                    sb.append(c);
                }
            }
            if (sb != null) {
                ans.add(sb.toString());
            }
            String[] strings = new String[ans.size()];
            for (int i = 0; i < ans.size(); i++) {
                strings[i] = StringUtils.ingoreString(ans.get(i));
            }
            return strings;
        }

        public static void handlerConstructorMethodInput(String arg, List<String> result, Method method) {
            String[] ss = parseConstrunctorClassString(arg);
            for (String s : ss) {
                result.add(s);
            }
        }

        public static void handlerConstructorMethodInput(String arg, List<String> result) {
            handlerConstructorMethodInput(arg, result, null);
        }

        public static void handlerConstructorMethodOutput(String exp, List<String> result, Method method) {
            Class<?> returnType = method.getReturnType();
            String returnName = returnType.getSimpleName();
            if (returnName.contains("[]")
                    || returnName.contains("List")
                    || returnName.contains("ArrayList")
                    || returnName.contains("TreeNode")
                    || returnName.contains("LinkedList")
                    || returnName.contains("ListNode")
                    || returnName.contains("Queue")
            ) {
                result.add(exp);
            } else {
                String s = parseConstrunctorClassString(exp)[0];
                result.add(s);
            }
        }

        public static <T> Class<?> loadOrigin(Class<T> src) {
            if (src == null) {
                return null;
            }
            String name = src.getName();
            if (name.contains("$")) {
                String[] ss = name.split("\\$");
                try {
                    return Class.forName(ss[0]);
                } catch (ClassNotFoundException e) {
                    return src;
                }
            } else {
                return src;
            }
        }

        public static double parseDouble(String d) {
            return parseDouble(Double.parseDouble(d));
        }

        public static double parseDouble(double d) {
            return Double.parseDouble(String.format("%.5f", d));
        }

        public static Object initObjcect(Class<?> src, Object[] args) {
            Object obj = null;
            try {
                obj = src.newInstance();
            } catch (IllegalAccessException e) {
                try {
                    Constructor<?> constructor = src.getDeclaredConstructor((Class<?>) null);
                    constructor.setAccessible(true);
                    obj = constructor.newInstance(args);
                } catch (Exception ex) {
                }
            } catch (Exception e) {
            }
            return obj;
        }

        public static boolean isBaseType(Class<?> c) {
            if (c == null) {
                return true;
            }
            String name = c.getSimpleName();
            if ("char".equals(name) || "Character".equals(name)) {
                return true;
            }
            if ("byte".equalsIgnoreCase(name)) {
                return true;
            }
            if ("Integer".equals(name) || "int".equals(name)) {
                return true;
            }
            if ("boolean".equalsIgnoreCase(name)) {
                return true;
            }
            if ("long".equalsIgnoreCase(name)) {
                return true;
            }
            if ("double".equalsIgnoreCase(name)) {
                return true;
            }
            if ("float".equalsIgnoreCase(name)) {
                return true;
            }
            return false;
        }

        public static int handlerVoidReturnType(Class<?>[] parameterTypes) {
            if (parameterTypes == null) return -1;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!isBaseType(parameterTypes[i])) return i;
            }
            return -1;
        }

        public static int[] getTestCaseInfo(TestCaseGroup annotation) {
            if (annotation == null || !annotation.use()) {
                return new int[]{1, Integer.MAX_VALUE};
            }
            int start = Math.max(1, Math.min(annotation.start(), annotation.end()));
            int end = Math.max(1, Math.max(annotation.start(), annotation.end()));
            return new int[]{start, end};
        }
    }

    public static class TestUtils {
        public static <T> boolean deepEqual(List<T> b, List<T> expect, boolean isStrict) {
            if (expect == b) {
                return true;
            }
            if (expect == null || b == null || expect.size() != b.size()) {

                printDiffInfo(String.valueOf(expect), String.valueOf(b));
                return false;
            }
            int n = expect.size();
            if (isStrict) {
                int idx = -1;
                for (int i = 0; i < n; i++) {
                    T t1 = expect.get(i);
                    T t2 = b.get(i);
                    if (t1 == null || !valid(t1, t2, t1.getClass().getSimpleName(), isStrict)) {
                        idx = i;
                        break;
                    }
                }
                if (idx == -1) {
                    return true;
                } else {
                    printDiffInfo(String.valueOf(expect), String.valueOf(b));
                    return false;
                }
            } else {
                Set<T> aset = new HashSet<>();
                Set<T> bset = new HashSet<>();
                for (int i = 0; i < n; i++) {
                    T t1 = expect.get(i);
                    T t2 = b.get(i);
                    aset.add(t1);
                    bset.add(t2);
                }
                return valid(aset, bset);
            }

        }

        public static <T> boolean deepEqual(T[] a, T[] b, boolean isStrict) {
            if (a == b) {
                return true;
            }
            if (a == null || b == null || a.length != b.length) {
                return false;
            }
            if (isStrict) {
                int n = a.length, x = -1;
                for (int i = 0; i < n; i++) {
                    if (!valid(a[i], b[i], b[i].getClass().getSimpleName(), isStrict)) {
                        x = i;
                        break;
                    }
                }
                if (x != -1) {

                    return false;
                }
                return true;
            } else {
                Set<T> aset = new HashSet<>();
                Set<T> bset = new HashSet<>();
                for (int i = 0; i < a.length; i++) {
                    aset.add(a[i]);
                    bset.add(b[i]);
                }
                return valid(aset, bset);
            }

        }

        public static <T> boolean deepEqual(T[][] a, T[][] b, boolean isStrict) {
            if (a == b) {
                return true;
            }
            if (a == null || b == null || a.length != b.length || a[0].length != b[0].length) {
                return false;
            }
            int m = a.length, n = a[0].length, x = -1, y = -1;
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (!valid(a[i][j], b[i][j], b[i][j].getClass().getSimpleName(), isStrict)) {
                        x = i;
                        y = j;
                        break;
                    }
                }
            }
            if (x == -1) {
                return true;
            } else {
                return false;
            }
        }

        public static boolean deepEqual(ListNode result, ListNode expect) {
            if (result == expect) return true;
            if (result == null || expect == null) return false;
            while (result != null && expect != null) {
                if (result.val != expect.val) {
                    return false;
                }
                result = result.next;
                expect = expect.next;
            }
            if (result != expect) {
                System.out.println("ListNode length not equal");
                return false;
            }
            return true;
        }

        public static boolean deepEqual(TreeNode result, TreeNode expect) {
            if (result == expect) return true;
            if (result == null || expect == null) return false;
            Deque<TreeNode> rq = new ArrayDeque<>();
            Deque<TreeNode> eq = new ArrayDeque<>();
            rq.add(result);
            eq.add(expect);
            while (!rq.isEmpty() && !eq.isEmpty()) {
                int s1 = rq.size();
                int s2 = eq.size();
                if (s1 != s2) {
                    return false;
                }
                int size = s1;
                while (size > 0) {
                    size--;
                    TreeNode rNode = rq.poll();
                    TreeNode eNode = eq.poll();
                    if (rNode == null || eNode == null) {
                        return false;
                    }
                    if (rNode.val != eNode.val) {
                        return false;
                    }

                    if (rNode.left != null) {
                        rq.add(rNode.left);
                    }
                    if (rNode.right != null) {
                        rq.add(rNode.right);
                    }

                    if (eNode.left != null) {
                        eq.add(eNode.left);
                    }
                    if (eNode.right != null) {
                        eq.add(eNode.right);
                    }

                }
            }
            return rq.size() == eq.size();
        }

        public static <T> boolean deepEqual(T[][][] a, T[][][] b, boolean isStrict) {
            if (a == b) {
                return true;
            }
            if (a == null || b == null || a.length != b.length || a[0].length != b[0].length || a[0][0].length != b[0][0].length) {
                System.out.println("error length not equal");
                return false;
            }
            int m = a.length, n = a[0].length, z = a[0][0].length;
            boolean f = true;
            int x = -1, y = -1, o = -1;
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < z; k++) {
                        if (a[i][j][k] == b[i][j][k]) {
                            continue;
                        }
                        if (!valid(a[i][j][k], b[i][j][k], b[i][j][k].getClass().getSimpleName(), isStrict)) {
                            x = i;
                            y = j;
                            o = k;
                            break;
                        }
                    }
                }
            }
            if (x == -1) {
                return true;
            } else {
                return false;
            }
        }

        public static boolean valid(Object result, Object expect, String returnType, boolean isStrict) {
            return valid(result, expect, returnType, isStrict, false);
        }

        public static boolean valid(Object result, Object expect, String returnType, boolean isStrict, boolean isPrintInfo) {
            if (result == expect) {
                return true;
            }
            if (result == null) {
                printDiffInfo(String.valueOf(expect), "null", isPrintInfo);
                return false;
            }
            if (returnType == null) {
                System.out.println(CustomColor.error("not support return type is null"));
                return false;
            }
            boolean ok = false;
            try {
                switch (returnType) {
                    case "int[]": {
                        Integer[] e = covert((int[]) expect);
                        Integer[] r = covert((int[]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.toString(e), Arrays.toString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "int[][]": {
                        Integer[][] e = covert((int[][]) expect);
                        Integer[][] r = covert((int[][]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.deepToString(e), Arrays.deepToString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "int[][][]": {
                        Integer[][][] e = covert((int[][][]) expect);
                        Integer[][][] r = covert((int[][][]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.deepToString(e), Arrays.deepToString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "Long[]":
                    case "long[]": {
                        Long[] e = covert((long[]) expect);
                        Long[] r = covert((long[]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.toString(e), Arrays.toString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "Long[][]":
                    case "long[][]": {
                        Long[][] e = covert((long[][]) expect);
                        Long[][] r = covert((long[][]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.deepToString(e), Arrays.deepToString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "Double":
                    case "double": {
                        double r = ReflectUtils.parseDouble(String.valueOf(result));
                        double e = ReflectUtils.parseDouble(String.valueOf(expect));
                        ok = r == e;
                        if (!ok) {
                            System.out.println("Expect:" + e);
                            System.out.println("Result:" + CustomColor.error(r));
                        }
                        return ok;
                    }
                    case "double[]": {
                        Double[] e = covert((double[]) expect);
                        Double[] r = covert((double[]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.toString(e), Arrays.toString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "double[][]": {
                        Double[][] e = covert((double[][]) expect);
                        Double[][] r = covert((double[][]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.deepToString(e), Arrays.deepToString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "float[]": {
                        Float[] e = covert((float[]) expect);
                        Float[] r = covert((float[]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.toString(e), Arrays.toString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "String[]": {
                        String[] r = (String[]) result;
                        String[] e = (String[]) expect;
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.toString(e), Arrays.toString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "String[][]":
                        ok = deepEqual((String[][]) result, (String[][]) expect, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.deepToString((String[][]) expect), Arrays.deepToString((String[][]) result), isPrintInfo);
                        }
                        return ok;
                    case "String[][][]":
                        ok = deepEqual((String[][][]) result, (String[][][]) expect, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.deepToString((String[][][]) expect), Arrays.deepToString((String[][][]) result), isPrintInfo);
                        }
                        return ok;
                    case "char[]": {
                        Character[] e = covert((char[]) expect);
                        Character[] r = covert((char[]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.toString(e), Arrays.toString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "char[][]": {
                        Character[][] e = covert((char[][]) expect);
                        Character[][] r = covert((char[][]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.deepToString(e), Arrays.deepToString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "char[][][]": {
                        Character[][][] e = covert((char[][][]) expect);
                        Character[][][] r = covert((char[][][]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.deepToString(e), Arrays.deepToString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "boolean[]": {
                        Boolean[] e = covert((boolean[]) expect);
                        Boolean[] r = covert((boolean[]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.toString(e), Arrays.toString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "boolean[][]": {
                        Boolean[][] e = covert((boolean[][]) expect);
                        Boolean[][] r = covert((boolean[][]) result);
                        ok = deepEqual(r, e, isStrict);
                        if (!ok) {
                            printDiffInfo(Arrays.deepToString(e), Arrays.deepToString(r), isPrintInfo);
                        }
                        return ok;
                    }
                    case "TreeNode": {
                        TreeNode e = (TreeNode) expect;
                        TreeNode r = (TreeNode) result;
                        ok = deepEqual(r, e);
                        return ok;
                    }
                    case "ListNode": {
                        ListNode e = (ListNode) expect;
                        ListNode r = (ListNode) result;
                        ok = deepEqual(r, e);
                        if (!ok) {
                            printDiffInfo(ListNode.print(e), ListNode.print(r), isPrintInfo);
                        }
                        return ok;
                    }

                    case "List":
                    case "ArrayList":
                        return deepEqual((ArrayList<Object>) result, (ArrayList<Object>) expect, isStrict);
                    default:
                        boolean t = expect != null && expect.equals(result);
                        boolean isArray = expect != null && expect.getClass().getSimpleName().contains("[]");
                        if (isArray) {
                            t = Arrays.deepEquals((Object[]) result, (Object[]) expect);
                            if (!t) {
                                printDiffInfo(Arrays.deepToString((Object[]) expect), Arrays.deepToString((Object[]) result), isPrintInfo);
                            }
                        } else {
                            if (!t) {
                                printDiffInfo(String.valueOf(expect), String.valueOf(result), isPrintInfo);
                            }
                        }
                        return t;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public static Boolean[] covert(boolean[] a) {
            Boolean[] t = new Boolean[a.length];
            for (int i = 0; i < a.length; i++) {
                t[i] = a[i];
            }
            return t;
        }

        public static Boolean[][] covert(boolean[][] a) {
            Boolean[][] t = new Boolean[a.length][a[0].length];
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[0].length; j++) {
                    t[i][j] = a[i][j];
                }
            }
            return t;
        }

        public static Integer[] covert(int[] a) {
            Integer[] t = new Integer[a.length];
            for (int i = 0; i < a.length; i++) {
                t[i] = a[i];
            }
            return t;
        }

        public static Long[] covert(long[] a) {
            Long[] t = new Long[a.length];
            for (int i = 0; i < a.length; i++) {
                t[i] = a[i];
            }
            return t;
        }

        public static Long[][] covert(long[][] a) {
            Long[][] t = new Long[a.length][a[0].length];
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[0].length; j++) {
                    t[i][j] = a[i][j];
                }
            }
            return t;
        }

        public static Float[] covert(float[] a) {
            Float[] t = new Float[a.length];
            for (int i = 0; i < a.length; i++) {
                t[i] = a[i];
            }
            return t;
        }

        public static Double[] covert(double[] a) {
            Double[] t = new Double[a.length];
            for (int i = 0; i < a.length; i++) {
                t[i] = a[i];
            }
            return t;
        }

        public static Double[][] covert(double[][] a) {
            Double[][] t = new Double[a.length][a[0].length];
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[0].length; j++) {
                    t[i][j] = a[i][j];
                }
            }
            return t;
        }

        public static Character[] covert(char[] a) {
            Character[] t = new Character[a.length];
            for (int i = 0; i < a.length; i++) {
                t[i] = a[i];
            }
            return t;
        }

        public static Integer[][] covert(int[][] a) {
            int m = a.length, n = a[0].length;
            Integer[][] t = new Integer[m][n];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    t[i][j] = a[i][j];
                }
            }
            return t;
        }

        public static Character[][] covert(char[][] a) {
            int m = a.length, n = a[0].length;
            Character[][] t = new Character[m][n];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    t[i][j] = a[i][j];
                }
            }
            return t;
        }

        public static Integer[][][] covert(int[][][] a) {
            int m = a.length, n = a[0].length, z = a[0][0].length;
            Integer[][][] t = new Integer[m][n][z];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < z; k++) {
                        t[i][j][k] = a[i][j][k];
                    }
                }
            }
            return t;
        }

        public static Character[][][] covert(char[][][] a) {
            int m = a.length, n = a[0].length, z = a[0][0].length;
            Character[][][] t = new Character[m][n][z];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < z; k++) {
                        t[i][j][k] = a[i][j][k];
                    }
                }
            }
            return t;
        }

        public static <T> boolean valid(Set<T> aset, Set<T> bset) {
            if (aset == bset) {
                return true;
            }
            if (aset.size() != bset.size()) {
                return false;
            }
            int cnt = 0;
            for (T a : aset) {
                if (bset.contains(a)) {
                    cnt++;
                } else {
                    return false;
                }
            }
            return cnt == aset.size();
        }

        public static void printDiffInfo(String e, String r) {
            printDiffInfo(e, r, true);
        }

        public static void printDiffInfo(String e, String r, boolean isPrintInfo) {
            if (!isPrintInfo) return;
            StringBuilder rb = new StringBuilder();
            StringBuilder eb = new StringBuilder();
            int m = r.length(), n = e.length();
            if (("[]".equals(r) && !"[]".equals(e)) || ("{}".equals(r) && !"{}".equals(e)) || ("null".equals(r) && !"null".equals(e))) {
                // rb.append(CustomColor.error(r));
                rb.append(r);
                eb.append(e);
            } else {
                for (int i = 0, j = 0; i < m || j < n; ++i, ++j) {
                    for (; i < m && isIgnore(r.charAt(i)); i++) {
                        if (StringUtils.isIgnore(r.charAt(i))) continue;
                        rb.append(r.charAt(i));
                    }
                    for (; j < n && isIgnore(e.charAt(j)); j++) {
                        if (StringUtils.isIgnore(e.charAt(j))) continue;
                        eb.append(e.charAt(j));
                    }
                    if (i >= m && j >= n) {
                        break;
                    }
                    StringBuilder temp1 = new StringBuilder();
                    StringBuilder temp2 = new StringBuilder();

                    while (i < m && !isIgnore(r.charAt(i))) {
                        temp1.append(r.charAt(i));
                        i++;
                    }
                    while (j < n && !isIgnore(e.charAt(j))) {
                        temp2.append(e.charAt(j));
                        j++;
                    }
                    String result = temp1.toString();
                    String expectResult = temp2.toString();
                    boolean isEquals = (StringUtils.isEmpty(result) && StringUtils.isEmpty(expectResult)) || (!StringUtils.isEmpty(expectResult) && expectResult.equals(result));
                    boolean deleteDot = false;
                    boolean isDot = false;
                    if (!isEquals) {
                        if (result.length() == 0) {
                            result = "\"\"";
                        } else {
                            isDot = result.charAt(result.length() - 1) == ',';
                            if (isDot) {
                                result = result.substring(0, result.length() - 1);
                            }
                        }
                    }
                    // rb.append(isEquals ? result : CustomColor.error(result));
                    rb.append(result);
                    if (isDot) {
                        rb.append(",");
                    }
                    eb.append(expectResult);
                    if (i < m) {
                        rb.append(r.charAt(i));
                        rb.append("");
                    }
                    if (j < n) {
                        eb.append(e.charAt(j));
                        eb.append("");
                    }
                }
            }
            System.out.println("Expect: " + eb.toString());
            System.out.println("Result: " + rb.toString());
        }

        public static boolean isIgnore(char c) {
            return StringUtils.isIgnore(c) || c == '[' || c == ']' || c == '{' || c == '}';
        }
    }

    public static class CustomColor {
        private static final int RGB_VALUE_MAX = 255;
        private static final int BACKGROUND_COLOR = 48;
        private static final int FONT_COLOR = 38;
        private static final String CLEAR_COLOR = "\u001B[0m";

        public static class MyColor {
            public int R;
            public int G;
            public int L;

            public MyColor(int r, int g, int l) {
                this.R = r;
                this.G = g;
                this.L = l;
            }

            public MyColor() {
                this.R = randomColorValue();
                this.G = randomColorValue();
                this.L = randomColorValue();
            }
        }

        public static int randomColorValue() {
            return (int) Math.floor(Math.random() * RGB_VALUE_MAX);
        }

        public static String fontColor(Object... content) {
            int R = randomColorValue();
            int G = randomColorValue();
            int L = randomColorValue();
            return fontColor(R, G, L, content);
        }

        public static String fontColor(MyColor myColor, Object... content) {
            return fontColor(myColor.R, myColor.G, myColor.L, content);
        }

        public static String fontColor(int R, int G, int L, Object... content) {
            return colorTemplate(FONT_COLOR, R, G, L, content);
        }

        public static String backgroundColor(Object... content) {
            int R = randomColorValue();
            int G = randomColorValue();
            int L = randomColorValue();
            return backgroundColor(R, G, L, content);
        }

        public static String backgroundColor(MyColor myColor, Object... content) {
            return backgroundColor(myColor.R, myColor.G, myColor.L, content);
        }

        public static String backgroundColor(int R, int G, int L, Object... content) {
            return colorTemplate(BACKGROUND_COLOR, R, G, L, content);
        }

        public static String colorTemplate(int type, int R, int G, int L, Object... content) {
            valid(R, G, L);
            StringBuilder s = new StringBuilder();
            for (Object s1 : content) {
                s.append(s1);
            }
            return String.format("\u001B[%d;2;%d;%d;%dm%s\u001B[0m", type, R, G, L, s);
        }

        public static String colorTemplate(int R1, int G1, int L1, int R2, int G2, int L2, String... content) {
            valid(R1, G1, L1, R2, G2, L2);
            StringBuilder s = new StringBuilder();
            for (String s1 : content) {
                s.append(s1);
            }
            return String.format("\u001B[%s;2;%d;%d;%dm\u001B[%s;2;%d;%d;%dm%s\u001B[0m", BACKGROUND_COLOR, R1, G1, L1, FONT_COLOR, R2, G2, L2, s.toString());
        }

        public static String colorTemplate(MyColor bc, MyColor fc, String... content) {
            return colorTemplate(bc.R, bc.G, bc.L, fc.R, fc.G, fc.L, content);
        }

        public static void valid(MyColor... myColors) {
            for (MyColor myColor : myColors) {
                valid(myColor.R, myColor.G, myColor.L);
            }
        }

        public static void valid(int... ints) {
            for (int result : ints) {
                if (result > RGB_VALUE_MAX) {
                    throw new RuntimeException("color value max is " + RGB_VALUE_MAX);
                }
            }
        }

        public static String success(Object... content) {
            return fontColor(89, 168, 105, content);
        }

        public static String pink(Object... content) {
            return fontColor(253, 54, 110, content);
        }

        public static String error(Object... content) {
            return fontColor(253, 54, 110, content);
        }

        public static void printSuccess(Object... content) {
            System.out.println(success(content));
        }
    }

    public static class TestData {
        public String info;
        public int[] testCaseGroup;
        Method method;
        Class<?> src;
        Class<?> origin;

        public TestData(Class<?> src, Class<?> origin) {
            this(null, src, src);
        }

        public TestData(Class<?> src) {
            this(null, src, src);
        }

        public TestData(Method method, Class<?> origin, Class<?> src) {
            this.method = method;
            this.origin = origin;
            this.src = src;
            this.process();
        }

        public void process() {
            this.info = this.getDescInfo();
            this.testCaseGroup = this.getTestCaseInfo();
        }

        public String getDescInfo() {
            return "";
        }

        public int[] getTestCaseInfo() {
            TestCaseGroup declaredAnnotation = method != null ? method.getDeclaredAnnotation(TestCaseGroup.class) : null;
            if (declaredAnnotation != null) {
                return ReflectUtils.getTestCaseInfo(declaredAnnotation);
            }
            declaredAnnotation = origin != null ? origin.getDeclaredAnnotation(TestCaseGroup.class) : null;
            if (declaredAnnotation != null) {
                return ReflectUtils.getTestCaseInfo(declaredAnnotation);
            }
            declaredAnnotation = src != null ? src.getDeclaredAnnotation(TestCaseGroup.class) : null;
            if (declaredAnnotation != null) {
                return ReflectUtils.getTestCaseInfo(declaredAnnotation);
            }
            return new int[]{1, 0x3fffff};
        }
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestCaseGroup {
        int start() default 1;

        int end() default 0x3f3f3f;

        boolean use() default true;
    }

    public static class ListNode {
        public int val;
        public ListNode next;

        public ListNode(int val) {
            this(val, null);
        }

        public ListNode(int val, ListNode listNode) {
            this.val = val;
            this.next = listNode;
        }

        public static ListNode createListNode(int... args) {
            ListNode temp = new ListNode(-1);
            ListNode cur = temp;
            for (int val : args) {
                cur.next = new ListNode(val);
                cur = cur.next;
            }
            return temp.next;
        }

        public static String print(ListNode node) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            while (node != null) {
                sb.append(node.val);
                if (node.next != null) {
                    sb.append(", ");
                }
                node = node.next;
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static class TreeNode {
        public int val;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int val) {
            this(val, null, null);
        }

        public TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", TreeNode.class.getSimpleName() + "[", "]")
                    .add("val=" + val)
                    .toString();
        }


        public static TreeNode widthBuildTreeNode(String[] ls) {
            if (ls == null || ls.length == 0 || ls[0] == null || isNullNode(ls[0])) {
                return null;
            }
            Deque<TreeNode> q = new ArrayDeque<>();
            TreeNode root = new TreeNode(Integer.parseInt(ls[0]));
            q.add(root);
            int i = 1;
            while (i < ls.length && !q.isEmpty()) {
                int s = q.size();
                while (s > 0 && i < ls.length) {
                    s--;
                    TreeNode node = q.poll();
                    if (node == null) continue;
                    if (!isNullNode(ls[i])) {
                        TreeNode left = new TreeNode(Integer.parseInt(ls[i]));
                        node.left = left;
                        q.add(left);
                    }
                    i++;
                    if (i >= ls.length) break;
                    if (!isNullNode(ls[i])) {
                        TreeNode right = new TreeNode(Integer.parseInt(ls[i]));
                        node.right = right;
                        q.add(right);
                    }
                    i++;
                }
            }
            return root;
        }

        private static boolean isNullNode(String s) {
            return s == null || s.length() == 0 || "null".equals(s) || "#".equals(s);
        }
    }
}
