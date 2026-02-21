g++ -D__IS_LOCAL__=1 -D__IS_LEETCODE__=1 "%1.cpp" -o "%1" && "%1" < "%1.txt" > "%1-out.txt"
