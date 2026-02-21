
#include <bits/stdc++.h>

using namespace std;

namespace dbg {
#define IS_DEBUG_MODE 1

// arithmetic
template <class T, std::enable_if_t<std::is_arithmetic_v<T>, int> = 0>
void __print(const T &x) {
  std::cout << std::boolalpha << x;
}

// string
void __print(const std::string &x) { std::cout << '\"' << x << '\"'; }

// bitset
template <std::size_t N> void __print(const std::bitset<N> &Bs) {
  std::cout << Bs;
}

// pair
template <typename A, typename B> void __print(const std::pair<A, B> &p) {
  std::cout << "(";
  __print(p.first);
  std::cout << ", ";
  __print(p.second);
  std::cout << ")";
}

// Iterable https://spectre-code.org/sfinae.html
template <typename T, typename = std::void_t<>>
struct is_iterable : public std::false_type {};

template <typename T>
struct is_iterable<T, std::void_t<decltype(std::declval<T>().begin(),
                                           std::declval<T>().end())>>
    : public std::true_type {};

template <typename T> using is_iterable_t = typename is_iterable<T>::type;

template <typename T>
inline constexpr bool is_iterable_v = is_iterable<T>::value;

template <class T, std::enable_if_t<dbg::is_iterable_v<T>, int> = 0>
void __print(const T &v) {
  int f = 0;
  std::cout << '{';
  for (const auto &i : v)
    std::cout << (f++ ? ", " : ""), __print(i);
  std::cout << "}";
}

// Debug Mode
void debug_out() { std::cout << std::endl; }
template <typename Head, typename... Tail> void debug_out(Head H, Tail... T) {
  std::cout << " ", __print(H), debug_out(T...);
}

#if IS_DEBUG_MODE
#define debug(...)                                                             \
  std::cout << __func__ << ":" << __LINE__ << " (" << #__VA_ARGS__ << ") = ",  \
      debug_out(__VA_ARGS__)
#else
#define debug(...) ((void)0)
#endif
}; // namespace dbg

