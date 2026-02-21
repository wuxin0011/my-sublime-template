using ll = long long;
struct Factorial {
    std::vector<ll> f,g;
    int mod;                     
    ll pow(ll a, int n) {
        ll res = 1LL;
        while (n > 0) {
            if (n & 1) {
                res = res * a % mod;
            }
            a = a * a % mod;
            n >>= 1;
        }
        return res;
    }
    Factorial(int N, int mod) : mod(mod) {
        N += 2;
        f.resize(N);
        g.resize(N);
        f[0] = 1;
        for (int i = 1; i < N; i++) {
            f[i] = f[i - 1] * i % mod;
        }
        g[N - 1] = pow(f[N - 1], mod - 2);
        for (int i = N - 2; i >= 0; i--) {
            g[i] = g[i + 1] * (i + 1) % mod;
        }
    }
    ll fac(int n) {
        return f[n];
    }
    ll fac_inv(int n) {
        return g[n];
    }
    ll comb(int n, int m) {
        if (n < m || m < 0 || n < 0) {
            return 0;
        }
        return (f[n] * g[m] % mod) * g[n - m] % mod;
    }
    ll permu(int n, int m) {
        if (n < m || m < 0 || n < 0) {
            return 0;
        }
        return f[n] * g[n - m] % mod;
    }
    ll catalan(int n) {
        return (comb(2 * n, n) - comb(2 * n, n - 1) + mod) % mod;
    }
    ll inv(int n) {
        return f[n - 1] * g[n] % mod;
    }
};