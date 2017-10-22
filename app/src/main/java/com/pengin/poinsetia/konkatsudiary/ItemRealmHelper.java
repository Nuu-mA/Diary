package com.pengin.poinsetia.konkatsudiary;

import android.app.DownloadManager;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ItemRealmHelper extends AbstractRealmHelper<Flower>{

    public static void insertOneShot(Flower flower) {
        executeTransactionOneShot(insertTransaction(flower));
    }

    private static Realm.Transaction insertTransaction(final Flower itemRealmObject) {
        return realm -> realm.insertOrUpdate(itemRealmObject);
    }

    @Override
    public void update(Flower flower) {
        // レコードの追加
        executeTransaction(realm -> realm.copyToRealmOrUpdate(flower));
    }

    @Override
    public void delete(int position) {
        // 指定した位置のレコード削除
        RealmResults results = findAll();
        executeTransaction(realm -> results.deleteFromRealm(position));
    }

    @Override
    public void setPosition(Flower flower, int position) {
        // リスト位置の再設定
        executeTransaction(realm -> flower.setPosition(position));
    }

    @Override
    public RealmResults<Flower> deleteUnderList(int position) {
        // 削除されたpositionよりも下の位置のリストを取ってくる
        return mRealm.where(Flower.class)
                     .greaterThan("position",position)
                     .findAllSorted("position");
    }

    @Override
    public Flower getRealmObject(int position) {
        // 指定した位置のFlowerを返却する
        return mRealm.where(Flower.class).equalTo("position",position).findFirst();
    }

    @Override
    public RealmResults<Flower> findAll() {
        return mRealm.where(Flower.class).findAll();
    }


}
