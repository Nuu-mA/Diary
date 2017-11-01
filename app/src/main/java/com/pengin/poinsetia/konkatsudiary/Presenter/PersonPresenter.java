package com.pengin.poinsetia.konkatsudiary.Presenter;


import android.util.Log;

import com.pengin.poinsetia.konkatsudiary.Model.Person;
import com.pengin.poinsetia.konkatsudiary.Model.PersonRepository;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;
import io.realm.RealmResults;

public class PersonPresenter implements PersonContract.Presenter {

    private final String TAG = "PersonPresenter";

    private PersonContract.View mView;
    private PersonRepository mRepository;

    public PersonPresenter(PersonContract.View view) {
        this.mView = view;
        mRepository = new PersonRepository();
    }

    /**
     * リストの初期表示イベント
     */
    @Override
    public void initList() {
        // Repositoryに初期のリスト取得要求
        RealmList<Person> personList = mRepository.getFirstList();
        // 1件以上あるならViewに表示要求
        if (personList.size() != 0) mView.showList(personList);
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
