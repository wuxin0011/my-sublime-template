template <class T, T (*op)(T, T), T (*e)()> 
struct Segment {
   vector<T> infos,arr;
   int n;
   void init0(vector<T> a){
     this->n = a.size();
     this->arr = a;
     this->infos.resize(n << 2,e());
     build(0,n-1,1);
   }
   Segment(int m){
    vector<T> a(m,e());
    this->init0(a);
   }
   Segment(vector<T> a){
     this->init0(a);
   }
   void up(int i,int l,int r) {
     infos[i] = op(infos[i<<1],infos[i << 1 | 1]);
   }
   void build(int l,int r,int i) {
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
   void update(int ql,T x,int l,int r,int i) {
       if(l==r){
          infos[i] = x;
       }else{
          int mid = l + ((r - l)>>1);
          if(ql<=mid){
            update(ql,x,l,mid,i << 1);
          }else{
            update(ql,x,mid+1,r,i<<1 | 1);
          }
          up(i,l,r);
       }
   }
   void add(int ql,T x,int l,int r,int i) {
       if(l==r){
          infos[i] = op(infos[i],x);
       }else{
          int mid = l + ((r - l)>>1);
          if(ql<=mid){
            add(ql,x,l,mid,i << 1);
          }else{
            add(ql,x,mid+1,r,i<<1 | 1);
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
        if(qr>mid) {
          int p = find_last<ck>(ql,qr,x,mid+1,r,i << 1 | 1) ;
          if(p>=0) return p;
        }
        return find_last<ck>(ql,qr,x,l,mid,i << 1);
    }

};

constexpr int inf = 1e9;

int op(int x,int y){
  return max(x,y);
}

int e(){
  return -inf;
}


bool ck(int x,int t) {
   return x >= t;
}

// https://leetcode.cn/problems/fruits-into-baskets-iii/
// class Solution {
// public:
//     int numOfUnplacedFruits(vector<int>& a, vector<int>& b) {
//         int ans=0;
//         int n = a.size();
//         int m = b.size();
//         Segment<int,op,e> seg(b);
//         for(int i = 0;i<n;i++){
//             int x = a[i];
//             int p = seg.find_first<ck>(0,m-1,x,0,m-1,1);
//             if(p>=0){
//                 seg.update(p,p,-1,0,m-1,1);
//             }else{
//                 ans++;
//             }
//         }
//         return ans;
//     }
// };