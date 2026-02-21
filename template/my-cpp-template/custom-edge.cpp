
constexpr int N = 1e5;


// =========================Edge========================
struct Edge {
  int nxt[N<<1],to[N<<1],weight[N<<1],head[N],cnt;
} E;


void addEdge(Edge& e,int u,int v,int w){
  ++e.cnt;e.nxt[e.cnt]=e.head[u];e.to[e.cnt]=v;e.weight[e.cnt]=w;e.head[u]=e.cnt;
}


void dfs(Edge& e,int u,int f,int d){
  for(int curId = e.head[u];curId;curId=e.nxt[curId]){
    if(e.to[curId]!=f){
      dfs(e,e.to[curId],u,d^1);
    }
  }
}

void clearEdge(int n){
  E.cnt=0;
  for(int i = 0;i<n;i++) E.head[i]=0;
}

// =========================Edge end========================
