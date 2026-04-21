package ru.keich.mon.indexedhashmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

public class IndexedHashMap<K, T> implements Map<K, T> {

	private Map<K, T> cache = new ConcurrentHashMap<>();
	private Map<String, Index<K, T>> index = new HashMap<>();
	private AtomicLong metricAdded = new AtomicLong(0);
	private AtomicLong metricUpdated = new AtomicLong(0);
	private AtomicLong metricRemoved = new AtomicLong(0);

	public IndexedHashMap() {
		super();
	}

	public void addIndexLongUniq(String name, Function<T, Long> mapper) {
		index.put(name, new IndexLongUniq<K, T>(mapper));
	}

	public void addIndexSmallInt(String name, int size, Function<T, Integer> mapper) {
		index.put(name, new IndexSmallInt<K, T>(mapper, size));
	}

	public void addIndexEqual(String name, Function<T, Set<Object>> mapper) {
		index.put(name, new IndexEqual<K, T>(mapper));;
	}
	
	public void addIndexSorted(String name, Function<T, Set<Object>> mapper) {
		index.put(name, new IndexSorted<K, T>(mapper));
	}
	
	public void addIndexUniqSorted(String name, Function<T, Set<Object>> mapper) {
		index.put(name, new IndexSortedUniq<K, T>(mapper));
	}
	
	public Set<String> getIndexNames() {
		return index.keySet();
	}

	@Override
	public T put(K key, T value) {
		return compute(key, (k, oldValue) -> value);
	}
	
	@Override
	public T putIfAbsent(K key, T value) {
		return compute(key, (k, old) -> (old == null) ? value : old);
	}
	
	private T _remove(K id, T obj) {
		for (var entry : index.entrySet()) {
			entry.getValue().remove(id, obj);
		}
		metricRemoved.incrementAndGet();
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T remove(Object key) {
		return compute((K) key, (k, v) -> null);
	}

	private T _update(K id, T oldObj, T newObj) {
		for (var entry : index.entrySet()) {
			entry.getValue().removeOldAndAppend(id, oldObj, newObj);
		}
		metricUpdated.incrementAndGet();
		return newObj;
	}

	private T _insert(K id, T obj) {
		for (var entry : index.entrySet()) {
			entry.getValue().append(id, obj);
		}
		metricAdded.incrementAndGet();
		return obj;
	}

	@Override
	public T compute(K key, BiFunction<? super K, ? super T, ? extends T> remappingFunction) {
		return cache.compute(key, (k, oldObj) -> {
			var newObj = remappingFunction.apply(k, oldObj);
			if (newObj == null) {
				return (oldObj != null) ?_remove(k, oldObj) : null;
			} 
			return (oldObj != null) ? _update(k, oldObj, newObj) : _insert(k, newObj);
		});
	}
	
	@Override
	public T computeIfAbsent(K key, Function<? super K, ? extends T> mappingFunction) {
		return compute(key, (k, old) -> (old == null) ? mappingFunction.apply(k) : old);
	}

	@Override
	public T computeIfPresent(K key, BiFunction<? super K, ? super T, ? extends T> remappingFunction) {
		return compute(key, (k, old) -> (old != null) ? remappingFunction.apply(key, old) : old);
	}

	@Override
	public T get(Object key) {
		return cache.get(key);
	}

	public List<T> get(Set<K> ids) {
		var out = new ArrayList<T>(ids.size());
		for (var id : ids) {
			var e = cache.get(id);
			if (e != null) {
				out.add(e);
			}
		}
		return out;
	}
	
	public Set<K> keySetPredicate(Function<T, Set<Object>> mapper, Predicate<Object> predicate) {
		Predicate<Map.Entry<K, T>> qPredicate = entry -> mapper.apply(entry.getValue())
				.stream()
				.filter(predicate)
				.findAny()
				.isPresent();
		return cache.entrySet().stream()
				.filter(qPredicate)
				.map(e -> e.getKey())
				.collect(Collectors.toSet());
	}
	
	public Set<K> keySetIndexEq(String fieldName, Object value) {
		return index.get(fieldName).get(value);
	}

	public Set<K> keySetIndexPredicate(String fieldName, Predicate<Object> predicate) {
		return index.get(fieldName).findByKey(predicate);
	}

	public Set<K> keySetIndexAll(String fieldName) {
		return index.get(fieldName).valueSet();
	}

	public Set<K> keySetIndexGetBefore(String fieldName, Object value) {
		return index.get(fieldName).getBefore(value);
	}

	public Set<K> keySetIndexGetAfterEqual(String fieldName, Object value) {
		return index.get(fieldName).getAfterEqual(value);
	}
	public Set<K> keySetIndexGetAfter(String fieldName, Object value) {
		return index.get(fieldName).getAfter(value);
	}

	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public boolean isEmpty() {
		return cache.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return cache.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return cache.containsValue(value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends T> m) {
		throw new UnsupportedOperationException("Method putAll is unsupported");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Method clear is unsupported");
	}

    /**
     * Returns a {@link Set} copy of the keys contained in this map.
     *
     * @return a set copy of the keys contained in this map
     */
	
	@Override
	public Set<K> keySet() {
		return new HashSet<K>(cache.keySet());
	}

	@Override
	public Collection<T> values() {
		throw new UnsupportedOperationException("Method values is unsupported");
	}

	@Override
	public Set<Entry<K, T>> entrySet() {
		throw new UnsupportedOperationException("Method entrySet is unsupported");
	}
	
	public Metrics getMetrics() {
		var idx = index.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> Long.valueOf(e.getValue().getSize())));
		var out = new Metrics(Long.valueOf(cache.size()), metricAdded.longValue(), metricUpdated.longValue(), metricRemoved.longValue(), idx);
		return out;
	}
	
}
