package ru.keich.mon.indexedhashmap;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

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

public class IndexEqual<K, T> implements Index<K, T> {
	private final Function<T, Set<Object>> mapper;
	private final Map<Object, Set<K>> objects = new HashMap<>();

	public IndexEqual(Function<T, Set<Object>> mapper) {
		this.mapper = mapper;
	}

	private void put(Object key, K id) {
		objects.compute(key, (k, set) -> {
			if (set == null) {
				set = new HashSet<>();
			}
			set.add(id);
			return set;
		});
	}

	private void del(Object key, K id) {
		objects.compute(key, (k, set) -> {
			if (set != null) {
				set.remove(id);
				if (set.isEmpty()) {
					return null;
				}
			}
			return set;
		});
	}

	@Override
	public synchronized Set<K> findByKey(Predicate<Object> predicate) {
		var out = new HashSet<K>();
		for (var entry : objects.entrySet()) {
			if (predicate.test(entry.getKey())) {
				out.addAll(entry.getValue());
			}
		}
		return out;
	}

	@Override
	public synchronized void append(K id, T obj) {
		mapper.apply(obj).forEach(key -> put(key, id));
	}

	@Override
	public synchronized void remove(K id, T obj) {
		mapper.apply(obj).forEach(key -> del(key, id));
	}

	@Override
	public synchronized Set<K> get(Object key) {
		var set = objects.get(key);
		if (set == null) {
			return Collections.emptySet();
		}
		return new HashSet<K>(set);
	}

	@Override
	public Set<K> getBefore(Object key) {
		throw new UnsupportedOperationException("Equal index has't this method");
	}

	@Override
	public Set<K> getAfter(Object key) {
		throw new UnsupportedOperationException("Equal index has't this method");
	}
	
	@Override
	public Set<K> getAfterEqual(Object key) {
		throw new UnsupportedOperationException("Equal index has't this method");
	}

	@Override
	public Set<K> getAfterFirst(Object key) {
		throw new UnsupportedOperationException("Equal index has't this method");
	}

	@Override
	public synchronized Set<K> valueSet() {
		var out = new HashSet<K>();
		var entries = objects.entrySet();
		for (var entry : entries) {
			out.addAll(entry.getValue());
		}
		return out;
	}

	@Override
	public synchronized int getSize() {
		return objects.size();
	}

	@Override
	public synchronized void removeOldAndAppend(K id, T oldObj, T newObj) {
		var oldSet = mapper.apply(oldObj);
		var newSet = mapper.apply(newObj);

		for (var key : oldSet) {
			if (!newSet.contains(key)) {
				del(key, id);
			}
		}
		for (var key : newSet) {
			if (!oldSet.contains(key)) {
				put(key, id);
			}
		}
	}

}
