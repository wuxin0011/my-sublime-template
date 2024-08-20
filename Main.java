
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        int T;
        T = 1;
        while(T>0) {
            solve();
            T--;
        }
        close();
    }

    static int MAXN = (int)1e5+1;
    static int MAXC = 401;

    static int[] a = new int[MAXN];
    static int[] cost = new int[MAXN];
    static long[] f = new long[MAXN];

    public static void solve() {
        int n = read();
        int m = read();
        for(int i = 0;i<n;i++) {
            a[i] = read();
            cost[i] = read();
        }
        Arrays.fill(f,0,m+1,0);
        for(int i = 0;i<n;i++) {
            for(int c = m;c >= a[i];c--) {
                 f[c] = Math.max(f[c],f[c-a[i]]+cost[i]*1L);
            }
        }
        pln(f[m]);
    }
    public static final String DEFAULT_FILE_NAME = "in.txt"; // default read file
    public static final boolean DELETE_CLOSE = false; // auto close ?
    public static BufferedInputStream br = null;
    public static PrintWriter ptr = null;

    static {
        try {
            File file = new File(DEFAULT_FILE_NAME);
            br = file.exists() ? new BufferedInputStream(new FileInputStream(file)) : br;
        } catch (Exception e) {

        }
        initIO(false);
    }

    public static void initIO(boolean flushIO) {
        br = flushIO || br == null ? new BufferedInputStream(System.in) : br;
        ptr = flushIO || ptr == null ? new PrintWriter(new BufferedOutputStream(System.out)) : ptr;
    }

    public static int read() {
         try {
            int c = br.read();
            int f = 1;
            int x = 0;
            while (c < '0' || c > '9') {
                if (c == '-') {
                    f = -1;
                }
                c = br.read();
            }
            while (c >= '0' && c <= '9') {
                x = x * 10 + (c - '0');
                c = br.read();
            }
            return x * f;
        } catch (IOException e) {
            System.err.println("read Error,place check your input is number !");
            return -1;
        }
    }

    static long readLong() {
        try {
            int c = br.read();
            int f = 1;
            long x = 0;
            while (c < '0' || c > '9') {
                if (c == '-') {
                    f = -1;
                }
                c = br.read();
            }
            while (c >= '0' && c <= '9') {
                x = x * 10 + (c - '0');
                c = br.read();
            }
            return x * f;
        } catch (IOException e) {
            System.err.println("read Error,place check your input is number !");
            return -1;
        }
    }

    public static void pln(Object obj) {
        ptr.println(obj);
    }

    public static void print(Object obj) {
        ptr.print(obj);
    }

    public static void close() {
        try {
            if (br != null)
                br.close();

            if (ptr != null) {
                ptr.flush();
                ptr.close();
            }
            if (DELETE_CLOSE) {
                br = null;
                ptr = null;
            }
        } catch (Exception ignore) {

        }
    }

    // --------------------------------------------string-----------------------------------------------------------

    // MAX string SK
    public static int MAX_STRING_LENGTH = (int) 1e5 + 1;

    public static char[] STRING_ARRAY = null;

    public static String readLine() {
        return readString(true);
    }

    public static String readString() {
        return readString(false);
    }

    public static String readString(boolean line) {
        try {
            if (STRING_ARRAY == null) {
                STRING_ARRAY = new char[MAX_STRING_LENGTH];
            }
            int x = br.read();
            if (x == -1) {
                System.err.println("No Any Input !");
                return "";
            }
            while (ignore(x)) {
                x = br.read();
            }
            int curSisze = 0;
            while (line ? (x == ' ' || !ignore(x)) : (x != ' ' && !ignore(x))) {
                STRING_ARRAY[curSisze++] = (char) x;
                x = br.read();
            }
            return new String(STRING_ARRAY, 0, curSisze);
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean ignore(int c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\b' || c == '\f' || c == -1;
    }

    public static boolean readBoolean() {
        return "true".equalsIgnoreCase(readString());
    }

    public static double readDouble() {
        return Double.parseDouble(readString());
    }

    public static float readFloat() {
        return Float.parseFloat(readString());
    }

   


}