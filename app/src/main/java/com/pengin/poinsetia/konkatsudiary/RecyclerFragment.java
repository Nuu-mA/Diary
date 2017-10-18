package com.pengin.poinsetia.konkatsudiary;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerFragment extends Fragment implements OnRecyclerListener,View.OnClickListener{

    private final int DIALOG_KEY = 100;

    private Activity mActivity = null;
    private View mView;

    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private RecyclerAdapter mAdapter = null;
    private ItemRealmHelper mRealmHelper;

    public interface RecyclerFragmentListener {
        void onRecyclerEvent();
    }

    public RecyclerFragment() {
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
                newFragment.show(getFragmentManager(), "flower");
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
        mRealmHelper = new ItemRealmHelper();
        // where
        RealmResults<Flower> results = mRealmHelper.findAll();
        int listSize = results.size();
        if (listSize != 0) {
            // positionでソート
            results = results.sort("position");
            // リスト表示
            mAdapter = new RecyclerAdapter(mActivity, results, this);
            mRecyclerView.setAdapter(mAdapter);

        }

        ItemTouchHelper mIth  = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        // 元の場所
                        int fromPos = viewHolder.getAdapterPosition();
                        // 移動後の場所
                        int toPos = target.getAdapterPosition();
                        insertList(fromPos,toPos);
                        mAdapter.notifyItemMoved(fromPos,toPos);
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        deleteList(position);
                        mAdapter.notifyItemRemoved(position);

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
                    String color = data.getStringExtra("color");
                    String name = data.getStringExtra("name");
                    // PrimaryKeyの取得
                    int primaryKey = getLastPrimaryKey() ;
                    int position = getPosition();
                    // 受け取った値でDB追加とリスト表示
                    createList(getFlower(primaryKey,color,name,position));
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
    private void createList(Flower flower) {
        // 空の時は追加しない
        if (!flower.getColor().equals("") &&
                !flower.getFlower().equals("")) {
            // insert
            ItemRealmHelper.insertOneShot(flower);
            // where
            RealmResults<Flower> results = mRealmHelper.findAll();
            // リスト表示
            mAdapter = new RecyclerAdapter(mActivity, results, this);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    /**
     * レコード追加用のPrimaryKeyを返す
     * @return PrimaryKey
     */
    private int getLastPrimaryKey() {
        RealmResults<Flower> results = mRealmHelper.findAll();
        if (results.size() == 0) {
            return 0;
        } else {
            // 最新のPrimaryKey + 1 を返却する
            results.sort("id");
            return results.last().getId() + 1;
        }
    }

    /**
     * レコード追加用のPositionを返す
     * @return Position
     */
    private int getPosition() {
        // コード数がListPositionになる
        RealmResults<Flower> results = mRealmHelper.findAll();
        return results.size();
    }

    /**
     * レコードの削除を実行
     */
    private void deleteList(int position) {
        RealmResults<Flower> results = mRealmHelper.getRealmObject(position);
        Flower flower = results.first();

        mRealmHelper.delete(position);
        // 削除後 position の 振り直し
        setUnderList(position);

    }

    /**
     * 指定位置より下のリスト番号を振り直す
     * @param position 振り直し始め番号
     */
    private  void setUnderList(int position) {
        // 削除した以下の位置のリストを取得
        RealmResults<Flower> results = mRealmHelper.deleteUnderList(position);
        if (results.size() != 0) {
            int newPos = position;
            for (int i = 0; i < results.size(); i++) {
                mRealmHelper.setPosition(results.get(i), newPos);
                newPos++;
            }
        }
    }

    /**
     * リストの入れ替え処理
     * @param fromPos 開始位置
     * @param toPos 差し込み位置
     */
    private void insertList (int fromPos, int toPos) {
        RealmResults<Flower> results = mRealmHelper.getRealmObject(fromPos);
        if (results.size() != 0) {
            Flower flower = results.first();
            boolean isUP = (fromPos > toPos);
            reassignedList(isUP ? toPos : fromPos, isUP);
            mRealmHelper.setPosition(flower, toPos);
        }

    }

    /**
     * 番号を振り直す
     * @param position 振り直し始め番号
     * @param isUP 上下
     */
    private void reassignedList(int position, boolean isUP) {
        // TODO 上下で処理を分ける (現在は 下から上に入れ替える時のみ正常に動作する)
        // 削除した以下の位置のリストを取得
        int startNum;
        int newPosition;
        if (isUP) {
            startNum = position - 1;
            newPosition = position + 1;
        }
        else {
            startNum = position + 1;
            newPosition = position -1;
        }

        RealmResults<Flower> results = mRealmHelper.deleteUnderList(startNum);
        if (results.size() != 0) {
            // 差し込まれるレコードの分、positionを１ずらす
            for (int i = 0; i < results.size(); i++) {
                mRealmHelper.setPosition(results.get(i), newPosition);
                newPosition++;
            }
        }
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
            builder.setView(view);
            builder.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent result = new Intent();
                            TextView colorEditText = (TextView)view.findViewById(R.id.color_name_text);
                            TextView nameEditText = (TextView)view.findViewById(R.id.flower_name_text);
                            String colorText = colorEditText.getText().toString();
                            String nameText = nameEditText.getText().toString();
                            result.putExtra("color",colorText);
                            result.putExtra("name",nameText);
                            if (getTargetFragment() != null) {
                                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
                            }
                        }
                    });
            return builder.create();
        }
    }

    /**
     * DB追加用のFlowerを生成する
     * @param id PrimaryKey
     * @param color 花の色
     * @param name 花の名前
     * @param position リストの初期位置
     * @return
     */
    private Flower getFlower(int id, String color, String name, int position) {

        Flower flower = new Flower();
        flower.setId(id);
        flower.setColor(color);
        flower.setFlower(name);
        flower.setPosition(position);

        return flower;
    }
}
