package com.trixiesoft.mychallenge.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.text.TextUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.Maybe
import java.io.IOException

class Geocode {
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

fun Address.name(): String {
    if (!TextUtils.isEmpty(featureName))
        return featureName
    if (maxAddressLineIndex > 0) {
        return getAddressLine(0)
    }
    if (locality != null) {
        return locality
    }
    if (adminArea != null) {
        return adminArea
    }
    if (countryCode != null) {
        return countryCode
    }
    return if (postalCode != null) {
        postalCode
    } else "???"
}