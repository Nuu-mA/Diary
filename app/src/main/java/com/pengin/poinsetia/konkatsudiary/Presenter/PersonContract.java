package com.pengin.poinsetia.konkatsudiary.Presenter;

import com.pengin.poinsetia.konkatsudiary.Model.Person;

import io.reactivex.Single;
import io.realm.RealmList;
import io.realm.RealmResults;

public interface PersonContract {

    interface Presenter {
        // リストの初期表示イベント
        void initList();

        // FABの押下イベント
        void pressFAB();

        // Dialog「閉じるボタン」押下
        void dialogResultOk(int age, String name);

        // リストの入れ替えイベント
        void onMoveList();

        // リストのスワイプ削除イベント
        void onSwiped();
    }

    interface View {
        // ダイアログの生成を行う
        void showDialog();

        // リストの表示を行う
        void showList(RealmList<Person> results);

        // Adapterに入れ替え後の通知を行う
        void notifyItemMoved();

    }

    interface Model {
        // リストの初期表示のリストデータ取得
        RealmList<Person> getFirstList();

        // 新しいPersonアイテムの生成
        RealmList<Person> createPerson(int age, String name);

        // インデックスの入れ替えを行う
        void itemIndexReplace();

        // アイテムの削除を行う
        void itemDelete();

        // アイテムインデックスの振り直しを行う
        void itemIndexRenumber();
    }

}
