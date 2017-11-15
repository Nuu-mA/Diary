package com.pengin.poinsetia.konkatsudiary.View;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pengin.poinsetia.konkatsudiary.Model.Person;
import com.pengin.poinsetia.konkatsudiary.Model.PersonRealmHelper;
import com.pengin.poinsetia.konkatsudiary.Presenter.PersonContract;
import com.pengin.poinsetia.konkatsudiary.Presenter.PersonPresenter;
import com.pengin.poinsetia.konkatsudiary.R;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment implements OnRecyclerListener,View.OnClickListener,
        PersonContract.View{

    private final String TAG = "PersonFragment";

    private final int DIALOG_KEY = 100;

    private final static int MSG_REMOV_LIST = 1;
    private final static int MSG_MOVE_LIST = 2;

    private Activity mActivity = null;
    private View mView;

    // RecyclerViewとAdapter
    private RecyclerView mRecyclerView = null;
    private PersonAdapter mAdapter = null;
    private PersonRealmHelper mRealmHelper;
    private Handler mHandler;

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
    }

    /**
     * Adapterに削除後の通知を行う
     */
    @Override
    public void notifyItemRemoved() {
        // Adapterにリストデータ変更を通知する
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Adapterに入れ替え後の通知を行う
     */
    @Override
    public void notifyItemMoved() {
        // Adapterにリストデータ変更を通知する
        mAdapter.notifyDataSetChanged();
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

        // メインHandlerの作成
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {

                    switch (msg.what) {
                        case MSG_REMOV_LIST:
                            int index = (int) msg.obj;
                            // リストのスワイプ削除イベント
                            mPresenter.onSwiped(index);
                            Log.d(TAG, "removedList");
                            break;
                        case MSG_MOVE_LIST:
                            int fromIndex = msg.arg1;
                            int toIndex = msg.arg2;
                            // リストの入れ替えイベント
                            mPresenter.onMoveList(fromIndex,toIndex);
                            break;
                    }
                }
            };
        }

        ItemTouchHelper mIth  = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        // 元の場所
                        int fromIndex = viewHolder.getAdapterPosition();
                        // 移動後の場所
                        int toIndex = target.getAdapterPosition();
                        // RealmDBのアイテム入れ替えを開始する
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_MOVE_LIST,fromIndex,toIndex));
                        // リストアイテムの移動通知
                        mAdapter.notifyItemMoved(fromIndex,toIndex);
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int index = viewHolder.getAdapterPosition();
                        // リストアイテムの削除通知
                        mAdapter.notifyItemRemoved(index);
                        // RealmDBのアイテム削除を開始する
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_REMOV_LIST,index));
                    }
                });
        mIth.attachToRecyclerView(mRecyclerView);

        // リスト初期表示イベント
        mPresenter.initList();
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
        // ハンドラーの停止
        if (mHandler != null) {
            mHandler = null;
        }
    }

    @Override
    public void onRecyclerClicked(View v, int position) {
        // セルクリック処理
    }

}
