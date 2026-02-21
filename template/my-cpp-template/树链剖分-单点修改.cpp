// https://leetcode.cn/problems/palindromic-path-queries-in-a-tree/submissions/698826298/
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

constexpr int inf = 1e9,N = 1e5 + 100,mod = 1e9 + 7;

// 树链剖分模板开始

int head[N],to[N<<1],nxt[N<<1],we[N<<1],cnt,dfncnt,dfn[N],sz[N],son[N],pa[N],a[N],seg[N],deep[N],top[N];


int n,opcnt;



// seg

struct Info {
    int x;

    Info() {
        x = 0;
    }

    Info(int c) {
        x = 0;
        x |= 1 << c;
    }


    Info operator+(const Info& o) const {
        Info info {};
        // for(int i = 0;i<26;i++){
        //     int v0 = x >> i & 1;
        //     int v1 = o.x >> i & 1;
        //     if(v0==v1)continue;
        //     info.x |= 1 << i;
        // }
        info.x = o.x ^ x;
        return info;
    }


} infos[N<<2];

void up(int i) {
    infos[i] = infos[i<<1] + infos[i<<1|1];
}

void build(int l,int r,int i) {
    if(l==r){
        infos[i] = Info{a[seg[l]]};
    }else{
        int mid = l + ((r - l)>>1);
        build(l,mid,i<<1);
        build(mid+1,r,i<<1|1);
        up(i);
    }
}



void update(int ql,int qr,Info info,int l,int r,int i) {
    if(l==r){
        infos[i] =  info;
    }else{
        int mid = l + ((r - l)>>1);
        if(ql<=mid){
            update(ql,qr,info,l,mid,i<<1);
        }
        if(qr>mid){
            update(ql,qr,info,mid+1,r,i<<1|1);
        }
        up(i);
    }
}

Info query(int ql,int qr,int l,int r,int i) {
    if(ql<=l&&r<=qr){
        return infos[i];
    }else{
        int mid = l + ((r - l)>>1);
        Info ans {};
        if(ql<=mid){
            ans = ans + query(ql,qr,l,mid,i<<1);
        }
        if(qr>mid){
            ans = ans + query(ql,qr,mid+1,r,i<<1|1);
        }
        return ans;
    }
}




// seg end



// build edge

void clear(int n) {
    for(int i = 0;i<=n;i++) {
        head[i]=0;
        dfn[i] = 0;
        son[i] = 0;
    }
    for(int i = 0;i < (n<<2);i++) {
        infos[i] = {};
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
    seg[id] = u;
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

Info pathQuery(int u,int v) {
    Info ans {};
    while(top[u] != top[v]){
        if(deep[top[u]] <= deep[top[v]]){
            ans = ans + query(dfn[top[v]],dfn[v],1,n,1);
            v = pa[top[v]];
        }else{
            ans = ans + query(dfn[top[u]],dfn[u],1,n,1);
            u = pa[top[u]];
        }
    }
    ans = ans + query(min(dfn[u],dfn[v]),max(dfn[u],dfn[v]),1,n,1);
    return ans;
}


void pathUpdate(int u,int v,Info info) {
    while(top[u] != top[v]){
        if(deep[top[u]] <= deep[top[v]]){
            update(dfn[top[v]],dfn[v],info,1,n,1);
            v = pa[top[v]];
        }else{
            update(dfn[top[u]],dfn[u],info,1,n,1);
            u = pa[top[u]];
        }
    }
    update(min(dfn[u],dfn[v]),max(dfn[u],dfn[v]),info,1,n,1);
}





// 树链剖分模板结束








// 下面为基本步骤

// 初始化操作 建图
// n = n0;
// clear(n);
// for (int i = 0; i < edges.size(); i++) {
//     int u = edges[i][0], v = edges[i][1];
//     u++, v++;
//     addEdge(u, v, 0);
//     addEdge(v, u, 0);
// }
// for (int i = 1; i <= s.size(); i++) {
//     a[i] = s[i - 1] - 'a';
// }
// dfs1(1, 0, 1);
// dfs2(1, 0);
// build(1, n, 1);

// op操作
// pathQuery(u,v)
// pathUpdate(u,v,Info{})