using namespace dbg;
namespace wuxin0011 {
using ll = long long;
using ull = unsigned long long;
using ui = unsigned int;
using pll = std::pair<ll, ll>;
using pii = std::pair<int, int>;
using pli = std::pair<ll, int>;
using pil = std::pair<int, ll>;
using pdd = std::pair<double, double>;
using vi = std::vector<int>;
template <typename T, size_t N> using van = std::vector<std::array<T, N>>;
template <typename T, size_t N> using an = std::array<T, N>;
using vll = std::vector<ll>;
using vd = std::vector<double>;
using vs = std::vector<string>;
using vvi = std::vector<vi>;
using vvvi = std::vector<vvi>;
using vvll = std::vector<vll>;
using vvvll = std::vector<vvll>;
using vpii = std::vector<pii>;
using vvpii = std::vector<vpii>;
using vpll = std::vector<pll>;
using vvpll = std::vector<vpll>;
using vpdd = std::vector<pdd>;
using vvd = std::vector<vd>;
#define Yn(ok) std::cout << ((ok) ? "Yes" : "No") << "\n"
#define YN(ok) std::cout << ((ok) ? "YES" : "NO") << "\n"
#define pln(x) std::cout << (x) << "\n"
#define pl(x, nxt) std::cout << (x) << ((nxt) ? "\n" : " ")
#define F(i, s, e, t)                                                          \
  for (int(i) = (s); (t) >= 1 ? ((i) <= (e)) : ((i) >= (e)); (i) += (t))
#define F1(i, e) for (int(i) = 0; (i) <= (e); (i) += 1)
#define all(v) (v).begin(), (v).end()
#define lower(a, x) std::lower_bound((a).begin(), (a).end(), x) - (a).begin()
#define upper(a, x) std::upper_bound((a).begin(), (a).end(), x) - (a).begin()
#define mst(x, v) memset((x), (v), sizeof((x)))
#define pb push_back
#define qb pop_back
#define pf push_front
#define qf pop_front
#define maxe max_element
#define mine min_element
#define rnq(a)                                                                 \
  do {                                                                         \
    std::sort((a).begin(), (a).end());                                         \
    (a).erase(std::unique((a).begin(), (a).end()), (a).end());                 \
  } while (0)

template <typename T> bool chmax(T &a, T b) {
  if (a >= b)
    return false;
  a = b;
  return true;
}
template <typename T> bool chmin(T &a, T b) {
  if (a <= b)
    return false;
  a = b;
  return true;
}
// init array start ===================================
template <typename T, size_t N>
void initMultiArray(T (&arr)[N], const T &value, size_t size = N) {
  size = std::min(size, N);
  for (size_t i = 0; i < size; ++i) {
    arr[i] = value;
  }
}

template <typename T, size_t N, typename... Args>
void initMultiArray(T (&arr)[N],
                    const typename std::remove_all_extents<T>::type &value,
                    size_t first_dim, Args... dims) {
  first_dim = std::min(first_dim, N);
  for (size_t i = 0; i < first_dim; ++i) {
    initMultiArray(arr[i], value, dims...);
  }
}

template <typename Array, typename ValueType, typename... Dims>
void initArray(Array &arr, const ValueType &value, Dims... dims) {
  // static_assert(sizeof...(Dims) <= std::rank<Array>::value,
  //              "Too many dimension arguments!");
  initMultiArray(arr, value, dims...);
}

// init array end ===================================

struct custom_hash {
  // http://xorshift.di.unimi.it/splitmix64.c
  static uint64_t splitmix64(uint64_t x) {
    x += 0x9e3779b97f4a7c15;
    x = (x ^ (x >> 30)) * 0xbf58476d1ce4e5b9;
    x = (x ^ (x >> 27)) * 0x94d049bb133111eb;
    return x ^ (x >> 31);
  }

  template <typename T> size_t operator()(T x) const {
    // static_assert(std::is_integral_v<T>, "Only supports integer types");
    static const uint64_t FIXED_RANDOM =
        chrono::steady_clock::now().time_since_epoch().count();
    return splitmix64(static_cast<uint64_t>(x) + FIXED_RANDOM);
  }

  template <typename T1, typename T2>
  size_t operator()(const std::pair<T1, T2> &p) const {
    uint64_t x = (uint64_t(p.first) << 32) | (uint64_t(p.second) & 0xFFFFFFFF);
    return (*this)(x);
  }

  template <typename T1, typename T2, typename T3>
  size_t operator()(const std::pair<T1, std::pair<T2, T3>> &p) const {
    uint64_t x = (uint64_t(p.first) << 32) ^ (uint64_t(p.second.first) << 16) ^
                 p.second.second;
    return (*this)(x);
  }

  template <typename T, size_t N>
  size_t operator()(const std::array<T, N> &arrays) const {
    uint64_t hash = 0;
    for (T x : arrays) {
      hash ^= splitmix64(static_cast<uint64_t>(x) + 0x9e3779b9 + (hash << 6) +
                         (hash >> 2));
    }
    return hash;
  }
};

constexpr int dir8[8][2] = {{-1, -1}, {0, -1}, {1, -1}, {1, 0},
                            {1, 1},   {0, 1},  {-1, 1}, {-1, 0}};
constexpr int dir4_1[4][2] = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
constexpr int dir4_2[4][2] = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
constexpr auto dirs = dir4_1;
constexpr int inf = 0x7fffffff;
constexpr ll linf = 1e18;

} // namespace wuxin0011
using namespace wuxin0011;

void solve();

int main() {
  ios::sync_with_stdio(0);
  cin.tie(0);
  int t = 1;
  // cin >> t;
  while (t--) {
    solve();
  }
  return 0;
}

constexpr int MOD = 1e8,N = 13;
void solve() {
   
}
