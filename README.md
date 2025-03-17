# WMbb

A window manager for macos.

## Dependencies

| dependency | version |
|------------|---------|
| gum        | 0.16.0  |


## Tasks
Run any of bellow tasks with

``` shell
mx tasks run <TASK>
```

### uberjar

```shell
clj -T:build uberjar
```

### uberjar-for-native-compile

``` shell
clj -T:build uber-native
```

### native-compile

``` shell
native-image -jar target/echo.jar --no-fallback --no-server --features=clj_easy.graal_build_time.InitClojureClasses ./target/hello
```
