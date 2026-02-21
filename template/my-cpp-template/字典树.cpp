
// https://leetcode.cn/problems/implement-trie-prefix-tree/submissions/699198962

struct Node {
    Node* son[26]{};
    int end = 0;
};

class Trie {
public:
    Node* root;
    Trie() { this->root = new Node(); }

    void insert(string word) {
        auto cur = root;
        int n = word.size();
        for (int i = 0; i < n; i++) {
            int c = word[i] - 'a';
            if (cur->son[c] == nullptr) {
                cur->son[c] = new Node();
            }
            cur = cur->son[c];
        }
        cur->end = 1;
    }

    bool search(string word) {
        auto cur = root;
        int n = word.size();
        for (int i = 0; i < n; i++) {
            int c = word[i] - 'a';
            if (cur->son[c] == nullptr) {
                return false;
            }
            cur = cur->son[c];
        }
        return cur->end;
    }

    bool startsWith(string word) {
        auto cur = root;
        int n = word.size();
        for (int i = 0; i < n; i++) {
            int c = word[i] - 'a';
            if (cur->son[c] == nullptr) {
                return false;
            }
            cur = cur->son[c];
        }
        return true;
    }
};

