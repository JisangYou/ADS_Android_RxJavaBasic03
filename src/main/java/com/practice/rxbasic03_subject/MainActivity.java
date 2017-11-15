package com.practice.rxbasic03_subject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

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
    // 구독 시점부터 데이터를 읽을 수 있다. 이전에 발행된 아이템은 읽을 수 없다.
    PublishSubject<String> publish = PublishSubject.create();

    public void doPublish(View view) {
        new Thread(
                () ->
                { for (int i = 0; i < 10; i++) {
                        publish.onNext("SOMETHING....!" + i);
                        Log.e("Publish", "============" + i);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {

                        }
                    }
                }
        ).start();
    }

    // publish에서 값 가져와서 세팅
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
    //구독이전에 발행된 마지막 아이템부터 구독할 수 있다.
    BehaviorSubject<String> behavior = BehaviorSubject.create();

    public void doBehavior(View view) {
        new Thread(
                () -> {
                    for (int i = 0; i < 10; i++) {
                        behavior.onNext("SOMETHING....!" + i);
                        Log.e("Behavior", "============" + i);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {

                        }
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
    // 발행된 아이템을 처음부터 모두 구독할 수 있다.

    ReplaySubject<String> replay = ReplaySubject.create();

    public void doReplay(View view) {
        new Thread(
                () -> {
                    for (int i = 0; i < 10; i++) {
                        replay.onNext("SOMETHING....!" + i);
                        Log.e("Replay", "============" + i);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {

                        }
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
    //Async 서브젝트
    AsyncSubject<String> async = AsyncSubject.create();
    public void doAsync(View view) {
        new Thread(
                () -> {
                    for (int i = 0; i < 10; i++) {
                        async.onNext("SOMETHING....!" + i);
                        Log.e("Async", "============" + i);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {

                        }
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

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Holder> {
    List<String> data = new ArrayList<>();

    public void setDataAndRefresh(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.text1.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView text1;

        public Holder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}
