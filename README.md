# JNav

[![GitHub](https://img.shields.io/github/license/KJinL/JNav?style=flat-square)](https://github.com/KJinL/JNav/blob/main/LICENSE)

JNav对安卓 jetpack compose navigation库进行了初步的封装，将原本复杂的导航配置及参数传递用更简单的方式进行调用，并消除navcontroller传递层次过深的问题。



## 下载

- 第一步

  ```kotlin
  dependencyResolutionManagement {
      repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
      repositories {
          maven("https://jitpack.io")
      }
  }
  ```

- 第二步

  ```kotlin
  implementation ("com.github.KJinL:JNav:1.0.0")
  ```



## 简单使用

- 创建导航route

  继承Destination类，创建导航目的地

  ```kotlin
  /**
   * 首屏
   */
  object FirstDestination : Destination("FirstPage")
  
  /**
   * 第二页
   */
  object SecondDestination : Destination("SecondPage")
  
  /**
   * 第三页
   */
  object ThirdDestination : Destination("ThirdPage")
  
  /**
   * 第四页
   */
  object FourthDestination : Destination("FourthPage")
  ```

- ​	使用组件NavigationEffect设置页面导航

  ```kotlin
  
  @Composable
  fun Greeting() {
      val navController = rememberNavController()
      /**
       * @param navController 设置导航控制器(可不传)
       * @param startDestination 导航启动页的路由
       */
      NavigationEffect(navController = navController,startDestination = FirstDestination.route) {
          composableWithDestination(FirstDestination) {
              FirstPage()
          }
          composableWithDestination(SecondDestination) {
              SecondPage()
          }
          composableWithDestination(ThirdDestination) {
              ThirdPage()
          }
          composableWithDestination(FourthDestination) {
              FourthPage()
          }
      }
  }
  ```

- ​	导航到指定页面

  - 方法

    ```kotlin
    fun to(
        route: String,
        popUpToRoute: String? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
        params: Any? = null
    ) {
        navigate(
            NavIntent.To(
                route, popUpToRoute, inclusive, isSingleTop, params
            )
        )
    }
    ```

  - 使用

    ```kotlin
    @Composable
    fun FirstPage() {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                // 使用NavApp的to方法跳转到指定的页面
                NavApp.to(SecondDestination.route)
            }) {
                Text(text = "跳到第二页")
            }
        }
    }
    ```

- 返回到指定页面

  - 方法

    ```kotlin
    /**
     * 返回，route不填默认弹出一页
     */
    fun back(
        route: String? = null,
        inclusive: Boolean = false,
        result: Any? = null
    ) {
        navigate(
            NavIntent.Back(route, inclusive, result)
        )
    }
    ```

  - 使用

    ```kotlin
    @Composable
    fun SecondPage() {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "这是第二页",
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    // 返回
                    NavApp.back()
                }
            )
            Button(onClick = {
                NavApp.back()
            }) {
                Text(text = "返回")
            }
        }
    }
    ```

- 弹出当前页面并导航到指定页面

  - 方法

    ```kotlin
    fun replace(
        route: String,
        isSingleTop: Boolean = false,
        params: Any? = null
    ) {
        navigate(
            NavIntent.Replace(
                route, isSingleTop, params
            )
        )
    }
    ```

  - 使用

    ```kotlin
    @Composable
    fun FirstPage() {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                // 使用NavApp的to方法跳转到指定的页面
                NavApp.replace(SecondDestination.route)
            }) {
                Text(text = "跳到第二页")
            }
        }
    }
    ```

- ​	清空栈并导航到指定页面

  - 方法

    ```kotlin
    fun offAllTo(
        route: String,
        params: Any? = null
    ) {
        navigate(NavIntent.OffAllTo(route, params))
    }
    ```

  - 使用

    ```kotlin
    @Composable
    fun SecondPage() {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "这是第二页",
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    // 返回
                    NavApp.back()
                }
            )
            Button(onClick = {
                NavApp.offAllTo(FirstDes.route)
            }) {
                Text(text = "返回")
            }
        }
    }
    ```



## 导航到指定页面并携带参数

​	在导航配置中，使用it.params进行参数获取，并传递到指定页面

- 示例: 从FirstPage跳转到ThirdPage并携带user参数

  - 在导航配置中获取参数并传递给ThirdPage

    ```kotlin
    composableWithDestination(ThirdDestination) {
        // 使用it.params()进行参数接收，并通过传入class转换为参数指定类型
        val params = it.params(User::class.java)
        ThirdPage(params)
    }
    ```

  - 在firstpage设置点击事件跳转到ThirdPage，并携带参数params

    ```kotlin
    @Composable
    fun FirstPage(){
        Button(onClick = { NavApp.to(ThirdDestination.route, params = User("李二狗")) }) {
            Text(text = "带参跳转第三页")
        }
    }
    ```

  - 在ThirdPage中接收接收参数

    ```kotlin
    @Composable
    fun ThirdPage(user: User? = null) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "这是第三页, 携带的参数为$user",
                textAlign = TextAlign.Center,
            )
            Button(onClick = {
                NavApp.back()
            }) {
                Text(text = "返回")
            }
        }
    }
    ```

## 返回(到指定页面)并携带返回值

​	在导航配置中，使用it.result进行返回值获取，并传递到指定页面	

- 示例: 从FourthPage返回到FirstPage并携带一个String Result

  - 在导航配置中获取result并传递给FirstPage

    ```kotlin
    composableWithDestination(FirstDestination) {
        val result = it.result(String::class.java)
        FirstPage(result)
    }
    ```

  - 在FourthPage中，设置按钮点击事件返回到FirstPage，并携带一个string返回值

    ```kotlin
    @Composable
    fun FourthPage() {
        var text by remember {
            mutableStateOf("")
        }
    
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(value = text, onValueChange = { text = it })
            Button(onClick = { NavApp.back(result = text) }) {
                Text(text = "输入完成，返回首页")
            }
        }
    }
    ```

  - 在FirstPage中接收参数并显示

    ```kotlin
    @Composable
    fun FirstPage(result: String? = null){
        if (result != null) {
            Text(text = result)
        }
    }
    ```



### 示例图片

<img src="./docs/nav.gif" alt="Animated GIF" style="zoom:25%;" />





#### 文档不清楚可以clone一下demo进行测试