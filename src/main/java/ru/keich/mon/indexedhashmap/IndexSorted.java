package ru.keich.mon.indexedhashmap;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
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

public class IndexSorted<K, T> implements Index<K, T> {
	private final Function<T, Set<Object>> mapper;
	private final SortedMap<Object, Set<K>> objects = new TreeMap<>();

	public IndexSorted(Function<T, Set<Object>> mapper) {
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
		var entries = objects.entrySet();
		for (var entry : entries) {
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
		var out = new HashSet<K>();
		var val = objects.get(key);
		if (val != null) {
			out.addAll(val);
		}
		return out;
	}

	@Override
	public synchronized Set<K> getBefore(Object key) {
		var out = new HashSet<K>();
		for (var set : objects.headMap(key).values()) {
			out.addAll(set);
		}
		return out;
	}
	
	@Override
	public synchronized Set<K> getAfter(Object key) {
		var out = new HashSet<K>();
		var iter = objects.tailMap(key).entrySet().iterator();
		if(iter.hasNext()) {
			var e = iter.next();
			if(!e.getKey().equals(key)) {
				out.addAll(e.getValue());
			}
		}
		while(iter.hasNext()) {
			out.addAll(iter.next().getValue());
		}
		return out;
	}

	@Override
	public synchronized Set<K> getAfterEqual(Object key) {
		var out = new HashSet<K>();
		for (var set : objects.tailMap(key).values()) {
			out.addAll(set);
		}
		return out;
	}

	@Override
	public synchronized Set<K> getAfterFirst(Object key) {
		var out = new HashSet<K>();
		var iter = objects.tailMap(key).entrySet().iterator();
		if(iter.hasNext()) {
			var e = iter.next();
			if(!e.getKey().equals(key)) {
				out.addAll(e.getValue());
				return out;
			}
		}
		if(iter.hasNext()) {
			out.addAll(iter.next().getValue());
			return out;
		}
		return out;
	}

	@Override
	public synchronized Set<K> valueSet() {
		var out = new HashSet<K>();
		for (var set : objects.values()) {
			out.addAll(set);
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
