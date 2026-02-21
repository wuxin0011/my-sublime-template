
// @author:SSerxhs
// https://leetcode.cn/contest/biweekly-contest-122/ranking/?region=local_v2 

// 维护前K（大|小）的和

template<typename T, typename T1=vector<T>, typename T2=less<T>> struct heap
{
private:
    priority_queue<T, T1, T2> p, q;
public:
    void push(const T &x)
    {
        if (!q.empty()&&q.top()==x)
        {
            q.pop();
            while (!q.empty()&&q.top()==p.top()) p.pop(), q.pop();
        }
        else p.push(x);
    }
    void pop()
    {
        p.pop();
        while (!q.empty()&&p.top()==q.top()) p.pop(), q.pop();
    }
    void pop(const T &x)
    {
        if (p.top()==x)
        {
            p.pop();
            while (!q.empty()&&p.top()==q.top()) p.pop(), q.pop();
        }
        else q.push(x);
    }
    T top() const { return p.top(); }
    int size() const { return p.size()-q.size(); }
    bool empty() const { return p.empty(); }
    vector<T> to_vector() const
    {
        vector<T> a;
        auto P=p, Q=q;
        while (P.size())
        {
            a.push_back(P.top()); P.pop();
            while (Q.size()&&P.top()==Q.top()) P.pop(), Q.pop();
        }
        return a;
    }
};
template<typename T, typename T1=vector<T>, typename T2=less<T>> struct ksum_pop
{
private:
    struct __cmp
    {
        bool operator()(const T &x, const T &y) const
        {
            return x!=y&&!T2()(x, y);
        }
    };
    heap<T, T1, __cmp> p;
    heap<T, T1, T2> q;
    ll cur;
public:
    ksum_pop():cur(0) { }
    void push(const T &x)
    {
        if (!q.size()||!T2()(x, q.top())) p.push(x), cur+=x; else q.push(x);
    }
    int size() const { return p.size()+q.size(); }
    void pop(const T &x)
    {
        if (q.size()&&!T2()(q.top(), x)) q.pop(x);
        else p.pop(x), cur-=x;
    }
    ll sum(int k)
    {
        while (p.size()<k)
        {
            cur+=q.top();
            p.push(q.top());
            q.pop();
        }
        while (p.size()>k)
        {
            cur-=p.top();
            q.push(p.top());
            p.pop();
        }
        return cur;
    }
};

