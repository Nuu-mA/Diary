package com.pengin.poinsetia.konkatsudiary.Model;


import android.util.Log;
import android.util.Printer;

import com.pengin.poinsetia.konkatsudiary.Presenter.PersonContract;
import com.pengin.poinsetia.konkatsudiary.View.PersonAdapter;

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

    @Override
    public RealmList<Person> createPerson(int age, String name) {
        // PrimaryKeyの取得
        int primaryKey = getLastPrimaryKey();
        int position = getIndex();
        RealmResults<Person> results = createList(getPerson(primaryKey, age, name, position));
        try {
            mPersonList.clear();
            mPersonList.addAll(results.subList(0,results.size()));
            Log.d("mPrimaryKey", primaryKey + "");
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

    /**
     * レコード追加用のPrimaryKeyを返す
     * @return PrimaryKey
     */
    private int getLastPrimaryKey() {
        RealmResults<Person> results = mRealmHelper.findAll();
        if (results.size() == 0) {
            return 0;
        } else {
            // 最新のPrimaryKey + 1 を返却する
            results.sort("id");
            return results.last().getId() + 1;
        }
    }

    /**
     * レコード追加用のIndexを返す
     * @return Index
     */
    private int getIndex() {
        // コード数がリストのインデックスになる
        RealmResults<Person> results = mRealmHelper.findAll();
        return results.size();
    }

    /**
     * DB追加用のPersonを生成する
     * @param id PrimaryKey
     * @param age 年齢
     * @param name 名前
     * @param index リストの初期位置
     * @return Person
     */
    private Person getPerson(int id, int age, String name, int index) {

        Person person = new Person();
        person.setId(id);
        person.setAge(age);
        person.setName(name);
        person.setIndex(index);

        return person;
    }

    // レコードの追加を実行
    private RealmResults<Person> createList(Person person) {
        // insert
        PersonRealmHelper.insertOneShot(person);
        // where
        RealmResults<Person> results = mRealmHelper.findAll();
        // positionでソート
        results = results.sort("index");

        return results;
    }
}
