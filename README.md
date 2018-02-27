## [ASM_SmartRunPluginImp 线程调度插件](src/main/groovy/com/by122006/buildsrc/ASM_SmartRunPluginImp.groovy)
    适用于任何需要线程切换的程序
* 使用 **注解** 进行方法线程设定

         @UIThread
         public void xx(xx) {

         }
   * 使用`@UIThread`设置方法运行于主线程
   * 使用`@BGThread`设置方法运行于**新的**后台线程

* 使用 **方法** 进行方法线程设定

    > 适用于lamada表达式等无法增加方法注解的地方

         public void xx(xx) {
            xxx//任意代码
            ThreadUtils.toBgThread();
            xxx//任意代码
         }
   * 使用`ThreadUtils.toUIThread();`设置方法运行于主线程
   * 使用`ThreadUtils.toBgThread();`设置方法运行于**新的**后台线程
   * 该方法调用只是标记，没有实际调用意义，只要在方法中存在，**无论逻辑是否运行该方法**
   * 注解设置优先于方法设置

* 其他注意事项

   * **返回值无效**：由于跨线程调用方法如果需要返回值，会造成调用方线程堵塞，所以返回值均不会接收
   * **同继承参数类型方法调用不正确**：如果有多个同名方法，且参数被继承或者为基础类型及基础类型包装器，可能会调用错误的方法(eg:xx(int)和xx(Integer))
   * **使用反射** 该反射支持混淆ba (性能消耗：1~4ms)
