#include <bits/stdc++.h>
using namespace std;
#ifdef __IS_LEETCODE__
#include "D://template_code//leetcode.cpp"
#endif

#ifdef __IS_LOCAL__
#include "D://template_code//debug.cpp"
using namespace dbg;
#else
#define debug(...) ((void)0)
#endif

using ll = long long;

constexpr int inf = 1e9,N = 2e5 + 10,mod = 1e9 + 7;


void solve() {
    int n,q;
    cin >> n >> q;

    vector<ll> a(n);
    for(int i = 0;i < n;i++) {
        cin >> a[i];
    }



    int blen = max(1,int(sqrt(n)));
    int bcount = (n + blen - 1) / blen;


    debug(bcount);

    vector<ll> addV(bcount+1),sv(n);
    vector<int> bi(n),bl(bcount),br(bcount);

    auto q_sort = [&](int id) -> void {
        int l = bl[id],r = br[id];
        for(int i = l;i<=r;i++) {
            sv[i] = a[i];
        }
        sort(sv.begin() + l,sv.begin() + r + 1);
    };


    for(int i = 0;i<n;i++) {
        bi[i] = i / blen;
    }

    for(int i = 0;i < bcount;i++) {
        bl[i] = min(n - 1,i * (blen));
        br[i] = min(n - 1,(i + 1) * blen - 1);
        if(bl[i] == n - 1 || br[i] == n - 1) {
            break;
        }
    }



    for(int i = 0;i<bcount;i++) {
        q_sort(i);
    }


    auto lb = [&](int l,int r,int x) -> int {
        int id = bi[l];
        while(l <= r) {
            int mid = l + ((r - l)>>1);
            if(sv[mid] + addV[id] >= x) {
                r = mid - 1;
            }else{
                l = mid + 1;
            }
        }
        return br[id] - l + 1;
    };



    auto add = [&](int l,int r,int x) -> void {
        // return;
        if(bi[l] == bi[r]) {
            if(l == bl[bi[l]] && r == br[bi[r]]) {
                addV[bi[l]] += x;
            }else{
                for(int i = l;i<=r;i++) {
                    a[i] += x;
                }
                q_sort(bi[l]);
            }
        }else{

            if(l == bl[bi[l]]) {
                addV[bi[l]] += x;
            }else{
                for(int i = l;i<=br[bi[l]];i++) {
                    a[i] += x;
                }
                q_sort(bi[l]);
            }

            if(r == br[bi[r]]) {
                addV[bi[r]] += x;
            }else{
                for(int i = bl[bi[r]];i<=r;i++) {
                    a[i] += x;
                }
                q_sort(bi[r]);
            }


            for(int id = bi[l] + 1;id < bi[r];id++) {
                addV[id] += x;
            }

        }
    };

    auto query = [&](int l,int r,int x) -> ll {
        ll ans = 0;
        // return ans;
        if(bi[l] == bi[r]) {
            if(l == bl[bi[l]] && r == br[bi[r]]) {
                ans += lb(l,r,x);
            }else{
                for(int i = l;i<=r;i++) {
                    if(a[i] + addV[bi[l]] >= x) {
                        ans++;
                    }
                }
            }
        }else{

            if(l == bl[bi[l]]) {
                ans += lb(l,br[bi[l]],x);
            }else{
                for(int i = l;i<=br[bi[l]];i++) {
                    if(a[i] + addV[bi[l]] >= x) {
                        ans++;
                    }
                }
            }

            if(r == br[bi[r]]) {
                ans += lb(bl[bi[r]],br[bi[r]],x);
            }else{
                for(int i = bl[bi[r]];i<=r;i++) {
                    if(a[i] + addV[bi[r]] >= x) {
                        ans++;
                    }
                }
            }


            for(int id = bi[l] + 1;id < bi[r];id++) {
                ans += lb(bl[id],br[id],x);
            }

        }

        return ans;
    };




    for(int i = 0;i < q;i++) {
        char op;
        int l,r,w;
        cin >> op;
        cin >> l >> r >> w;
        l--,r--;
        // debug(op,l,r,w);
        if(op == 'M') {
            add(l,r,w);
        }else {
            cout << query(l,r,w) << "\n";
        }
    }
}



int main() {
    ios::sync_with_stdio(0);
    cin.tie(0);
    int tt = 1;
    // cin >> tt;
    while(tt--)solve();
    return 0;
}
