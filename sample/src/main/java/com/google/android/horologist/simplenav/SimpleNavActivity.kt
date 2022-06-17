package com.google.android.horologist.nav


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize


import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.wearNavComposable


/*
 * MainActivity inherits AppCompatActivity which is a ComponentActivity
 */
class SimpleNavActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            navController = rememberSwipeDismissableNavController()
            WearNavApp(navController = navController)
        }
    }
}

@Composable
fun WearNavApp(navController: NavHostController) {
    // remove the swipe dismiss state since not pagerScreen is used.
//    val swipeDismissState = rememberSwipeToDismissBoxState()
//    val navState = rememberSwipeDismissableNavHostState(swipeDismissState)

    WearNavScaffold(
        startDestination = NavScreen.Menu.route,
        navController = navController,
        // state = navState
    ) {
        scalingLazyColumnComposable(
            route = NavScreen.Menu.route,
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 0) }
        ) {
            NavMenuScreen(
                navigateToRoute = { route -> navController.navigate(route)},
                scrollState = it.scrollableState,
                // focusRequester = remember { FocusRequester() } // todo: put it into a viewModel
                )
        }
        wearNavComposable(NavScreen.Activity.route) { _,_ ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Activity")
            }
        }
        wearNavComposable(NavScreen.Graph.route) { _,_ ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Graph")
            }
        }
        wearNavComposable(NavScreen.Setting.route) { _,_ ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Setting")
            }
        }
    }

}
