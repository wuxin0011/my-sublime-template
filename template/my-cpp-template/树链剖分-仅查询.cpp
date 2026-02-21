// https://leetcode.cn/submissions/detail/696927112/

using ll = long long;

constexpr int inf = 1e9, N = 1e5 + 100, mod = 1e9 + 7;

int head[N], to[N << 1], nxt[N << 1], we[N << 1], cnt, dfncnt, dfn[N], sz[N],
    son[N], pa[N], a[N], seg[N], deep[N], top[N];

// ----------------------------------------seg

int n, opcnt, root = 1, tag[N << 2];


int infos[N << 2];




void up(int i) { infos[i] = max(infos[i << 1], infos[i << 1 | 1]); }

void build(int l, int r, int i) {
    tag[i] = 0;
    if (l == r) {
        infos[i] = a[seg[l] - 1];
    } else {
        int mid = l + ((r - l) >> 1);
        build(l, mid, i << 1);
        build(mid + 1, r, i << 1 | 1);
        up(i);
    }
}



int query(int ql, int qr, int l, int r, int i) {
    if (ql <= l && r <= qr) {
        return infos[i];
    } else {
        int mid = l + ((r - l) >> 1);
        int ans=-inf;
        if(ql<=mid)ans=max(ans,query(ql,qr,l,mid,i<<1));
        if(qr>mid)ans=max(ans,query(ql,qr,mid+1,r,i<<1|1));
        return ans;
    }
}

// ----------------------------------------seg

// build edge

void clear(int n) {
    for (int i = 0; i <= n; i++) {
        head[i] = 0;
        dfn[i] = 0;
        son[i] = 0;
    }
    cnt = 0;
    dfncnt = 0;
}

void addEdge(int u, int v) {
    ++cnt;
    nxt[cnt] = head[u], to[cnt] = v, we[cnt] = 0, head[u] = cnt;
}

// dfs1

void dfs1(int u, int fa, int d) {
    sz[u] = 1;
    deep[u] = d;
    pa[u] = fa;
    for (int e = head[u]; e > 0; e = nxt[e]) {
        int v = to[e];
        if (v != fa) {
            dfs1(v, u, d + 1);
            sz[u] += sz[v];
            if (son[u] == 0 || sz[son[u]] < sz[v]) {
                son[u] = v;
            }
        }
    }
}

void dfs2(int u, int t) {
    dfncnt++;
    int id = dfncnt;
    dfn[u] = id;
    top[u] = t;
    seg[id] = u;
    if (son[u] == 0)
        return;
    dfs2(son[u], t);
    for (int e = head[u]; e > 0; e = nxt[e]) {
        int v = to[e];
        if (v != pa[u] && v != son[u]) {
            dfs2(v, v);
        }
    }
}


int pathQuery(int u, int v) {
    int ans = -inf;
    // cout<<u<<" "<<v<<" ,n = "<<n<<"\n";
    while (top[u] != top[v]) {
        if (deep[top[u]] <= deep[top[v]]) {
            ans = max(ans,query(dfn[top[v]], dfn[v], 1, n, 1));
            v = pa[top[v]];
        } else {
            ans = max(ans,query(dfn[top[u]], dfn[u], 1, n, 1));
            u = pa[top[u]];
        }
    }
    ans = max(ans,query(min(dfn[u], dfn[v]), max(dfn[u], dfn[v]), 1, n, 1));
    return ans;
}


class Solution {
public:
    int numberOfGoodPaths(vector<int>& vals, vector<vector<int>>& edges) {

        n = vals.size();
        if(n==1)return 1;
        // cout<<"n "<<n<<",size = "<<(vals.size())<<"\n";
        int mx = 0,cnt=0;
        map<int,vector<int>> map_vals;
        for(int i = 0;i<n;i++){
            a[i] = vals[i];
            if(a[i]>mx){
                mx=a[i];
                cnt=1;
            }else if(mx==a[i]){
                cnt++;
            }
            map_vals[a[i]].push_back(i + 1);
        }
    
       

        clear(n);

         for(vector<int>& e: edges){
            int u = e[0],v=e[1];
            u++;
            v++;
            addEdge(u,v);
            addEdge(v,u);
        }

        dfs1(root, 0, 1);

        dfs2(root, 0);

        build(1, n, 1);

        // pathQuery(x, y)
        // cout<<pathQuery(1,4)<<"\n";
        int ans=n;
        
        for(auto&[k,v] : map_vals){
            int m = v.size();
            if(k==mx){
                ans += m *(m-1)/2;
                continue;
            }
            // cout<<"k = " << k<<",v =  ";
            // cout<<"[ ";
            // for(int i = 0;i<m;i++){
            //     cout<<v[i]<<", "[i==m-1];
            // }
            // cout<<"]\n";
            for(int i = 0;i<m;i++){
                for(int j =i + 1;j<m;j++){
                    int x = pathQuery(v[i],v[j]);
                    if(x<=k){
                        // cout<<"add ans" << v[i] <<"  "<< v[j] <<"\";
                        ans++;
                    }
                }
            }
        }
      
        return ans;
    }
};