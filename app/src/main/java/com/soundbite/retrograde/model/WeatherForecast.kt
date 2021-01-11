package com.soundbite.retrograde.model

import com.soundbite.retrograde.toFormattedDate
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data model for storing weather data for any geographical coordinates
 * including current weather, minute forecast for 1 hour,
 * hourly forecast for 48 hours, daily forecast for 7 days,
 * national weather alerts, and historical weather data for the previous 5 days.
 *
 * @param lat Geographical coordinates of the location (latitude)
 * @param lon Geographical coordinates of the location (longitude)
 * @param timezone Timezone name for the requested location
 * @param timezoneOffset Shift in seconds from UTC
 * @param currentWeather Current weather data
 * @param minutely Minute forecast weather data
 * @param hourly Hourly forecast weather data
 * @param alerts National weather alerts data
 *
 */
@JsonClass(generateAdapter = true)
data class WeatherForecast(
        val lat: Double,
        val lon: Double,
        val timezone: String,
        @Json(name = "timezone_offset") val timezoneOffset: Long,
        @Json(name = "current") val currentWeather: CurrentWeather,
        val minutely: List<Minutely>,
        val hourly: List<Hourly>,
        val daily: List<Daily>,
        val alerts: List<Alerts>
) {
    override fun toString(): String {
        val coordinates = String.format("lat = %.2f, lon = %.2f", lat, lon)
        val timeData = String.format("timezone = %s, timezoneOffset = %d", timezone, timezoneOffset)
        return "COORDINATES:\n\t$coordinates\n" +
                "TIMEZONE:\n\t$timeData\n" +
                "CURRENT WEATHER:\n$currentWeather\n" +
                "MINUTELY:\n\t$minutely\n" +
                "HOURLY:\n\t$hourly\n" +
                "DAILY:\n\t$daily" +
                "ALERTS:\n\t$alerts"
    }
}

/**
 * Data model for storing the current weather data.
 *
 * @param dt Current time, Unix, UTC
 * @param sunrise Sunrise time, Unix, UTC
 * @param sunset Sunset time, Unix, UTC
 * @param temp Temperature
 * @param feelsLike Temperature. This temperature parameter accounts
 * for the human perception of weather
 * @param pressure Atmospheric pressure on the sea level, hPa
 * @param humidity Humidity, %
 * @param dewPoint Atmospheric temperature below which water droplets
 * begin to condense and dew can form
 * @param clouds Cloudiness, %
 * @param uvi Current UV index
 * @param visibility Average visibility, meters
 * @param windSpeed Wind speed. Units - default: meter/sec, metric: meter/sec, imperial: miles/hour
 * @param windGust Wind gust. Units - default: meter/sec, metric: meter/sec, imperial: miles/hour
 * @param windDeg Wind direction, degrees (meteorological)
 * @param rain Rain weather data
 * @param snow Snow weather data
 * @param weather Descriptions for the current weather data
 */
data class CurrentWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    @Json(name = "dew_point") val dewPoint: Double,
    val clouds: Int,
    val uvi: Double,
    val visibility: Int,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_gust") val windGust: Double? = null,
    @Json(name = "wind_deg")val windDeg: Int,
    val rain: Rain? = null,
    val snow: Snow? = null,
    val weather: List<Weather>
) {
    override fun toString(): String {
        return "\tdt = ${dt.toFormattedDate()}\n" +
                "\tsunrise = ${sunrise.toFormattedDate()}\n" +
                "\tsunset = ${sunset.toFormattedDate()}\n" +
                "\ttemp = ${"%.2f".format(temp)}\n" +
                "\tfeelsLike = $feelsLike\n" +
                "\tpressure = $pressure\n" +
                "\thumidity = $humidity\n" +
                "\tdewPoint = ${".2f".format(dewPoint)}\n" +
                "\tclouds = $clouds\n" +
                "\tuvi = $uvi\n" +
                "\tvisibility = $visibility\n" +
                "\twindSpeed = ${".2f".format(windSpeed)}\n" +
                "\twindGust = ${".2f".format(windGust)}\n" +
                "\twindDeg = ${".2f".format(windDeg)}\n" +
                "\train = $rain\n" +
                "\tsnow = $snow\n" +
                "\tweather = $weather\n"
    }
}

