# Copy/Map between two structures

OSGL tool provides flexible support to allow developer copy/map between any two data structures.

## 1. API at a glance

```java
// do shallow copy from `foo` to `bar`
$.copy(foo).to(bar);

// deep copy from `foo` to `bar
$.deepCopy(foo).to(bar);

// deep copy using loose name match
$.deepCopy(foo).looseMatching().to(bar);

// deep copy with filter
$.deepCopy(foo).filter("-password,-address.streetNo").to(bar);

// deep copy with special name mapping rule
$.deepCopy(foo)
    .map("id").to("no")
    .map("subject").to("title")
    .to(bar);

// merge data from `foo` to `bar`
$.merge(foo).to(bar);

// map data from `foo` to `bar`
$.map(foo).to(bar);

// map data from `foo` to `bar` using strict name match
$.map(foo).strictMatching().to(bar);

// merge map data from `foo` to `bar`
$.mergeMap(foo).to(bar);
```

## 2. Concept

### 2.1 Semantic

OSGL mapping framework support the following five different semantics:

1. `SHALLOW_COPY`, copy the first level fields
2. `DEEP_COPY`, copy recursively until immutable type reached
3. `MERGE`, similar to DEEP_COPY, but append elements from source container to target container including array
4. `MAP`, similar to `DEEP_COPY`, with value type conversion support
5. `MERGE_MAP`, similar to `MERGE`, with value type conversion support

#### 2.1.1 Immutable type

The following types are considered to be immutable types:

* primitive types
* wrapper type of primitive types
* String
* Enum
* Any type that has been regisered into `OsglConfig` via `registerImmutableClassNames` API
* Any type that when applied to the predicate function in `OsglConfig` which is registered via `registerImmutableClassPredicate($.Predicate<Class>)` API, cause `true` returned.

### 2.2 Name mapping

OSGL mapping framework support the following three different name mapping rules:

1. Strict matching, require source name be equal to target name
2. Keyword matching or loose matching, match keyword of two names. For example, the following names are considered to be match to each other
    * foo_bar
    * foo-bar
    * fooBar
    * FooBar
    * Foo-Bar
    * Foo_Bar
3. Special matching rules can be set for each mapping process to match completely two different names.

### 2.3 Filter


