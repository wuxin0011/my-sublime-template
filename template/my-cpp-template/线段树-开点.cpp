// https://leetcode.cn/problems/falling-squares/submissions/692031903/
// 开点线段树模板
// 开点线段树模板
template <class T, T (*op)(T, T), T (*e)(),T (*addLazy0)(T, T)> 
struct LazySegment {
    int n;
    vector<T> infos,addv,updatev;
    vector<int> left,right,addtag,updatetag;
    int cnt;
    void init0(){
        this->left.resize(n<<1,0);
        this->right.resize(n<<1,0);
        this->updatetag.resize(n << 2,0);
        this->addtag.resize(n << 2,0);
        this->infos.resize(n << 2,e());
        this->addv.resize(n << 2,e());
        this->updatev.resize(n << 2,e());
        this->cnt = 1;
    }
    LazySegment(int n) {
        this->n = n;
        this->init0();
    }
    void updateLazy(int i, int l, int r, T v) {
        infos[i] = v;
        updatev[i] = v;
        updatetag[i] = 1;
        addtag[i] = 0;
    }

    void addLazy(int i, int l, int r, T v) {
        infos[i] = addLazy0(infos[i],v);
        addtag[i] = 1;
        addv[i] = v;
    }

     void up(int i, int l, int r) {
        infos[i] = op(infos[left[i]], infos[right[i]]);
    }

     void addLeftSon(int i) {
        if (left[i] == 0) {
            left[i] = ++cnt;
            infos[left[i]] = e();
        }
    }

     void addRightSon(int i) {
        if (right[i] == 0) {
            right[i] = ++cnt;
            infos[right[i]] = e();
        }
    }


    void down(int i, int l, int r) {
        int mid = l + ((r - l) >> 1);
        if (updatetag[i] != 0) {
            if (mid - l + 1 > 0) {
                addLeftSon(i);
                updateLazy(left[i], l, mid, updatev[i]);
            }
            if (r - mid > 0) {
                addRightSon(i);
                updateLazy(right[i], mid + 1, r, updatev[i]);
            }
            updatetag[i] = 0;
            addtag[i] = 0;
        }
        if (addtag[i] != 0) {
            if (mid - l + 1 > 0) {
                addLeftSon(i);
                addLazy(left[i], l, mid, addv[i]);
            }
            if (r - mid > 0) {
                addRightSon(i);
                addLazy(right[i], mid + 1, r, addv[i]);
            }
            addtag[i] = 0;
        }
    }


     void update(int ql, int qr, T v, int l, int r, int i) {
        if (ql <= l && r <= qr) {
            updateLazy(i, l, r, v);
        } else {
            int mid = l + ((r - l) >> 1);
            down(i, l, r);
            if (ql <= mid) {
                addLeftSon(i);
                update(ql, qr, v, l, mid, left[i]);
            }
            if (qr > mid) {
                addRightSon(i);
                update(ql, qr, v, mid + 1, r, right[i]);
            }
            up(i, l, r);
        }
    }


     void add(int ql, int qr, T v, int l, int r, int i) {
        if (ql <= l && r <= qr) {
            addLazy(i, l, r, v);
        } else {
            int mid = l + ((r - l) >> 1);
            down(i, l, r);
            if (ql <= mid) {
                addLeftSon(i);
                add(ql, qr, v, l, mid, left[i]);
            }
            if (qr > mid) {
                addRightSon(i);
                add(ql, qr, v, mid + 1, r, right[i]);
            }
            up(i, l, r);
        }
    }


    T query(int ql, int qr, int l, int r, int i) {
        if (ql <= l && r <= qr) {
            return infos[i];
        } else {
            int mid = l + ((r - l) >> 1);
            down(i, l, r);
            T ans = e();
            if (ql <= mid) {
                if (left[i] != 0) {
                    ans = op(ans, query(ql, qr, l, mid, left[i]));
                }
            }
            if (qr > mid) {
                if (right[i] != 0) {
                    ans = op(ans, query(ql, qr, mid + 1, r, right[i]));
                }
            }
            return ans;
        }
    }


    void build(int l, int r, int i,vector<T> arr) {
        if (l == r) {
            infos[i] = arr[l];
        } else {
            int mid = l + ((r - l) >> 1);
            if (mid - l + 1 > 0) {
                addLeftSon(i);
                build(l, mid, left[i],arr);
            }
            if (r - mid > 0) {
                addRightSon(i);
                build(mid + 1, r, right[i],arr);
            }
            up(i, l, r);
        }
    }


    template <bool(*ck)(T,T)>
    int find_first(int ql,int qr,T x,int l,int r,int i){
        if(qr < l || r < ql ) return -1;
        if(!ck(infos[i],x)) return -1;
        if(l == r) return l;
        down(i,l,r);
        int mid = l + ((r - l)>>1);
        if (ql <= mid) {
            int p = findFirst(ql,qr,x, l, mid, left[i]);
            if (p >= 0) return p;
        }
        return findFirst(ql,qr, x, mid + 1, r, right[i]);
    }


    template <bool(*ck)(T,T)>
    int find_last(int ql,int qr,T x,int l,int r,int i){
        if(qr < l || r < ql ) return -1;
        if(!ck(infos[i],x)) return -1;
        if(l == r) return l;
        down(i,l,r);
        int mid = l + ((r - l)>>1);
        if (ql <= mid) {
            int p = find_last(ql,qr, x, mid + 1, r, right[i]) ;
            if (p >= 0) return p;
        }
        return find_last(ql,qr,x, l, mid, left[i]);
    }

    void clear(){
        for(int i = 0;i<infos.size();i++){
            infos[i] = e();
            addtag[i] = updatetag[i] = 0;
            if(i<left.size()){
                left[i] = right[i]=0;
            }
        }
        
        cnt = 1;
    }
};
