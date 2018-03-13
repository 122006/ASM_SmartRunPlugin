**方法暂挂：

1. 全局扫描方法 xxUI，记录

↓↓↓↓↓方法检索↓↓↓↓↓

1. 获得对应smartrun方法

2. 计算方法栈结构，构建extends ExtraClass：内部参数，标号id，方法栈快照内容

3. 重命名方法，使用方法新参数表(方法参数，extends ExtraClass)

循环{

4. 遍历方法，维护外置参数表（传参、内参的结构），维护当前栈表的结构，检索到使用到的bg for wait方法(生成标号id)

5. 复制bg for wait方法，并重命名为xxUiWait_ui方法名_标号id，方法参数为（原参数，ui方法参数，extends ExtraClass）

6. 新方法的结尾需要回调ui方法，并传入（ui方法参数，extends ExtraClass）

7. 方法调用前，根据记录外置参数表和栈表结构创建extends ExtraClass类，命名为ExtraClass_ui方法名_标号id

8. 重定向bg for wait方法调用为新方法并传入对应参数

9. 方法调用后：清空栈，返回ui方法参数为默认值

10. 新建frame，接受跳转


}

11. 在方法开始增加：根据标号恢复参数表、栈内容，并跳转到指令方法后运行位置
