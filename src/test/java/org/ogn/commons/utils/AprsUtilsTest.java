/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;
import org.ogn.commons.utils.AprsUtils.Coordinate;

public class AprsUtilsTest {

	private static final int APRS_ID_MAX_LENGTH = 9;

	@Test
	public void testClientId() {
		String clientId = AprsUtils.generateClientId();
		assertTrue(clientId.contains("-"));
		assertTrue(clientId.length() <= APRS_ID_MAX_LENGTH);

		int last = 0;
		for (int i = 0; i < 100; i++) {
			clientId = AprsUtils.generateClientId();

			int n = -1;
			final String suffix = null;
			try {
				final String[] tokens = clientId.split("-");
				assertEquals(2, tokens.length);

				n = Integer.parseInt(tokens[1], 16);
				if (last != 0) {
					assertNotEquals(last, n);
				}
				last = n;
			} catch (final NumberFormatException ex) {
				fail("could not convert suffix: " + suffix + " to integer");
			}

		}

	}

	@Test
	public void generatePassTest() throws Exception {
		assertEquals(-1, AprsUtils.generatePass("EPZR"));
		assertEquals(-1, AprsUtils.generatePass("MyStation"));
		assertEquals(-1, AprsUtils.generatePass("AB-C01"));
	}

	@Test
	public void testDegToMeters() {
		assertEquals(111194.926, AprsUtils.degToMeters(1.0f), 0.01);
	}

	@Test
	public void testDegToDms() {
		final double lat = 51.179500000000004;
		final double lon = -1.0328333333333335;
		assertEquals(51.1077f, AprsUtils.degToDms(lat), 0.01f);
		assertEquals(1.0197f, AprsUtils.degToDms(lon), 0.01f);
		assertEquals(lat, AprsUtils.dmsToDeg(AprsUtils.degToDms(lat)), 1e-10f);
	}

	@Test
	public void testDegToIgc() {
		final double lat = 51.179500000000004;
		final double lon = -1.0328333333333335;

		assertEquals("5110770N", AprsUtils.degToIgc(lat, Coordinate.LAT));
		assertEquals("00101970W", AprsUtils.degToIgc(lon, Coordinate.LON));
	}

	@Test
	public void testToUtcTimestamp() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		final Date d = new Date();
		cal.setTime(d);

		long timestamp = d.getTime();
		final int h = cal.get(Calendar.HOUR_OF_DAY);
		final int m = cal.get(Calendar.MINUTE);
		final int s = cal.get(Calendar.SECOND);

		long t = AprsUtils.toUtcTimestamp(h, m, s);
		assertTrue(t - timestamp <= 100);

		// the timestamp should be rounded to a sec
		assertTrue(t % 1000 == 0);

		cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 23);
		cal.set(Calendar.SECOND, 44);

		timestamp = cal.getTimeInMillis() / 1000 * 1000;
		t = AprsUtils.toUtcTimestamp("062344");
		assertEquals(0, t - timestamp);
		assertTrue(t % 1000 == 0);

		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

		timestamp = cal.getTimeInMillis() / 1000 * 1000;

		t = AprsUtils.toUtcTimestamp("235959");
		assertEquals(0, t - timestamp);
		assertTrue(t % 1000 == 0);

		final LocalDate date = LocalDate.of(2016, 10, 5);
		final LocalTime time = LocalTime.of(11, 12, 33);
		t = AprsUtils.toUtcTimestamp(date, time.getHour(), time.getMinute(), time.getSecond());
		// the timestamp should be rounded to a sec
		assertTrue(t % 1000 == 0);
	}

	@Test
	public void testDistance() {

		double lat1 = 43.04283f;
		final double lon1 = 0.55f;

		final double lat2 = 43.466f;
		double lon2 = 0.72f;

		final long distMetres = Math.round(AprsUtils.calcDistance(lat1, lon1, lat2, lon2));
		assertEquals(49027, distMetres);

		double distKm = AprsUtils.calcDistanceInKm(lat1, lon1, lat2, lon2);
		assertEquals(49.03, distKm, 0.1);

		lat1 = 43.44383f;

		distKm = AprsUtils.calcDistanceInKm(lat1, lon1, lat2, lon2);
		assertEquals(13.94, distKm, 0.001);

		lon2 = -0.72f;
		distKm = AprsUtils.calcDistanceInKm(lat1, lon1, lat2, lon2);
		assertEquals(102.5, distKm, 0.1);
	}
}