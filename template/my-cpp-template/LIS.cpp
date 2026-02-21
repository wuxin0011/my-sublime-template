std::vector<int> LIS(const std::vector<int>& a,bool rev = false) {
    int n = a.size();
    std::vector<int> dp(n,1),g;
    int sz = 0;
    for(int i = rev?n - 1 : 0;rev?i>=0:i<n;rev?i--:i++) {
        int j = std::lower_bound(g.begin(),g.end(),a[i]) - g.begin();
        if(j==sz){
            g.push_back(a[i]);
            sz++;
        }
        else{
            g[j] = a[i];
        }
        dp[i] = max(dp[i],sz);
    }
    return dp;
}
