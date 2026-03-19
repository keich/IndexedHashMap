package ru.keich.mon.indexedhashmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
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
	private final ReentrantLock lock = new ReentrantLock();

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
	public Set<K> findByKey(Predicate<Object> predicate) {
		lock.lock();
		try {
			var out = new HashSet<K>();
			for (var entry : objects.entrySet()) {
				if (predicate.test(entry.getKey())) {
					out.addAll(entry.getValue());
				}
			}
			return out;
		} finally {
		    lock.unlock();
		}
	}

	@Override
	public void append(K id, T obj) {
		lock.lock();
		try {
			mapper.apply(obj).forEach(key -> put(key, id));
		} finally {
		    lock.unlock();
		}
	}

	@Override
	public void remove(K id, T obj) {
		lock.lock();
		try {
			mapper.apply(obj).forEach(key -> del(key, id));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<K> get(Object key) {
		lock.lock();
		try {
			var out = new HashSet<K>();
			var val = objects.get(key);
			if (val != null) {
				out.addAll(val);
			}
			return out;
		} finally {
			lock.unlock();
		}
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
	public Set<K> valueSet() {
		lock.lock();
		try {
			var out = new HashSet<K>();
			var entries = objects.entrySet();
			for (var entry : entries) {
				out.addAll(entry.getValue());
			}
			return out;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public int getSize() {
		lock.lock();
		try {
			return objects.size();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void removeOldAndAppend(K id, T oldObj, T newObj) {
		lock.lock();
		try {
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
		} finally {
			lock.unlock();
		}
	}

}
