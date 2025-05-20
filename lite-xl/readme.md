## lite-xl 使用指南

[下载](https://lite-xl.com/)

### 配置插件

[插件官网](https://github.com/lite-xl/lite-xl-plugins/tree/master/plugins)

在`C:\Users\Administrator\.config\lite-xl\init.lua` 文件加入这几个插件配置 

```lua
require "plugins.autosave"
require "plugins.autopair"
require "plugins.autoinsert"
require "plugins.autowrap"
require "plugins.align_carets"
require "plugins.autosaveonfocuslost"
require "plugins.opacity"
```

然后在 `C:\Users\Administrator\.config\lite-xl\plugins` 目录中添加上面同名的插件

如何需要其他插件注册方式和上面一样，写算法基本上是够用了



说明 获取名称注册脚本 可以使用如下py

```python
import os

def list_lua_files():
    current_dir = os.getcwd()
    lua_files = [f for f in os.listdir(current_dir) 
                if f.endswith('.lua') and os.path.isfile(os.path.join(current_dir, f))]
    for file in lua_files:
        # require "plugins.autosave"
        print(f"require \"plugins.{file.replace('.lua','')}\"")

list_lua_files()

```

