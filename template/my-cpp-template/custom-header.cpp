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
#define F(i, s, e, t) for (int(i) = (s);  (t) >= 1 ? ((i) <= (e)) : ((i) >= (e)); (i) += (t))
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



constexpr int dir8[8][2] = {{-1, -1}, {0, -1}, {1, -1}, {1, 0},
                            {1, 1},   {0, 1},  {-1, 1}, {-1, 0}};
constexpr int dir4_1[4][2] = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
constexpr int dir4_2[4][2] = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
constexpr auto dirs = dir4_1;
constexpr int inf = 0x7fffffff;
constexpr ll linf = 1e18;

} // namespace wuxin0011
using namespace wuxin0011;