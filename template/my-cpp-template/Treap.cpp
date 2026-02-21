
// 该模测试部分 ： https://leetcode.cn/problems/xx4gT2/submissions/667971855/
// 数组版本 https://leetcode.cn/problems/xx4gT2/submissions/667973116/

template<typename T>
class Treap {
private:
    struct Node {
        T key;
        int height;
        int left, right;
        int count, size;
        double priority;
        
        Node() : key(T()), height(0), left(0), right(0), count(0), size(0), priority(0.0) {}
        Node(const T& k) : key(k), height(0), left(0), right(0), count(1), size(1), priority(0.0) {}
    };

    std::vector<Node> nodes;
    int head;
    int cnt;
    std::mt19937 rng;
    std::uniform_real_distribution<double> dist;

    void up(int i) {
        if (i == 0) return;
        nodes[i].size = nodes[nodes[i].left].size + nodes[nodes[i].right].size + nodes[i].count;
    }

    int leftRotate(int i) {
        int r = nodes[i].right;
        nodes[i].right = nodes[r].left;
        nodes[r].left = i;
        up(i);
        up(r);
        return r;
    }

    int rightRotate(int i) {
        int l = nodes[i].left;
        nodes[i].left = nodes[l].right;
        nodes[l].right = i;
        up(i);
        up(l);
        return l;
    }

    int add(int i, const T& num) {
        if (i == 0) {
            if (cnt + 1 >= nodes.size()) {
                nodes.emplace_back(num);
                cnt = nodes.size() - 1;
            } else {
                nodes[++cnt] = Node(num);
            }
            nodes[cnt].priority = dist(rng);
            return cnt;
        }
        
        if (nodes[i].key == num) {
            nodes[i].count++;
        } else if (nodes[i].key > num) {
            nodes[i].left = add(nodes[i].left, num);
            if (nodes[nodes[i].left].priority > nodes[i].priority) {
                i = rightRotate(i);
            }
        } else {
            nodes[i].right = add(nodes[i].right, num);
            if (nodes[nodes[i].right].priority > nodes[i].priority) {
                i = leftRotate(i);
            }
        }
        up(i);
        return i;
    }

    int small(int i, const T& num) const {
        if (i == 0) {
            return 0;
        }
        if (nodes[i].key >= num) {
            return small(nodes[i].left, num);
        } else {
            return nodes[nodes[i].left].size + nodes[i].count + small(nodes[i].right, num);
        }
    }

    int index(int i, int x) const {
        if (nodes[nodes[i].left].size >= x) {
            return index(nodes[i].left, x);
        } else if (nodes[nodes[i].left].size + nodes[i].count < x) {
            return index(nodes[i].right, x - nodes[nodes[i].left].size - nodes[i].count);
        }
        return i;
    }

    T floor(int i, const T& num) const {
        if (i == 0) {
            return T();
        }
        if (nodes[i].key > num) {
            return floor(nodes[i].left, num);
        } else {
            T rightFloor = floor(nodes[i].right, num);
            if (rightFloor == T() && nodes[i].key <= num) {
                return nodes[i].key;
            }
            return (rightFloor != T() && rightFloor <= num) ? rightFloor : nodes[i].key;
        }
    }

    T ceil(int i, const T& num) const {
        if (i == 0) {
            return T();
        }
        if (nodes[i].key < num) {
            return ceil(nodes[i].right, num);
        } else {
            T leftCeil = ceil(nodes[i].left, num);
            if (leftCeil == T() && nodes[i].key >= num) {
                return nodes[i].key;
            }
            return (leftCeil != T() && leftCeil >= num) ? leftCeil : nodes[i].key;
        }
    }

    int remove(int i, const T& num) {
        if (i == 0) return 0;
        
        if (nodes[i].key < num) {
            nodes[i].right = remove(nodes[i].right, num);
        } else if (nodes[i].key > num) {
            nodes[i].left = remove(nodes[i].left, num);
        } else {
            if (nodes[i].count > 1) {
                nodes[i].count--;
            } else {
                if (nodes[i].left == 0 && nodes[i].right == 0) {
                    return 0;
                } else if (nodes[i].left == 0) {
                    i = nodes[i].right;
                } else if (nodes[i].right == 0) {
                    i = nodes[i].left;
                } else {
                    if (nodes[nodes[i].left].priority > nodes[nodes[i].right].priority) {
                        i = rightRotate(i);
                        nodes[i].right = remove(nodes[i].right, num);
                    } else {
                        i = leftRotate(i);
                        nodes[i].left = remove(nodes[i].left, num);
                    }
                }
            }
        }
        up(i);
        return i;
    }

public:
    Treap(int n = 1000) : head(0), cnt(0), rng(std::random_device{}()), dist(0.0, 1.0) {
        nodes.resize(n + 1);
        nodes[0] = Node();
        nodes[0].size = 0;
        nodes[0].count = 0;
    }

    void add(const T& num) {
        head = add(head, num);
    }


    // num 的排名

    int rank(const T& num) const {
        return small(head, num) + 1;
    }

    // 查询第x

    T index(int x) const {
        if (x < 1 || x > nodes[head].size) {
            return T();
        }
        int nodeIndex = index(head, x);
        return nodes[nodeIndex].key;
    }

    // 小于num的最大值）
    T floor(const T& num) const {
        return floor(head, num);
    }

    //（大于num的最小值）
    T ceil(const T& num) const {
        return ceil(head, num);
    }

    void remove(const T& num) {
        head = remove(head, num);
    }

    bool contains(const T& key) const {
        int r1 = rank(key);
        int r2 = rank(key + (key < key ? -1 : 1));
        return r1 != r2;
    }

    int size() const {
        return nodes[head].size;
    }

    void clear() {
        std::fill(nodes.begin(), nodes.end(), Node());
        nodes[0].size = 0;
        nodes[0].count = 0;
        cnt = 0;
        head = 0;
    }

    void reserve(int n) {
        nodes.reserve(n + 1);
    }
};