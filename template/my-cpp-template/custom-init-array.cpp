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