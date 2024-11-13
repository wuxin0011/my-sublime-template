import java.util.*; 
import java.io.*; 

/*
@testcase-start

@testcase-end
*/

public class LC_02 {
    public static void main(String[] args) {

         _LCUtil.run(LC_02.class,"findMax","../in/leetcode/LC_02-in.txt");
         // _LCUtil.run(LC_02.class,"null","__TEST_CASE__");
	}


    public static int findMax(int[] a) {
        int ans = a[0];
        for(int x : a) ans = Math.max(ans,x);
        return ans;
    }
}
