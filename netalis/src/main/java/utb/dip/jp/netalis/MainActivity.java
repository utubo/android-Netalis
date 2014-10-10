package utb.dip.jp.netalis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import utb.dip.jp.netalis.U.STATUS;

public class MainActivity extends BaseActivity implements ActionBar.TabListener  {

    /////////////////////////////////////////////
    // static members
    static Menu mainMenu = null;
    static MyListView todoListView = null;

    /** DB */
    public static DBAdapter dbAdapter;

    static Map<STATUS, TasksAdapter> taskArrayAdapters = new HashMap<STATUS, TasksAdapter>();

    static final int EDIT_ACTIVITY = 10001;

    /////////////////////////////////////////////
    // instance members
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    boolean isShowSelectAtRandomToast = true;

    /////////////////////////////////////////////
    // methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = U.notNull(getActionBar());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = find(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // Set up the action bar.
        /*
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setIcon(mSectionsPagerAdapter.getIcon(i))
                            .setTabListener(this));
        }
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        */
        IconTextAdapter ad = new IconTextAdapter(this);
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            STATUS s = STATUS.positionOf(i);
            ad.add(mSectionsPagerAdapter.getPageTitle(i), s.getIcon(this));
        }
        actionBar.setListNavigationCallbacks(ad,
            new ActionBar.OnNavigationListener() {
                @Override
                public boolean onNavigationItemSelected(int i, long l) {
                    return false;
                }
            }
        );
        // アプリタイトルを非表示に設定
        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // App
        dbAdapter = new DBAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        //if (id == R.id.action_settings) {
        //    return true;
        //}
        switch (id) {
            case R.id.action_nav_back :
            case R.id.action_nav_forward : {
                int d = (id == R.id.action_nav_back) ? -1 : 1;
                int count = mSectionsPagerAdapter.getCount();
                int p = (mViewPager.getCurrentItem() + d + count) % count;
                mViewPager.setCurrentItem(p, true);
                return true;
            }
            case R.id.action_task_undo : {
                Toast.makeText(
                        this,
                        undoToastText,
                        Toast.LENGTH_SHORT
                ).show();
                undo();
                return true;
            }
            case R.id.action_task_add : {
                // select TO-DO tab
                if (mViewPager.getCurrentItem() != STATUS.TODO.position) {
                    mViewPager.setCurrentItem(STATUS.TODO.position, true);
                }
                // startEditIntent
                Intent intent = new Intent(this, EditActivity.class );
                Task task = new Task();
                TasksAdapter.putExtra(intent, task);
                startActivityForResult(intent, EDIT_ACTIVITY);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                return true;
            }
            case R.id.action_task_random_select : {
                mViewPager.setCurrentItem(STATUS.TODO.position, true);
                if (todoListView != null) {
                    int count = todoListView.getAdapter().getCount();
                    if (count < 1) {
                        return true;
                    }
                    if (isShowSelectAtRandomToast) {
                        isShowSelectAtRandomToast = false;
                        Toast.makeText(
                            this,
                            R.string.action_task_random_select,
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                    final int i = U.rnd.nextInt(count);
                    todoListView.setSelectionCenter(i);
                    todoListView.blink(i);
                }
                return true;
            }
            case R.id.action_convert_json : {
                Intent intent = new Intent(this, ConvertJSONActivity.class );
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(STATUS.positionOf(position).titleId).toUpperCase();
        }
    }

    /**
     * リスト再読み込み
     */
    public static void refreshTaskAdapters() {
        for (STATUS status : STATUS.values()) {
            TasksAdapter tasksAdapter = taskArrayAdapters.get(status);
            if (tasksAdapter != null) {
                tasksAdapter.clear();
                // データ読み込み
                tasksAdapter.loadMore(dbAdapter);
                tasksAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Taskリストの取得
     * @param status ステータス
     * @param context コンテキスト
     * @return TaskAdapter
     */
    public static TasksAdapter getTasksAdapter(STATUS status, Context context) {
        TasksAdapter tasksAdapter = taskArrayAdapters.get(status);
        if (tasksAdapter == null) {
            tasksAdapter = new TasksAdapter(context, status);
            dbAdapter.open();
            try {
                // キャンセルタグを読み込む前に、30日以上立ったものを削除。
                if (status == STATUS.CANCEL) {
                    dbAdapter.deleteCanceledTask();
                }
                // データ読み込み
                tasksAdapter.loadMore(dbAdapter);
            } finally {
                dbAdapter.close();
            }
            taskArrayAdapters.put(status, tasksAdapter);
        }
        return taskArrayAdapters.get(status);
    }

    static Task undoTask = null;
    static String undoToastText = "";

    public static void setUndoTask(Task task) {
        undoTask = task == null ? null : task.tryClone();
        if (mainMenu != null)
            U.notNull(mainMenu.findItem(R.id.action_task_undo)).setEnabled(task != null);
        undoToastText = "Undo.";
    }

    public void undo() {
        if (undoTask == null) {
            return;
        }
        dbAdapter.open();
        try {
            Task nowTask = dbAdapter.selectTask(undoTask.uuid);
            dbAdapter.saveTask(
                undoTask,
                DBAdapter.QUERY_OPTION.WITHOUT_UPDATE_LASTUPDATE,
                DBAdapter.QUERY_OPTION.FORCE_UPDATE
            );
            boolean isRedo = "Redo.".equals(undoToastText);
            setUndoTask(nowTask);
            undoToastText = (isRedo ? "Undo" : "Redo.");
            refreshTaskAdapters();
        } finally {
            dbAdapter.close();
        }
    }

    /**
     * 他画面からの復帰処理
     * @param requestCode 要求画面
     * @param resultCode 結果
     * @param intent インテント
     */
    public void onActivityResult( int requestCode, int resultCode, Intent intent ) {
        // startActivityForResult()の際に指定した識別コードとの比較
        if( requestCode == EDIT_ACTIVITY ){
            // 返却結果ステータスとの比較
            if( resultCode == RESULT_OK ){
                // 返却されてきたintentから値を取り出す
                Task newTask = TasksAdapter.fromExtra(intent);
                Task task;
                TasksAdapter a = getTasksAdapter(STATUS.valueOf(newTask.status), this);
                if (newTask.uuid == null) {
                    if (U.isEmpty(newTask.task)) {
                        return;
                    }
                    // 新規追加
                    setUndoTask(null);
                    task = newTask;
                    task.status = STATUS.TODO.intValue;
                } else {
                    task = a.find(newTask.uuid);
                    // 更新なし
                    if (U.eq(newTask.task, task.task) &&
                        U.eq(newTask.color, task.color) &&
                        U.eq(newTask.priority, task.priority)
                    ) {
                        return;
                    }
                    setUndoTask(task);
                    a.remove(task);
                }
                task.task = newTask.task;
                task.status = newTask.status;
                task.color = newTask.color;
                task.priority = newTask.priority;
                task.lastupdate = MyDate.now().format();
                dbAdapter.open();
                dbAdapter.saveTask(task);
                dbAdapter.close();
                int i;
                for (i = 0; i < a.getCount(); i ++) {
                    if (a.getItem(i).priority <= task.priority) {
                        break;
                    }
                }
                a.insert(task, i);
                a.notifyDataSetChanged();
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /** The fragment argument representing the section number for this fragment. */
        private static final String ARG_STATUS = "arg_status";

        /** Returns a new instance of this fragment for the given section number. */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_STATUS, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            // レイアウトのセットアップ
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            if (rootView == null) {
                return null;
            }
            // ステータス
            STATUS status = STATUS.valueOf(getArguments().getInt(ARG_STATUS));
            // インフォ
            ((TextView) rootView.findViewById(R.id.pageInfo)).setText(status.infoId);
            // ガイド
            ((ImageView) rootView.findViewById(R.id.guidNextImageView)).setImageResource(status.next().getIcon(getActivity()));
            ((ImageView) rootView.findViewById(R.id.guidPrevImageView)).setImageResource(status.prev().getIcon(getActivity()));
            // リスト
            final TasksAdapter tasksAdapter = getTasksAdapter(status, this.getActivity());
            final MyListView listView = U.find(rootView, R.id.listView);
            listView.setAdapter(tasksAdapter);
            SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
                listView,
                new TasksDismissCallbacks(status, getActivity())
            );
            listView.setOnTouchListener(touchListener);
            listView.setOnScrollListener(touchListener.makeScrollListener(
                    new AbsListView.OnScrollListener() {
                        // 下までいったら自動読み込み
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            if (!tasksAdapter.hasMore)
                                return;
                            if (totalItemCount - visibleItemCount == firstVisibleItem) {
                                dbAdapter.open();
                                try {
                                    tasksAdapter.loadMore(dbAdapter);
                                } finally {
                                    dbAdapter.close();
                                }
                                tasksAdapter.notifyDataSetChanged();
                            }
                        }
                    }
            ));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), EditActivity.class );
                    TasksAdapter.putExtra(intent, tasksAdapter.getItem(i));
                    getActivity().startActivityForResult(intent, EDIT_ACTIVITY);
                    getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                }
            });
            if (status == STATUS.TODO) {
                MainActivity.todoListView = listView;
            }
            touchListener.setGuidView(rootView.findViewById(R.id.guidView));
            return rootView;
        }

    }

    /**
     * リストアイテムのスワイプ時イベント
     */
    public static class TasksDismissCallbacks implements SwipeDismissListViewTouchListener.DismissCallbacks {

        STATUS status;
        Context context;

        /**
         * コンストラクタ
         * @param status ステータス
         * @param context コンテキスト
         */
        public TasksDismissCallbacks(STATUS status, Context context) {
            this.status = status;
            this.context = context;
        }

        /**
         * 削除可とするか？
         * @param position リスト上の位置
         * @return 常にtrue。
         */
        @Override
        public boolean canDismiss(int position) {
            return true;
        }

        /**
         * スワイプ完了イベント
         * @param listView               The originating {@link ListView}.
         * @param reverseSortedPositions An array of positions to dismiss, sorted in descending
         * @param dismissRight 右方向へのスワイプならtrue。
         */
        @SuppressLint("ShowToast")
        @Override
        public void onDismiss(ListView listView, int[] reverseSortedPositions, boolean dismissRight) {
            dbAdapter.open();
            try {
                // 表示中のリスト
                TasksAdapter tasksAdapter = getTasksAdapter(status, context);
                Set<TasksAdapter> changed = new HashSet<TasksAdapter>();
                // 変更があったリスト
                changed.add(tasksAdapter);
                for (int position : reverseSortedPositions) {
                    Task task = tasksAdapter.getItem(position);
                    setUndoTask(task);
                    task.status += dismissRight ? 1 : -1;
                    if (task.status == STATUS.TODO.intValue - 1) {
                        task.status = STATUS.CANCEL.intValue;
                    }
                    STATUS otherStatus = STATUS.valueOf(task.status);
                    if (otherStatus != STATUS.REMOVE) {
                        // タスクを移動
                        dbAdapter.saveTask(task);
                        TasksAdapter otherAdapter = getTasksAdapter(otherStatus, context);
                        otherAdapter.insert(task, 0);
                        changed.add(otherAdapter);
                    } else {
                        // DBから削除
                        dbAdapter.deleteTask(task);
                    }
                    // リストから削除
                    tasksAdapter.remove(task);
                }
                // 移動先のリストも含めて更新を通知。
                for (TasksAdapter a : changed) {
                    a.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
            } finally {
                dbAdapter.close();
            }
        }
    }

}
