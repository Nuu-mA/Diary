package com.pengin.poinsetia.konkatsudiary.Model;


import android.util.Log;
import android.util.Printer;

import com.pengin.poinsetia.konkatsudiary.Presenter.PersonContract;

import io.reactivex.Single;
import io.realm.RealmList;
import io.realm.RealmResults;

public class PersonRepository implements PersonContract.Model{

    private final String TAG = "PersonRepository";
    private PersonRealmHelper mRealmHelper;
    private RealmList<Person> mPersonList;

    public PersonRepository() {
        mRealmHelper = new PersonRealmHelper();
        mPersonList = new RealmList<>();
    }

    /**
     * リストの初期表示のリストデータ取得
     */
    @Override
    public RealmList<Person> getFirstList() {
            try {
                RealmResults<Person> results = mRealmHelper.findAll();
                mPersonList.addAll(results.subList(0,results.size()));
                Log.d(TAG,"onSuccess");
                return mPersonList;
            } catch (Exception ex) {
                Log.d(TAG,"onError: " + ex);
                return null;
            }
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
