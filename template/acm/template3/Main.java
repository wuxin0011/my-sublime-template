package template.acm.template3;

/**
 * @author: wuxin0011
 * @Description:
 */
import java.io.*;

public class Main{
    public static void main(String[] args) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
        String s = in.readLine();
        out.println(s);
        out.flush();
        out.close();
    }
}

