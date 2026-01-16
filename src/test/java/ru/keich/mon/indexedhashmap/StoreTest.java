package ru.keich.mon.indexedhashmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import ru.keich.mon.indexedhashmap.query.QueryPredicate;

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

public class StoreTest {

	private static final String ID1_VALUE = "id1";
	private static final String ID2_VALUE = "id2";
	private static final String ID3_VALUE = "id3";
	private static final String NAME1_VALUE = "someTestName1";
	private static final String NAME2_VALUE = "someName2";
	private static final String NAME3_VALUE = "someName3";
	private static final Long VERSION1_VALUE = 1L;
	private static final Long VERSION2_VALUE = 2L;
	private static final Long VERSION3_VALUE = 3L;
	private static final String SET11_VALUE = "SomeSet11";
	private static final String SET12_VALUE = "SomeSet12";
	private static final String SET13_VALUE = "SomeSet13";
	private static final String SET21_VALUE = "SomeSet21";
	private static final String SET22_VALUE = "SomeSet22";
	private static final String SET23_VALUE = "SomeSet23";
	private static final String SET31_VALUE = "SomeSet31";
	private static final String SET32_VALUE = "SomeSet32";
	private static final String SET33_VALUE = "SomeSet33";
	private static final Entry<String, String> ENTRY11_VALUE = Map.entry("Field1", "Value1");
	private static final Entry<String, String> ENTRY12_VALUE = Map.entry("Field2", "Value2");
	private static final Entry<String, String> ENTRY13_VALUE = Map.entry("Field3", "ValueTest3");
	private static final Entry<String, String> ENTRY21_VALUE = Map.entry("Field4", "Value4");
	private static final Entry<String, String> ENTRY22_VALUE = Map.entry("Field5", "Value5");
	private static final Entry<String, String> ENTRY23_VALUE = Map.entry("Field6", "Value6");
	private static final Entry<String, String> ENTRY31_VALUE = Map.entry("Field7", "Value7");
	private static final Entry<String, String> ENTRY32_VALUE = Map.entry("Field8", "Value8");
	private static final Entry<String, String> ENTRY33_VALUE = Map.entry("Field9", "Value9");

	private void putDefaultTestEntities(IndexedHashMap<String, TestEntity> store) {
		var entity1 = new TestEntity(ID1_VALUE, NAME1_VALUE, VERSION1_VALUE,
				Set.of(SET11_VALUE, SET12_VALUE, SET13_VALUE),
				Map.ofEntries(ENTRY11_VALUE, ENTRY12_VALUE, ENTRY13_VALUE));
		var entity2 = new TestEntity(ID2_VALUE, NAME2_VALUE, VERSION2_VALUE,
				Set.of(SET21_VALUE, SET22_VALUE, SET23_VALUE),
				Map.ofEntries(ENTRY21_VALUE, ENTRY22_VALUE, ENTRY23_VALUE));
		var entity3 = new TestEntity(ID3_VALUE, NAME3_VALUE, VERSION3_VALUE,
				Set.of(SET31_VALUE, SET32_VALUE, SET33_VALUE),
				Map.ofEntries(ENTRY31_VALUE, ENTRY32_VALUE, ENTRY33_VALUE));
		store.put(entity1.getId(), entity1);
		store.put(entity2.getId(), entity2);
		store.put(entity3.getId(), entity3);
	}

