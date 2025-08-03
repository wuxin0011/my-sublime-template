 新增 lsp 配置

`preference`->`borwse packages`

 到目录后回退到上一级目录 形如 `C:\Users\Administrator\AppData\Roaming\Sublime Text`

 将目录直接 cv 过去覆盖







  

 ## 说明



[Packages](./Packages) 中包含一下内容

- CppFastOlympicCoding 修改了一下button按钮样式
  - 主要是修改了这两个文件 [test_styles](/Packages/CppFastOlympicCoding/Highlight/test_styles.css) 和 [test_config.html](/Packages/CppFastOlympicCoding/Highlight/test_config.html)
- CompetitiveProgrammingParser 基于 [CompetitiveProgrammingParser](https://github.com/codeuniverse101/CompetitiveProgrammingParser) 修改，补充了模板方式 
  - "template_dir" 指定模板存放目录
  - "lang_template_suffix" 模板后缀 默认是 "_template.txt" 如果选择 cpp 会拼接成 `template_dir + lang_extension +  lang_template_suffix  `  没有逗号
  - "support_list" 创建语言模板可以自定义
  - "create_author" 在模板中替换自定义用户名 模板标志符号 `{create_author}` 需要自定义用户名才能替换
  - "create_url" 在模板中替换创建访问地址 模板标志符号 `{create_url}` 如果模板中有会自动替换
  - "create_time" 在模板中替换创建日期 模板标志符号 `{create_time}` 如果模板中有会自动替换
  - "override_file"  创建文件是否覆盖源文件 默认 不覆盖 ，如果需要覆盖请设置成 true 或者 ok
- User 是 sublime 个人配置



[Packages](./Packages) 是sublime个人常用编辑器配置文件 一般是复制到本地目录 `C:\Users\Administrator\AppData\Roaming\Sublime Text\Packages` 中即可