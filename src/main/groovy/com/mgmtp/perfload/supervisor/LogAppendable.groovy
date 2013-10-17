/*
 * Copyright (c) 2013 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mgmtp.perfload.supervisor

import org.apache.commons.lang3.StringUtils


/**
 * @author rnaegele
 */
class LogAppendable implements Appendable {

	@Override
	public Appendable append(CharSequence csq) throws IOException {
		if (StringUtils.isNotBlank(csq)) {
			println "\t$csq"
		}
		return this
	}

	@Override
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		return append(csq?.subSequence(start, end))
	}

	@Override
	public Appendable append(char c) throws IOException {
		return append(String.valueOf(c))
	}
}
