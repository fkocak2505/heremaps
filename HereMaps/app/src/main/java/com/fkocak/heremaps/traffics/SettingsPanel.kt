package com.fkocak.heremaps.traffics

import android.app.Activity
import android.view.View
import android.widget.RadioGroup
import android.widget.Switch
import com.fkocak.heremaps.R
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapTrafficLayer
import com.here.android.mpa.mapping.MapTransitLayer
import com.here.android.mpa.mapping.TrafficEvent


/**
 * This class encapsulates the properties and functionality of the settings panel,which provides the
 * UI elements to control the map attributes.
 */
class SettingsPanel(private val m_activity: Activity, private val m_map: Map) {
    // Initialize UI elements
    private var m_mapModeGroup: RadioGroup? = null
    private var m_mapTransitGroup: RadioGroup? = null
    private var m_flowSwitch: Switch? = null
    private var m_incidentSwitch: Switch? = null
    private fun initUIElements() {
        m_mapModeGroup = m_activity.findViewById<View>(R.id.mapModeRadioGroup) as RadioGroup
        m_mapTransitGroup = m_activity.findViewById<View>(R.id.transitGroup) as RadioGroup
        m_flowSwitch = m_activity.findViewById<View>(R.id.flowSwitch) as Switch
        m_incidentSwitch = m_activity.findViewById<View>(R.id.incidentSwitch) as Switch
        setUIListeners()
    }

    /**
     * Change map scheme as selected option.
     */
    private fun setUIListeners() {
        m_mapModeGroup!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.mapModeBtn -> m_map.mapScheme =
                    Map.Scheme.NORMAL_DAY
                R.id.hybridModeBtn -> m_map.mapScheme =
                    Map.Scheme.HYBRID_DAY
                R.id.terrainModeBtn -> m_map.mapScheme =
                    Map.Scheme.TERRAIN_DAY
                else -> {
                }
            }
        }
        /**
         * Change map transit layer as selected option
         */
        m_mapTransitGroup!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.nothingTransitbBtn -> m_map.mapTransitLayer!!.mode =
                    MapTransitLayer.Mode.NOTHING
                R.id.stopTransitBtn -> m_map.mapTransitLayer!!.mode =
                    MapTransitLayer.Mode.STOPS_AND_ACCESSES
                R.id.everythingTransitBtn -> m_map.mapTransitLayer!!.mode =
                    MapTransitLayer.Mode.EVERYTHING
                else -> {
                }
            }
        }
        /**
         * Enable or disable FLOW map traffic layer.
         */
        m_flowSwitch!!.setOnCheckedChangeListener { buttonView, isChecked -> /* TrafficInfo has to be turned on first */
            m_map.isTrafficInfoVisible = isChecked
            m_map.mapTrafficLayer.setEnabled(MapTrafficLayer.RenderLayer.FLOW, isChecked)
        }
        /**
         * Enable or disable INCIDENT map traffic layer.
         */
        m_incidentSwitch!!.setOnCheckedChangeListener { buttonView, isChecked -> /* TrafficInfo has to be turned on first */
            m_map.isTrafficInfoVisible = isChecked
            m_map.mapTrafficLayer.setEnabled(
                MapTrafficLayer.RenderLayer.INCIDENT,
                isChecked
            )
            m_map.mapTrafficLayer.displayFilter = TrafficEvent.Severity.VERY_HIGH
        }
    }

    init {
        initUIElements()
    }
}