package ru.keich.mon.indexedhashmap;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import ru.keich.mon.indexedhashmap.query.predicates.QueryPredicate;

/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TestEntity extends BaseEntity<String> {

	public static final String FIELD_NAME = "name";
	public static final String FIELD_SOMESET = "someSet";
	public static final String FIELD_SOMEMAP = "someMap";
	public static final String FIELD_VERSION = "version";

	private final String name;
	private final Long version;
	private final Set<String> someSet;
	private final Map<String, String> someMap;

	public TestEntity(String id, String name, Long version, Set<String> someSet, Map<String, String> someMap) {
		super(id);
		this.name = name;
		this.version = version;
		this.someSet = someSet;
		this.someMap = someMap;
	}

	public static Set<Object> getNameUpperCaseForIndex(TestEntity e) {
		return Optional.ofNullable(e.getName()).map(s -> Collections.singleton((Object) s))
				.orElse(Collections.emptySet());
	}

	private String getName() {
		return name;
	}

	private Set<String> getSomeSet() {
		return someSet;
	}

	private Map<String, String> getSomeMap() {
		return someMap;
	}

	private Long getVersion() {
		return version;
	}

	public static Set<Object> getNameForIndex(TestEntity e) {
		return Collections.singleton(e.getName());
	}

	public static Long getVersionForIndex(TestEntity e) {
		return e.getVersion();
	}

	public static Set<Object> getSomeSetForIndex(TestEntity e) {
		return e.getSomeSet().stream().collect(Collectors.toSet());
	}

	public static Set<Object> getSomeMapForIndex(TestEntity e) {
		return e.getSomeMap().entrySet().stream().collect(Collectors.toSet());
	}

	public static boolean testVersion(TestEntity e, QueryPredicate predicate) {
		return predicate.test(e.getVersion());
	}

	@Override
	public String toString() {
		return "TestEntity [name=" + name + ", someSet=" + someSet + "]";
	}
}
