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

int head[N],to[N<<1],nxt[N<<1],we[N<<1],cnt,dfncnt,dfn[N],sz[N],son[N],pa[N],a[N],seg[N],deep[N],top[N];


// ----------------------------------------seg

int n,opcnt,root = 1,tag[N<<2];

struct Info{
    int color_l = -1;
    int color_r = -1;
    int sums = 0;

    Info() {

    }

    Info(int x){
        color_l = x;
        color_r = x;
        sums = 1;
    }
    Info(int x,int l,int r) {
        this->sums = x;
        this->color_l = l;
        this->color_r = r;
    }

    // 合并操作
    Info operator+(const Info& info2) const {
        Info info1 = {this->sums,this->color_l,this->color_r};
        if(color_l == -1) return info2;
        if(info2.color_l == -1) return info1;
        Info info{};
        info.color_l = info1.color_l;
        info.color_r = info2.color_r;
        info.sums = info1.sums + info2.sums  + (info1.color_r == info2.color_l ? -1 : 0);
        return info;
    }

    // 重写相等操作
    friend bool operator==(const Info& info1,const Info& info2)  {
        if(info1.color_l == -1 || info1.color_r == -1) return false;
        if(info2.color_l == -1 || info2.color_r == -1) return false;
        return info1.sums == info2.sums && info1.color_r == info2.color_r && info1.color_l == info2.color_l;
    }
}  infos[N<<2];






void up(int i) {
    infos[i] = infos[i<<1] + infos[i<<1 | 1];
}

void build(int l,int r,int i) {
    tag[i]=0;
    if(l==r){
        infos[i] = Info{a[seg[l]]};
    }else{
        int mid = l + ((r - l)>>1);
        build(l,mid,i<<1);
        build(mid+1,r,i<<1|1);
        up(i);
    }
}


void updateLazy(int i,int sz,Info x){
    infos[i] = x;
    tag[i]=1;
}

void down(int i,int l,int r) {
    int mid = l + ((r - l)>>1);
    int ln = mid - l + 1,rn = r - mid;
    if(tag[i]!=0){
        updateLazy(i<<1,ln,infos[i]);
        updateLazy(i<<1|1,rn,infos[i]);
        tag[i]=0;
    }
}

void update(int ql,int qr,Info x,int l,int r,int i) {
    if(ql<=l&&r<=qr){
        updateLazy(i,r - l + 1,x);
    }else{
        int mid = l + ((r - l)>>1);
        down(i,l,r);
        if(ql<=mid){
            update(ql,qr,x,l,mid,i<<1);
        }
        if(qr>mid){
            update(ql,qr,x,mid+1,r,i<<1|1);
        }
        up(i);
    }
}

Info query(int ql,int qr,int l,int r,int i) {
    if(ql<=l&&r<=qr){
        return infos[i];
    }else{
        int mid = l + ((r - l)>>1);
        down(i,l,r);
        if(ql>mid){
            return query(ql,qr,mid+1,r,i<<1|1);
        }else if(qr <= mid) {
            return query(ql,qr,l,mid,i<<1);
        }else {
           return query(ql,qr,l,mid,i<<1)+query(ql,qr,mid+1,r,i<<1|1); 
        }
    
    }
}


// ----------------------------------------seg



// build edge

void clear(int n) {
    for(int i = 0;i<=n;i++) {
        head[i]=0;
        dfn[i] = 0;
        son[i] = 0;
    }
    for(int i = 0;i<(n<<2);i++) {
        infos[i] = {};
        tag[i] = 0;
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

void pathUpdate(int u,int v,Info val){
    while(top[u] != top[v]){
        if(deep[top[u]] <= deep[top[v]]){
            update(dfn[top[v]],dfn[v],val,1,n,1);
            v = pa[top[v]];
        }else{
            update(dfn[top[u]],dfn[u],val,1,n,1);
            u = pa[top[u]];
        }
    }
    update(min(dfn[u],dfn[v]),max(dfn[u],dfn[v]),val,1,n,1);
}



Info pathQuery(int u,int v) {
    Info ans{0,-1,-1},son{},father{};
    while(top[u] != top[v]){
        if(deep[top[u]] <= deep[top[v]]){
            ans = ans +  query(dfn[top[v]],dfn[v],1,n,1);
            son = query(dfn[top[v]],dfn[top[v]],1,n,1);
            father = query(dfn[pa[top[v]]],dfn[pa[top[v]]],1,n,1);
            v = pa[top[v]];
        }else{
            ans = ans + query(dfn[top[u]],dfn[u],1,n,1) ;
            son = query(dfn[top[u]],dfn[top[u]],1,n,1);
            father = query(dfn[pa[top[u]]],dfn[pa[top[u]]],1,n,1);
            u = pa[top[u]];
        }
        if(son==father){
            ans.sums--;
        }
    }
    ans = ans + query(min(dfn[u],dfn[v]),max(dfn[u],dfn[v]),1,n,1);
    return ans;
}



void solve() { 
    cin >> n >> opcnt;

    clear(n);

    for(int i = 1;i<=n;i++){
         cin >> a[i];
    }

    for(int i = 1;i<n;i++){
        int u,v;
        cin>>u>>v;
        addEdge(u,v,0);
        addEdge(v,u,0);
    }
    

    dfs1(root,0,1);

    dfs2(root,0);


    build(1,n,1);

    while(opcnt>0){
        opcnt--;
        char op;
        int x,y,z;
        cin >> op;
        if(op=='Q'){
            cin >> x >> y;
            cout << (pathQuery(x,y).sums) << "\n";
        }else{
            cin >> x >> y >> z;
            pathUpdate(x,y,Info{z});
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
