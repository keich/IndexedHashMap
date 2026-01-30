
# About The Project
ConcurrentHashMap with indexing technology that improve performance. 
Used as an in-memory key-value database for Objects.

# Getting Started

1. Clone the repository

```sh
   git clone https://github.com/keich/IndexedHashMap.git
```
2. Install NPM packages

```sh
   npm install
```

3. Add dependency to your project

```sh
  	<dependency>
  		<groupId>ru.keich.mon</groupId>
  		<artifactId>IndexedHashMap</artifactId>
  		<version>0.2.2</version>
  	</dependency>
```

## Usage 

1. Create class

```java
	import java.util.Collections;
	import java.util.Set;

	public class TestEntity {
		private final String name;

		public TestEntity(String name) {
			this.name = name;
		}

		public static final String FIELD_NAME = "name";

		public String getName() {
			return name;
		}

		public static Set<Object> getNameForIndex(TestEntity e) {
			return Collections.singleton(e.getName());
		}
	}
```

2. Create instance of IndexedHashMap

```java
	IndexedHashMap<String, TestEntity> store = new IndexedHashMap<>();
```

3. Put and get data

```java
	TestEntity entity = new TestEntity("Hello world");
	store.put("key1", entity);
	TestEntity result = store.get("key1");
```

4. Lock object for insert or update

```java
	store.compute("key1", (k, obj) -> {
	    // Object with key1 missing
	    if(obj == null) {
	        //Insert
	        return new TestEntity("Hello world 1");
	    }
	    // Object exists
	    // Replace
	    return new TestEntity("Hello wolrd 2");
	    // Or remove
	    //return null;
	});
```

## Work with indexes

1. Determine what data to put in the index 

```java
	public static final String FIELD_NAME = "name";
      
	public static Set<Object> getNameForIndex(TestEntity e) {
		return Collections.singleton(e.getName());
	}       
```
2. Do before insert data

```java
	store.addIndexEqual(TestEntity.FIELD_NAME, TestEntity::getNameForIndex);
```

3. Query objects from store

```java
	//Simple equal
	Set<String> result1 = store.keySetIndexEq(TestEntity.FIELD_NAME, "Hello world");
	//Search by index with predicate
	Predicate<Object> repdicate = (t) -> t.toString().contains("world");
	Set<String> result2 = store.keySetIndexPredicate(TestEntity.FIELD_NAME, repdicate);
	result2.forEach(key -> {
		System.out.println(store.get(key));
	});
```

### Index types

| Index class     | Description             |
| --------------- | ----------------------- |
| IndexEqual      | Index from HashMap      |
| IndexLongUniq   | Index from TreeMap for uniq Long values |
| IndexSorted     | Index from TreeMap for a objects implements Comparable |
| IndexSortedUniq | Index from TreeMap for uniq a objects implements Comparable |
| IndexSmallInt   | Index for Integer with small range(Uses array) |





