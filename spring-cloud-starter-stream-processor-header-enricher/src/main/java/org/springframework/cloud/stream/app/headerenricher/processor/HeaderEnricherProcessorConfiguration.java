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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.support.ExpressionEvaluatingHeaderValueMessageProcessor;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A Processor app that adds expression-evaluated headers.
 *
 * @author Gary Russell
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(HeaderEnricherProcessorProperties.class)
public class HeaderEnricherProcessorConfiguration {

	@Autowired
	private HeaderEnricherProcessorProperties properties;

	@Bean
	@Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
	public HeaderEnricher headerEnricher() throws Exception {
		Map<String, ExpressionEvaluatingHeaderValueMessageProcessor<?>> headersToAdd = new HashMap<>();
		if (StringUtils.hasText(this.properties.getHeaders())) {
			ObjectMapper objectMapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, String> headers = objectMapper.readValue(this.properties.getHeaders(), Map.class);
			for (Entry<String, String> entry : headers.entrySet()) {
				headersToAdd.put(entry.getKey(), processor(entry.getValue()));
			}
		}
		HeaderEnricher headerEnricher = new HeaderEnricher(headersToAdd);
		headerEnricher.setDefaultOverwrite(this.properties.isOverwrite());
		return headerEnricher;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // Need a new processor for each header
	public ExpressionEvaluatingHeaderValueMessageProcessor<?> processor(String expression) {
		return new ExpressionEvaluatingHeaderValueMessageProcessor<>(expression, null);
	}

}
