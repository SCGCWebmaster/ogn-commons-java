/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.AircraftBeaconWithDescriptor;
import org.ogn.commons.beacon.impl.AircraftDescriptorImpl;
import org.ogn.commons.beacon.impl.aprs.AprsAircraftBeacon;
import org.ogn.commons.beacon.impl.aprs.AprsLineParser;

public class JsonUtilsTest {
	AprsLineParser parser = AprsLineParser.get();

	@Test
	public void test() {
		String aprsSentence = "PH-844>APRS,qAS,Veendam:/102529h5244.42N/00632.07E'089/077/A=000876 id06DD82AC -474fpm +0.1rot 7.8dB 1e +0.7kHz gps2x3 hear8222 hear3342";

		AircraftBeacon beacon = (AircraftBeacon) parser.parse(aprsSentence);

		String json = JsonUtils.toJson(beacon);
		assertNotNull(json);

		AircraftBeacon beacon2 = JsonUtils.fromJson(json, AprsAircraftBeacon.class);
		assertNotNull(beacon2);

		assertEquals(beacon, beacon2);
		assertNotSame(beacon, beacon2);

		aprsSentence = "FLRDD9350>APRS,qAS,EHHO:/102537h5243.82N/00631.41E'/A=000026 id06DD9350 +020fpm +0.0rot 33.8dB 0e +3.0kHz gps3x3 hear8222 hear82AC";
		beacon = (AircraftBeacon) parser.parse(aprsSentence);

		json = JsonUtils.toJson(beacon);
		assertNotNull(json);

		beacon2 = JsonUtils.fromJson(json, AprsAircraftBeacon.class);
		assertNotNull(beacon2);

		assertEquals(beacon, beacon2);
		assertNotSame(beacon, beacon2);
	}

	@Test
	public void test2() {
		final String aprsSentence = "FLRDD9350>APRS,qAS,EHHO:/102537h5243.82N/00631.41E'/A=000026 id06DD9350 +020fpm +0.0rot 33.8dB 0e +3.0kHz gps3x3 hear8222 hear82AC";
		final AircraftBeacon beacon = (AircraftBeacon) parser.parse(aprsSentence);

		AircraftBeaconWithDescriptor beaconWithDescr = new AircraftBeaconWithDescriptor(beacon, Optional.empty());

		AircraftBeaconWithDescriptor beaconWithDescr2 = JsonUtils.fromJson(JsonUtils.toJson(beaconWithDescr),
				AircraftBeaconWithDescriptor.class);

		assertFalse(beaconWithDescr.getDescriptor().isPresent());
		assertEquals(beaconWithDescr, beaconWithDescr2);

		beaconWithDescr = new AircraftBeaconWithDescriptor(beacon,
				Optional.of(new AircraftDescriptorImpl("F-ABC", "XY", "Cirrus75", true, true)));

		beaconWithDescr2 = JsonUtils.fromJson(JsonUtils.toJson(beaconWithDescr), AircraftBeaconWithDescriptor.class);

		assertTrue(beaconWithDescr2.getDescriptor().isPresent());
		assertEquals(beaconWithDescr, beaconWithDescr2);
	}
}
