package org.filteredpush.qc.date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Exception identifying problems extracting a time from a string.
 * 
 * @author mole
 *
 */
public class TimeExtractionException extends Exception {

	private static final long serialVersionUID = -4492137609405770867L;
	private static final Log logger = LogFactory.getLog(TimeExtractionException.class);

	/**
	 *  default constructor
	 */
	public TimeExtractionException() {
		super("Error extracting time");
	}

	/**
	 * {@inheritDoc}
	 */
	public TimeExtractionException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public TimeExtractionException(Throwable cause) {
		super(cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public TimeExtractionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public TimeExtractionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
