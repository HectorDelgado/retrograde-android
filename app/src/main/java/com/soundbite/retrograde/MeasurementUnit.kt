package com.soundbite.retrograde

sealed class MeasurementUnit(val value: String)
class Standard: MeasurementUnit("standard")
class Metric: MeasurementUnit("metric")
class Imperial: MeasurementUnit("imperial")
