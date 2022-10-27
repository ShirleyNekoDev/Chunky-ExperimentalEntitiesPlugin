package de.groovybyte.chunky.experimentalentitiesplugin.ui

import de.groovybyte.chunky.experimentalentitiesplugin.entities.*
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.Node
import se.llbit.chunky.main.Chunky
import se.llbit.chunky.renderer.CameraViewListener
import se.llbit.chunky.renderer.scene.Scene
import se.llbit.chunky.renderer.scene.SceneManager
import se.llbit.chunky.ui.RenderControlsFxController
import se.llbit.chunky.ui.render.RenderControlsTab
import tornadofx.*

/**
 * @author ShirleyNekoDev
 */
class ExperimentalEntitiesTab(
    val chunky: Chunky
) : RenderControlsTab, Fragment() {

    override fun getTabTitle(): String = "Experimental Entities"

    override fun getTabContent(): Node = root

    private val sceneManager: SceneManager get() = chunky.sceneManager

    lateinit var cameraViewListener: CameraViewListener
    override fun setController(controller: RenderControlsFxController) {
        cameraViewListener = controller.chunkyController
    }

    private fun onInitialized() {
    }

    override fun update(scene: Scene) {
        // TODO: scene changed!
    }

    val nextEntityScale = SimpleDoubleProperty(1.0)

    override val root = vbox(10.0) {
        sceneProperty().onChangeOnce { onInitialized() }

        paddingAll = 10.0
        useMaxWidth = true

        label("Next Entity Scale")
        spinner(
            min = 0.1,
            max = 10.0,
            initialValue = 1.0,
            amountToStepBy = 0.1,
            editable = true,
            property = nextEntityScale,
            enableScroll = true
        )

        for (registration in EntityRegistry.registeredEntities) {
            button(registration.entityName) {
                action {
                    registration.spawn(sceneManager.scene, nextEntityScale.get())
                }
            }
        }
    }
}
