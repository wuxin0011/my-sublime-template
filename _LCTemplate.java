
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class _LCTemplate {
    
    private static String root_dir = null;
    private static final File file = new File("");
    private static final String compileDir = "bin";
    private static final String FilePrefix = "LC_";
    private static final String LC_UTIL_NAME = "_LCUtil";

    static {
        String path = file.getAbsolutePath();
        if(path.contains(compileDir)){
            path = path.replace(compileDir,"");
        }
        root_dir = path;
        checkCode00();
    }

    public static void main(String[] args) {
        int cnt = countJavaFile();
        String fileName =FilePrefix +(cnt < 10 ? ("0" + cnt):String.valueOf(cnt)) + ".java";
        String javaFilePath = root_dir + fileName;
        String template = content(fileName);
        createInputAndOutputFile(javaFilePath);
        readContent(template,javaFilePath);
    }


    public static int countJavaFile(){
        int ans = 0;
        File f = new File(root_dir);
        File[] fs = f.listFiles();
        for(File temp : fs ){
            if(temp.getAbsolutePath().endsWith(".java") && temp.getAbsolutePath().contains(FilePrefix)){
                ans++;
            }
        }
        return ans + 1;
    }


    public static void readContent(String content,String javaFilePath){
        try{
            File file = new File(javaFilePath);
            if(!file.exists()){
                file.createNewFile();
            }

            BufferedWriter w = new BufferedWriter(new FileWriter(file));
            w.write(content,0,content.length());
            w.flush();
            w.close();
            System.out.println(javaFilePath + " create success !");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void printMethodInfo(Class<?> c){
        Method[] mts  = c.getDeclaredMethods();
        for(Method m : mts){
            System.out.println("\n"+m+"\n");
        }
    }

    public static String content(String className){
        if(className == null || className.length() == 0){
            throw new NullPointerException("className is null");
        }
        if(className.endsWith(".java")){
            className = className.replace(".java","");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("import java.util.*; \n");
        sb.append("import java.io.*; \n\n");
        sb.append("public class ").append(className).append(" {");
        sb.append("\n    public static void main(String[] args) {\n\n");
        sb.append("         ");
        sb.append(LC_UTIL_NAME);
        sb.append(".run(");
        sb.append(className);
        sb.append(".class");
        sb.append(",");
        sb.append("\"null\"");
        sb.append(",");
        sb.append("\"");
        sb.append("../in/");
        sb.append(className);
        sb.append("/in.txt");
        sb.append("\"");
        sb.append(");\n");
        sb.append("\t}\n");
        sb.append("}");
        return sb.toString();

    }


    public static void createInputAndOutputFile(String javaFilePath) {
        try{
            File file = new File(javaFilePath);
            String fileName = file.getName().replace(".java","");
            String path = root_dir + File.separator + "in" + File.separator  + fileName + File.separator;
            createNewFile(path +  "in.txt");
            // createNewFile(path +  "out.txt");
            // createNewFile(path +  "temp.txt");
        }catch(Exception e) {
            // ignore
        }
    }

    public static void createNewFile(String path) {
        try{
            
            File f = new File(path);
            File p = new File(f.getParent());

            if(!p.exists()) {
                p.mkdirs();
            }

            if(!f.exists()){
                f.createNewFile();
            }
        }catch(Exception e) {
            // ignore
        }
    }



    // check code00 
    public static void checkCode00() {
        try{

            String path = root_dir + "in" + File.separator + _LCTemplate.class.getSimpleName() + File.separator;
            createNewFile(path +  "in.txt");
            createNewFile(path +  "out.txt");
            createNewFile(path +  "temp.txt");
        }catch(Exception e) {
            // ignore
        }
    }


}
