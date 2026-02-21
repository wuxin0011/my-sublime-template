#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// 容斥原理预处理模板 开始
vector<ll> lcm_val,lcm_a;
vector<int> bits;


ll gcd0(ll a, ll b) {
    while (b) {
        ll t = b;
        b = a % b;
        a = t;
    }
    return a;
}

ll lcm0(ll a, ll b) {
    if (a == 0 || b == 0) return 0;
    return a / gcd0(a, b) * b;
}

void init(ll max_x,vector<ll> a){
    int n = a.size();
    int S = 1 << n;
    lcm_val.resize(S,1);
    bits.resize(S, 0);
    lcm_a = a;
    
    for (int mask = 1; mask < S; ++mask) {
        int lsb = mask & -mask;
        int i = __builtin_ctz(lsb);
        int prev = mask ^ lsb;
        bits[mask] = bits[prev] + 1;
        lcm_val[mask] = lcm0(lcm_val[prev], a[i]);
        if (lcm_val[mask] > max_x) {
            lcm_val[mask] = max_x + 1;
        }
    }

}



ll calc_x(ll x) {
    ll total = 0;
    for (int mask = 1; mask < (1 << int(lcm_a.size())); ++mask) {
        ll term = x / lcm_val[mask];
        if (bits[mask] % 2 == 1) {
            total += term;
        } else {
            total -= term;
        }
    }
    
    return total;
}
// 容斥原理预处理模板 结束






// https://leetcode.cn/problems/ugly-number-iii/
class Solution {
public:
    int nthUglyNumber(int n, int a, int b, int c) {
         ll l = 0,r = 1e15;
         init(r,{a,b,c});
         while(l<=r){
            ll mid = l + ((r - l)>>1);
            ll cnt = calc_x(mid);
            if(cnt>=n)r=mid-1;
            else l=mid+1;
         }
         return int(l);
    }
};

