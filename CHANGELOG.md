# OSGL Tool Change Log

1.13.2 20/May/2018
* Make mapping framework support `C.Range` #92

1.13.1 19/May/2018
* Move `ISObject`, `IStorageService` and `SObject` file from osgl-storage project to the tool project as `IO` utility refers to these symbols

1.13.0 19/May/2018
* Add `Lang.resetArray(x)` utils #101
* Mapping framework - Add MERGE_MAPPING semantic #100
* Allow it to inject flexible logic to decide whether a class is immutable #99
* Mapping framework - improve filter spec handling #98
* Mapping framework: allow map between different names #97
* Mapping framework: copy from a container to another container should always be allowed #94
* Support internal cache #95

1.12.0 13/May/2018
* Allow it to change a mapping stage's semantic #91
* **compatible break change** Make `rootClass` specified in `Lang.fieldsOf()` be exclusive #90
* Allow waive global filter for a mapping process #89
* `Lang.bool("false")` returns `true` #88
* Add `Lang.isPrimitiveType(Class)` and `Lang.isWrapperType(Class)` method #87
* Add `Lang.isPrimitiveType(String)` method #86

1.11.4 11/May/2018
* Mapping global filter update #85

1.11.3 10/May/2018
* It cleared all fields when filter is specified #84
* Support global mapping filter #83

1.11.2 10/May/2018
* Support different copy semantic #82
* Mapping framework failed to do type case within Map #77
* Mapping from map to map key lost when using `Rule.KEYWORD_MATCHING` #78
* Mapping from map to map type lost when component type is a `ParameterizedType` #79
* Mapping into array always set on the first element in the array #80

1.11.1 8/May/2018
* Clean unused copy code from Lang
* Allow specify converter registry to _MapStage

1.11.0
* Provide a Mapping framework #75
* OSGL conversion framework refactory #76
    - new registry data structure
    - new converter path routing mechanism
* Provide a tool for bean copy #69
* `S.buffer(char[])` error #74
* Add `S.reversed(String)` method #73
* Add dotted style to `Keyword` #71
* `Lang.asEnum` - support Keyword based variation non-exact matching #72

1.10.0 - 5/Apr/2018
* Bulk fix to OSGL conversion framework
* Add fluent API for string split to `S` utility #68
* NullPointerException with `S.join(Iterable).by(String).get()` #67

1.9.0 - 30/Mar/2018
* Add fluent API to `org.osg.IO` class #66
* Create a byte array buffer to support `BufferedOutput` to handle binary data #65
* `BufferedOutput` cannot handle binary data properly #64
* `S.Buffer` - append byte array is not properly implemented #63

1.8.1 - 25/Mar/2018
* fix fastjson dependency version error

1.8.0 - 25/Mar/2018
* update fastjson to 1.2.47
* Enhacements to `S` namespace #62
* Add converter framework #61
* Add Image Utilities #60
* Add `WriterOutput` and `OutputStreamOutput` #59
* Add `BufferedOutput` utility class #58
* Add `IO.checksum` utility methods #57

1.7.3 - 15/Mar/2018
* `S.Buffer.reset()` semantic inconsistent with constructor #55
* Make S.Buffer thread local instance retention size be configurable #56
* Make crypto be null safe #54

1.7.2 - 13/Mar/2018
* The deprecated `IO.writeContent(String, Writer)` has empty method body #53

1.7.1 - 9/Mar/2018
* `Osgl.anyNull` logic error #52

1.7.0 - 4/Mar/2018
* Potential memory leak with `S.buffer()` #50
    - `S.buffer` set maximum threadlocal buffer instance size to be `512` bytes
* Add `Output` interface #49
* Add `IO.flush` methods #48
* `E.illegalArgumentIfNot` implement logic error #47


1.6.2
* Fix `StringValueResolver` NPE issue for `char[]` type

* 1.6.1
* Codec.encodeUrlSafeBase64 shall not use `.` to pad #46
* Add `char[]` StringValueResolver implementation #45
* Add `Osgl.isPrimitive(Class)` method
* Add `Crypto.passwordHash(char[])` method
* Add `$.getFieldValue(Object, Field)` method
* New methods in `E` utility:
    - `E.illegalArgumentIfNot(...)`
    - `E.illegalStateIfNot(...)`
    - `E.unsupportedIfNot(...)`

