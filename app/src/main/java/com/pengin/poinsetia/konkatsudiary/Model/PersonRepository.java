package com.pengin.poinsetia.konkatsudiary.Model;


import android.util.Log;

import com.pengin.poinsetia.konkatsudiary.Presenter.PersonContract;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.realm.RealmResults;

public class PersonRepository implements PersonContract.Model{

    private final String TAG = "PersonRepository";
    private PersonRealmHelper mRealmHelper;

    public PersonRepository(PersonRealmHelper realmHelper) {
        this.mRealmHelper = realmHelper;
    }

    /**
     * リストの初期表示のリストデータ取得
     */
    @Override
    public Single getFirstList() {

        return Single.create((SingleOnSubscribe<RealmResults<Person>>) emitter -> {
            try {
                RealmResults<Person> results = mRealmHelper.findAll();
                emitter.onSuccess(results);
                Log.d(TAG,"onSuccess");
            } catch (Exception ex) {
                emitter.onError(ex);
                Log.d(TAG,"onError");
            }
        });
    }

    /**
     * インデックスの入れ替えを行う
     */
    @Override
    public void itemIndexReplace() {

    }

    /**
     * アイテムの削除を行う
     */
    @Override
    public void itemDelete() {

    }

    /**
     * アイテムインデックスの振り直しを行う
     */
    @Override
    public void itemIndexRenumber() {

    }
}
