import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StreamTokenizer;

public class Test {

	public static int MAXN = 100001;

	public static long[] arr = new long[MAXN];

	public static long[] sum = new long[MAXN << 2];

	public static long[] max = new long[MAXN << 2];

	public static void up(int i) {
		sum[i] = sum[i << 1] + sum[i << 1 | 1];
		max[i] = Math.max(max[i << 1], max[i << 1 | 1]);
	}

	public static void build(int l, int r, int i) {
		if (l == r) {
			sum[i] = arr[l];
			max[i] = arr[l];
		} else {
			int mid = (l + r) >> 1;
			build(l, mid, i << 1);
			build(mid + 1, r, i << 1 | 1);
			up(i);
		}
	}

	public static void sqrt(int jobl, int jobr, int l, int r, int i) {
		if (l == r) {
			long sqrt = (long) Math.sqrt(max[i]);
			sum[i] = sqrt;
			max[i] = sqrt;
		} else {
			int mid = (l + r) >> 1;
			if (jobl <= mid && max[i << 1] > 1) {
				sqrt(jobl, jobr, l, mid, i << 1);
			}
			if (jobr > mid && max[i << 1 | 1] > 1) {
				sqrt(jobl, jobr, mid + 1, r, i << 1 | 1);
			}
			up(i);
		}
	}

	public static long query(int jobl, int jobr, int l, int r, int i) {
		if (jobl <= l && r <= jobr) {
			return sum[i];
		}
		int mid = (l + r) >> 1;
		long ans = 0;
		if (jobl <= mid) {
			ans += query(jobl, jobr, l, mid, i << 1);
		}
		if (jobr > mid) {
			ans += query(jobl, jobr, mid + 1, r, i << 1 | 1);
		}
		return ans;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StreamTokenizer in = new StreamTokenizer(br);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		in.nextToken();
		int n = (int) in.nval;
		for (int i = 1; i <= n; i++) {
			in.nextToken();
			arr[i] = (long) in.nval;
		}
		build(1, n, 1);
		in.nextToken();
		int m = (int) in.nval;
		for (int i = 1, op, jobl, jobr, tmp; i <= m; i++) {
			in.nextToken();
			op = (int) in.nval;
			in.nextToken();
			jobl = (int) in.nval;
			in.nextToken();
			jobr = (int) in.nval;
			if (jobl > jobr) {
				tmp = jobl;
				jobl = jobr;
				jobr = tmp;
			}
			if (op == 0) {
				sqrt(jobl, jobr, 1, n, 1);
			} else {
				out.println(query(jobl, jobr, 1, n, 1));
			}
		}
		out.flush();
		out.close();
		br.close();
	}

}
