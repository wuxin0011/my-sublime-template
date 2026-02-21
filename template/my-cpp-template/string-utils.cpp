vector<string> filter(string& s,char flag) {
    vector<string> ans;
    string temp;
    for(char c : s) {
        if(c == flag) {
            if(temp.size() > 0) { 
                ans.push_back(temp);
            }
            temp = "";

        }
    }
    return ans;
};


bool hasNum(const string& s){
    for(char c : s) {
        if('0'<=c&&c<='9') return true;
    }
    return false;
}

bool isNum(const string& s){
    for(char c : s) {
        if(!('0'<=c&&c<='9')) {
            return false;
        }
    }
    return true;
}


bool isChar(const string& s) {
    for(char c : s) {
        if(!('a'<=c&&c<='z' || 'A' <= c && c <= 'Z')) {
            return false;
        }
    }
    return true;
}


bool isVol(char c) {
    return c == 'a' || c == 'e' || c == 'i' ||c == 'o' || c == 'u' || c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U'; 
}

