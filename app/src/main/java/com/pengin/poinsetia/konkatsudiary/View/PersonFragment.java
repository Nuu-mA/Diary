package com.pengin.poinsetia.konkatsudiary.View;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pengin.poinsetia.konkatsudiary.Model.PersonRealmHelper;
import com.pengin.poinsetia.konkatsudiary.Model.Person;
import com.pengin.poinsetia.konkatsudiary.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment implements OnRecyclerListener,View.OnClickListener{

    private final int DIALOG_KEY = 100;

    private Activity mActivity = null;
    private View mView;

    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private PersonAdapter mAdapter = null;
    private PersonRealmHelper mRealmHelper;

    public interface RecyclerFragmentListener {
        void onRecyclerEvent();
    }

    public PersonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        // RecyclerViewの参照を取得
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        // レイアウトマネージャを設定(ここで縦方向の標準リストであることを指定)
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        // フローティングアクションボタンの実装
        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab :
                // ダイアログフラグメンドの生成
                DialogFragment newFragment = new FlowerDialogFragment();
                newFragment.setTargetFragment(this,DIALOG_KEY);
                newFragment.show(getFragmentManager(), "person");
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                new LinearLayoutManager(getActivity()).getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        Realm.init(mActivity);
        // Helperの作成
        // リストの初期表示
        mRealmHelper = new PersonRealmHelper();
        // where
        RealmResults<Person> results = mRealmHelper.findAll();
        int listSize = results.size();
        if (listSize != 0) {
            // positionでソート
            results = results.sort("index");
            // リスト表示
            mAdapter = new PersonAdapter(mActivity, results, this);
            mRecyclerView.setAdapter(mAdapter);

        }

        ItemTouchHelper mIth  = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        // 元の場所
                        int fromIndex = viewHolder.getAdapterPosition();
                        // 移動後の場所
                        int toIndex = target.getAdapterPosition();
                        // インデックスの入れ替えを行う
                        indexReplace(fromIndex,toIndex);
                        mAdapter.notifyItemMoved(fromIndex,toIndex);
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int index = viewHolder.getAdapterPosition();
                        deleteList(index);
                        mAdapter.notifyItemRemoved(index);

                    }
                });
        mIth.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 指定したrequestCodeで戻ってくる
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == DIALOG_KEY){
                if (data != null) {
                    int age = data.getIntExtra("age",0);
                    String name = data.getStringExtra("name");
                    // PrimaryKeyの取得
                    int primaryKey = getLastPrimaryKey() ;
                    int position = getIndex();
                    // 受け取った値でDB追加とリスト表示
                    createList(getPerson(primaryKey,age,name,position));
                    Log.d("mPrimaryKey", primaryKey +"");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.setAdapter(null);
        // DB クローズ
        mRealmHelper.destroy();
    }

    @Override
    public void onRecyclerClicked(View v, int position) {
        // セルクリック処理
    }

    // ~~~~~~ Repository に移動予定 ~~~~~~

    // レコードの追加を実行
    private void createList(Person person) {
        // 空の時は追加しない
        if (person.getAge()!=0 &&
                !person.getName().equals("")) {
            // insert
            PersonRealmHelper.insertOneShot(person);
            // where
            RealmResults<Person> results = mRealmHelper.findAll();
            // positionでソート
            results = results.sort("index");
            // リスト表示
            mAdapter = new PersonAdapter(mActivity, results, this);
            mRecyclerView.setAdapter(mAdapter);
        }
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
     * レコードの削除を実行
     */
    private void deleteList(int index) {
        mRealmHelper.delete(index);
        // 削除後 index の 振り直し
        setUnderList(index);
    }

    /**
     * 指定位置より下のIndexを振り直す
     * @param index 振り直し始め番号
     */
    private  void setUnderList(int index) {
        // 削除した以下の位置のリストを取得
        RealmResults<Person> results = mRealmHelper.deleteUnderList(index);
        if (results.size() != 0) {
            int newPos = index;
            // 一度Arrayに詰める
            ArrayList<Person> persons = new ArrayList<>();
            for (int i = 0; i < results.size();i++ ) {
                persons.add(results.get(i));
            }
            // Arrayの情報を元に振り直しを実行する
            for (Person person : persons) {
                mRealmHelper.setIndex(person,newPos);
                newPos++;
            }
            persons.clear();
        }
    }

    /**
     * Indexの入れ替えを実行する
     * @param fromPos 移動前Index
     * @param toPos 移動後Index
     */
    private void indexReplace(int fromPos, int toPos) {
        Person fromPerson = mRealmHelper.getRealmObject(fromPos);
        Person toPerson = mRealmHelper.getRealmObject(toPos);
        mRealmHelper.setIndex(fromPerson, toPos);
        mRealmHelper.setIndex(toPerson, fromPos);
    }

    /**
     * Flower入力用Dialog
     */
    public static class FlowerDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyAlertDialogStyle);
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.flower_dialog_layout, null);
            Intent result = new Intent();
            // 年齢選択用プルダウンリスト
            Spinner selectedAge = (Spinner) view.findViewById(R.id.person_age_spinner);
            selectedAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        String age = parent.getItemAtPosition(position).toString();
                        result.putExtra("age", Integer.parseInt(age));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // do Nothing
                }
            });
            builder.setView(view);
            builder.setNegativeButton("閉じる", (dialog, id) -> {

                TextView nameEditText = (TextView)view.findViewById(R.id.person_name_text);
                String nameText = nameEditText.getText().toString();
                result.putExtra("name",nameText);
                if (getTargetFragment() != null) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
                }
            });
            return builder.create();
        }
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
}
