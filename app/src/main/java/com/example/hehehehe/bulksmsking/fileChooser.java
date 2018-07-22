package com.example.hehehehe.bulksmsking;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.applandeo.FilePicker;
import com.applandeo.constants.FileType;
import com.applandeo.listeners.OnSelectFileListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by hehehehe on 1/30/2018.
 */

public class fileChooser extends Fragment {


    private RecyclerView mRecyclerView;
    private addadapter mRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SparseBooleanArray mSelectedItemsIds;
    Context context;
    private RelativeLayout relativeLayout;
    static List<addressname> paths = new ArrayList<>();
    DatabaseHelper myDb ;
    backgroundTask btinsert,btretrieve;
    btretrieveall bt;
    Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.filechooser, container,false);
        setHasOptionsMenu(true);
        this.context = getContext();
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.recycler);
        myDb = new DatabaseHelper(context);
        if(myDb.getCount()>0){
            bt = new btretrieveall(context);
            try {
                paths = bt.execute("retrieveAll").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        mRecyclerAdapter = new addadapter(paths);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider));

        mRecyclerView.setAdapter(mRecyclerAdapter);
        relativeLayout = (RelativeLayout) rootview.findViewById(R.id.relative);
        myDb = new DatabaseHelper(getActivity());
        btinsert = new backgroundTask(context);
        btretrieve = new backgroundTask(context);
        hideKeyboard(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return rootview;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filechooser,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        this.context = getContext();

        switch(item.getItemId()){
            case R.id.add: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
                }
                //openPicker();
                new Handler().postDelayed(new Runnable() {
                            public void run() {
                                openPicker();
                            }
                        }, 0);
            }
                break;
            case android.R.id.home: {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {

        super.onStart();
        hideKeyboard(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void openPicker() {
        new FilePicker.Builder(getActivity(), listener)
                //this method let you decide how far user can go up in the directories tree
                .mainDirectory(Environment.getExternalStorageDirectory().getPath())
                //this method let you choose what types of files user will see in the picker
                .fileType(FileType.TEXT)
                //this method let you hide files, only directories will be visible for user
                .hideFiles(false)
                //this method let you decide which directory user will see after picker opening
                .directory(Environment.getExternalStorageDirectory().getAbsolutePath())
                .show();
    }

    private OnSelectFileListener listener = (File file) -> {
        boolean bool = false;
        for(int i=0;i<paths.size();i++){
            if(paths.get(i).getFilePath().equals(file.getAbsolutePath()))
                bool=true;
            else
                bool = false;
        }
        if(!bool){
            if(file.getName().endsWith(".txt")) {
                btinsert = new backgroundTask(context);
                btretrieve = new backgroundTask(context);
                String[] x = new String[0];
                try {
                    x = btinsert.execute("insert",file.getName(),file.getAbsolutePath()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                String[] b = new String[3];
                if(x==null){
                    Toast.makeText(context,"Inserted Successfully",Toast.LENGTH_SHORT).show();
                    try {
                        b = btretrieve.execute("retrieve").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    paths.add(new addressname(Integer.parseInt(b[0]), b[1], b[2]));
                }else
                    Toast.makeText(context,"Already Present",Toast.LENGTH_SHORT).show();


//                Cursor res = myDb.getAllData();
//                if(res.getCount() <= 0) {
//                    // show message
//                    showMessage("Error","Nothing found");
//                    return;
//                }
//                while (res.moveToLast()) {
//
//                    paths.add(new addressname(Integer.parseInt(res.getString(0)), res.getString(1), res.getString(2)));
//                }



                mRecyclerAdapter.notifyData(paths);

            }else
                Toast.makeText(context,"Please Select Only Text File",Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(context,"File is already in list",Toast.LENGTH_SHORT).show();
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                //using flag below you can check if user granted storage permissions for the picker
                && requestCode == FilePicker.STORAGE_PERMISSIONS) {
            openPicker();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    Intent i = new Intent(getActivity(),MainActivity.class);
                    startActivity(i);
                    return true;
                }
                return false;
            }
        });
        hideKeyboard(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
