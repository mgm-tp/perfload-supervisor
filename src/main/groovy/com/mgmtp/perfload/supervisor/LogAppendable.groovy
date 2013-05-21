package com.mgmtp.perfload.supervisor

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author rnaegele
 */
class LogAppendable implements Appendable {
	private final Logger log = LoggerFactory.getLogger(getClass())

	boolean error

	@Override
	public Appendable append(CharSequence csq) throws IOException {
		if (error) {
			log.error(csq)
		}else {
			log.info(csq)
		}
		return this
	}

	@Override
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		if (csq == null) {
			return this
		}
		return append(csq.subSequence(start, end))
	}

	@Override
	public Appendable append(char c) throws IOException {
		return append(String.valueOf(c))
	}
}
