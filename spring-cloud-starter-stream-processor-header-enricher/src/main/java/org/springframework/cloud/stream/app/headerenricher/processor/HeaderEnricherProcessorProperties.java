/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.headerenricher.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Header Enricher Processor module.
 *
 * @author Gary Russell
 */
@ConfigurationProperties("headerenricher")
public class HeaderEnricherProcessorProperties {

	/**
	 * a JSON map document representing headers in which values are SpEL expressions, e.g {"h1":"exp1","h2":"exp2"}
	 */
	private String headers;

	/**
	 *  set to true to overwrite any existing message headers
	 */
	private boolean overwrite = false;

	public String getHeaders() {
		return this.headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public boolean isOverwrite() {
		return this.overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

}
