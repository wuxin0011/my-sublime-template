namespace TreeAncestorTemplate {
    constexpr int N = 1e5+10;
    using i64 = long long;
    int power;
    int st[N][20],deep[N];
    int head[N],to[N<<1],nxt[N<<1],wt[N<<1],cnt;
    i64 weight_dis[N];

    void addEdge(int u,int v,int w) {
        ++cnt;
        nxt[cnt]=head[u];
        to[cnt]=v;
        wt[cnt]=w;
        head[u]=cnt;
    }


    void dfs(int u,int fa,int d) {
        deep[u] = d;
        st[u][0] = fa;
        for(int i = 1;i< power && fa != -1 && st[u][i-1] >= 0;i++){
            st[u][i] = st[st[u][i-1]][i-1];
        }
        for(int e = head[u];e;e=nxt[e]){
            int w = wt[e],v = to[e];
            if(v != fa) {
                weight_dis[v] = weight_dis[u] + w;
                dfs(v,u,d+1);
            }
        }
    }

    int get_lca(int a,int b) {
        if(a==b)return a;
        if(deep[a]<deep[b])swap(a,b);
        for(int i = power - 1;i>=0;i--){
            if(st[a][i] >= 0 && deep[st[a][i]] >= deep[b]) a = st[a][i];
        }
        if(a==b)return a;
        for(int i = power - 1;i>=0;i--){
            if(st[a][i] != st[b][i]) {
                a = st[a][i];
                b = st[b][i];
            }
        }
        return max(0,st[a][0]);
    }


    int get_kth(int node,int k) {
        if(node==-1)return -1;
        if(deep[node]<=k) return -1;
        int d = deep[node] - k;
        for(int i = power - 1;i>=0;i--){
            if(deep[st[node][i]] >= d) {
                node=st[node][i];
            }
        }
        return node;
    }

    int get_dis(int u,int v){
        return deep[u] + deep[v] - 2 * deep[get_lca(u,v)];
    }

    i64 get_weigth_dis(int u,int v){
        return weight_dis[u] + weight_dis[v] - 2LL * weight_dis[get_lca(u,v)];
    }


    int up_to_dis(int u,i64 d){
        i64 res = weight_dis[u];
        for(int i = power - 1;i>=0;i--){
            if(st[u][i]>=0 && res - weight_dis[st[u][i]] <= d) {
                u = st[u][i];
            }
        }
        return u;
    }

    void init(int n){
        cnt = 0;
        for(int i = 0;i<n;i++) head[i] = 0;
        power = log2(n) + 1;
    }

    void init(int n,vector<vector<int>>& edges){
        init(n);
        for(int i = 0;i<edges.size();i++){
            int u = edges[i][0],v = edges[i][1],w = edges[i][2];
            addEdge(u,v,w);
            addEdge(v,u,w);
        }
        dfs(0,0,1);
    }


    

}


using namespace TreeAncestorTemplate;
