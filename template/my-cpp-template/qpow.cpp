constexpr int MOD = 1e9 + 7;
using ll = long long;

int sqrt(int m) {
    int i = 1;
    for(;i * i <= m;i++) {
    }
    return i;
}

ll qpow(ll x,ll n) {
    ll ans = 1;
    for(;n > 0;n >>= 1){
        if(n&1)ans = ans * x % MOD;
        x = x * x % MOD;
    }
    return ans;
}

ll calc(ll x,ll v) {
   v = x * v % MOD;
   if(v < 0){
      v += MOD;
   }
   return v % MOD;
}

ll inv(ll x) {
    return qpow(x,MOD-2);
}