/**
 * Data model for storing the description for the current weather.
 *
 * @param id Weather condition id
 * @param main Group of weather parameters (Rain, Snow, Extreme, etc)
 * @param description Weather condition within the group
 * @param icon Weather icon id
 */
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) {
    override fun toString(): String {
        return "\tid = $id\n" +
                "\tmain = $main\n" +
                "\tdescription = $description\n" +
                "\ticon = $icon"
    }
}

/**
 * Data model for storing minute forecast weather data.
 *
 * @param dt Time of the forecasted data, unix, UTC
 * @param precipitation Precipitation volume, mm
 */
data class Minutely(
    val dt: Long,
    val precipitation: Int
) {
    override fun toString(): String {
        return "\tdt = ${dt.toFormattedDate()} " +
                "\tprecipitation = $precipitation\n"
    }
}

/**
 * Data model for storing rain data.
 *
 * @param volume Rain volume for last hour, mm
 */
data class Rain(
    @Json(name = "1h") val volume: Int
) {
    override fun toString(): String {
        return "\tvolume = $volume\n"
    }
}

/**
 * Data model for storing snow data.
 *
 * @param volume Snow volume for last hour, mm
 */
data class Snow(
    @Json(name = "1h") val volume: Int
) {
    override fun toString(): String {
        return "\tvolume = $volume\n"
    }
}

/**
 * Data model for storing the hourly forecast weather data.
 *
 * @param dt Time of the forecasted data, Unix, UTC
 * @param temp Temperature. Units - default: kelvin, metric: Celsius, imperial: Fahrenheit
 * @param feelsLike Temperature. This accounts for the human perception of weather.
 * Units - default: kelvin, metric: celsius, imperial: Fahrenheit
 * @param pressure Atmospheric pressure on the sea level. hPa
 * @param humidity Humidity, %
 * @param dewPoint Atmospheric temperature below which water droplets begin to condense
 * and dew can form. Units - default: kelvin, metric: Celsius, imperial: Fahrenheit
 * @param uvi UV index
 * @param clouds Cloudiness, %
 * @param visibility Average visibility, meters
 * @param windSpeed Wind speed. Units - default: meter/sec, metric: meter/sec, imperial: miles/hour
 * @param windGust Wind gust. Units - default: meter/sec, metric: meter/sec, imperial: miles/hour
 * @param windDeg Wind direction, degrees (meteorological)
 * @param pop Probability of precipitation
 * @param rain Rain weather data
 * @param snow Snow weather data
 * @param weather Descriptions for the hourly weather data
 */
data class Hourly(
    val dt: Long,
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    @Json(name = "dew_point")val dewPoint: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    @Json(name = "wind_speed")val windSpeed: Double,
    @Json(name = "wind_gust")val windGust: Double? = null,
    @Json(name = "wind_deg")val windDeg: Int,
    val pop: Double,
    val rain: Rain? = null,
    val snow: Snow? = null,
    val weather: List<Weather>
) {
    override fun toString(): String {
        return "\tdt = ${dt.toFormattedDate()}\n" +
                "\ttemp = $temp\n" +
                "\tfeelsLike = $feelsLike\n" +
                "\tpressure = $pressure\n" +
                "\thumidity = $humidity\n" +
                "\tdewPoint = ${".2f".format(dewPoint)}\n" +
                "\tuvi = $uvi\n" +
                "\tclouds = $clouds\n" +
                "\tvisibility = $visibility\n" +
                "\twindSpeed = ${".2f".format(windSpeed)}\n" +
                "\twindGust = ${".2f".format(windGust)}\n" +
                "\twindDeg = ${".2f".format(windDeg)}\n" +
                "\tpop = ${".2f".format(pop)}\n" +
                "\train = $rain\n" +
                "\tsnow = $snow\n" +
                "\tweather = $weather\n"
    }
}

