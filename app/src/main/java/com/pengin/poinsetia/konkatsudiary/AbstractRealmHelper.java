package com.pengin.poinsetia.konkatsudiary;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;


public abstract class AbstractRealmHelper<T extends RealmObject> {

    protected final Realm mRealm;

    public AbstractRealmHelper() {
        mRealm = getRealm();
    }

    static Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    protected static void executeTransactionOneShot(Realm.Transaction transaction) {
        Realm realm = getRealm();
        realm.executeTransaction(transaction);
        realm.close();
    }

    public abstract void update(T t);

    public abstract RealmResults<T> findAll();

    public abstract void delete(int position);

    public abstract void setPosition(Flower flower,int position);

    public abstract RealmResults<T> deleteUnderList(int position);

    public abstract RealmResults<T> getRealmObject(int position);

    protected void executeTransaction(Realm.Transaction transaction) {
        mRealm.executeTransaction(transaction);
    }

    public void destroy() {
        mRealm.close();
    }

}
