package utb.dip.jp.netalis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import utb.dip.jp.netalis.Utils.STATUS;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /////////////////////////////////////////////
    // static members
    static ListView todoListView = null;

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

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
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

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setIcon(mSectionsPagerAdapter.getIcon(i))
                            .setTabListener(this));
        }

        // App
        dbAdapter = new DBAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
                return true;
            }
            case R.id.action_task_random_select : {
                mViewPager.setCurrentItem(STATUS.TODO.position, true);
                if (todoListView != null) {
                    if (isShowSelectAtRandomToast) {
                        isShowSelectAtRandomToast = false;
                        Toast.makeText(
                            this,
                            R.string.action_task_random_select,
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                    final int i = Utils.rnd.nextInt(todoListView.getAdapter().getCount());
                    todoListView.setSelection(i);
                    todoListView.post(new Runnable() {
                        @Override
                        public void run() {
                            View v = todoListView.getChildAt(i - todoListView.getFirstVisiblePosition());
                            if (v != null) {
                                ObjectAnimator oa = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f, 0f, 1f);
                                oa.setDuration(1500);
                                oa.start();
                            }
                        }
                    });
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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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
            Locale l = Locale.getDefault();
            switch (position + 1) {
                case 1:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }

        public int getIcon(int position) {
            switch (position + 1) {
                case 1:
                    return getResources().getIdentifier("@*android:drawable/ic_menu_home", null, getPackageName());
                case 2:
                    return getResources().getIdentifier("@*android:drawable/ic_menu_mark", null, getPackageName());
                case 3:
                    return android.R.drawable.ic_menu_delete;
            }
            return 0;
        }

    }

    public static void refreshTaskAdapters() {
        for (STATUS status : STATUS.values()) {
            TasksAdapter tasksAdapter = taskArrayAdapters.get(status);
            if (tasksAdapter != null) {
                tasksAdapter.clear();
                // データ読み込み
                for (Task task : dbAdapter.selectTasks(status)) {
                    tasksAdapter.add(task);
                }
                tasksAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 多数リストの取得
     * @param status ステータス
     * @param context コンテキスト
     * @return TaskAdapter
     */
    public static TasksAdapter getTasksAdapter(STATUS status, Context context) {
        TasksAdapter tasksAdapter = taskArrayAdapters.get(status);
        if (tasksAdapter == null) {
            tasksAdapter = new TasksAdapter(context);
            dbAdapter.open();
            try {
                // キャンセルタグを読み込む前に、30日以上立ったものを削除。
                if (status == STATUS.CANCEL) {
                    dbAdapter.deleteCanceledTask();
                }
                // データ読み込み
                for (Task task : dbAdapter.selectTasks(status)) {
                    tasksAdapter.add(task);
                }
            } finally {
                dbAdapter.close();
            }
            taskArrayAdapters.put(status, tasksAdapter);
        }
        return taskArrayAdapters.get(status);
    }

    public void onActivityResult( int requestCode, int resultCode, Intent intent ) {
        // startActivityForResult()の際に指定した識別コードとの比較
        if( requestCode == EDIT_ACTIVITY ){
            // 返却結果ステータスとの比較
            if( resultCode == RESULT_OK ){
                // 返却されてきたintentから値を取り出す
                Task newTask = TasksAdapter.toTask(intent);
                Task task;
                TasksAdapter a = getTasksAdapter(STATUS.valueOf(newTask.status), this);
                if (newTask.uuid == null) {
                    if (Utils.isEmpty(newTask.task)) {
                        return;
                    }
                    // 新規追加
                    task = newTask;
                    task.status = STATUS.TODO.intValue;
                } else {
                    task = a.find(newTask.uuid);
                    // 更新なし
                    if (Utils.eq(newTask.task, task.task) && Utils.eq(newTask.color, task.color)) {
                        return;
                    }
                    a.remove(task);
                }
                task.task = newTask.task;
                task.status = newTask.status;
                task.color = newTask.color;
                task.lastupdate = MyDate.now().format();
                dbAdapter.open();
                dbAdapter.saveTask(task);
                dbAdapter.close();
                a.insert(task, 0);
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
            STATUS status = STATUS.valueOf(getArguments().getInt(ARG_STATUS));
            // リスト
            final TasksAdapter tasksAdapter = getTasksAdapter(status, this.getActivity());
            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            listView.setAdapter(tasksAdapter);
            SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
                listView,
                new TasksDismissCallbacks(status, getActivity())
            );
            listView.setOnTouchListener(touchListener);
            listView.setOnScrollListener(touchListener.makeScrollListener());
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), EditActivity.class );
                    TasksAdapter.putExtra(intent, tasksAdapter.getItem(i));
                    getActivity().startActivityForResult(intent, EDIT_ACTIVITY);
                }
            });
            if (status == STATUS.TODO) {
                MainActivity.todoListView = listView;
            }
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
                    task.status += dismissRight ? 1 : -1;
                    if (task.status == STATUS.TODO.intValue - 1) {
                        task.status = STATUS.CANCEL.intValue;
                    }
                    STATUS otherStatus = STATUS.valueOf(task.status);
                    if (otherStatus != STATUS.OTHER) {
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
