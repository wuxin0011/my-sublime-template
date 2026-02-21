// https://oi-wiki.org/ds/sparse-table/#__tabbed_1_2

template <typename T> class SparseTable {
    using VT = vector<T>;
    using VVT = vector<VT>;
    using func_type = function<T(const T&, const T&)>;
    VVT ST;
    static T default_func(const T& t1, const T& t2) { return max(t1, t2); }
    func_type op;

public:
    vector<int> logn;
    SparseTable(const vector<T>& v, func_type _func = default_func) {
        op = _func;
        int n = v.size();
        logn.resize(max(4, n + 5), 0);
        for (int i = 2; i <= max(4,n); ++i) {
            logn[i] = logn[i / 2] + 1;
        }
        int l1 = logn[n] + 1;
        ST.assign(n, VT(l1, 0));
        for (int i = 0; i < n; ++i) {
            ST[i][0] = v[i];
        }
        for (int j = 1; j < l1; ++j) {
            int pj = (1 << (j - 1));
            for (int i = 0; i + pj < n; ++i) {
                ST[i][j] = op(ST[i][j - 1], ST[i + (1 << (j - 1))][j - 1]);
            }
        }
    }
    //[l,r]
    T query(int l, int r) {
        int lt = r - l + 1;
        int q = logn[lt];
        //  int q = floor(log2(lt));
        return op(ST[l][q], ST[r - (1 << q) + 1][q]);
    }
};
