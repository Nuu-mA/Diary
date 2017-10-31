package com.pengin.poinsetia.konkatsudiary.Presenter;


import android.util.Log;

import com.pengin.poinsetia.konkatsudiary.Model.Person;
import com.pengin.poinsetia.konkatsudiary.Model.PersonRepository;

import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

public class PersonPresenter implements PersonContract.Presenter {

    private final String TAG = "PersonPresenter";

    private PersonContract.View mView;
    private PersonRepository mRepository;

    public PersonPresenter(PersonContract.View view, PersonRepository repository) {
        this.mView = view;
        this.mRepository = repository;
    }

    /**
     * リストの初期表示イベント
     */
    @Override
    public void initList() {
        mRepository.getFirstList()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(new DisposableSingleObserver<RealmResults<Person>> () {
                    @Override
                    public void onSuccess(RealmResults<Person> results) {
                        Log.d(TAG,"onSuccess");
                        if (results.size() != 0) {
                            mView.showList(results);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,"onError");
                        // error
                    }
                });
    }

    /**
     * FABの押下イベント
     */
    @Override
    public void pressFAB() {

    }

    /**
     * リストの入れ替えイベント
     */
    @Override
    public void onMoveList() {

    }

    /**
     * リストのスワイプ削除イベント
     */
    @Override
    public void onSwiped() {

    }


}
