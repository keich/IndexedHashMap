<!-- ABOUT THE PROJECT -->

# About The Project
ConcurrentHashMap with indexing technology that improve performance. 
Used as an in-memory key-value database for Objects.

<!-- GETTING STARTED -->

# Getting Started

1. Clone the repo

```sh
   git clone https://github.com/github_username/repo_name.git
```
2. Install NPM packages

```sh
   npm install
```

3. Add dependency

```sh
  	<dependency>
  		<groupId>ru.keich.mon</groupId>
  		<artifactId>IndexedHashMap</artifactId>
  		<version>0.0.1</version>
  	</dependency>
```

<!-- GETTING STARTED -->

## Usage 

1. Create class extended BaseEntity

```java
public class TestEntity extends BaseEntity<String> {

	private final String name;
	private final Long version;
	private final Set<String> someSet;
	private final Map<String, String> someMap;

	public TestEntity(String id, String name, Long version, Set<String> someSet
			, Map<String, String> someMap) {
		super(id);
		this.name = name;
		this.version = version;
		this.someSet = someSet;
		this.someMap = someMap;
	}
}
```

2. Create instance of IndexedHashMap

```java
IndexedHashMap<String, TestEntity> store = new IndexedHashMap<>(null,
		this.getClass().getSimpleName());
```

3. Put and get data

```java
TestEntity entity = new TestEntity("key1", "Hello word", 1L, Collections.emptySet()
		, Collections.emptyMap());
store.put(entity);
TestEntity result = store.get("key1");
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
store.addIndex(TestEntity.FIELD_NAME, IndexType.EQUAL, TestEntity::getNameForIndex);
```

3. Query object from store

```java
QueryPredicate repdicate = Predicates.notEqual(TestEntity.FIELD_NAME, NAME1_VALUE);
Set<String> result = store.keySet(repdicate);
result.forEach(key -> {
	System.out.println(store.get(key));
});
```

### Index data type support

| Operator | Вescription  | Long | Class String  | Class Map.Entry | Class Set               |
| -------- | ------------ | -----| ------------- | --------------- | ----------------------- |
| NE       | Not equal    | ok   | ok            | Undefined behavior | Undefined behavior. Use NI |
| EQ       | Equal        | ok   | ok            | Equal key and value | Return object with set contains object  |
| LT       | Less than    | ok   | ok            | Exception   | Exception  |
| GT       | Gather than  | ok   | ok            | Exception   | Exception  |
| GE       | Gather equal | ok   | ok            | Exception   | Exception  |
| CO       | Contain      | Undefined behavior | search sub string | search Entry key equal and value sub string  |
| NC       | Not contain  | Undefined behavior  | vice versa CO |  vice versa CO  | Some string in set has sub string |
| NI       | Not include(uses for Set)  | Undefined behavior  |    vice versa CO   |   Undefined behavior  |  Return object with set not contains object |











