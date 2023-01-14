/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.fitness

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import com.google.android.horologist.compose.tools.WearSquareDevicePreview
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.barChart
import com.himanshoe.charty.bar.config.BarConfig
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.axis.AxisConfig
import com.himanshoe.charty.common.dimens.ChartDimens

/**
 * use "charty" compose chart lib, which draw on Compose Canvas
 *
 * Details regarding the "charty" libs BarChart
 * https://github.com/hi-manshu/Charty
 * https://github.com/hi-manshu/Charty/blob/main/docs/BarChart.md
 *
 * @param state: BarChartState object, contains the chart data to display
 */
@Composable
fun ComposableBarChart(modifier: Modifier = Modifier, state: BarChartState, gradientBeginColor: Color, gradientEndColor: Color) {
    BarChart(
        modifier = modifier.size(width = 120.dp, height = 50.dp),
        onBarClick = { }, // not clickable
        colors = listOf(gradientBeginColor, gradientEndColor), // for gradient color, use param color for solid color
        barData = state.barData,
        chartDimens = ChartDimens(8.dp), // add chart padding to be 8.dp
        barConfig = BarConfig(hasRoundedCorner = true),
        axisConfig = AxisConfig(
            xAxisColor = Color.LightGray,
            showAxis = false, // not showing axis
            isAxisDashed = false,
            showUnitLabels = true,
            showXLabels = true,
            yAxisColor = Color.LightGray,
            textColor = Color.White // for dark mode else Color.Black
        )
    )
}


/**
 * our custom DrawScope function using predefined variables
 */
fun DrawScope.gradientShadeBarChart(
    state: BarChartState,
    gradientBeginColor: Color,
    gradientEndColor: Color
) {
    drawIntoCanvas {
        // chart lib DrawScope function
        barChart(
            barData = state.barData,
            colors = listOf(gradientBeginColor, gradientEndColor),
            onBarClick = {},
            axisConfig = AxisConfig(
                xAxisColor = Color.LightGray,
                showAxis = false, // not showing axis
                isAxisDashed = false,
                showUnitLabels = true,
                showXLabels = true,
                yAxisColor = Color.LightGray,
                textColor = Color.White // for dark mode else Color.Black
            ),
            barConfig = BarConfig(hasRoundedCorner = true),
            clickedBar = Offset(-10F, -10F),
            maxYValue = state.barData.maxOf {
                it.yValue
            } // get the highest Bar value as the max Y Value for the graph
        )
    }
}


/**
 * @param data  a list of Pair, while the first is x value of the BarChart and second is y float value of the BarChart
 */
data class BarChartState(val data: List<Pair<Any, Float>>) {
    val barData by lazy {
        this.data.map { BarData(it.first, it.second) }.toList()
    }
}

object BarChartHelper {
    val gradientBeginColor = Color(0xFF91AF4C)
    val gradientEndColor = Color(0xFFAF6B4C)
    /**
     * should the input has no equal size, will raise an IllegalArgumentException
     */
    private fun buildBarChartState(xList: List<String>, yList: List<Float>): BarChartState {
        require(xList.size == yList.size) { "xList, yList args must have the same length" }
        return BarChartState( xList.zip(yList) {x,y -> Pair(x,y)} )
    }

    fun demoBarChartState(): BarChartState {
        return buildBarChartState(
            xList = listOf("M", "T", "W", "T", "F"),
            yList = listOf(2f, 3f, 2f, 4f, 6f)
        )
    }

}

/**
 * Preview of chart called from Canvas Composable of the chart lib
 */
@WearSquareDevicePreview
@Composable
fun ComposableBarChartPreview() {
    val state = remember { BarChartHelper.demoBarChartState() }
    ComposableBarChart(
        state = state,
        gradientBeginColor = BarChartHelper.gradientBeginColor,
        gradientEndColor = BarChartHelper.gradientEndColor
    )
}

/**
 * Preview of chart called from DrawScope of the chart lib
 */
@WearSquareDevicePreview
@Composable
fun CanvasGradientShadePreview() {
    val state = remember { BarChartHelper.demoBarChartState() }
    Canvas(modifier = Modifier) {
        gradientShadeBarChart(
            state = state,
            gradientBeginColor = BarChartHelper.gradientBeginColor,
            gradientEndColor = BarChartHelper.gradientEndColor
        )
    }
}

