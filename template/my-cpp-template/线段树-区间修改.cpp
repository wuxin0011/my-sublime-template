// https://www.luogu.com.cn/record/257913040
// https://www.luogu.com.cn/record/257919238


// 区间修改线段树
template <class T, T (*op)(T, T), T (*e)(),T (*addLazy0)(T, T)> 
struct LazySegment {
   vector<T> infos,arr,addv,updatev;
   vector<int> updateTag,addTag;
   int n;
   void init0(vector<T> a){
     this->n = a.size();
     this->arr = a;
     this->updateTag.resize(n << 2,0);
     this->addTag.resize(n << 2,0);
     this->infos.resize(n << 2,e());
     this->addv.resize(n << 2,e());
     this->updatev.resize(n << 2,e());
     build(0,n-1,1);
   }
   LazySegment(int m){
    vector<T> a(m,e());
    this->init0(a);
   }
   LazySegment(vector<T> a){
     this->init0(a);
   }
   void up(int i,int l,int r) {
     infos[i] = op(infos[i << 1],infos[i << 1 | 1]);
   }
   void addLazy(int i,int sz,T x){
    addTag[i] = 1;
    addv[i] = x;
    infos[i] = addLazy0(infos[i],x);
   }
   void updateLazy(int i,int sz,T x){
    infos[i] = x;
    updateTag[i] = 1;
    addTag[i] = 0;
    updatev[i] = x;
   }
   void down(int i,int l,int r){

     int mid = l + ((r-l)>>1);
     if(updateTag[i] != 0) {
        updateLazy(i<<1,mid-l+1,updatev[i]);
        updateLazy(i<<1|1,r-mid,updatev[i]);
        updateTag[i] = 0;
        addTag[i] = 0;
     }

     if(addTag[i] != 0) {
        addLazy(i<<1,mid-l+1,addv[i]);
        addLazy(i<<1|1,r-mid,addv[i]);
        addTag[i] = 0;
     }

   }
   void build(int l,int r,int i) {
      addTag[i] = updateTag[i] = 0;
      if(l==r) {
         infos[i] = arr[l];
      }else{
        int mid = l + ((r - l)>>1);
        build(l,mid,i<<1);
        build(mid+1,r,i<<1 | 1);
        up(i,l,r);
      }
   }
   T query(int ql,int qr,int l,int r,int i) {
       if(ql<=l&&r<=qr){
        return infos[i];
       }else{
        int mid = l + ((r - l)>>1);
        down(i,l,r);
        T ans = e();
        if(ql<=mid){
          ans=op(ans,query(ql,qr,l,mid,i<<1));
        }
        if(qr>mid){
          ans=op(ans,query(ql,qr,mid+1,r,i<<1 | 1));
        }
        return ans;
       }
   }

   T all_query() {
       return infos[1];
   }
   void update(int ql,int qr,T x,int l,int r,int i) {
       if(ql<=l&&r<=qr){
          updateLazy(i,r - l + 1,x);
       }else{
          int mid = l + ((r - l)>>1);
          down(i,l,r);
          if(ql<=mid){
            update(ql,qr,x,l,mid,i << 1);
          }
          if(qr>mid){
            update(ql,qr,x,mid+1,r,i<<1 | 1);
          }
          up(i,l,r);
       }
   }
   void add(int ql,int qr,T x,int l,int r,int i) {
       if(ql<=l&&r<=qr){
          addLazy(i,r - l + 1,x);
       }else{
          int mid = l + ((r - l)>>1);
          down(i,l,r);
          if(ql<=mid){
            add(ql,qr,x,l,mid,i << 1);
          }
          if(qr>mid){
            add(ql,qr,x,mid+1,r,i<<1 | 1);
          }
          up(i,l,r);
       }
   }
    template <bool(*ck)(T,T)>
    int find_first(int ql,int qr,T x,int l,int r,int i){
        if(qr < l || r < ql ) return -1;
        if(!ck(infos[i],x)) return -1;
        if(l == r) return l;
        int mid = l + ((r - l)>>1);
        down(i,l,r);
        if(ql<=mid) {
          int p = find_first<ck>(ql,qr,x,l,mid,i << 1);
          if(p>=0) return p;
        }
        return find_first<ck>(ql,qr,x,mid+1,r,i << 1 | 1);
    }


    template <bool(*ck)(T,T)>
    int find_last(int ql,int qr,T x,int l,int r,int i){
        if(qr < l || r < ql ) return -1;
        if(!ck(infos[i],x)) return -1;
        if(l == r) return l;
        int mid = l + ((r - l)>>1);
        down(i,l,r);
        if(qr>mid) {
          int p = find_last<ck>(ql,qr,x,mid+1,r,i << 1 | 1) ;
          if(p>=0) return p;
        }
        return find_last<ck>(ql,qr,x,l,mid,i << 1);
    }

};



