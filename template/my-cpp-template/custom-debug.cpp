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
