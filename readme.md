## 说明

这是个人的[sublime](https://www.sublimetext.com/)配置,如果需要更多定制化配置可以参考官方文档[sublime document](https://docs.sublimetext.io/)

- template 是常用模板

  

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



[template_code](./template_code) 是替换模板示例 请他语言请安装这个格式,在 `CompetitiveProgrammingParser `设置中指定模板目录 `template_dir`



[cph](./cph) 是浏览器下载样例插件 [competitive-companion](https://github.com/jmerle/competitive-companion) ，我这里是备份一个 配置端口是 `12345`

## 模板快捷键

以为 `Java` 举例子



安装好配置文件后即可激活快捷键 不同快捷键即可生成不同模板

- `jlc` 生成 [LeetCode](https://leetcode.cn) 风格的模板

- `iosm` 简易版的快读输入输出 只支持读入整数
- `fio` 简易版的快读输入输出  只支持读入整数 附带一些常用工具函数
- `fiof` 完整版本输入输出 支持多种格式读入以及不同风格ACM模式读入
- `fastio` 封装好的快读输入的完整版模板



如果需要自定义请参考官方文档[snippets](https://docs.sublimetext.io/guide/extensibility/snippets.html)

## 对拍

- ACM 推荐请使用 `CppFastOlympic` ，上面模板也支持读入但不支持对拍
  - [CppFastOlympic 配置](https://verytoolz.com/blog/06a6e6743a/)

- 核心函数模式请使用 [_LCUtil](./_LCUtil.java) 
  - 如果需要查看完整版本请查阅[gitee](https://gitee.com/wuxin0011/LeetCode) or [github](https://github.com/wuxin0011/LeetCode) ,这个函数是这个中迁移过来的

## 激活

- [hexed(opens new window)](https://hexed.it/)
- [github sublime 4 actived](https://gist.github.com/skoqaq/3f3e8f28e23c881143cef9cf49d821ff)



## 字体

推荐一下常用字体 下载地址一般在浏览器中搜索相关关键词就能找到

- jetbrains mono
- fira code
- roboto mono
- monaco

安装下载完毕后，需要在编辑器中选择所需要的字体