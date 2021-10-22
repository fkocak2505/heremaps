package com.fkocak.heremaps

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.here.android.mpa.common.*
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapRoute
import com.here.android.mpa.routing.*
import java.io.File
import java.util.*


class MainActivity : FragmentActivity() {
    private var map: Map? = null
    private var mapRoute: MapRoute? = null

    private var mapFragment: AndroidXMapFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMap()

        initRouter()
    }

    private fun router() {
        mapRoute?.let {
            map?.removeMapObject(it)
            mapRoute = null
        }
        createRoute(mutableListOf())
    }

    private fun createRoute(excludedRoutingZones: MutableList<RoutingZone>) {
        val coreRouter = CoreRouter()
        val routePlan = RoutePlan()

        val routeOptions = RouteOptions()
        routeOptions.transportMode =
            RouteOptions.TransportMode.CAR
        routeOptions.setHighwaysAllowed(true)
        routeOptions.routeType =
            RouteOptions.Type.FASTEST
        routeOptions.routeCount = 1

        if (excludedRoutingZones.isNotEmpty()) {
            routeOptions.excludeRoutingZones(toStringIds(excludedRoutingZones))
        }

        routePlan.routeOptions = routeOptions

        val startPoint = RouteWaypoint(GeoCoordinate(39.947294, 32.632387))
        val destination = RouteWaypoint(GeoCoordinate(39.908688, 32.776121))

        routePlan.addWaypoint(startPoint)
        routePlan.addWaypoint(destination)

        coreRouter.calculateRoute(
            routePlan,
            object : Router.Listener<List<RouteResult>, RoutingError> {
                override fun onProgress(i: Int) {

                }

                override fun onCalculateRouteFinished(
                    routeResults: List<RouteResult>,
                    routingError: RoutingError
                ) {

                    if (routingError == RoutingError.NONE) {
                        val route: Route = routeResults[0].route

                        mapRoute = MapRoute(route)

                        mapRoute?.isManeuverNumberVisible = true
                        map?.addMapObject(mapRoute!!)

                        map?.zoomTo(
                            route.boundingBox!!, Map.Animation.NONE,
                            Map.MOVE_PRESERVE_ORIENTATION
                        )

//                        calculateTta(route)

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Hata.. : $routingError",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun calculateTta(route: Route) {
        val ttaExcluding: RouteTta = route.getTtaExcludingTraffic(Route.WHOLE_ROUTE)!!
        val ttaIncluding: RouteTta = route.getTtaIncludingTraffic(Route.WHOLE_ROUTE)!!
        val tvInclude = findViewById<TextView>(R.id.title)
        tvInclude.text = "Tta included: ${ttaIncluding.duration}"
        val tvExclude = findViewById<TextView>(R.id.title1)
        tvExclude.text = "Tta excluded: ${ttaExcluding.duration}"
    }

    private fun toStringIds(excludedRoutingZones: MutableList<RoutingZone>): MutableList<String> {
        val ids: MutableList<String> = mutableListOf()
        for (zone in excludedRoutingZones) {
            ids.add(zone.id)
        }
        return ids
    }

    private fun initMap() {
        mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapfragment) as AndroidXMapFragment?

        MapSettings.setDiskCacheRootPath("${applicationContext.getExternalFilesDir(null)}" + File.separator.toString() + ".here-maps")

        mapFragment!!.init { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                map = mapFragment!!.map

                // Set the zoom level to the average between min and max
                map!!.zoomLevel = (map!!.maxZoomLevel + map!!.minZoomLevel) / 2

                map?.projectionMode = Map.Projection.MERCATOR

                map!!.setCenter(
                    GeoCoordinate(39.947294, 32.632387, 0.0),
                    Map.Animation.NONE
                )

            } else {
                println("ERROR: Cannot initialize Map Fragment")
            }
        }
    }

    private fun initRouter() {
        val mapEngine = MapEngine.getInstance()
        val appContext = ApplicationContext(this@MainActivity)
        mapEngine.init(appContext) { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                router()
            } else {
                println("ERROR: Routerr..")
            }
        }
    }
}