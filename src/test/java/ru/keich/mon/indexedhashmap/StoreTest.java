package ru.keich.mon.indexedhashmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ru.keich.mon.indexedhashmap.IndexedHashMap.IndexType;
import ru.keich.mon.indexedhashmap.query.predicates.Predicates;

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
		store.put(entity1);
		store.put(entity2);
		store.put(entity3);
	}

	private void queryEqual(IndexedHashMap<String, TestEntity> store) {
		putDefaultTestEntities(store);
		var repdicate = Predicates.equal(TestEntity.FIELD_NAME, NAME1_VALUE);
		var result = store.keySet(repdicate);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryEqualByField() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addQueryField(TestEntity.FIELD_NAME, TestEntity::getNameForIndex);
		queryEqual(store);
	}

	@Test
	public void queryEqualByIndex() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addIndex(TestEntity.FIELD_NAME, IndexType.EQUAL, TestEntity::getNameForIndex);
		queryEqual(store);
	}

	public void queryNotEqual(IndexedHashMap<String, TestEntity> store) {
		putDefaultTestEntities(store);
		var repdicate = Predicates.notEqual(TestEntity.FIELD_NAME, NAME1_VALUE);
		var result = store.keySet(repdicate);
		assertEquals(2, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotEqualByField() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addQueryField(TestEntity.FIELD_NAME, TestEntity::getNameForIndex);
		queryNotEqual(store);
	}

	@Test
	public void queryNotEqualByIndex() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addIndex(TestEntity.FIELD_NAME, IndexType.EQUAL, TestEntity::getNameForIndex);
		queryNotEqual(store);
	}

	public void queryLessThan(IndexedHashMap<String, TestEntity> store) {
		putDefaultTestEntities(store);
		var repdicate = Predicates.lessThan(TestEntity.FIELD_VERSION, VERSION2_VALUE);
		var result = store.keySet(repdicate);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
	}

	@Test
	public void queryLessThanByField() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addQueryFieldLong(TestEntity.FIELD_VERSION, TestEntity::getVersionForIndex);
		queryLessThan(store);
	}

	@Test
	public void queryLessThanByIndex() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addIndexLongUniq(TestEntity.FIELD_VERSION, TestEntity::getVersionForIndex);
		queryLessThan(store);
	}

	public void queryGreaterEqual(IndexedHashMap<String, TestEntity> store) {
		putDefaultTestEntities(store);
		var repdicate = Predicates.greaterEqual(TestEntity.FIELD_VERSION, VERSION2_VALUE);
		var result = store.keySet(repdicate);
		assertEquals(2, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryGreaterEqualByField() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addQueryFieldLong(TestEntity.FIELD_VERSION, TestEntity::getVersionForIndex);
		queryGreaterEqual(store);
	}

	@Test
	public void queryGreaterEqualByIndex() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addIndexLongUniq(TestEntity.FIELD_VERSION, TestEntity::getVersionForIndex);
		queryGreaterEqual(store);
	}

	public void queryGreaterThan(IndexedHashMap<String, TestEntity> store) {
		putDefaultTestEntities(store);
		var repdicate = Predicates.greaterThan(TestEntity.FIELD_VERSION, VERSION2_VALUE);
		var result = store.keySet(repdicate);
		assertEquals(1, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryGreaterThanByField() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addQueryFieldLong(TestEntity.FIELD_VERSION, TestEntity::getVersionForIndex);
		queryGreaterThan(store);
	}

	@Test
	public void queryGreaterThanByIndex() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addIndexLongUniq(TestEntity.FIELD_VERSION, TestEntity::getVersionForIndex);
		queryGreaterThan(store);
	}

	public void queryContainString(IndexedHashMap<String, TestEntity> store) {
		putDefaultTestEntities(store);
		var p1 = Predicates.contain(TestEntity.FIELD_NAME, "Test");
		var result = store.keySet(p1);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryContainStringByField() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addQueryField(TestEntity.FIELD_NAME, TestEntity::getNameUpperCaseForIndex);
		queryContainString(store);
	}

	@Test
	public void queryContainStringByIndex() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addIndex(TestEntity.FIELD_NAME, IndexType.EQUAL, TestEntity::getNameUpperCaseForIndex);
		queryContainString(store);
	}

	public void queryNotContainString(IndexedHashMap<String, TestEntity> store) {
		putDefaultTestEntities(store);
		var p1 = Predicates.notContain(TestEntity.FIELD_NAME, "Test");
		var result = store.keySet(p1);
		assertEquals(2, result.size());
		assertTrue(!result.contains(ID1_VALUE));
		assertTrue(result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotContainStringByField() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addQueryField(TestEntity.FIELD_NAME, TestEntity::getNameUpperCaseForIndex);
		queryNotContainString(store);
	}

	@Test
	public void queryNotContainStringByIndex() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addIndex(TestEntity.FIELD_NAME, IndexType.EQUAL, TestEntity::getNameUpperCaseForIndex);
		queryNotContainString(store);
	}

	public void queryContainMap(IndexedHashMap<String, TestEntity> store) {
		putDefaultTestEntities(store);
		var testEntry = Map.entry(ENTRY13_VALUE.getKey(), "Test");
		var p1 = Predicates.contain(TestEntity.FIELD_SOMEMAP, testEntry);
		var result = store.keySet(p1);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryContainMapByField() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addQueryField(TestEntity.FIELD_SOMEMAP, TestEntity::getSomeMapForIndex);
		queryContainMap(store);
	}

	@Test
	public void queryContainMapByIndex() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addIndex(TestEntity.FIELD_SOMEMAP, IndexType.EQUAL, TestEntity::getSomeMapForIndex);
		queryContainMap(store);
	}
	
	public void queryNotContainMap(IndexedHashMap<String, TestEntity> store) {
		putDefaultTestEntities(store);
		var testEntry = Map.entry(ENTRY13_VALUE.getKey(), "Hello");
		var p1 = Predicates.notContain(TestEntity.FIELD_SOMEMAP, testEntry);
		var result = store.keySet(p1);
		assertEquals(1, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(!result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotContainMapByField() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addQueryField(TestEntity.FIELD_SOMEMAP, TestEntity::getSomeMapForIndex);
		queryNotContainMap(store);
	}

	@Test
	public void queryNotContainMapByIndex() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addIndex(TestEntity.FIELD_SOMEMAP, IndexType.EQUAL, TestEntity::getSomeMapForIndex);
		queryNotContainMap(store);
	}

	public void queryNotIncludeSet(IndexedHashMap<String, TestEntity> store) {
		putDefaultTestEntities(store);
		var p1 = Predicates.notInclude(TestEntity.FIELD_SOMESET, SET22_VALUE);
		var result = store.keySet(p1);
		assertEquals(2, result.size());
		assertTrue(result.contains(ID1_VALUE));
		assertTrue(!result.contains(ID2_VALUE));
		assertTrue(result.contains(ID3_VALUE));
	}

	@Test
	public void queryNotIncludeSetByField() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addQueryField(TestEntity.FIELD_SOMESET, TestEntity::getSomeSetForIndex);
		queryNotIncludeSet(store);
	}

	@Test
	public void queryNotIncludeSetByIndex() {
		var store = new IndexedHashMap<String, TestEntity>(null, this.getClass().getSimpleName());
		store.addIndex(TestEntity.FIELD_SOMESET, IndexType.EQUAL, TestEntity::getSomeSetForIndex);
		queryNotIncludeSet(store);
	}

}
