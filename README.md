#  Spring Boot & Redis

## 

+ Spring Boot 2.1.0+
+ Redis
+ Lombok
+ Guava 28.0
+ Commons Validator 1.6
+ Commons Pool 2.6.0

## 



## 

**Step 0:  Redis**

```bash
# on Windows
scoop install redis
redis-server

# on Mac
brew install redis
redis-server
```

**Step 1:  `url-shortener` **

```console
$ mvn install
...
[INFO] BUILD SUCCESS
...
$ mvn spring-boot:run
...
2019-08-21 21:03:50.215  INFO 10244 --- [ main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2019-08-21 21:03:50.219  INFO 10244 --- [ main] i.g.y.s.u.UrlShortenerApplication        : Started UrlShortenerApplication in 6.01 seconds (JVM running for 12.165)
```

**Step 2:**

```console
$ curl -X POST http://127.0.0.1:8080/v1 \
  -H 'Content-Type: text/plain' \
  -d https://y0ngb1n.github.io
515bbe2b
```

**Step 3: **

```console
$ curl -X GET http://127.0.0.1:8080/v1/515bbe2b
https://y0ngb1n.github.io
```


```console
...
2019-08-21 21:42:26.788 DEBUG 10244 --- [nio-8080-exec-2] i.g.y.s.u.c.UrlShortenerController       : URL Id generated: 515bbe2b
2019-08-21 21:42:40.748 DEBUG 10244 --- [nio-8080-exec-3] i.g.y.s.u.c.UrlShortenerController       : URL Retrieved: https://y0ngb1n.github.io
```

## 🚀 

