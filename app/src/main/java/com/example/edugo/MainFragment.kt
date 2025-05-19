package com.example.edugo

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.collision.Plane
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await
import kotlinx.coroutines.launch
import java.util.Vector

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var arFragment : ArFragment
    private val arSceneView get() = arFragment.arSceneView
    private val scene get() = arSceneView.scene

    private var model: Renderable? = null
    private var modelView: ViewRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arFragment = (childFragmentManager.findFragmentById(R.id.arFragment) as ArFragment).apply {
            setOnSessionConfigurationListener { session, config ->
                // Modify the AR session configuration here
            }
            setOnViewCreatedListener { arSceneView ->
                arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL)
            }
//            setOnTapArPlaneListener(::onTapPlane)
            arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
                if (model == null || modelView == null) {
                    Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
                    return@setOnTapArPlaneListener
                }

                // Create the Anchor.
                scene.addChild(AnchorNode(hitResult.createAnchor()).apply {
                    addChild(TransformableNode(arFragment.transformationSystem).apply {
                        renderable = model
                        renderableInstance.setCulling(false)
                        renderableInstance.animate(true).start()
                        addChild(Node().apply {
                            localPosition = Vector3(0.0f, 1f, 0.0f)
                            localScale = Vector3(0.7f, 0.7f, 0.7f)
                            renderable = modelView
                        })
                    })
                })
            }

        }

        // Launch coroutine with repeatOnLifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED){
                loadModels()
            }
        }
    }

    private suspend fun loadModels(){
        model = ModelRenderable.builder()
            .setSource(context, Uri.parse("res/raw/gingerbread_house.obj"))
            .setIsFilamentGltf(true)
            .await()
        modelView = ViewRenderable.builder()
            .setView(context, R.layout.view_renderable_infos)
            .await()
    }

//    private fun onTapPlane(hitResult: HitResult, plane: Plane, motionEvent: MotionEvent){
//        if (model == null || modelView == null){
//            Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Create the Anchor
//        scene.addChild(AnchorNode(hitResult.createAnchor()).apply {
//            // Create the transformable model and add it to the anchor
//            addChild(TransformableNode(arFragment.transformationSystem).apply {
//                renderable = model
//                renderableInstance.setCulling(false)
//                renderableInstance.animate(true).start()
//                // Add the view
//                addChild(Node().apply {
//                    // Define the relative position
//                    localPosition = Vector3(0.0f, 1f, 0.0f)
//                    localScale = Vector3(0.7f, 0.7f, 0.7f)
//                    renderable = modelView
//                })
//            })
//        })
//    }

}