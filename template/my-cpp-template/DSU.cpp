struct DSU {
    vector<int> fa,sz;
    int size;
    DSU(int n){
        fa.resize(n);
        sz.resize(n);
        for(int i = 0;i<n;i++) {
            fa[i] = i;
            sz[i] = 1;
        }
        size = n;
    }
    int find(int x){
        while(fa[x] != x){
            fa[x] = fa[fa[x]];
            x = fa[x];
        }
        return x;
    }
    bool same(int x,int y) {
        return find(x) == find(y);
    }
    bool merge(int x,int y){
        x = find(x);
        y = find(y);
        if(x == y) return false;
        if(sz[x] < sz[y]) swap(x,y);
        fa[y] = x;
        sz[x] += sz[y];
        size--;
        return true;
    }
};