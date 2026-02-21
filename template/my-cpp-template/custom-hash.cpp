
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