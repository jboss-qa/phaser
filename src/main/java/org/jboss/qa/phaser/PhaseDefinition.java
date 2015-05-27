/*
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.phaser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public class PhaseDefinition<A extends Annotation> implements Comparable<PhaseDefinition> {

	@Getter private Object id;
	@Getter private Object parentId;
	@NonNull @Getter private Number order;
	@NonNull @Getter private Phase phase;
	@NonNull @Getter private A annotation;
	@Getter private Method method;

	@Override
	public int compareTo(PhaseDefinition o) {
		return Double.compare(order.doubleValue(), o.order.doubleValue());
	}
}