	@Test
	public void queryEqualByField() {
		var store = new IndexedHashMap<String, TestEntity>();
		putDefaultTestEntities(store);
		var result = store.keySetPredicate(TestEntity::getNameForIndex,  k -> k.equals(NAME1_VALUE));
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryEqualByIndex() {
		var store = new IndexedHashMap<String, TestEntity>();
		store.addIndexEqual(TestEntity.FIELD_NAME, TestEntity::getNameForIndex);
		putDefaultTestEntities(store);
		var result = store.keySetIndexEq(TestEntity.FIELD_NAME, NAME1_VALUE);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotEqualByField() {
		var store = new IndexedHashMap<String, TestEntity>();
		putDefaultTestEntities(store);
		var result = store.keySetPredicate(TestEntity::getNameForIndex, k -> !k.equals(NAME1_VALUE));
		assertEquals(2, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotEqualByIndex() {
		var store = new IndexedHashMap<String, TestEntity>();
		store.addIndexEqual(TestEntity.FIELD_NAME, TestEntity::getNameForIndex);
		putDefaultTestEntities(store);
		var result = store.keySetIndexPredicate(TestEntity.FIELD_NAME, k -> !k.equals(NAME1_VALUE));
		assertEquals(2, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryLessThanByField() {
		var store = new IndexedHashMap<String, TestEntity>();
		putDefaultTestEntities(store);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		var result = store.keySetPredicate(TestEntity::getVersionForIndex, k -> ((Comparable)k).compareTo(VERSION2_VALUE) < 0);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
	}

	@Test
	public void queryLessThanByIndex() {
		var store = new IndexedHashMap<String, TestEntity>();
		store.addIndexLongUniq(TestEntity.FIELD_VERSION, TestEntity::getVersionForIndexLong);
		putDefaultTestEntities(store);
		var result = store.keySetIndexGetBefore(TestEntity.FIELD_VERSION, VERSION2_VALUE);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
	}

	@Test
	public void queryGreaterEqualByField() {
		var store = new IndexedHashMap<String, TestEntity>();
		putDefaultTestEntities(store);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		var result = store.keySetPredicate(TestEntity::getVersionForIndex, k -> ((Comparable)k).compareTo(VERSION2_VALUE) >= 0);
		assertEquals(2, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryGreaterEqualByIndex() {
		var store = new IndexedHashMap<String, TestEntity>();
		store.addIndexLongUniq(TestEntity.FIELD_VERSION, TestEntity::getVersionForIndexLong);
		putDefaultTestEntities(store);
		var result = store.keySetIndexGetAfterEqual(TestEntity.FIELD_VERSION, VERSION2_VALUE);
		assertEquals(2, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryGreaterThanByField() {
		var store = new IndexedHashMap<String, TestEntity>();
		putDefaultTestEntities(store);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		var result = store.keySetPredicate(TestEntity::getVersionForIndex, k -> ((Comparable)k).compareTo(VERSION2_VALUE) > 0);
		assertEquals(1, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryGreaterThanByIndex() {
		var store = new IndexedHashMap<String, TestEntity>();
		store.addIndexLongUniq(TestEntity.FIELD_VERSION, TestEntity::getVersionForIndexLong);
		putDefaultTestEntities(store);
		var result = store.keySetIndexGetAfter(TestEntity.FIELD_VERSION, VERSION2_VALUE);
		assertEquals(1, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryContainStringByField() {
		var store = new IndexedHashMap<String, TestEntity>();
		putDefaultTestEntities(store);
		var result = store.keySetPredicate(TestEntity::getNameUpperCaseForIndex, k -> k.toString().contains("Test"));
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryContainStringByIndex() {
		var store = new IndexedHashMap<String, TestEntity>();
		store.addIndexEqual(TestEntity.FIELD_NAME, TestEntity::getNameUpperCaseForIndex);
		putDefaultTestEntities(store);
		var result = store.keySetIndexPredicate(TestEntity.FIELD_NAME, k -> k.toString().contains("Test"));
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotContainStringByField() {
		var store = new IndexedHashMap<String, TestEntity>();
		putDefaultTestEntities(store);
		var result = store.keySetPredicate(TestEntity::getNameUpperCaseForIndex, k -> !k.toString().contains("Test"));
		assertEquals(2, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotContainStringByIndex() {
		var store = new IndexedHashMap<String, TestEntity>();
		store.addIndexEqual(TestEntity.FIELD_NAME, TestEntity::getNameUpperCaseForIndex);
		putDefaultTestEntities(store);
		var result = store.keySetIndexPredicate(TestEntity.FIELD_NAME, k -> !k.toString().contains("Test"));
		assertEquals(2, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryContainMapByField() {
		var store = new IndexedHashMap<String, TestEntity>();
		putDefaultTestEntities(store);
		var testEntry = Map.entry(ENTRY13_VALUE.getKey(), "Test");		
		@SuppressWarnings("rawtypes")
		Predicate<Object> predicate = (o) -> {
			var entry1 = (Entry) o;
			if (entry1.getKey().equals(testEntry.getKey())) {
				return entry1.getValue().toString().contains(testEntry.getValue().toString());
			}
			return false;
		};
		var result = store.keySetPredicate(TestEntity::getSomeMapForIndex, predicate);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryContainMapByIndex() {
		var store = new IndexedHashMap<String, TestEntity>();
		store.addIndexEqual(TestEntity.FIELD_SOMEMAP, TestEntity::getSomeMapForIndex);
		putDefaultTestEntities(store);
		var testEntry = Map.entry(ENTRY13_VALUE.getKey(), "Test");		
		@SuppressWarnings("rawtypes")
		Predicate<Object> predicate = (o) -> {
			var entry1 = (Entry) o;
			if (entry1.getKey().equals(testEntry.getKey())) {
				return entry1.getValue().toString().contains(testEntry.getValue().toString());
			}
			return false;
		};
		var result = store.keySetIndexPredicate(TestEntity.FIELD_SOMEMAP, predicate);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotContainMapByField() {
		var store = new IndexedHashMap<String, TestEntity>();
		putDefaultTestEntities(store);
		var testEntry = Map.entry(ENTRY13_VALUE.getKey(), "Hello");
		@SuppressWarnings("rawtypes")
		Predicate<Object> predicate = (o) -> {
			var entry1 = (Entry) o;
			if (entry1.getKey().equals(testEntry.getKey())) {
				return !entry1.getValue().toString().contains(testEntry.getValue().toString());
			}
			return false;
		};
		var result = store.keySetPredicate( TestEntity::getSomeMapForIndex, predicate);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotContainMapByIndex() {
		var store = new IndexedHashMap<String, TestEntity>();
		store.addIndexEqual(TestEntity.FIELD_SOMEMAP, TestEntity::getSomeMapForIndex);
		putDefaultTestEntities(store);
		var testEntry = Map.entry(ENTRY13_VALUE.getKey(), "Hello");
		@SuppressWarnings("rawtypes")
		Predicate<Object> predicate = (o) -> {
			var entry1 = (Entry) o;
			if (entry1.getKey().equals(testEntry.getKey())) {
				return !entry1.getValue().toString().contains(testEntry.getValue().toString());
			}
			return false;
		};
		var result = store.keySetIndexPredicate(TestEntity.FIELD_SOMEMAP, predicate);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotIncludeSetByField() {
		var store = new IndexedHashMap<String, TestEntity>();
		putDefaultTestEntities(store);		
		var has = store.keySetPredicate(TestEntity::getSomeSetForIndex, k -> k.equals(SET22_VALUE));
		var result = store.keySet();
		result.removeAll(has);
		assertEquals(2, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotIncludeSetByIndex() {
		var store = new IndexedHashMap<String, TestEntity>();
		store.addIndexEqual(TestEntity.FIELD_SOMESET, TestEntity::getSomeSetForIndex);
		putDefaultTestEntities(store);		
		var has = store.keySetIndexEq(TestEntity.FIELD_SOMESET, SET22_VALUE);
		var result = store.keySet();
		result.removeAll(has);
		assertEquals(2, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

}


// TODO add test other indexes