1.6.0 16/Jan/2017
* Improve exception handling of Osgl.invokeXxx methods #44

1.5.2 31/Dec/2017
* String value resolver: support `*` in numeric type value #42

1.5.1 28/Dec/2017
* `java.lang.StringIndexOutOfBoundsException` with `S.ensureStartsWith()` #41

1.5.0   
* Update build process - use osgl-maven-parent
* Enhance `CacheService` - incr/decr API #40
* `NullPointerException` on `N.isNumeric(var)` and `S.isNumeric(var)` when `var` is `null` #39
* Add filter method to `C.Map` #33
* Improve `C.Set` and `C.Map` API #29
* Add extract method to C #28
* Improve S API #27
* #26 Add method to generate Map from List
* #11 Make API accept Visitor be real Visitor

1.4.0
* Add alias to Osgl Tuple types #25 
* Add more getter method to Osgl tuple types #24 
* Add fast split APIs in `S` util #23 

1.3.1
* catch up 1.2.3 update

1.3
* Add a method to detect if a string is an integer #21 

1.2.3
* `Osgl.findPropertyParameterizedType` method shall treat array as List #22 

1.2.2
* `Osgl.getProperty` shall pickup the first element in an iterable if index not provided #20 
* `Osgl.getProperty` API shall handle array like a list #19 

1.2.1
* Treat `Locale` as simple type #18 
* Performance issue with `S.concat(...)` API #17 

1.2.0
* Add `Osgl.fill(element, array)` API methods #16
* Add `IO.readLines(URL)` method #15
* Add `N.isNumeric(String)` utility method #14
* Add `N.isNegative(BigDecimal)`,`N.isNegative(BigNumber)` ... methods #13
* Add `S.padLeft` and `S.padRight` methods #12

1.1.0
* Add new corresponding API for Osgl.asEnum(Class, String) and make it support case sensitive enum evaluation #6 
* Add `S.dos2unix(String)` and `S.unix2dos(String)` method #9 
* Add `S.concat(Object ...)` methods #10

1.0.3
* NPE on Generics.typeParamImplementations when passed in class is an interface #8

1.0.2
* FastStr.contentEquals failed when begin cursor is greater than size #7

1.0.1
* Osgl.asEnum(Class, String) API should be null safe #5

1.0
* base line from 0.11

0.11
* S.builder() APIs now reuse StringBuilder instance through ThreadLocal
* S.buffer() APIs is created to replace S.builder() APIs
* Added Osgl.asEnum(Class<? extends Enum>, String) API
* Add S.concat(String ...) API
* Add S.ensureEndsWith and S.ensureStartsWith API
* Add S.pathConcat API
* Add S.quote API
* Remove commons-codec from dependencies
* Add Codec.UTF_8 constant
* Add C.empty(Map) APIs
* Use ThreadLocalRandom to replace new Random
* Add Generics.buildTypeParamImplLookup(Class) API

0.10
* IO.copy now returns the number of bytes copied
* S.lowerFirst(String) API

