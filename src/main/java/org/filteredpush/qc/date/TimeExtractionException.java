package org.filteredpush.qc.date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimeExtractionException extends Exception {

	private static final long serialVersionUID = -4492137609405770867L;
	private static final Log logger = LogFactory.getLog(TimeExtractionException.class);

	public TimeExtractionException() {
		// TODO Auto-generated constructor stub
	}

	public TimeExtractionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public TimeExtractionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public TimeExtractionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TimeExtractionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
