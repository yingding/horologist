/*
 * Copyright 2022 The Android Open Source Project
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

@file:OptIn(ExperimentalHorologistComposeToolsApi::class, ExperimentalHorologistTilesApi::class)

package com.google.android.horologist.fitness

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.wear.tiles.ColorBuilders.argb
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters
import androidx.wear.tiles.DimensionBuilders.DpProp
import androidx.wear.tiles.DimensionBuilders.ExpandedDimensionProp
import androidx.wear.tiles.DimensionBuilders.WrappedDimensionProp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.Image
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement
import androidx.wear.tiles.ResourceBuilders.ImageResource
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.material.Colors
import androidx.wear.tiles.material.Text
import androidx.wear.tiles.material.Typography
import androidx.wear.tiles.material.layouts.PrimaryLayout
import com.google.android.horologist.compose.tools.ExperimentalHorologistComposeToolsApi
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.compose.tools.WearLargeRoundDevicePreview
import com.google.android.horologist.compose.tools.WearPreviewDevices
import com.google.android.horologist.compose.tools.WearPreviewFontSizes
import com.google.android.horologist.fitness.ChartTileRenderer.ResourceState
import com.google.android.horologist.fitness.ChartTileRenderer.TileState
import com.google.android.horologist.sample.R
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi
import com.google.android.horologist.tiles.canvas.canvasToImageResource
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer

/**
 * Need to import the TileState, and ResourceState from ChartTileRenderer
 */
class ChartTileRenderer(context: Context) :
    SingleTileLayoutRenderer<TileState, ResourceState>(context) {
    override fun createTheme(): Colors = FitnessTheme

    private val expandedDimensionProp = ExpandedDimensionProp.Builder().build()

    private val wrapDimensionProp = WrappedDimensionProp.Builder().build()

    override fun renderTile(
        state: TileState,
        deviceParameters: DeviceParameters
    ): LayoutElement {
        return PrimaryLayout.Builder(deviceParameters)
            .setPrimaryLabelTextContent(
                Text.Builder(context, state.activities)
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(theme.onSurface))
                    .build()
            )
            .setContent(
                LayoutElementBuilders.Column.Builder()
                    .setWidth(expandedDimensionProp)
                    .setHeight(wrapDimensionProp)
                    .addContent(
                        Image.Builder()
                            .setResourceId(Runs)
                            .setHeight(DpProp.Builder().setValue(100f).build())
                            .setWidth(DpProp.Builder().setValue(100f).build())
                            .build()
                    )
                    .build()
            )
            .setPrimaryChipContent(
                Text.Builder(context, state.distance)
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(theme.onSurface))
                    .build()
            )
            .build()
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceResults: ResourceState,
        deviceParameters: DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        if (resourceResults.runsLive != null) {
            addIdToImageMapping(Runs, resourceResults.runsLive)
        } else if (resourceResults.runsPreview != null) {
            addIdToImageMapping(Runs, resourceResults.runsPreview)
        }
    }

    data class TileState(val activities: String, val distance: String)

    data class ResourceState(
        val runsPreview: ImageResource? = null,
        val runsLive: ImageResource? = null
    )

    companion object {
        const val Runs = "runs"
    }
}

@OptIn(ExperimentalHorologistComposeToolsApi::class, ExperimentalHorologistTilesApi::class)
@WearPreviewDevices
@WearPreviewFontSizes
@Composable
fun ChartTilePreview() {
    val context = LocalContext.current

    val tileState = remember {
        TileState(
            activities = "8 runs this week",
            distance = "68 km total",
        )
    }

    val resourceState = remember {
        ResourceState(
            runsPreview = drawableResToImageResource(R.drawable.runs), // drawable
            runsLive = null
        )
    }

    val renderer = remember {
        ChartTileRenderer(context)
    }

    TileLayoutPreview(
        tileState,
        resourceState,
        renderer
    )
}

@WearLargeRoundDevicePreview
@Composable
fun ChartTilePreviewNoRun() {
    val context = LocalContext.current

    val tileState = remember {
        TileState(
            activities = "No runs this week",
            distance = "0 km total",
        )
    }

    val resourceState = remember {
        ResourceState() // no resource
    }

    val renderer = remember {
        ChartTileRenderer(context)
    }

    TileLayoutPreview(
        tileState,
        resourceState,
        renderer
    )
}

@WearLargeRoundDevicePreview
@Composable
fun ChartTilePreviewWithDrawing() {
    val context = LocalContext.current

    val tileState = remember {
        TileState(
            activities = "8 runs this week",
            distance = "68 km total",
        )
    }
    val density = LocalDensity.current

    /* app bar chart compose canvas lib code */
    val state = remember { BarChartHelper.demoBarChartState() }

    val resourceState = remember {
        // draw with Compose Canvas, and set the size
        val runLive = canvasToImageResource(Size(160f, 100f), density) {
            /* app bar chart compose canvas lib code, the colors must be defined inside DrawScope or as constant */
            gradientShadeBarChart(
                state = state,
                gradientBeginColor = BarChartHelper.gradientBeginColor,
                gradientEndColor = BarChartHelper.gradientEndColor
            )
        }

        ResourceState(
            runsPreview = drawableResToImageResource(R.drawable.runs),
            runsLive = runLive
        )
    }

    val renderer = remember {
        ChartTileRenderer(context)
    }

    TileLayoutPreview(
        tileState,
        resourceState,
        renderer
    )
}
