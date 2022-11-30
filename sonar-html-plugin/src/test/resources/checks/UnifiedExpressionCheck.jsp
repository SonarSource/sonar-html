<a foo="${foo.myMethod1()}" />
<a foo="${foo:myMethod1()}" />
<a foo="${foo:myMethod2()}" />

<a foo="${}" />

<a th:text="#{foo.bar(${foo.bar})}"> </a>

<a foo="${{'one':1,}" />
<c:set var = "myMap" value = "${{'one':1, 'two':2}}"/>
