package com.pengin.poinsetia.konkatsudiary;


import android.support.test.runner.AndroidJUnit4;

import com.pengin.poinsetia.konkatsudiary.Model.Person;
import com.pengin.poinsetia.konkatsudiary.Model.PersonRepository;
import com.pengin.poinsetia.konkatsudiary.Presenter.PersonPresenter;
import com.pengin.poinsetia.konkatsudiary.View.PersonFragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import io.realm.RealmList;

import static org.mockito.Mockito.times;

@RunWith(AndroidJUnit4.class)
public class PersonPresenterTest {

    @Mock
    private PersonFragment mockView;

    @Mock
    private PersonRepository mockRepository;

    @Test
    public void リストの初期表示イベントテスト() throws Exception {
        // Mock作り
        mockRepository = Mockito.mock(PersonRepository.class);
        mockView = Mockito.mock(PersonFragment.class);

        // dummy用のPerson作成
        Person person = new Person();
        person.setId(0);
        person.setAge(20);
        person.setName("numa");
        person.setIndex(0);

        RealmList<Person> dummyPersonList = new RealmList<>();
        dummyPersonList.add(person);
        Mockito.when(mockRepository.getFirstList()).thenReturn(dummyPersonList);
        // テスト実行
        PersonPresenter presenter = new PersonPresenter(mockView,mockRepository);
        presenter.initList();
        // リスト表示ルートに入ったか確認する:showList のコール回数が1回
        Mockito.verify(mockView,times(1)).showList(dummyPersonList);
    }

    @Test
    public void リストの初期表示しないイベントテスト() throws Exception {
        // Mock作り
        mockRepository = Mockito.mock(PersonRepository.class);
        mockView = Mockito.mock(PersonFragment.class);

        // リスト表示用Personを追加しない
        RealmList<Person> dummyPersonList = new RealmList<>();
        Mockito.when(mockRepository.getFirstList()).thenReturn(dummyPersonList);
        // テスト実行
        PersonPresenter presenter = new PersonPresenter(mockView,mockRepository);
        presenter.initList();
        // リスト表示ルートに入らないことを確認する:showList のコール回数が0回
        Mockito.verify(mockView,times(0)).showList(dummyPersonList);
    }

    @Test
    public void FAB押下イベントテスト() throws Exception {
        // Mock
        mockView = Mockito.mock(PersonFragment.class);

        // テスト実行
        PersonPresenter presenter = new PersonPresenter(mockView,mockRepository);
        presenter.pressFAB();
        // リスト表示ルートに入ったか確認する:showDialog のコール回数が1回
        Mockito.verify(mockView,times(1)).showDialog();
    }

    @Test
    public void Dialog閉じるボタン押下テスト() throws Exception {
        // Mock作り
        mockRepository = Mockito.mock(PersonRepository.class);
        mockView = Mockito.mock(PersonFragment.class);

        // dummy用のPerson作成
        Person person = new Person();
        person.setId(0);
        person.setAge(20);
        person.setName("poinsetia");
        person.setIndex(0);

        RealmList<Person> dummyPersonList = new RealmList<>();
        dummyPersonList.add(person);
        Mockito.when(mockRepository.createPerson(20,"poinsetia")).thenReturn(dummyPersonList);
        // テスト実行
        PersonPresenter presenter = new PersonPresenter(mockView,mockRepository);
        presenter.dialogResultOk(20,"poinsetia");
        // リスト表示ルートに入ったか確認する:showList のコール回数が1回
        Mockito.verify(mockView,times(1)).showList(dummyPersonList);
    }

    @Test
    public void Dialog閉じるボタン押下イベントテスト() throws Exception {
        // Mock作り
        mockRepository = Mockito.mock(PersonRepository.class);
        mockView = Mockito.mock(PersonFragment.class);

        // dummy用のPerson作成
        Person person = new Person();
        person.setId(0);
        person.setAge(20);
        person.setName("poinsetia");
        person.setIndex(0);

        RealmList<Person> dummyPersonList = new RealmList<>();
        dummyPersonList.add(person);
        Mockito.when(mockRepository.createPerson(20,"poinsetia")).thenReturn(dummyPersonList);
        // テスト実行
        PersonPresenter presenter = new PersonPresenter(mockView,mockRepository);
        presenter.dialogResultOk(20,"poinsetia");
        // リスト表示ルートに入ったか確認する:showList のコール回数が1回
        Mockito.verify(mockView,times(1)).showList(dummyPersonList);
    }

    @Test
    public void Dialog閉じるボタン押下イベント値なしテスト() throws Exception {
        // Mock作り
        mockRepository = Mockito.mock(PersonRepository.class);
        mockView = Mockito.mock(PersonFragment.class);

        RealmList<Person> dummyPersonList = new RealmList<>();
        Mockito.when(mockRepository.createPerson(0,"")).thenReturn(dummyPersonList);
        // テスト実行
        PersonPresenter presenter = new PersonPresenter(mockView,mockRepository);
        presenter.dialogResultOk(0,"");
        // リスト表示ルートに入らなかったか確認する:showList のコール回数が0回
        Mockito.verify(mockView,times(0)).showList(dummyPersonList);
    }

    @Test
    public void リストの入れ替えイベントテスト() throws Exception {
        // Mock
        mockRepository = Mockito.mock(PersonRepository.class);
        mockView = Mockito.mock(PersonFragment.class);

        // テスト実行
        PersonPresenter presenter = new PersonPresenter(mockView,mockRepository);
        presenter.onMoveList(0,1);
        // リスト表示ルートに入ったか確認する:notifyItemMoved のコール回数が1回
        Mockito.verify(mockView,times(1)).notifyItemMoved();
    }

    @Test
    public void リストのスワイプ削除イベントテスト() throws Exception {
        // Mock作り
        mockRepository = Mockito.mock(PersonRepository.class);
        mockView = Mockito.mock(PersonFragment.class);

        // dummy用のPerson作成
        Person person = new Person();
        person.setId(0);
        person.setAge(20);
        person.setName("poinsetia");
        person.setIndex(0);

        RealmList<Person> dummyPersonList = new RealmList<>();
        dummyPersonList.add(person);
        Mockito.when(mockRepository.itemDelete(0)).thenReturn(dummyPersonList);
        // テスト実行
        PersonPresenter presenter = new PersonPresenter(mockView,mockRepository);
        presenter.onSwiped(0);
        // リスト表示ルートに入ったか確認する:notifyItemRemoved のコール回数が1回
        Mockito.verify(mockView,times(1)).notifyItemRemoved();
    }

    @Test
    public void リストのスワイプ削除しないテスト() throws Exception {
        // Mock作り
        mockRepository = Mockito.mock(PersonRepository.class);
        mockView = Mockito.mock(PersonFragment.class);

        RealmList<Person> dummyPersonList = new RealmList<>();
        Mockito.when(mockRepository.itemDelete(5)).thenReturn(dummyPersonList);
        // テスト実行
        PersonPresenter presenter = new PersonPresenter(mockView,mockRepository);
        presenter.onSwiped(0);
        // リスト表示ルートに入ったか確認する:notifyItemRemoved がコールされないこと
        Mockito.verify(mockView,times(0)).notifyItemRemoved();
    }


}
