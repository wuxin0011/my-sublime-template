#include<bits/stdc++.h>


// api

// int|char|double|folat|bool|long long
// auto a1 = LeetCode::parse1array<int>(s);
// auto a2 = LeetCode::parse2array<int>(s);
// auto a3 = LeetCode::parse3array<int>(s);
// auto a4 = LeetCode::parse4array<int>(s);


// string
// auto s1 = LeetCode::parse1string(s);
// auto s2 = LeetCode::parse2string(s);
// auto s3 = LeetCode::parse3string(s);

// ============================================
namespace LeetCode {


  bool is_ignore(const char& c) {
    return c == ' ' || c == '\n' || c == '\r' || c == '\f' || c == '\b' || c == '\"';
  }
    template <typename T> T getValue(std::string s) {
  const std::string name = typeid(T).name();
  // cout<<"name = " << name << "\n";
  // int
  if (name.size() == 1 && name[0] == 'i') {
    return std::stoi(s);
  }
  // long long
  else if (name.size() == 1 && name[0] == 'x') {
    return std::stoll(s);
  }
  // unsigned long long
  else if (name.size() == 1 && name[0] == 'y') {
    return std::stoull(s);
  }
  // unsigned int
  else if (name.size() == 1 && name[0] == 'j') {
    return std::stoul(s);
  }
  // char
  else if (name.size() == 1 && name[0] == 'c') {
    return s[0];
  }
  // double
  else if (name.size() == 1 && name[0] == 'd') {
    return std::stod(s);
  }
  // folat
  else if (name.size() == 1 && name[0] == 'f') {
    return std::stof(s);
  }
  // bool
  else if (name.size() == 1 && name[0] == 'b') {
    return s[0] == 't';
  }
  // string
  else if(name.find("string")) {

  }
  return 0;
}

template <typename T> std::vector<T> parse1array(const std::string &temp) {
  std::vector<T> b;
  int deep = 0, add = 0;
  std::string s;
  const std::string name = typeid(T).name();
  for (auto &c : temp) {
    if (is_ignore(c))
      continue;
    if (c == '[') {
      deep++;
    } else if (c == ']') {
      deep--;
      if (add) {
        b.push_back(getValue<T>(s));
        s = "";
      }
      if (deep == 0)
        return b;
    } else if (c == ',') {
      if (add) {
        b.push_back(getValue<T>(s));
        s = "";
      }
    } else if (c == '-') {
      s += c;
    } else {
      s += c;
      add = 1;
    }
  }
  return b;
};

template <typename T>
std::vector<std::vector<T>> parse2array(const std::string &temp) {
  // std::cout << "Type: " << typeid(T).name() << "\n";
  std::vector<std::vector<T>> ans;
  int deep = 0;
  std::string s;
  for (auto &c : temp) {
    if(c == '['){
        deep++;
        if(deep==2) {
          s = "";
        }
      }else if(c == ']') {
        deep--;
        s += c;
        if(deep==1) {
          ans.push_back(parse1array<T>(s));
          s = "";
        }
      }
      s += c;
  }
  return ans;
};


template <typename T>
std::vector<std::vector<std::vector<T>>> parse3array(const std::string &temp) {
    std::vector<std::vector<std::vector<T>>> ans;
    int d = 0;
    std::string s;
    for (char c : temp) {
        if(c == '['){
          d++;
          if(d==2) {
            s = "";
          }
        }else if(c == ']') {
          d--;
          if(d==1) {
            ans.push_back(parse2array<T>(s));
          }
        }
        s += c;

    }
    return ans;
}


template <typename T>
std::vector<std::vector<std::vector<std::vector<T>>>> parse4array(const std::string &temp) {
    std::vector<std::vector<std::vector<std::vector<T>>>> ans;
    int d = 0;
    std::string s;
    for (char c : temp) {
        if(c == '['){
          d++;
          if(d==2) {
            s = "";
          }
        }else if(c == ']') {
          d--;
          if(d==1) {
            ans.push_back(parse3array<T>(s));
          }
        }
        s += c;

    }
    return ans;
}


std::vector<std::string> parse1string(const std::string &temp) {
  std::vector<std::string> b;
  int deep = 0, add = 0;
  std::string s;
  for (auto &c : temp) {
    if (is_ignore(c))
      continue;
    if (c == '[') {
      deep++;
    } else if (c == ']') {
      deep--;
      if (add) {
        b.push_back(s);
        s = "";
      }
      if (deep == 0)
        return b;
    } else if (c == ',') {
      if (add) {
        b.push_back(s);
        s = "";
      }
    } else if (c == '-') {
      s += c;
    } else {
      s += c;
      add = 1;
    }
  }
  return b;
};


std::vector<std::vector<std::string>> parse2string(const std::string &temp) {
  std::vector<std::vector<std::string>> ans;
  int deep = 0;
  std::string s;
  for (auto &c : temp) {
    if(c == '['){
        deep++;
        if(deep==2) {
          s = "";
        }
      }else if(c == ']') {
        deep--;
        s += c;
        if(deep==1) {
          ans.push_back(parse1string(s));
          s = "";
        }
      }
      s += c;
  }
  return ans;
};

std::vector<std::vector<std::vector<std::string>>> parse3string(const std::string &temp) {
  std::vector<std::vector<std::vector<std::string>>> ans;
  int deep = 0;
  std::string s;
  for (auto &c : temp) {
    if(c == '['){
        deep++;
        if(deep==2) {
          s = "";
        }
      }else if(c == ']') {
        deep--;
        s += c;
        if(deep==1) {
          ans.push_back(parse2string(s));
          s = "";
        }
      }
      s += c;
  }
  return ans;
};

}