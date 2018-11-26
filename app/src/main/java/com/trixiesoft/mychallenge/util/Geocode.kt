package com.trixiesoft.mychallenge.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.Maybe
import java.io.IOException

class Geocode {
    // minimal wrapper for the builtin Android geocoder, uses a maybe, skips if more than one result
    // I've experienced issues in the past when asking for only one result has very bad results
    // if given time I'd score the results manually
    companion object {
        fun fromName(context: Context, addressString: String, bounds: LatLngBounds?): Maybe<Address> {
            return Maybe.defer {
                if (!Geocoder.isPresent())
                    throw UnsupportedOperationException("Geocoder not available")
                val gc = Geocoder(context.applicationContext)
                val list: List<Address>
                try {
                    list = if (bounds != null) {
                        gc.getFromLocationName(
                            addressString, 5,
                            bounds.southwest.latitude, bounds.southwest.longitude,
                            bounds.northeast.latitude, bounds.northeast.longitude
                        )
                    } else {
                        gc.getFromLocationName(addressString, 5)
                    }
                } catch (error: IOException) {
                    return@defer Maybe.error<Address>(Exception("Failed to retrieve results"))
                }

                if (list.isEmpty())
                    // find something other than null to return for no address found
                    return@defer Maybe.empty<Address>()
                else {
                    // look for the address that is within the bounds first, the first if none are
                    for (address in list) {
                        if (address.hasLatitude() && address.hasLatitude()) {
                            val point = LatLng(address.latitude, address.longitude)
                            if (bounds!!.contains(point))
                                return@defer Maybe.just(address)
                        }
                    }
                    return@defer Maybe.just(list[0])
                }
            }
        }
    }
}

// simple address simplification
fun Address.toSingleLine(): String {
    val sb = StringBuilder()
    if (maxAddressLineIndex > -1) {
        // we have an address...
        for (ii in 0..maxAddressLineIndex) {
            if (sb.isNotEmpty())
                sb.append(" ")
            sb.append(getAddressLine(ii))
        }
        return sb.toString()
    }
    if (locality != null) {
        sb.append(locality)
        sb.append(" ")
    }
    if (adminArea != null) {
        sb.append(adminArea)
        sb.append(" ")
    }
    if (countryCode != null) {
        sb.append(countryCode)
        sb.append(" ")
    }
    if (postalCode != null) {
        sb.append(postalCode)
        sb.append(" ")
    }
    return sb.toString()
}
