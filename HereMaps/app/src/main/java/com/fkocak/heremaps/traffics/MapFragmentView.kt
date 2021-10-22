package com.fkocak.heremaps.traffics

import android.app.AlertDialog
import android.graphics.PointF
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fkocak.heremaps.R
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.MapSettings
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.common.ViewObject
import com.here.android.mpa.mapping.*
import com.here.android.mpa.mapping.Map
import java.io.File


/**
 * This class encapsulates the properties and functionality of the Map view.
 */
class MapFragmentView(private val m_activity: AppCompatActivity) {
    private var m_mapFragment: AndroidXMapFragment? = null
    private var m_map: Map? = null
    private var m_settingsBtn: ImageButton? = null
    private var m_settingsPanel: SettingsPanel? = null
    private var m_settingsLayout: LinearLayout? = null
    private val mapFragment: AndroidXMapFragment?
        get() = m_activity.supportFragmentManager.findFragmentById(R.id.mapfragment) as AndroidXMapFragment?

    private fun initMapFragment() {
        m_mapFragment = mapFragment
        val path = File(m_activity.getExternalFilesDir(null), ".here-map-data")
            .absolutePath
        MapSettings.setDiskCacheRootPath(path)
        if (m_mapFragment != null) {
            m_mapFragment!!.init { error ->

                if (error == OnEngineInitListener.Error.NONE) {
                    m_map = m_mapFragment!!.map

                    m_map!!.setCenter(
                        GeoCoordinate(52.500556, 13.398889, 0.0),
                        Map.Animation.NONE
                    )

                    m_map!!.zoomLevel =
                        (m_map!!.maxZoomLevel + m_map!!.minZoomLevel) / 2

                    initMapGestureListener()

                } else {
                    AlertDialog.Builder(m_activity).setMessage(
                        """
                            Error : ${error.name}
                            
                            ${error.details}
                            """.trimIndent()
                    )
                        .setTitle(R.string.engine_init_error)
                        .setNegativeButton(
                            android.R.string.cancel
                        ) { _, _ -> m_activity.finish() }.create().show()
                }
            }
        }
    }

    private fun initSettingsPanel() {
        m_settingsBtn = m_activity.findViewById<View>(R.id.settingButton) as ImageButton

        /* click settings panel button to open or close setting panel. */
        m_settingsBtn!!.setOnClickListener {
            m_settingsLayout =
                m_activity.findViewById<View>(R.id.settingsPanelLayout) as LinearLayout
            if (m_settingsLayout!!.visibility == View.GONE) {
                m_settingsLayout!!.visibility = View.VISIBLE
                if (m_settingsPanel == null) {
                    m_settingsPanel = SettingsPanel(m_activity, m_map!!)
                }
            } else {
                m_settingsLayout!!.visibility = View.GONE
            }
        }
    }

    private fun initMapGestureListener(){
        m_mapFragment?.mapGesture?.addOnGestureListener(object : MapGesture.OnGestureListener{
            override fun onPanStart() {

            }

            override fun onPanEnd() {

            }

            override fun onMultiFingerManipulationStart() {

            }

            override fun onMultiFingerManipulationEnd() {

            }

            override fun onMapObjectsSelected(p0: MutableList<ViewObject>): Boolean {
                for (obj in p0) {
                    if (obj.baseType == ViewObject.Type.PROXY_OBJECT) {
                        val proxyObj = obj as MapProxyObject
                        if (proxyObj.type == MapProxyObject.Type.TRAFFIC_EVENT) {
                            val trafficEventObj = proxyObj as TrafficEventObject
                            val trafficEvent = trafficEventObj.trafficEvent

                            println("EVENT: ${trafficEvent!!.eventText}")
                        } else {
                            val trafficEventObj = proxyObj as TransitStopObject
                            val trafficEvent = trafficEventObj.transitStopInfo
                            println("EVENT: ${trafficEvent.officialName}")
                        }
                    }
                }

                return true
            }

            override fun onTapEvent(p0: PointF): Boolean {
                return true
            }

            override fun onDoubleTapEvent(p0: PointF): Boolean {
                return true
            }

            override fun onPinchLocked() {

            }

            override fun onPinchZoomEvent(p0: Float, p1: PointF): Boolean {
                return true
            }

            override fun onRotateLocked() {

            }

            override fun onRotateEvent(p0: Float): Boolean {
                return true
            }

            override fun onTiltEvent(p0: Float): Boolean {
                return true
            }

            override fun onLongPressEvent(p0: PointF): Boolean {
                return true
            }

            override fun onLongPressRelease() {

            }

            override fun onTwoFingerTapEvent(p0: PointF): Boolean {
                return true
            }
        }, 0, false)
    }

    init {
        initMapFragment()
        initSettingsPanel()
    }
}