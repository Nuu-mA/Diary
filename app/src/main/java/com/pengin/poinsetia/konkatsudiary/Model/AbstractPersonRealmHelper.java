package com.pengin.poinsetia.konkatsudiary.Model;

import com.pengin.poinsetia.konkatsudiary.Model.Person;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;


public abstract class AbstractPersonRealmHelper<T extends RealmObject> {

    protected final Realm mRealm;

    public AbstractPersonRealmHelper() {
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

    public abstract void setIndex(Person person, int index);

    public abstract RealmResults<T> deleteUnderList(int index);

    public abstract RealmObject getRealmObject(int index);

    protected void executeTransaction(Realm.Transaction transaction) {
        mRealm.executeTransaction(transaction);
    }

    public void destroy() {
        mRealm.close();
    }

}