/**
 * Data model for storing the daily forecast weather data.
 *
 * @param dt Time of the forecasted data, Unix, UTC
 * @param sunrise Sunrise time, Unix, UTC
 * @param sunset Sunset time, Unix, UTC
 * @param temp Temperature weather data for the day.
 * @param feelsLike Temperature. This accounts for the human perception of weather.
 * @param pressure Atmospheric pressure on the sea level. hPa
 * @param humidity Humidity, %
 * @param dewPoint Atmospheric temperature below which water droplets begin to condense
 * and dew can form. Units - default: kelvin, metric: Celsius, imperial: Fahrenheit
 * @param windSpeed Wind speed. Units - default: meter/sec, metric: meter/sec, imperial: miles/hour
 * @param windGust Wind gust. Units - default: meter/sec, metric: meter/sec, imperial: miles/hour
 * @param windDeg Wind direction, degrees (meteorological)
 * @param clouds Cloudiness, %
 * @param uvi UV index
 * @param pop Probability of precipitation
 * @param rain Rain weather data
 * @param snow Snow weather data
 * @param weather Descriptions for the hourly weather data
 */
data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Temp,
    @Json(name = "feels_like")val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    @Json(name = "dew_point")val dewPoint: Double,
    @Json(name = "wind_speed")val windSpeed: Double,
    @Json(name = "wind_gust")val windGust: Double? = null,
    @Json(name = "wind_deg")val windDeg: Int,
    val clouds: Int,
    val uvi: Double,
    val pop: Double,
    val rain: Rain? = null,
    val snow: Snow? = null,
    val weather: List<Weather>
) {
    override fun toString(): String {
        return "\tdt = ${dt.toFormattedDate()}\n" +
                "\tsunrise = ${sunrise.toFormattedDate()}\n" +
                "\tsunset = ${sunset.toFormattedDate()}\n" +
                "\ttemp = $temp\n" +
                "\tfeelsLike = $feelsLike\n" +
                "\tpressure = $pressure\n" +
                "\thumidity = $humidity\n" +
                "\tdewPoint = ${".2f".format(dewPoint)}\n" +
                "\twindSpeed = ${".2f".format(windSpeed)}\n" +
                "\twindGust = ${".2f".format(windGust)}\n" +
                "\twindDeg = ${".2f".format(windDeg)}\n" +
                "\tclouds = $clouds\n" +
                "\tuvi = $uvi\n" +
                "\tpop = ${".2f".format(pop)}\n" +
                "\train = $rain\n" +
                "\tsnow = $snow\n" +
                "\tweather = $weather\n"
    }
}

/**
 * Data model for storing various temperatures throughout one day.
 * Units - default: kelvin, metric: Celsius, imperial: Fahrenheit
 *
 * @param morn Morning temperature
 * @param day Day temperature
 * @param eve Evening temperature
 * @param night Night temperature
 * @param min Min daily temperature
 * @param max Max daily temperature
 */
data class Temp(
    val morn: Double,
    val day: Double,
    val eve: Double,
    val night: Double,
    val min: Double,
    val max: Double
) {
    override fun toString(): String {
        return "\tmorn = ${".2f".format(morn)}\n" +
                "\tday = ${".2f".format(day)}\n" +
                "\teve = ${".2f".format(eve)}\n" +
                "\tnight = ${".2f".format(night)}\n" +
                "\tmin = ${".2f".format(min)}\n" +
                "\tmax = ${".2f".format(max)}\n"
    }


}

/**
 * Data model for storing various temperatures (human perception factored in) throughout one day.
 * Units - default: kelvin, metric: celsius, imperial: Fahrenheit
 *
 * @param morn Morning temperature
 * @param day Day temperature
 * @param eve Evening temperature
 * @param night Night temperature
 */
data class FeelsLike(
    val morn: Double,
    val day: Double,
    val eve: Double,
    val night: Double
) {
    override fun toString(): String {
        return "\tmorn = ${".2f".format(morn)}\n" +
                "\tday = ${".2f".format(day)}\n" +
                "\teve = ${".2f".format(eve)}\n" +
                "\tnight = ${".2f".format(night)}\n"
    }
}

/**
 * Data model for storing national weather alert data from major national weather warning systems.
 *
 * @param senderName Name of the alert source
 * @param event Alert event name
 * @param start Date and time of the start of the alert, Unix, UTC
 * @param end Date and time of the end of the alert, Unix, UTC
 * @param description Description of the alert
 */
data class Alerts(
    @Json(name = "sender_name")val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
) {
    override fun toString(): String {
        return "\tevent = $event\n" +
                "\tstart = ${start.toFormattedDate()}\n" +
                "\tend = ${end.toFormattedDate()}\n" +
                "\tdescription = $description\n"
    }
}




