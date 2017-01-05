/*
 * Copyright 2015-2017 the original author or authors.
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.test.matcher.HeaderMatcher;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for the Header Enricher Processor.
 *
 * @author Gary Russell
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE,
	properties = { 	"headerenricher.headers={\"foo\" : \"'bar'\", \"baz\" : \"'fiz'\", \"buz\" : \"payload\", "
						+ "\"jaz\" : \"@value\" }",
					"headerenricher.overwrite = true" })
@DirtiesContext
public class HeaderEnricherProcessorIntegrationTests {

	@Autowired
	protected Processor channels;

	@Autowired
	protected MessageCollector collector;

	@Test
	public void test() throws Exception {
		this.channels.input().send(MessageBuilder.withPayload("hello").setHeader("baz", "qux").build());
		Message<?> out = this.collector.forChannel(this.channels.output()).poll(10, TimeUnit.SECONDS);
		assertThat(out, notNullValue());
		assertThat(out, HeaderMatcher.hasHeader("foo", equalTo("bar")));
		assertThat(out, HeaderMatcher.hasHeader("baz", equalTo("fiz")));
		assertThat(out, HeaderMatcher.hasHeader("buz", equalTo("hello")));
		assertThat(out, HeaderMatcher.hasHeader("jaz", equalTo("beanValue")));
	}

	@SpringBootApplication
	@Import(HeaderEnricherProcessorConfiguration.class)
	public static class HeaderEnricherProcessorApplication {

		@Bean
		public String value() {
			return "beanValue";
		}

	}

}
