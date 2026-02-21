
// https://leetcode.cn/problems/largest-magic-square/
class MAT {
public:
    vector<vector<long long>> sums;   
    vector<vector<long long>> sums_l; 
    vector<vector<long long>> sums_r; 
    vector<vector<int>> g;         
    int m, n;                     
    MAT(vector<vector<int>>& grid) : g(grid) {
        m = grid.size();
        n = grid[0].size();
        sums = vector<vector<long long>>(m + 3, vector<long long>(n + 3, 0));
        sums_l = vector<vector<long long>>(m + 3, vector<long long>(n + 3, 0));
        sums_r = vector<vector<long long>>(m + 3, vector<long long>(n + 3, 0));
        
        init();
        init_l();
        init_r();
    }
    void init() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                sums[i + 1][j + 1] = sums[i + 1][j] + sums[i][j + 1] + g[i][j] - sums[i][j];
            }
        }
    }

    void init_l() {
        for (int i0 = m - 1; i0 >= 0; i0--) {
            int i = i0, j = 0;
            while (i < m && j < n) {
                sums_l[i + 1][j + 1] = sums_l[i][j] + g[i][j];
                i++;
                j++;
            }
        }
        for (int j0 = 1; j0 < n; j0++) {
            int i = 0, j = j0;
            while (i < m && j < n) {
                sums_l[i + 1][j + 1] = sums_l[i][j] + g[i][j];
                i++;
                j++;
            }
        }
    }
    void init_r() {
        for (int i0 = m - 1; i0 >= 0; i0--) {
            int i = i0, j = n - 1;
            while (i < m && j >= 0) {
                sums_r[i + 1][j + 1] = sums_r[i][j + 2] + g[i][j];
                i++;
                j--;
            }
        }
        for (int j0 = n - 2; j0 >= 0; j0--) {
            int i = 0, j = j0;
            while (i < m && j >= 0) {
                sums_r[i + 1][j + 1] = sums_r[i][j + 2] + g[i][j];
                i++;
                j--;
            }
        }
    }
    // [x0,y0] 左上 
    // [x1,y1] 右下
    long long get(int x0, int y0, int x1, int y1) {
        return sums[x1 + 1][y1 + 1] - sums[x1 + 1][y0] - sums[x0][y1 + 1] + sums[x0][y0];
    }
    // [x0,y0] 左上 
    // [x1,y1] 右下
    long long get_l(int x0, int y0, int x1, int y1) {
        return sums_l[x1 + 1][y1 + 1] - sums_l[x0][y0];
    }
    // [x0,y0] 左下
    // [x1,y1] 右上
    long long get_r(int x0, int y0, int x1, int y1) {
        return sums_r[x0 + 1][y0 + 1] - sums_r[x1][y1 + 2];
    }
};

