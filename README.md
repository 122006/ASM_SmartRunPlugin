## [ASM_SmartRunPluginImp 线程调度插件](src/main/groovy/com/by122006/buildsrc/ASM_SmartRunPluginImp.groovy)
    适用于任何需要线程切换的程序

* 插件引入

    Step 1. 在你的根目录中加入以下仓库目录

	    allprojects {
		    repositories {
		    	...
		    	maven { url 'https://jitpack.io' }
		    }
		    dependencies {
		        ...
                classpath 'com.github.122006.ASM_SmartRunPluginImp:ASMPlugin:版本号'
            }
	    }
    Step 2. 在需要使用插件的module中增加以下内容

	    apply plugin: 'smartrun'

    以及

	    dependencies {
	        ...
	        compile 'com.github.122006.ASM_SmartRunPluginImp:Utils:版本号'
	    }

    当前版本号：[![](https://jitpack.io/v/122006/ASM_SmartRunPluginImp.svg)](https://jitpack.io/#122006/ASM_SmartRunPluginImp)

* 使用 **注解** 进行方法线程设定

         @UIThread
         public void xx(xx) {

         }
   * 使用`@UIThread`设置方法运行于主线程
   * 使用`@BGThread`设置方法运行于后台线程

* 使用 **方法** 进行方法线程设定

    > 适用于lamada表达式等无法增加方法注解的地方

         public void xx(xx) {
            xxx//任意代码
            ThreadUtils.toBgThread();
            xxx//任意代码
         }
   * 使用`ThreadUtils.toUIThread();`设置方法运行于主线程
   * 使用`ThreadUtils.toBgThread();`设置方法运行于后台线程
   * 该方法调用只是标记，没有实际调用意义，只要在方法中存在，**无论逻辑是否运行该方法**
   * 注解设置优先于方法设置

* 高级功能

    >`BGThread`：后台线程 `UIThread`：主线程，下同

    >eg. @BGThread(Style = Async, OutTime = 2000, Result = Skip)

    * **支持跨线程返回**

        >@BGThread(OutTime = 2000, Result = Wait)

        1. `Result`：是否等待返回 （等待：`Wait` 跳过：`Skip`）
        2. 使用该方法后，调用线程会被挂起（如果调用线程为`UIThread`，其不会被挂起），等待方法返回、或者超时于`OutTime`后，返回方法返回值的默认值并恢复原线程运行
        3. 只适用于`BGThread`。`UIThread`不应该被等待返回
        4. 默认：不等待方法返回值(`Result = Skip`)。如果设置需要返回，默认超时`OutTime`为2000
        5. 具体返回类型与方法返回值有关。如果方法返回值为`void`且设置需要返回，原线程依然会被堵塞直到方法返回，即使不会有任何返回值

    * 需要返回值时支持超时

        1. 见上条

    * 设置同步异步线程运行方案

        >@BGThread(Style = Async)

        1. `Style`：当前方法的运行模式 （异步：`Async` 同步：`Sync`）
        2. 如果调用者线程和方法线程均为 `BGThread`，`Sync`会直接调用方法，`Async`会在新线程中调用方法
        3. `Sync`将 **忽略** 设置的超时及返回值（`OutTime`、`Result`）
        4. 默认：异步(`Style = Async`)
        4. 只适用于`BGThread`。`UIThread`均为同步任务

* 其他注意事项

   * **同继承参数类型方法调用不正确**：如果有多个同名方法，且参数被继承或者为基础类型及基础类型包装器，可能会调用错误的方法(eg:xx(int)和xx(Integer))
