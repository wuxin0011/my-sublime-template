

public class QualityFactor {
	public static void main(String[] args) {
		for(int i = 0;i < 100;i++) {
			QualityFactor(i);
		}
	}

	public static void Qualityfactor(int t) {
		for(int k = 1; k * k <= t;k++) {
			if(t % k != 0) {
				continue;
			}
			System.out.printf("%s*%s = %s\n", k,t / k,t);
		}
	}
}