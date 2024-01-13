package com.kajin.nav

import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


object NavApp {
    private fun navigate(destination: NavIntent) {
        NavChannel.navigate(destination)
    }

    fun back(
        route: String? = null,
        inclusive: Boolean = false,
        result: Any? = null
    ) {
        navigate(
            NavIntent.Back(route, inclusive, result)
        )
    }

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

    fun offAllTo(
        route: String,
        params: Any? = null
    ) {
        navigate(NavIntent.OffAllTo(route, params))
    }
}


sealed class NavIntent() {

    /**
     * 返回堆栈弹出到指定目标
     * @property route 指定目标
     * @property inclusive 是否弹出指定目标
     * @constructor
     * 【"4"、"3"、"2"、"1"】 Back("2",true)->【"4"、"3"】
     * 【"4"、"3"、"2"、"1"】 Back("2",false)->【"4"、"3"、"2"】
     */
    data class Back<T>(
        val route: String? = null,
        val inclusive: Boolean = false,
        val result: T?
    ) : NavIntent()


    /**
     * 导航到指定目标
     * @property route 指定目标
     * @property popUpToRoute 返回堆栈弹出到指定目标
     * @property inclusive 是否弹出指定popUpToRoute目标
     * @property isSingleTop 是否是栈中单实例模式
     * @constructor
     */
    data class To<T>(
        val route: String,
        val popUpToRoute: String? = null,
        val inclusive: Boolean = false,
        val isSingleTop: Boolean = false,
        val params: T?
    ) : NavIntent()

    /**
     * 替换当前导航/弹出当前导航并导航到指定目的地
     * @property route 当前导航
     * @property isSingleTop 是否是栈中单实例模式
     * @constructor
     */
    data class Replace<T>(
        val route: String,
        val isSingleTop: Boolean = false,
        val params: T?
    ) : NavIntent()

    /**
     * 清空导航栈并导航到指定目的地
     * @property route 指定目的地
     * @constructor
     */
    data class OffAllTo<T>(
        val route: String,
        val params: T?
    ) : NavIntent()

}

internal object NavChannel {

    private val channel = Channel<NavIntent>(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )

    internal var navChannel = channel.receiveAsFlow()

    internal fun navigate(destination: NavIntent) {
        channel.trySend(destination)
    }
}

/**
 * 导航
 */
fun NavController.handleComposeNavigationIntent(intent: NavIntent) {
    when (intent) {
        is NavIntent.Back<*> -> {
            if (intent.result != null) {
                previousBackStackEntry?.savedStateHandle?.set(
                    "result",
                    obj2Json(intent.result)
                )
            }
            if (intent.route != null) {
                popBackStack(intent.route, intent.inclusive)
            } else {
                currentBackStackEntry?.destination?.route?.let {
                    popBackStack()
                }
            }
        }

        is NavIntent.To<*> -> {
            val route = setPath(intent.route, intent.params)
            navigate(route) {
                launchSingleTop = intent.isSingleTop
                intent.popUpToRoute?.let { popUpToRoute ->
                    popUpTo(popUpToRoute) { inclusive = intent.inclusive }
                }
            }
        }

        is NavIntent.Replace<*> -> {
            val route = setPath(intent.route, intent.params)
            navigate(route) {
                launchSingleTop = intent.isSingleTop
                currentBackStackEntry?.destination?.route?.let {
                    popBackStack()
                }
            }
        }

        is NavIntent.OffAllTo<*> -> {
            val route = setPath(intent.route, intent.params)
            navigate(route) {
                popUpTo(0)
            }
        }
    }
}

fun NavGraphBuilder.composableWithDestination(
    destination: Destination,
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        enterTransition,
    popExitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        destination.route,
        destination.arguments,
        deepLinks,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition,
        content
    )
}


val gson = Gson()
fun setPath(route: String, data: Any?): String {
    if (data == null) {
        return route.replace("{params}", "")
    }
    var json = ""
    try {
        json = obj2Json(data)
    } catch (e: Exception) {
        Log.e("Nav.kt", "数据转换失败 ===> ${e.message}")
    }
    return route.replace("{params}", json)
}

fun obj2Json(obj: Any): String {
    var json = ""
    try {
        json = gson.toJson(obj)
    } catch (e: Exception) {
        Log.e("Nav.kt", "数据转换失败 ===> ${e.message}")
    }
    return json
}


fun <T> NavBackStackEntry.params(clazz: Class<T>): T? {
    val paramsJson = arguments?.getString("params") ?: ""
    var obj: T? = null
    try {
        obj = gson.fromJson(paramsJson, clazz)
    } catch (e: Exception) {
        Log.e("Nav.kt", "json to obj error === > ${e.message}")
    }
    return obj
}

fun <T> NavBackStackEntry.result(clazz: Class<T>): T? {
    val resultJson = savedStateHandle.get<String>("result")
    var obj: T? = null
    try {
        obj = gson.fromJson(resultJson, clazz)
    } catch (e: Exception) {
        Log.e("Nav.kt", "json to obj error === > ${e.message}")
    }
    return obj
}

/**
 * 封装导航控件
 */
@Composable
fun NavigationEffect(
    navController: NavHostController = rememberNavController(),
    startDestination: String, builder: NavGraphBuilder.() -> Unit,
) {
    val activity = (LocalContext.current as? Activity)
    val flow = NavChannel.navChannel
    LaunchedEffect(activity, navController, flow) {
        flow.collect {
            if (activity?.isFinishing == true) {
                return@collect
            }
            navController.handleComposeNavigationIntent(it)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        builder = builder
    )
}


/**
 * 页面导航定义
 */
abstract class Destination(
    path: String,
    val arguments: List<NamedNavArgument> = listOf(navArgument("params") { nullable = true })
) {
    var route: String = "$path?params={params}"
}
