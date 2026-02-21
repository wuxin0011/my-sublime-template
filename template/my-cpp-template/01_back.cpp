// bitset 优化的01背包
bool check(const vector<int>& b, int target) {
    if (target < 0) return false;
    if (target == 0) return true;
    // 根据题意修改bitset大小
    bitset<1001> dp;  
    dp[0] = 1;          
    int n = b.size();
    for (int i = 0; i < n; i++) {
        if (b[i] <= target) {
            dp |= dp << b[i];  
        }
        if (dp[target]) return true;
    }
    return dp[target];
};