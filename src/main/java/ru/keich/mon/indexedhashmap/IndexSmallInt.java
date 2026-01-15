package ru.keich.mon.indexedhashmap;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/*
 * Copyright 2026 the original author or authors.
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

public class IndexSmallInt<K, T> implements Index<K, T> {
	;
	private final Function<T, Integer> mapper;
	private final HashSet<K> objects[];
	private final int size;

	@SuppressWarnings("unchecked")
	public IndexSmallInt(Function<T, Integer> mapper, int size) {
		this.mapper = mapper;
		this.size = size;
		objects = new HashSet[size];
		for (int i = 0; i < size; i++) {
			objects[i] = new HashSet<K>();
		}
	}

	private void put(Integer val, K id) {
		objects[val].add(id);
	}

	private void del(Integer val, K id) {
		objects[val].remove(id);
	}

	@Override
	public synchronized Set<K> findByKey(Predicate<Object> predicate) {
		var out = new HashSet<K>();
		for (Integer i = 0; i < size; i++) {
			if (predicate.test(i)) {
				out.addAll(objects[i]);
			}
		}
		return out;
	}

	@Override
	public synchronized void append(K id, T obj) {
		put(mapper.apply(obj), id);
	}

	@Override
	public synchronized void remove(K id, T obj) {
		del(mapper.apply(obj), id);
	}

	@Override
	public synchronized Set<K> get(Object key) {
		Integer val = (Integer) key;
		return new HashSet<K>(objects[val]);
	}

	@Override
	public synchronized Set<K> getBefore(Object key) {
		Integer val = (Integer) key;
		var out = new HashSet<K>();
		for (int i = 0; i < val; i++) {
			out.addAll(objects[i]);
		}
		return out;
	}

	@Override
	public synchronized Set<K> getAfter(Object key) {
		Integer val = (Integer) key;
		var out = new HashSet<K>();
		for (int i = val + 1; i < size; i++) {
			out.addAll(objects[i]);
		}
		return out;
	}

	@Override
	public synchronized Set<K> getAfterEqual(Object key) {
		Integer val = (Integer) key;
		var out = new HashSet<K>();
		for (int i = val; i < size; i++) {
			out.addAll(objects[i]);
		}
		return out;
	}

	@Override
	public synchronized Set<K> getAfterFirst(Object key) {
		var out = new HashSet<K>();
		Integer val = ((Integer) key) + 1;
		if (val < size) {
			out.addAll(objects[val]);
		}
		return out;
	}

	@Override
	public synchronized Set<K> valueSet() {
		var out = new HashSet<K>();
		for (int i = 0; i < size; i++) {
			out.addAll(objects[i]);
		}
		return out;
	}

	@Override
	public synchronized int getSize() {
		return size;
	}

	@Override
	public synchronized void removeOldAndAppend(K id, T oldObj, T newObj) {
		var oldVal = mapper.apply(oldObj);
		var newVal = mapper.apply(newObj);

		if (!oldVal.equals(newVal)) {
			del(oldVal, id);
			put(newVal, id);
		}
	}

}
