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

3. Add dependency
   ```sh
  	<dependency>
  		<groupId>ru.keich.mon</groupId>
  		<artifactId>IndexedHashMap</artifactId>
  		<version>0.0.1</version>
  	</dependency>

<!-- GETTING STARTED -->
## Usage 

1. Create class extended BaseEntity;
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


4. 


