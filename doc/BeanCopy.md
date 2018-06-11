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

OSGL bean copy framework relies on Java reflection to get internal structure of the source and target bean. Unlike some other bean copy tools, OSGL bean copy framework use field instead of getter/setter methods.

### 2.1 Semantic

OSGL mapping framework support the following five different semantics:

1. `SHALLOW_COPY`, copy the first level fields
2. `DEEP_COPY`, copy recursively until immutable type reached
3. `MERGE`, similar to DEEP_COPY, but append elements from source container to target container including array
4. `MAP`, similar to `DEEP_COPY`, with value type conversion support
5. `MERGE_MAP`, similar to `MERGE`, with value type conversion support

#### 2.1.1 Immutable type

Immutable type is an important concept. When OSGL detect a source bean property is immutable typed, it will stop dig further down the structure, and copy the reference to the target bean directly.

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

Here is an example of using special mapping rules:

```java
$.deepCopy(foo)
    .map("id").to("no")
    .map("subject").to("title")
    .to(bar);
```

The above call tells mapping framework to map `id` field in `foo` to `no` field in target `bar`, and map `subject` field in `foo` to `title` field in `bar`.

### 2.3 Filter

Filter can be used to skip copying/mapping certain fields. Filter is provided with a list of field names separated by `,`, if a field name is prefixed with `-` it means the field must not be copied/mapped. If the field were prefixed with `+` or without prefix, then it means the field shall be copied/mapped and the fields that are not mentioned shall NOT be copied/mapped. Examples:

* `-email,-password` - do not copy/map email and password fields, all other fields shall be copied/mapped
* `+email` - copy only email field, all other fields shall not be copied.
* `-cc.cvv` - do not copy `cvv` field in the instance of `cc` field, all other fields shall be copied
* `-cc,+cc.cvv` - copy `cvv` field in the instance of `cc` field, all other fields in the `cc` instance shall not be copied, all fields other than `cc` instance shall be copied.

To apply filter use the following API:

```java
$.deepCopy(foo).filter("-password,-address.streetNo").to(bar);
```

**Note** filter matches the field names in the target object.

### 2.4 root class

OSGL copy/mapping tool applied on fields instead of Getter/Setter methods. The exploring of fields of a bean is a recursive procedure till it reaches the `Object.class`. However there are cases that it needs to stop the fields exploring journey at a certain parent class. For example, suppose we have defined the following Base class:

```java
public abstract class ModelBase {
    public Date _created;
}
```

Your other model classes extends from `ModelBase`, and your Dao use the `_created` field to check whether the instance is new (when _created is `null`) or an existing record.

Now you want to copy an existing record int an new record to prepopulate that new record for updates, and in the end you will save the updated copy as an new record. Thus in this case you do not want to copy the `_created` field which is defined in `ModelBase`. Here is how to do it with `rootClass`:

```java
MyModel copy = $.copy(existing).rootClass(ModelBase.class).to(MyModel.class);
```

### 2.5 Target generic type

If you want to map from a container to another container with different element type, you need to provide the `targetGenericType` parameter to make it work:

```java
List<Foo> fooList = C.list(new Foo(), new Foo());
List<Bar> barList = C.newList();
$.map(fooList).targetGenericType(new TypeReference<List<Bar>>(){}).to(barList);
```

### 2.6 Type convert

If you need to map from a type to a different type field in the target bean, OSGL allows you to specify a type converter, for example, suppose you have a source bean defined as:

```java
public class RawData {
    Calendar date;
    public RawData(long currentTimeMillis) {
        date = Calendar.getInstance();
        date.setTimeInMillis(currentTimeMillis);
    }
}
```

And your target type is:

```java
public static class ConvertedData {
    DateTime date;
}
```

If you want to map from `RawData` to `ConvertedData`, you need a type converter to help convert `Calendar date` in `RawData` to `DateTime date` in `ConvertedData`:

```
public static Lang.TypeConverter<Calendar, DateTime> converter = new Lang.TypeConverter<Calendar, DateTime>() {
    @Override
    public DateTime convert(Calendar calendar) {
        return new DateTime(calendar.getTimeInMillis());
    }
};
```

Now you can use the following API to specify the converter defined:

```
@Test
public void testWithTypeConverter() {
    RawData src = new RawData($.ms());
    ConvertedData tgt = $.map(src).withConverter(converter).to(ConvertedData.class);
    eq(tgt.date.getMillis(), src.date.getTimeInMillis());
}
```

**Note**

1. you probably don't need to define too many type converters for common types as most of them has already been defined and registered in OSGL. See Converter document (TBD).

2. type converter only applied on MAP and MERGE_MAP semantic. For SHALLOW_COPY, DEEP_COPY and MERGE, converter will not be used.

#### 2.6.1 Convert hint

There are some case that your type convert relies on certain configuration. For example you want to convert a String to a Date, you need to provide the date format string as a convert hint; Another case is you convert a string to int, you can provide a convert int as the radix.

The source type:

```java
public static class RawDataV2 {
    String date;
    public RawDataV2(String date) {
        this.date = date;
    }
}
```

The target type:

```java
public static class ConvertedDataV2 {
    Date date;
}
```

As shown above, we need to map String typed date in source to Date typed date in target, so in the following code we provide the date format string as the hint:

```java
RawDataV2 src = new RawDataV2("20180518");
ConvertedDataV2 tgt = $.map(src).conversionHint(Date.class, "yyyyMMdd").to(ConvertedDataV2.class);
```

It's worth note that the hint `"yyyyMMdd"` is provided along with a type `Date.class`, this tells OSGL to use the hint when the convert target is of `Date.class` type.

### 2.7 Instance Factory

During the copy/mapping process it might need to create an new instance of a certain type, by default OSGL relies on `Lang.newInstance(Class)` call to create the new instance. In certain environment it might have the need to inject other instance create logic, for example when app running in ActFramework might want to delegate the instance creation to `Act.getInstance(Class)` call. OSGL allows it to

1. replace global instance factory
2. specify instance factory for one copy/mapping operation

#### 2.7.1 Register a global instance factory

Sample code of registering a global instance factory:

```java
OsglConfig.registerGlobalInstanceFactory(new $.Function<Class, Object>() {
    final App app = Act.app();
    @Override
    public Object apply(Class aClass) throws NotAppliedException, $.Break {
        return app.getInstance(aClass);
    }
});
```

