package ru.keich.mon.indexedhashmap;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
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

public class IndexLongUniq<K, T> implements Index<K, T> {
	private final Function<T, Long> mapper;
	private final SortedMap<Object, K> objects = new TreeMap<>();
	private final ReentrantLock lock = new ReentrantLock();

	public IndexLongUniq(Function<T, Long> mapper) {
		this.mapper = mapper;
	}

	private void put(Long key, K id) {
		if (objects.containsKey(key)) {
			throw new UnsupportedOperationException("Already contains key " + key);
		}
		objects.put(key, id);
	}

	private void del(Long key, K id) {
		objects.remove(key);
	}

	@Override
	public Set<K> findByKey(Predicate<Object> predicate) {
		lock.lock();
		try {
			var out = new HashSet<K>();
			for (var entry : objects.entrySet()) {
				if (predicate.test(entry.getKey())) {
					out.add(entry.getValue());
				}
			}
			return out;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void append(K id, T entity) {
		lock.lock();
		try {
			put(mapper.apply(entity), id);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void remove(K id, T entity) {
		lock.lock();
		try {
			del(mapper.apply(entity), id);
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
				out.add(val);
			}
			return out;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<K> getBefore(Object key) {
		lock.lock();
		try {
			var out = new HashSet<K>();
			for (var val : objects.headMap(key).values()) {
				out.add(val);
			}
			return out;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<K> getAfter(Object key) {
		lock.lock();
		try {
			var out = new HashSet<K>();
			var iter = objects.tailMap(key).entrySet().iterator();
			if(iter.hasNext()) {
				var e = iter.next();
				if(!e.getKey().equals(key)) {
					out.add(e.getValue());
				}
			}
			while(iter.hasNext()) {
				out.add(iter.next().getValue());
			}
			return out;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public Set<K> getAfterEqual(Object key) {
		lock.lock();
		try {
			var out = new HashSet<K>();
			for (var val : objects.tailMap(key).values()) {
				out.add(val);
			}
			return out;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<K> valueSet() {
		lock.lock();
		try {
			return new HashSet<K>(objects.values());
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
			var oldVal = mapper.apply(oldObj);
			var newVal = mapper.apply(newObj);

			if (!oldVal.equals(newVal)) {
				del(oldVal, id);
				put(newVal, id);
			}
		} finally {
			lock.unlock();
		}
	}

}
