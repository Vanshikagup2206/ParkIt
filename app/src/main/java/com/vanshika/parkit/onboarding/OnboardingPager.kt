package com.vanshika.parkit.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vanshika.parkit.MainNavRoutes
import com.vanshika.parkit.ui.theme.ThemePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingPager(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 6 })

    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)

    val activeColor = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF1565C0)
    val inactiveColor = if (isDarkTheme) Color.Gray else Color.LightGray
    val buttonColor = if (isDarkTheme) Color(0xFF1565C0) else Color(0xFFD0E8FF)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --- Pager ---
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> WelcomeOnBoarding()
                    1 -> FindAndBookScreen()
                    2 -> GetInstantUpdatesScreen()
                    3 -> VoiceAssistanceScreen()
                    4 -> AdminAnalyticsScreen()
                    5 -> GetStartedScreen()
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Buttons Row (above dots) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    OnBoardingPreference.setOnBoardingShown(context)
                    navController.navigate(MainNavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text(
                        text = "Skip",
                        color = if (isDarkTheme) Color.White else Color(0xFF1565C0)
                    )
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < pagerState.pageCount - 1) {
                            safeScroll(scope, pagerState, pagerState.currentPage + 1)
                        } else {
                            OnBoardingPreference.setOnBoardingShown(context)
                            navController.navigate(MainNavRoutes.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(
                        text = if (pagerState.currentPage == pagerState.pageCount - 1)
                            "Get Started"
                        else
                            "Next",
                        color = if (isDarkTheme) Color.White else Color(0xFF0D47A1)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // --- Dots Indicator ---
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagerState.pageCount) { index ->
                    val isSelected = pagerState.currentPage == index
                    val animatedColor by animateColorAsState(
                        targetValue = if (isSelected) activeColor else inactiveColor
                    )
                    val size by animateDpAsState(
                        targetValue = if (isSelected) 12.dp else 8.dp
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(size)
                            .clip(CircleShape)
                            .background(animatedColor)
                            .clickable {
                                safeScroll(scope, pagerState, index)
                            }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun safeScroll(
    scope: CoroutineScope,
    pagerState: androidx.compose.foundation.pager.PagerState,
    targetPage: Int
) {
    val safeTarget = targetPage.coerceIn(0, pagerState.pageCount - 1)
    scope.launch {
        pagerState.animateScrollToPage(safeTarget)
    }
}