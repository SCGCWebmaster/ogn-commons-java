package org.ogn.commons.db.ogn;

import org.ogn.commons.beacon.AircraftDescriptor;
import org.ogn.commons.beacon.impl.AircraftDescriptorImpl;
import org.ogn.commons.db.FileDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles OGN ddb.
 * 
 * @author wbuczak
 */
public class OgnDb extends FileDb {

	private static final Logger LOG = LoggerFactory.getLogger(OgnDb.class);

	private static final String DEFAULT_DEVICES_DB_URL = "https://ddb.glidernet.org/download/";

	private static final String DELIMITER = ",";
	private static final String COMMENT = "#";
	private static final String YES = "Y";

	public OgnDb() {
		this(DEFAULT_DEVICES_DB_URL);
	}

	public OgnDb(String dbFileUri) {
		super(dbFileUri);
	}

	@Override
	protected AircraftDescriptorWithId processLine(String line) {

		final String str = line.trim();

		// skip header line
		if (str.length() == 0 || line.startsWith(COMMENT))
			return null;

		LOG.trace(line);

		final String[] tokens = str.split(DELIMITER);

		if (tokens.length < 6)
			throw new IllegalArgumentException("this line does not comply with the format: " + line);

		// DEVICE_TYPE,DEVICE_ID,AIRCRAFT_MODEL,REGISTRATION,CN,TRACKED,IDENTIFIED

		final String id = tokens[1].substring(1, tokens[1].length() - 1).trim();
		final String model = tokens[2].substring(1, tokens[2].length() - 1).trim();
		final String regNumber = tokens[3].substring(1, tokens[3].length() - 1).trim();
		final String cn = tokens[4].substring(1, tokens[4].length() - 1).trim();

		final String tracked = tokens[5].substring(1, tokens[5].length() - 1).trim();
		final String identified = tokens[6].substring(1, tokens[6].length() - 1).trim();

		final AircraftDescriptor desc = new AircraftDescriptorImpl(regNumber, cn, model, toBoolean(tracked),
				toBoolean(identified));

		return new AircraftDescriptorWithId(id, desc);
	}

	/**
	 * @param flag
	 * @return
	 */
	private static boolean toBoolean(String flag) {
		return flag.equalsIgnoreCase(YES) ? true : false;
	}

	@Override
	protected String getDefaultDbFileUri() {
		return DEFAULT_DEVICES_DB_URL;
	}

}