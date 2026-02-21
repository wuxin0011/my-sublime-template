template<typename T>
struct Q {
    T *q;
    int l = 0,r = 0;
    Q(int sz){
        q = new T[sz];
    }
    void clear() {
        l = r = 0;
    }
    bool empty(){
        return l >= r;
    }
    void push_front(T x){
        q[--l]=x;
    }
    void push_back(T x){
        q[r++] = x;
    }
    T pop(int index){
        return index==-1?q[--r]:q[l++];
    }
    T operator[](int index) {
        return q[index + (index >= 0 ? l : r)];
    }
    T get(int index){
        return q[index + (index>=0 ? l : r)];
    }
    int size() {
        return r - l;
    }
};

/*


template<typename T>
struct Q {
    T *q;
    int l = 0,r = 0;
    Q(int sz){
        q = new T[sz];
    }
    void clear() {
        l = r = 0;
    }
    bool empty(){
        return l >= r;
    }
    void push_front(T x){
        q[--l]=x;
    }
    void push_back(T x){
        q[r++] = x;
    }
    void push(T x){
        push_back(x);
    }
    T pop(){
        return pop(0);
    }
    T pop_front(){
        return pop(0);
    }
    T pop_back(){
        return pop(-1);
    }
    T pop(int index){
        return index==-1?q[--r]:q[l++];
    }
    T front(){
        return get(0);
    }
    T back(){
        return get(-1);
    }
    T get(int index){
        return q[index + (index>=0 ? l : r)];
    }
    T operator[](int index) {
        return q[index + (index >= 0 ? l : r)];
    }
    int size() {
        return r - l;
    }
};


*/