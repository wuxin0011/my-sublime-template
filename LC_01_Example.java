import java.util.*; 
import java.io.*; 

/*
@testcase-start

[100,200,10,100000]
100000

[100,200,10,10]
200

@testcase-end
*/

public class LC_01_Example {
    public static void main(String[] args) {

         // _LCUtil.run(LC_01.class,"null","../in/LC_01_Example/in.txt");
         _LCUtil.run(LC_01_Example.class,"findMax","__TEST_CASE__");
	}


    static int findMax(int[] a) {
        int ans = a[0];
        for(int x : a) {
            ans = Math.max(ans,x);
        }
        return ans;
    }
}
