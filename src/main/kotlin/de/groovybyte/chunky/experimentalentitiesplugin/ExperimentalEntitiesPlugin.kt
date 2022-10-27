package de.groovybyte.chunky.experimentalentitiesplugin

import de.groovybyte.chunky.experimentalentitiesplugin.ui.ExperimentalEntitiesTab
import se.llbit.chunky.Plugin
import se.llbit.chunky.main.Chunky
import se.llbit.chunky.main.ChunkyOptions
import se.llbit.chunky.ui.ChunkyFx
import se.llbit.chunky.ui.render.RenderControlsTabTransformer

/**
 * @author ShirleyNekoDev
 */
class ExperimentalEntitiesPlugin : Plugin {
    override fun attach(chunky: Chunky) {
        if (!chunky.isHeadless) {
            attachTabs(chunky)
        }
    }

    private fun attachTabs(chunky: Chunky) {
        val oldTransformer: RenderControlsTabTransformer = chunky.renderControlsTabTransformer
        chunky.renderControlsTabTransformer = RenderControlsTabTransformer { tabs ->
            oldTransformer
                .apply(tabs)
                .apply { add(ExperimentalEntitiesTab(chunky)) }
        }
    }
}

fun main() {
    // Start Chunky normally with this plugin attached.
    Chunky.loadDefaultTextures()
    val chunky = Chunky(ChunkyOptions.getDefaults())
    ExperimentalEntitiesPlugin().attach(chunky)
    ChunkyFx.startChunkyUI(chunky)
}
