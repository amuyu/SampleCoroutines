# SampleCoroutines
[kotlin.coroutines](https://github.com/Kotlin/kotlinx.coroutines) 사용법을 알아보기 위한
간단한 샘플입니다.

## light-weight threads
coroutines 에서는 launch라는 명령을 사용해서 thread 처럼 사용이 가능합니다.
아래는 일정 time이 지난 후에 TextView 의 색을 변경하는 코드입니다.
delay라는 명령도 coroutines 에서 제공하는 명령입니다.
```kotlin
launch(UI) {
  delay(time)
    changeColor()
}
```

coroutines 에서 구현된 thread 는 light-weight 하기 때문에 다음과 같은 구현이 가능합니다.
코틀린의 guide를 보면 thread를 사용해서 동일하게 구현하면 에러가 난다고 해서 아래 코드와
유사하게 작성해서 실행해보았더니 설명하고 있는대로 `out of memory`가 발생했습니다.
아래는 100000개의 리스트를 생성하는 코드입니다.
```kotlin
val jobs = List(100_000) { // create a lot of coroutines and list their jobs
    launch(CommonPool) {
        delay(1000L)
    }
}

jobs.forEach {
    it.join()
    addList()
} // wait for all jobs to complete
```

## Waiting for a job
coroutines는 coroutine 에서의 작업이 완료되는 시점을 알 수 있습니다.
launch 라는 명령은 Job을 리턴하는데 이 Job의 join() 명령을 사용하면
coroutine의 작업이 종료될 때 까지 대기 하다 이 후의 명령을 수행합니다.
아래는 UI에 1씩 증가하는 count를 표시하는 작업을 수행 후, 완료하게 되면
버튼을 활성화는 코드입니다.
```
mainJob = launch(CommonPool) {
    repeat(10) { i ->
        count = i
        showCount()
        delay(500L)
    }
}
mainJob?.join()
Logger.d("")
enableRunBtn(true)
```
여기서 join() 명령을 main coroutine 에서 사용하면
UI가 갱신되지 않고 작업이 완료될 때까지 멈춰있게 되니 주의해야겠습니다.

## Cancelation
thread 처럼 Job도 진행 중인 작업을 취소할 수 있습니다.
job의 cancel() 명령을 사용하면 작업 중인 coroutine을 종료합니다.
아래는 1씩 증가하는 count를 표시하는 작업을 버튼을 눌렀을 때, 멈추도록 하는 코드입니다.
```kotlin
mainJob = launch(CommonPool) {
    repeat(1000) { i ->
        count = i
        showCount()
        delay(500L)
    }
}

...
mainJob.cancel()

```

cancel() 명령을 호출했지만 loop가 특정 연산으로 동작중 일 때는 취소되지 않습니다.
이럴 때는 연산 외에 `isActive` 같은 cancel 상태 정보를 갖는 property를 사용해야 한다.
아래는 Guide에 안내되는 코드입니다.
```kotlin
fun main(args: Array<String>) = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    val job = launch(CommonPool) {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) { // cancellable computation loop
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancel() // cancels the job
    delay(1300L) // delay a bit to see if it was cancelled....
    println("main: Now I can quit.")
}
```


## Timeout
Coroutines에서 제공하는 withTimeout()을 사용하면 설정한 시간이 지난 후,
coroutine을 종료할 수 있습니다.
아래는 1씩 증가하는 count를 표시하는 작업을 1000ms 후에 종료하는 코드입니다.
```kotlin
withTimeout(1000) {
    repeat(1000) { i ->
        count = i
        showCount()
        delay(500L)
    }
}
```
