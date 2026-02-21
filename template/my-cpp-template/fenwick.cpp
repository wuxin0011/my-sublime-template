template <class T> 
struct fenwick_tree {
  public:
    fenwick_tree() : _n(0) {}
    explicit fenwick_tree(int n) : _n(n), tree(n) {}
    void add(int i, T x) {
        i++;
        while (i <_n) {
            tree[i] += x;
            i += (i & -i);
        }
    }

    T sum(int i) {
        T s = 0;
        i++;
        while (i > 0) {
            s += tree[i];
            i -= (i & -i);
        }
        return s;
    }


    T sum(int l, int r) {
        return sum(r) - sum(l - 1);
    }

  private:
    int _n;
    std::vector<T> tree;
    
};
