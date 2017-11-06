package com.pengin.poinsetia.konkatsudiary.View;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.pengin.poinsetia.konkatsudiary.Presenter.PersonContract;
import com.pengin.poinsetia.konkatsudiary.Presenter.PersonPresenter;
import com.pengin.poinsetia.konkatsudiary.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment implements OnRecyclerListener,View.OnClickListener,
        PersonContract.View{

    private final String TAG = "PersonFragment";

    private final int DIALOG_KEY = 100;

    private Activity mActivity = null;
    private View mView;

    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private PersonAdapter mAdapter = null;
    private PersonRealmHelper mRealmHelper;

    private PersonPresenter mPresenter;

    /**
     * ダイアログの生成を行う
     */
    @Override
    public void showDialog() {
        DialogFragment newFragment = new AddPersonDialogFragment();
        newFragment.setTargetFragment(this,DIALOG_KEY);
        newFragment.show(getFragmentManager(), "person");
    }

    /**
     * リストの表示を行う
     */
    @Override
    public void showList(RealmList<Person> personList) {
        mAdapter = new PersonAdapter(mActivity, personList, this);
        mRecyclerView.setAdapter(mAdapter);
        Log.d(TAG,"showList");
    }

    /**
     * Adapterに入れ替え後の通知を行う
     */
    @Override
    public void notifyItemMoved() {

    }

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
                // FABの押下イベント
                mPresenter.pressFAB();
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
//        mRealmHelper = new PersonRealmHelper();
        mPresenter = new PersonPresenter(this);

        // リスト初期表示イベント
        mPresenter.initList();

        ItemTouchHelper mIth  = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        // 元の場所
                        int fromIndex = viewHolder.getAdapterPosition();
                        // 移動後の場所
                        int toIndex = target.getAdapterPosition();
                        // ★リストの入れ替えイベント・Presenter
                        // ★インデックスの入れ替えを行う・Model
                        indexReplace(fromIndex,toIndex);
                        // ★入れ替え後の通知を行う・View
                        mAdapter.notifyItemMoved(fromIndex,toIndex);
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int index = viewHolder.getAdapterPosition();
                        // ★リストのスワイプ削除イベント・Presenter
                        // ★アイテムの削除を行う・Model
                        deleteList(index);
                        mAdapter.notifyItemRemoved(index);

                    }
                });
        mIth.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 指定したrequestCodeで戻ってくる
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DIALOG_KEY) {
                if (data != null) {
                    int age = data.getIntExtra("age", 0);
                    String name = data.getStringExtra("name");
                    // AddPerson 生成イベント
                    if (age != 0 &&
                            !name.equals("")) {
                        mPresenter.dialogResultOk(age, name);
                    }
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

    /**
     * レコードの削除を実行
     */
    private void deleteList(int index) {
        mRealmHelper.delete(index);
        // ★削除後 index の 振り直し・Model
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


}
