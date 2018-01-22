# ADS04 Android

## 수업 내용

- RxJava_Subject 학습

## Code Review

### MainActivity

```Java
public class MainActivity extends AppCompatActivity {

    List<String> data = new ArrayList<>();
    RecyclerView recyclerView;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new CustomAdapter();
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // publish 서브젝트
    // 구독 시점부터 데이터를 읽을 수 있다 > 이전에 발행된 아이템은 읽을 수 없다
    PublishSubject<String> publish = PublishSubject.create();

    public void doPublish(View view) {
        new Thread(
                () -> {
                    for (int i = 0; i < 10; i++) {
                        publish.onNext("SOMETHING...." + i);
                        Log.e("Publish", "============" + i);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) { /* do nothing */}
                    }
                }
        ).start();
    }

    // publish 에서 값 가져와서 세팅
    public void getPublish(View view) {
        data.clear();
        publish.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        str -> {
                            data.add(str);
                            adapter.setDataAndRefresh(data);
                        }
                );
    }

    // Behavior 서브젝트
    // 구독 이전에 발행된 마지막 아이템부터 구독할 수 있다
    BehaviorSubject<String> behavior = BehaviorSubject.create();

    public void doBehavior(View view) {
        new Thread(
                () -> {
                    for (int i = 0; i < 10; i++) {
                        behavior.onNext("SOMETHING...." + i);
                        Log.e("Behavior", "============" + i);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) { /* do nothing */}
                    }
                }
        ).start();
    }

    public void getBehavior(View view) {
        data.clear();
        behavior.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        str -> {
                            data.add(str);
                            adapter.setDataAndRefresh(data);
                        }
                );
    }

    // Replay 서브젝트
    // 발행된 아이템을 처음부터 모두 구독할 수 있다
    ReplaySubject<String> replay = ReplaySubject.create();

    public void doReplay(View view) {
        new Thread(
                () -> {
                    for (int i = 0; i < 10; i++) {
                        replay.onNext("SOMETHING...." + i);
                        Log.e("Replay", "============" + i);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) { /* do nothing */}
                    }
                }
        ).start();
    }

    public void getReplay(View view) {
        data.clear();
        replay.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        str -> {
                            data.add(str);
                            adapter.setDataAndRefresh(data);
                        }
                );
    }

    // Async 서브젝트
    AsyncSubject<String> async = AsyncSubject.create();

    public void doAsync(View view) {
        new Thread(
                () -> {
                    for (int i = 0; i < 10; i++) {
                        async.onNext("SOMETHING...." + i);
                        Log.e("Async", "============" + i);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) { /* do nothing */}
                    }
                    async.onComplete();
                }
        ).start();
    }

    public void getAsync(View view) {
        data.clear();
        async.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        str -> {
                            data.add(str);
                            adapter.setDataAndRefresh(data);
                        }
                );
    }
}
```


## 보충설명

### Subject 

> Observable(발행) 과 동시에 Observer(구독)을 할 수 있는 역할
> Subject는 이벤트를 전달받아 구독자들에게 이벤트를 전파하는 중간다리
> 아이템(데이터)을 발행하고 있는 Observable 들의 데이터를 합치거나, 구독 시점 변경 가능

#### PublishSubject

![publish](http://reactivex.io/documentation/operators/images/S.PublishSubject.png)

- 구독한 시점으로부터 이후에 발생하는 이벤트들을 전달받음.

#### BehaviorSubject

![Behavior](http://reactivex.io/documentation/operators/images/S.BehaviorSubject.png)

- 가장 최근에 emit된 item으로 구독을 시작
- 구독 이전에 발행된 마지막 아이템부터 구독할 수 있음.

#### ReplaySubject

![Replay](http://reactivex.io/documentation/operators/images/S.ReplaySubject.png)

- 구독 이전의 모든 item들을 받음.
- 발행된 아이템을 처음부터 모두 구독할 수 있음.

#### AsyncSubject

![Async](http://reactivex.io/documentation/operators/images/S.AsyncSubject.png)

- onComplete가 호출되기 직전의 item만 받음

### subscribeOn, observeOn

> observeOn() : Observable이 아이템을 전파할 때, 사용할 스레드를 지정하는 것

> subscribeOn(): subscribeOn은 구독(subscribe)에서 사용할 스레드를 지정하는 것

- 예시

``` java
예를 들어, Observable은 네트워크 연결하고, 결과를 받으며, 구독에서 결과를 처리합니다. 그러면 Observable은 백그라운드 스레드에서 네트워크 요청 작업을 수행하고, 구독은 결과를 화면에 보여주기 위해 메인 스레드에서 수행합니다.
Observable은 백그라운드 스레드에서 동작하도록 observeOn으로 백그라운드 스레드를 지정하고, 구독은 메인 스레드에서 동작하도록 subscribeOn으로 메인 스레드를 지정합니다 
출처 : http://blog.weirdx.io/post/26576
```

### 출처

- 출처 : http://gaemi.github.io/android/2015/05/20/RxJava-with-Android-1-RxJava-%EC%82%AC%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0.html
- 출처 : http://blog.weirdx.io/post/26576
- 참고하면 좋은 사이트 : https://pilgwon.github.io/blog/2017/10/09/RxSwift-By-Examples-2-Observable-And-The-Bind.html

## TODO

- 활용할 수 있는 상황 생각해보기.
> 'Subject를 활용한다면, Android 에서는 EventBus 와 같은 형태로도 사용이 가능합니다. 즉 RxJava 를 사용하면 다른 EventBus 라이브러리가 불필요해집니다.'
- 상기의 내용에서 EventBus가 어떤 라이브러리인지 조사하고, Subject를 사용하면 어떻게 대체를 할 수 있는지 공부

## Retrospect

- 예제와 인터넷자료 조사를 통해 어떤 용도로 쓰이는지, 왜 이러한 것을 만들었는지 조금씩 이해가 가고 있으나, 람다식과 함께 사용하려니 난해함.

## Output
- 생략