package ru.keich.mon.indexedhashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
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
	private final ReentrantLock lock = new ReentrantLock();

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
	public Set<K> findByKey(Predicate<Object> predicate) {
		lock.lock();
		try {
			var out = new HashSet<K>();
			for (Integer i = 0; i < size; i++) {
				if (predicate.test(i)) {
					out.addAll(objects[i]);
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
			put(mapper.apply(obj), id);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void remove(K id, T obj) {
		lock.lock();
		try {
			del(mapper.apply(obj), id);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<K> get(Object key) {
		lock.lock();
		try {
			Integer val = (Integer) key;
			return new HashSet<K>(objects[val]);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<K> getAll(Collection<Object> keys) {
		lock.lock();
		try {
			var out = new HashSet<K>();
			for (var key : keys) {
				Integer ikey = (Integer) key;
				out.addAll(objects[ikey]);
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
			Integer val = (Integer) key;
			var out = new HashSet<K>();
			for (int i = 0; i < val; i++) {
				out.addAll(objects[i]);
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
			Integer val = (Integer) key;
			var out = new HashSet<K>();
			for (int i = val + 1; i < size; i++) {
				out.addAll(objects[i]);
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
			Integer val = (Integer) key;
			var out = new HashSet<K>();
			for (int i = val; i < size; i++) {
				out.addAll(objects[i]);
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
			var out = new HashSet<K>();
			for (int i = 0; i < size; i++) {
				out.addAll(objects[i]);
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
			return size;
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
