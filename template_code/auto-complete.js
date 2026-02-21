// ==UserScript==
// @name         auto-completed1111
// @namespace    auto-completed1111
// @version      0.0.1
// @author       wuxin0011
// @description  快捷键
// @license      MIT
// @icon         https://assets.leetcode.cn/aliyun-lc-upload/uploaded_files/2021/03/73c9f099-abbe-4d94-853f-f8abffd459cd/leetcode.png?x-oss-process=image%2Fformat%2Cwebp
// @match        https://leetcode.cn/problems/*
// @match        https://leetcode.cn/contest/*/problems/*
// @match        https://leetcode.com/circle/discuss/*
// @match        https://leetcode.com/problems/*
// @match        https://leetcode.com/contest/*/problems/*
// @match        https://www.lanqiao.cn/problems/*
// @grant        GM_addStyle
// @grant        GM_deleteValue
// @grant        GM_getResourceText
// @grant        GM_getValue
// @grant        GM_registerMenuCommand
// @grant        GM_setValue
// ==/UserScript==

(function() {
    'use strict';

    // 等待 Monaco Editor 加载
    function initMonaco() {
        // 检查是否在 LeetCode 编辑器页面
        const isEditorPage = window.location.pathname.includes('/problem/') || window.location.pathname.includes('/problems/') ||
                           window.location.pathname.includes('/contest/');

        if (!isEditorPage) {
            return;
        }

        // 等待 Monaco 加载
        function waitForMonaco() {
            if (typeof monaco === 'undefined' || !monaco.languages) {
                setTimeout(waitForMonaco, 500);
                return;
            }

            // 注册 Java 代码补全
            monaco.languages.registerCompletionItemProvider('java', {
                provideCompletionItems: function(model, position) {
                    const word = model.getWordUntilPosition(position);
                    const range = {
                        startLineNumber: position.lineNumber,
                        endLineNumber: position.lineNumber,
                        startColumn: word.startColumn,
                        endColumn: word.endColumn
                    };

                    return {
                        suggestions: [
                             {
                                label: 'newint1',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: `int[] a = new int[$0];\n`,
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '创建新的 HashSet'
                            },
                            {
                                label: 'newint2',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: `int[][] a = new int[$0][];\n`,
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '创建新的 HashSet'
                            },
                            {
                                label: 'newset',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: 'Set<Integer> $1 = new HashSet<>();',
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '创建新的 HashSet'
                            },
                             {
                                label: 'newtree',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: 'TreeMap<Integer,Integer> $1 = new TreeMap<>();',
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '创建新的 TreeMap'
                            },
                            {
                                label: 'newmap',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: 'Map<Integer, Integer> $1 = new HashMap<>();',
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '创建新的 HashMap'
                            },
                            {
                                label: 'newlist',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: 'List<$1> list = new ArrayList<>();',
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '创建新的 ArrayList'
                            },
                            {
                                label: 'fori',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: 'for (int $0 = 0; $0 < n; $0++) {\n   \n}',
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: 'for 循环'
                            },
                            {
                                label: 'ford',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: 'for (int $0 = n - 1; $0 >= 0; $0--) {\n   \n}',
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '倒序 for 循环'
                            },
                            {
                                label: 'mapfor',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: 'for (Map.Entry<$1, $2> entry : map.entrySet()) {\n    $0\n}',
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '遍历 Map'
                            },
                            {
                                label: 'newsb',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: 'StringBuilder sb = new StringBuilder();\n$0',
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '创建 StringBuilder'
                            },
                            {
                                label: 'pln',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: `System.out.println($0);`,
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '创建 StringBuilder'
                            },
                            {
                                label: 'plf',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: `System.out.printf($0);`,
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '创建 StringBuilder'
                            }
                        ]
                    };
                }
            });


           // 注册 c++
            monaco.languages.registerCompletionItemProvider('cpp', {
                provideCompletionItems: function(model, position) {
                    const word = model.getWordUntilPosition(position);
                    const range = {
                        startLineNumber: position.lineNumber,
                        endLineNumber: position.lineNumber,
                        startColumn: word.startColumn,
                        endColumn: word.endColumn
                    };

                    return {
                        suggestions: [

                            {
                                label: 'fori',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: 'for (int $0 = 0; $0 < n; $0++) {\n   \n}',
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: 'for 循环'
                            },
                            {
                                label: 'ford',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: 'for (int $0 = n - 1; $0 >= 0; $0--) {\n   \n}',
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '倒序 for 循环'
                            },                       
                            {
                                label: 'lcsimple',
                                kind: monaco.languages.CompletionItemKind.Snippet,
                                insertText: `
#define IS_DEBUG_MODE 1
template <class T, std::enable_if_t<std::is_arithmetic_v<T>, int> = 0>
void __print(const T& x) {std::cout << std::boolalpha << x;}
void __print(const std::string& x) { std::cout << '"' << x << '"'; }
template <std::size_t N> void __print(const std::bitset<N>& Bs) {std::cout << Bs;}
template <typename A, typename B> void __print(const std::pair<A, B>& p) { std::cout << "(";__print(p.first);std::cout << ", ";__print(p.second);std::cout << ")";}
template <typename T, typename = std::void_t<>>
struct is_iterable : public std::false_type {};
template <typename T>
struct is_iterable<T, std::void_t<decltype(std::declval<T>().begin(),std::declval<T>().end())>>: public std::true_type {};
template <typename T> using is_iterable_t = typename is_iterable<T>::type;
template <typename T>
inline constexpr bool is_iterable_v = is_iterable<T>::value;
template <class T, std::enable_if_t<is_iterable_v<T>, int> = 0>
void __print(const T& v) {int f = 0;std::cout << '{'; for (const auto& i : v) {std::cout << (f++ ? ", " : ""), __print(i);}std::cout << "}";}
void debug_out() { std::cout << std::endl; }
template <typename Head, typename... Tail> void debug_out(Head H, Tail... T) {std::cout << " ", __print(H), debug_out(T...);}
#if IS_DEBUG_MODE
#define debug(...) std::cout << __func__ << ":" << __LINE__ << " (" << #__VA_ARGS__<< ") = ",debug_out(__VA_ARGS__)
#else
#define debug(...) ((void)0)
#endif

template <typename T, size_t N>
void mst(T (&arr)[N], const T& value, size_t size = N) {size = std::min(size, N);for (size_t i = 0; i < size; ++i) arr[i] = value;}
template <typename T, size_t N, typename... Args>
void mst(T (&arr)[N],const typename std::remove_all_extents<T>::type& value,size_t first_dim, Args... dims) {first_dim = std::min(first_dim, N);for (size_t i = 0; i < first_dim; ++i) mst(arr[i], value, dims...);}


#define F(i, s, e, t)  for (int(i) = (s); (t) >= 1 ? ((i) <= (e)) : ((i) >= (e)); (i) += (t))
#define all(v) (v).begin(), (v).end()
#define lower(a, x) std::lower_bound((a).begin(), (a).end(), x) - (a).begin()
#define upper(a, x) std::upper_bound((a).begin(), (a).end(), x) - (a).begin()
#define len(x) int((x).size())
#define pb push_back
#define qb pop_back
#define pf push_front
#define qf pop_front
#define rnq(a) do {std::sort((a).begin(), (a).end());(a).erase(std::unique((a).begin(), (a).end()),(a).end());} while (0)

using ll = long long;
using ull = unsigned long long;
using pll = std::pair<ll, ll>;
using pii = std::pair<int, int>;
using vi = std::vector<int>;
using vll = std::vector<ll>;
using vvi = std::vector<vi>;
constexpr ll  linf = 1e18;
constexpr int inf = 1e9,N = 4e5 + 10,mod = 1e9 + 7;

$0
`,
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '倒序 for 循环'
                            },


                            {

label: 'cppio',
kind: monaco.languages.CompletionItemKind.Snippet,
insertText: `
#include <bits/stdc++.h>
using namespace std;
#define IS_DEBUG_MODE 1
template <class T, std::enable_if_t<std::is_arithmetic_v<T>, int> = 0>
void __print(const T& x) {std::cout << std::boolalpha << x;}
void __print(const std::string& x) { std::cout << '"' << x << '"'; }
template <std::size_t N> void __print(const std::bitset<N>& Bs) {std::cout << Bs;}
template <typename A, typename B> void __print(const std::pair<A, B>& p) { std::cout << "(";__print(p.first);std::cout << ", ";__print(p.second);std::cout << ")";}
template <typename T, typename = std::void_t<>>
struct is_iterable : public std::false_type {};
template <typename T>
struct is_iterable<T, std::void_t<decltype(std::declval<T>().begin(),std::declval<T>().end())>>: public std::true_type {};
template <typename T> using is_iterable_t = typename is_iterable<T>::type;
template <typename T>
inline constexpr bool is_iterable_v = is_iterable<T>::value;
template <class T, std::enable_if_t<is_iterable_v<T>, int> = 0>
void __print(const T& v) {int f = 0;std::cout << '{'; for (const auto& i : v) {std::cout << (f++ ? ", " : ""), __print(i);}std::cout << "}";}
void debug_out() { std::cout << std::endl; }
template <typename Head, typename... Tail> void debug_out(Head H, Tail... T) {std::cout << " ", __print(H), debug_out(T...);}
#if IS_DEBUG_MODE
#define debug(...) std::cout << __func__ << ":" << __LINE__ << " (" << #__VA_ARGS__<< ") = ",debug_out(__VA_ARGS__)
#else
#define debug(...) ((void)0)
#endif

template <typename T, size_t N>
void mst(T (&arr)[N], const T& value, size_t size = N) {size = std::min(size, N);for (size_t i = 0; i < size; ++i) arr[i] = value;}
template <typename T, size_t N, typename... Args>
void mst(T (&arr)[N],const typename std::remove_all_extents<T>::type& value,size_t first_dim, Args... dims) {first_dim = std::min(first_dim, N);for (size_t i = 0; i < first_dim; ++i) mst(arr[i], value, dims...);}


#define F(i, s, e, t)  for (int(i) = (s); (t) >= 1 ? ((i) <= (e)) : ((i) >= (e)); (i) += (t))
#define all(v) (v).begin(), (v).end()
#define lower(a, x) std::lower_bound((a).begin(), (a).end(), x) - (a).begin()
#define upper(a, x) std::upper_bound((a).begin(), (a).end(), x) - (a).begin()
#define len(x) int((x).size())
#define pb push_back
#define qb pop_back
#define pf push_front
#define qf pop_front
#define rnq(a) do {std::sort((a).begin(), (a).end());(a).erase(std::unique((a).begin(), (a).end()),(a).end());} while (0)

using ll = long long;
using ull = unsigned long long;
using pll = std::pair<ll, ll>;
using pii = std::pair<int, int>;
using vi = std::vector<int>;
using vll = std::vector<ll>;
using vvi = std::vector<vi>;
constexpr ll  linf = 1e18;
constexpr int inf = 1e9,N = 4e5 + 10,mod = 1e9 + 7;


void solve() {
  $0
}

int main() {
  ios::sync_with_stdio(0);
  cin.tie(0);
  int tt = 1;
  // cin >> tt;
  while(tt--)solve();
  return 0;
}

`,
                                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                                range: range,
                                documentation: '倒序 for 循环'
                            }

                        ]
                    };
                }
            });

            console.log('LeetCode Monaco 代码补全已启用');
        }

        waitForMonaco();
    }

    // 监听页面变化（LeetCode 使用 SPA）
    function observePageChanges() {
        // 初始执行
        setTimeout(()=>{
            initMonaco();
        },3000)

        // 监听路由变化
        // let lastUrl = location.href;
        // new MutationObserver(() => {
        //     const url = location.href;
        //     if (url !== lastUrl) {
        //         lastUrl = url;
        //         setTimeout(initMonaco, 1000); // 等待新页面加载
        //     }
        // }).observe(document, { subtree: true, childList: true });
    }

    // 页面加载完成后执行
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', observePageChanges);
    } else {
        observePageChanges();
    }

})();