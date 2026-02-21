vector<int> kmp(string& s,string& p) {
    if(s.size() < p.size()) return {};
    int n = s.size(),m = p.size();
    vector<int> nxt((int)p.size(),0);
    vector<int> ans;
    for(int i = 1,cnt = 0;i < m;i++) {
        while(cnt && p[i] != p[cnt]) {
            cnt = nxt[cnt - 1];
        }
        if(p[i] == p[cnt]) {
            cnt++;
        }
        nxt[i] = cnt;
    }
    for(int i = 0,cnt = 0;i<n;i++) {
        while(cnt && p[cnt] != s[i]) cnt = nxt[cnt- 1];
        if(p[cnt] == s[i]) cnt++;
        if(cnt == m) {
            ans.push_back(i - m + 1);
            cnt = nxt[cnt - 1];
        }
    }
    return ans;
}
