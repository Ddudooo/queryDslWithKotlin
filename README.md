# 실전! QueryDSL

코틀린으로 작성하는 JPA, QueryDSL 예제

## Gradle 설정

```kotlin
val queryDsl = "4.3.1"

plugins {   
		//...
    kotlin("kapt") version "1.4.32"
}

dependencies {
		//...
		implementation("com.querydsl:querydsl-jpa:${queryDsl}")
		kapt("com.querydsl:querydsl-apt:${queryDsl}:jpa")
		kaptTest("com.querydsl:querydsl-apt:${queryDsl}:jpa")
}
```
#### 참고
* [Using kapt](https://kotlinlang.org/docs/kapt.html#annotation-processor-arguments)
* [Kotlin 도입 과정에서 만난 문제와 해결 방법](https://d2.naver.com/helloworld/6685007)