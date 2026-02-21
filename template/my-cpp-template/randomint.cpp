namespace  Random {
    double random_float(int base = 1) {
        std::random_device rd; 
        std::mt19937 gen(rd());
        std::uniform_real_distribution<> dist_real(0.0, 1.0);
        return dist_real(gen) * base;
    }

    int random_int(int minval,int maxval) {
        std::random_device rd; 
        std::mt19937 gen(rd());
        std::uniform_int_distribution<> dist_int(minval,maxval);
        return int(dist_int(gen));
    }
    std::vector<int> random_int_array(int minval,int maxval){
        int n = random_int(1,20);
        std::vector<int> ans;
        for(int i = 0;i < n;i++) {
            ans.push_back(random_int(minval,maxval));
        }
        return ans;
    }


    // falg
    // az => 'a-z'
    // AZ => 'A-Z'
    // Az => 'a-z' + 'A-Z'
    // aZ => 'a-z' + 'A-Z'
    // 01 => 01
    // 00 => 128
    std::string random_string(std::string flag = "az"){
        std::string ans;
        int build = 1;
        int m = random_int(1,20);
        if(flag.size() == 2) {
            char st = flag[0],ed = flag[1];
            if(st == 'a' && ed == 'z' || st == 'A' && ed == 'Z' || st == '0' && ed == '1') {
                for(int i = 0;i<m;i++) {
                    ans += char(random_int(st,ed));
                }
            }else if(st == 'A' && ed == 'z' || st == 'a' && ed == 'Z') {
                for(int i = 0;i<m;i++) {
                    int ok = random_int(0,1);
                    int c1 = random_int('A', 'Z');
                    int c2 = random_int('a', 'z');
                    ans += char(ok ? c1 : c2);
                }
            }else {
                build = 0;
            }
        }
        if(!build) {
            for(int i = 0;i<m;i++) {
                ans += char(random_int(0,127));
            }
        }
        return ans;
    }


    std::vector<std::string> random_string_array(std::string flag,int min_n = 1,int max_n = 20) {
        int n = random_int(std::max(1,min_n),std::max(1,std::max(min_n,max_n)));
        std::vector<std::string> ss;
        while(n--) {
            ss.push_back(random_string(flag));
        }
        return ss;
    }

}
