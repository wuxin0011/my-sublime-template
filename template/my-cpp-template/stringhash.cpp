
// 字符串哈希模板开始

using ll = long long;

ll base0, mod0;
int inited_mod = 0;
class StringHash {
private:
    ll mod, base;
    vector<ll> h, p;
    vector<ll> rev_h;
    int n;

public:
    StringHash(const string& s) {
        if (!inited_mod) {
            base0 = rand() % (100 - 26 + 1) + 26;
            ll mod_min = 1e9 + 7;
            ll mod_max = (1LL << 31) - 1;
            mod0 = mod_min + rand() % (mod_max - mod_min + 1);
        }
        inited_mod = 1;
        this->base = base0;
        this->mod = mod0;
        this->n = s.size();
        h.resize(n + 1, 0);
        p.resize(n + 1, 1);
        for (int i = 1; i <= n; ++i) {
            h[i] = (h[i - 1] * base + 1LL * s[i - 1]) % mod;
            if (h[i] < 0)
                h[i] = (h[i] % mod + mod) % mod;
            p[i] = (p[i - 1] * base) % mod;
            if (p[i] < 0)
                p[i] = (p[i] % mod + mod) % mod;
        }
        rev_h.resize(n + 1, 0);
        for (int i = 1; i <= n; ++i) {
            rev_h[i] = (rev_h[i - 1] * base + 1LL * s[n - i]) % mod;
            if (rev_h[i] < 0)
                rev_h[i] = (rev_h[i] % mod + mod) % mod;
        }
    }

    ll get(int l, int r, bool is_rev = false) {
        if (is_rev) {
            ll res =(rev_h[n - l] - rev_h[n - r - 1] * p[r - l + 1] % mod) % mod;
            return res < 0 ? (res % mod + mod) % mod : res;
        } else {
            ll res = (h[r + 1] - h[l] * p[r - l + 1] % mod) % mod;
            return res < 0 ? (res % mod + mod) % mod : res;
        }
    }

    bool is_palindrome(int l, int r) {
        return l >= r || (get(l, r, false) == get(l, r, true));
    }
};
// 字符串哈希模板结束

