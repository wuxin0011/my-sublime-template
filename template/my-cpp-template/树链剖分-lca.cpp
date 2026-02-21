// https://github.com/wuxin0011/LeetCode/blob/main/main/java/template/graph/readme.md
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

constexpr int inf = 1e9,N = 5e5 + 100,mod = 1e9 + 7;

int head[N],to[N<<1],nxt[N<<1],we[N<<1],cnt,dfncnt,dfn[N],sz[N],son[N],pa[N],deep[N],top[N];





// build edge

void clear(int n) {
    for(int i = 0;i<=n;i++) {
        head[i]=0;
        dfn[i] = 0;
        son[i] = 0;
    }
    cnt=0;
    dfncnt=0;
}

void addEdge(int u,int v,int w) {
    ++cnt;
    nxt[cnt]=head[u],to[cnt]=v,we[cnt]=w,head[u]=cnt;
}



// dfs1

void dfs1(int u,int fa,int d) {
    sz[u] = 1;
    deep[u] = d;
    pa[u] = fa;
    for(int e = head[u];e > 0;e = nxt[e]) {
        int v = to[e];
        if(v != fa) {
            dfs1(v,u,d+1);
            sz[u] += sz[v];
            if(son[u] == 0 || sz[son[u]] < sz[v]) {
                son[u] = v;
            }
        }
    }
}

void dfs2(int u,int t) {
    dfncnt++;
    int id = dfncnt;
    dfn[u] = id;
    top[u] = t;
    // seg[id] = u;
    if(son[u]==0)return;
    dfs2(son[u],t);
    for(int e = head[u];e > 0;e = nxt[e]) {
        int v = to[e];
        if(v != pa[u] && v != son[u]) {
            dfs2(v,v);
        }
    }
}

int lca(int u,int v) {
    while(top[u] != top[v]){
        if(deep[top[u]] <= deep[top[v]]){
            v = pa[top[v]];
        }else{
            u = pa[top[u]];
        }
    }
    return deep[u] < deep[v] ? u : v;
}

int dis(int u,int v) {
    return deep[u] + deep[v] - 2*deep[lca(u,v)];
}


void solve() { 
    int n,opcnt,root;
    cin >> n >> opcnt >> root;

    clear(n);

    for(int i = 1;i<n;i++){
        int u,v;
        cin>>u>>v;
        addEdge(u,v,0);
        addEdge(v,u,0);
    }
    // debug(root);

    dfs1(root,0,1);

    dfs2(root,0);

    while(opcnt>0){
        opcnt--;
        int u,v;
        cin >> u >> v;
        cout << lca(u,v) << "\n";
        // debug(u,v);
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