0.9
* Provide better support for Iterator and Enumeration
* Move CacheService interface from osgl-cache to osgl-tool
* add C.List.split(Predicate) function #2
* Add KVStore.toMap() method #3
* Add IO.loadProperties(File) method
* Add IO.loadProperties(InputStream) method
* Add IO.loadProperties(String) method
* Add IO.loadProperties(Reader) method
* Add BigDecimalValueObjectCodec
* Fix ValueObject.toJSONString() issue when type is String and content contains illegal character, .e.g (")
* Fix issue in S.COMMON_SEP, change "[,;:\\s]" to "[,;:\\s]+"
* Add $.F.identity(Class) method
* Add Keyword utility
* Add N.isPerfectSquare(long) method
* Add $.IS_SERVER flag
* Add $.IS_64 flag
* Add FastStr.of(byte[], String) factory method
* Add IO.readLine(xxx, int limit) APIs to allow read limit lines from file/inputstream/reader
* Crypto encrypt and decrypt methods now accept byte[] as key and salt
* Add $.fieldOf(Class, String, Boolean) method
* Make Const class implement Serializable
* Add $.getMethod(Class, String, Class[]) method
* Add KV interface (implemented by KVStore)
* Add FastJsonKvCodec (fastjson serialize/deserialize KV) and FastJsonObjectCodec (convert JSONObject to/from ValueObject)
* Add Osgl.F.propertyExtractor API

0.8
* Rename "_" to "O" as "$" will be an illegal identifier after Java 8
* Increase unit test coverage
* Improve JavaDoc quality

0.7.0-SNAPSHOT
* _.newInstance(String) now will pass the Caller's class loader
* rename C.List.sort to C.List.sorted to avoid conflict with Java 8 List.sort
* rename StrBase.chars - StrBase.charArray to avoid conflict with Java 8 CharSequence.chars
* Add static T NPE(T) to osgl (_) class
* Add encodeUrlSafeBase64 and decodeUrlSafeBase64 method to Codec
* Add genSecret, genRandomDigit, genRandomStr to Crypto

0.7.1-SNAPSHOT
* Fix issue _.newInstance(...) failure when there are overloaded constructor with first parameter type is the same
* Add T _.async(_.F0<T>, long)
* Add N.round(float, int) and N.round(double, int) method
* Add C.Sequence.count(T) method

0.6.0-SNAPSHOT
* Add S.uuid() method
* Add new reader() and is() method to IO class dealing with URL and InputStreamReader
* Add eq2(Object, Object) to _, to allow do equal matching against two arrays
* Add asPrimitive() method set to _, to allow convert Object array to primitive array
* Add toString2() to _ to output array type object in a good format
* Add COMMON_SEP to S; add SPLIT and split(String) to S.F
* Better handling of primitive types in S.newInstance and S.classForName
* S.builder now accept primitive types and object types as constructor; original S.builder(int) semantic is now expressed in S.sizedBuilder(int)
* Add append(String) and prepend(String) functor to S.F
* Add reverse(array) methods to _
* add doFillInStackTrace to FastRuntimeException
* Fix issue: S.capFirst() throw StringIndexOutfBoundsException when parameter is empty string
* C.List.insert() now accept negative number to count the insert position from tail to head
* Add _.nil() method

0.5.5-SNAPSHOT
* Add C.List.unique(Comparator<T>) method

0.5.4-SNAPSHOT
* support primitives in _.forClassName and _.newInstace methods

0.5.3-SNAPSHOT
* add CONTAINS and contains to S.F

0.5.2-SNAPSHOT
* add random(List list) to _

0.5.1-SNAPSHOT
* Enhance Crypto encrypt/decrypt algorithm

0.5.0-SNAPSHOT
* _.Var implements _.Func0 so we can use it to implement "constant function" that
  takes a value and returns it when needed

0.4.5-SNAPSHOT
* ListBase.unique not working as expected

0.4.4-SNAPSHOT
* C.list(List) shouldn't sort the list
* Add S.F.IS_BLANK function
* Add S.F.NULL_SAFE function

0.4.3-SNAPSHOT
* Fix FeatureBase::setFeature NPE issue

0.4.2-SNAPSHOT
* Add "unique" to "C.List"
* remove "generalVisitor(final Function<? super T, ?> f)" from "_".
  "_.visitor(final Function<? super T, ?> f)" should be used instead

0.4.1-SNAPSHOT
* Add "public static ISObject zip(ISObject... objects)" method to IO utiltiy class

0.4-SNAPSHOT
* S.empty(String) now do not trim the string being passed in,
* Added S.blank(String) with the same semantic of previous S.empty(String)
* Fixed FastStr trim issue when begin, end pointer is not at default position
* FastStr now works with Java 6
* Unsafe now works with Java 6
* C enhancements:
** Set: add withIn, without, onlyIn for set operations
** added new set() method allows passing an collection to prepopulate the set
* OSGL updates:
** new classForName method allows passing class loader to load the class
* Iterators now is public class
* ListBuilder's new factor method allows passing initial capacity parameter

0.3.1-SNAPSHOT
* Added: ContextLocal, FastStr, Unsafe

0.3-SNAPSHOT
* base version when history log started
