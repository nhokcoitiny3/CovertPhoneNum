package com.example.tiny.covertphonenum.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhokc.convertphonenum.R;
import com.example.nhokc.convertphonenum.databinding.ActivityMainBinding;
import com.example.tiny.covertphonenum.model.models.Group;
import com.example.tiny.covertphonenum.model.models.NumberFeild;
import com.example.tiny.covertphonenum.presenter.adapter.AdapterRcvGroup;
import com.example.tiny.covertphonenum.presenter.adapter.AdapterRcvPrefix;
import com.example.tiny.covertphonenum.model.models.Contact;
import com.example.tiny.covertphonenum.presenter.impl.IOnRecyclerViewItemClickListener;
import com.example.tiny.covertphonenum.model.databasehelper.MyDatabaseHelper;
import com.example.tiny.covertphonenum.model.models.Prefix;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterRcvGroup.IGroup, AdapterRcvPrefix.IPrefix, IOnRecyclerViewItemClickListener {
    public static final int RequestPermissionCode = 1;
    private ActivityMainBinding binding;
    private List<Contact> contactsOld;
    private List<Contact> contactsNew;
    private List<Group> groupList;
    private List<Prefix> prefixList;
    private MyDatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        enableRuntimePermission();
        initialize();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add:
                createDialogAdd();
                return true;

            default:
        }
        return super.onOptionsItemSelected(item);

    }

    public void initialize() {
        db = new MyDatabaseHelper(this);
        db.createDefaultPrefixsIfNeed();
        prefixList = db.getAllPrefix();
       /* initializeGroup();
        initializeRCVGroup();*/
        initializeRCV();
        binding.btnUpdate.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }


    public void initializeRCV() {
        AdapterRcvPrefix adapterRcvPrefix = new AdapterRcvPrefix();
        adapterRcvPrefix.setIdata(this);

        binding.rcvPrefix.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        });
        binding.rcvPrefix.setItemAnimator(new DefaultItemAnimator());
        binding.rcvPrefix.setAdapter(adapterRcvPrefix);
        adapterRcvPrefix.setOnItemClickListener(this);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                createDialogConfirm();
                break;
            case R.id.btn_add:
                createDialogAdd();
                initializeRCV();
                break;
            default:
                break;
        }
    }

    private void handler() {
        contactsNew = new ArrayList<>();
        for (int i = 0; i < contactsOld.size(); i++) {
            String id = contactsOld.get(i).getId();
            String name = contactsOld.get(i).getName();
            List<NumberFeild> numberFeildList = new ArrayList<>();
            for (int j = 0; j < contactsOld.get(i).getNumber().size(); j++) {
                String number = stripNonDigits(contactsOld.get(i).getNumber().get(j).getNumberContact());
                if(number.length()<=10){
                    continue;
                }
                String headerTemp = number.substring(0, 2);
                if ((headerTemp.equals("00") && number.length()<=13) || number.length() <= 10 || (headerTemp.equals("84") && number.length() <= 11)) {
                    continue;
                } else if (!headerTemp.equals("84") && (!headerTemp.equals("00"))) {
                    String header = number.substring(0, 4);
                    String content = number.substring(4);
                    for (int k = 0; k < prefixList.size(); k++) {
                        if (header.equals(prefixList.get(k).getOldPRe())) {
                            header = prefixList.get(k).getNewPre();
                            String numberNew = header + content;
                            numberFeildList.add(new NumberFeild(numberNew,contactsOld.get(i).getNumber().get(j).getNumberContact(), contactsOld.get(i).getNumber().get(j).getTypeContact()));
                        }
                    }

                }else if (headerTemp.equals("00")) {
                        String header = "0" + number.substring(4, 7);
                        String content = number.substring(7);
                        for (int k = 0; k < prefixList.size(); k++) {
                            if (header.equals(prefixList.get(k).getOldPRe())) {
                                header = prefixList.get(k).getNewPre();
                                String numberNew = "0084" + header.substring(1) + content;
                                numberFeildList.add(new NumberFeild(numberNew,contactsOld.get(i).getNumber().get(j).getNumberContact(), contactsOld.get(i).getNumber().get(j).getTypeContact()));
                            }
                        }
                    }   else{
                    String header = "0" + number.substring(2, 5);
                    String content = number.substring(5);
                    for (int k = 0; k < prefixList.size(); k++) {
                        if (header.equals(prefixList.get(k).getOldPRe())) {
                            header = prefixList.get(k).getNewPre();
                            String numberNew = "+84" + header.substring(1) + content;
                            numberFeildList.add(new NumberFeild(numberNew, contactsOld.get(i).getNumber().get(j).getNumberContact(),contactsOld.get(i).getNumber().get(j).getTypeContact()));
                        }
                    }

                }
            }
            if (numberFeildList.size() > 0) {
                contactsNew.add(new Contact(id, name, numberFeildList));
            }
        }

        if (contactsNew.size() > 0) {
            for (int j = 0; j < contactsNew.size(); j++) {
                update(contactsNew.get(j));
            }
        }


    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onRecyclerViewItemClicked(final int position, int id) {
        RecyclerView.ViewHolder viewHolder = binding.rcvPrefix.findViewHolderForAdapterPosition(position);
        @SuppressLint("RtlHardcoded") PopupMenu popup = new PopupMenu(MainActivity.this, viewHolder.itemView, Gravity.RIGHT);
        popup.getMenuInflater().inflate(R.menu.menu_item_rcv, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_add:
                        createDialogAdd();
                        break;
                    case R.id.item_edit:
                        createDialogEdit(prefixList.get(position), position);
                        break;
                    case R.id.item_delete:
                        createDialogDelete(position);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        @SuppressLint("RestrictedApi") MenuPopupHelper menuHelper = new MenuPopupHelper(MainActivity.this, (MenuBuilder) popup.getMenu(), viewHolder.itemView);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.RIGHT);
        menuHelper.show();
    }


    public void createDialogAdd() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        dialog.setContentView(R.layout.dialog_add_prefix);
        dialog.setTitle("Thêm đầu số");
        dialog.show();

        final LinearLayout btnOk = dialog.findViewById(R.id.btn_ok);
        final LinearLayout btnCancel = dialog.findViewById(R.id.btn_cancel);

        final EditText edtOld = dialog.findViewById(R.id.edt_old);
        final EditText edtNew = dialog.findViewById(R.id.edt_new);


        edtOld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String txtOld = edtOld.getText().toString();

                if (txtOld.isEmpty()) {
                    edtOld.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtOld.substring(0, 1).equals("0"))) {
                    edtOld.setError("Cần bắt đầu bằng số 0!");

                } else if (txtOld.length() < 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else if (txtOld.length() > 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getOldPRe().equals(txtOld)) {
                            edtOld.setError("Đầu số đã tồn tại!");

                        }
                    }

                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txtOld = edtOld.getText().toString();

                if (txtOld.isEmpty()) {
                    edtOld.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtOld.substring(0, 1).equals("0"))) {
                    edtOld.setError("Cần bắt đầu bằng số 0!");

                } else if (txtOld.length() < 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else if (txtOld.length() > 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getOldPRe().equals(txtOld)) {
                            edtOld.setError("Đầu số đã tồn tại!");

                        }
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String txtOld = edtOld.getText().toString();

                if (txtOld.isEmpty()) {
                    edtOld.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtOld.substring(0, 1).equals("0"))) {
                    edtOld.setError("Cần bắt đầu bằng số 0!");

                } else if (txtOld.length() > 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else if (txtOld.length() < 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getOldPRe().equals(txtOld)) {
                            edtOld.setError("Đầu số đã tồn tại!");

                        }
                    }

                }


            }
        });
        edtOld.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String txtOld = edtOld.getText().toString();

                if (txtOld.isEmpty()) {
                    edtOld.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtOld.substring(0, 1).equals("0"))) {
                    edtOld.setError("Cần bắt đầu bằng số 0!");

                } else if (txtOld.length() > 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else if (txtOld.length() < 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getOldPRe().equals(txtOld)) {
                            edtOld.setError("Đầu số đã tồn tại!");

                        }
                    }

                }

            }
        });
        edtNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                String txtNew = edtNew.getText().toString();
                if (txtNew.isEmpty()) {
                    edtNew.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtNew.substring(0, 1).equals("0"))) {
                    edtNew.setError("Cần bắt đầu bằng số 0!");

                } else if (txtNew.length() < 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else if (txtNew.length() > 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getNewPre().equals(txtNew)) {
                            edtNew.setError("Đầu số đã tồn tại!");
                        }
                    }

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String txtNew = edtNew.getText().toString();
                if (txtNew.isEmpty()) {
                    edtNew.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtNew.substring(0, 1).equals("0"))) {
                    edtNew.setError("Cần bắt đầu bằng số 0!");

                } else if (txtNew.length() < 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else if (txtNew.length() > 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getNewPre().equals(txtNew)) {
                            edtNew.setError("Đầu số đã tồn tại!");
                        }
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                String txtNew = edtNew.getText().toString();
                if (txtNew.isEmpty()) {
                    edtNew.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtNew.substring(0, 1).equals("0"))) {
                    edtNew.setError("Cần bắt đầu bằng số 0!");

                } else if (txtNew.length() < 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else if (txtNew.length() > 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getNewPre().equals(txtNew)) {
                            edtNew.setError("Đầu số đã tồn tại!");

                        }
                    }

                }


            }
        });

        edtNew.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String txtNew = edtNew.getText().toString();
                if (txtNew.isEmpty()) {
                    edtNew.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtNew.substring(0, 1).equals("0"))) {
                    edtNew.setError("Cần bắt đầu bằng số 0!");

                } else if (txtNew.length() < 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else if (txtNew.length() > 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getNewPre().equals(txtNew)) {
                            edtNew.setError("Đầu số đã tồn tại!");

                        }
                    }

                }
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtOld = dialog.findViewById(R.id.edt_old);
                EditText edtNew = dialog.findViewById(R.id.edt_new);
                String oldPrefix = edtOld.getText().toString();
                String newPrefix = edtNew.getText().toString();
                if ((edtNew.getError() == null && edtOld.getError() == null) && !newPrefix.isEmpty() && !oldPrefix.isEmpty()) {
                    boolean check = true;
                    if ((!oldPrefix.substring(0, 1).equals("0")) && (!newPrefix.substring(0, 1).equals("0"))) {
                        check = false;
                        edtOld.requestFocus();
                        edtOld.setError("Cần bắt đầu bằng số 0!");
                        edtNew.setError("Cần bắt đầu bằng số 0!");
                    } else if ((oldPrefix.substring(0, 1).equals("0")) && (!newPrefix.substring(0, 1).equals("0"))) {
                        check = false;
                        edtNew.requestFocus();
                        edtNew.setError("Cần bắt đầu bằng số 0!");

                    } else if ((!oldPrefix.substring(0, 1).equals("0")) && (newPrefix.substring(0, 1).equals("0"))) {
                        check = false;
                        edtOld.requestFocus();
                        edtOld.setError("Cần bắt đầu bằng số 0!");
                    } else {
                        for (int i = 0; i < prefixList.size(); i++) {
                            if (prefixList.get(i).getOldPRe().equals(oldPrefix) && prefixList.get(i).getNewPre().equals(newPrefix)) {
                                check = false;
                                edtOld.requestFocus();
                                edtOld.setError("Đầu số đã tồn tại!");
                                edtNew.setError("Đầu số đã tồn tại!");
                            } else if (!prefixList.get(i).getOldPRe().equals(oldPrefix) && prefixList.get(i).getNewPre().equals(newPrefix)) {
                                check = false;
                                edtNew.requestFocus();
                                edtNew.setError("Đầu số đã tồn tại!");
                            } else if (prefixList.get(i).getOldPRe().equals(oldPrefix) && !prefixList.get(i).getNewPre().equals(newPrefix)) {
                                check = false;
                                edtOld.requestFocus();
                                edtOld.setError("Đầu số đã tồn tại!");
                            }
                        }

                    }
                    if (check) {
                        dialog.dismiss();
                        Prefix prefix = new Prefix(prefixList.size() + 1, oldPrefix, newPrefix);
                        db.addPrefix(prefix);
                        prefixList.add(prefix);
                        initializeRCV();
                        Toast.makeText(MainActivity.this, "Thêm dầu số thành công!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorOld = "";
                    String errorNew = "";
                    if (edtOld.getError() != null) {
                        errorOld = edtOld.getError().toString();
                    }
                    if (edtNew.getError() != null) {
                        errorNew = edtNew.getError().toString();
                    }
                    if (newPrefix.isEmpty() && oldPrefix.isEmpty()) {
                        edtOld.requestFocus();
                        edtOld.setError("Vui lòng nhập dữ liệu!");
                        edtNew.setError("Vui lòng nhập dữ liệu!");
                    }
                    if (errorNew == "" && errorOld != "" && newPrefix.isEmpty()) {
                        edtOld.requestFocus();
                        edtOld.setError(errorOld);
                        edtNew.setError("Vui lòng nhập dữ liệu!");
                    }
                    if (errorNew == "" && errorOld != "" && !newPrefix.isEmpty()) {
                        edtOld.requestFocus();
                        edtOld.setError(errorOld);
                    }
                    if (errorNew != "" && errorOld == "" && !oldPrefix.isEmpty()) {
                        edtNew.requestFocus();
                        edtNew.setError(errorNew);
                    }
                    if (errorNew != "" && errorOld == "" && oldPrefix.isEmpty()) {
                        edtOld.requestFocus();
                        edtNew.setError(errorNew);
                        edtOld.setError("Vui lòng nhập dữ liệu!");
                    }
                    if (errorNew != "" && errorOld != "") {
                        edtOld.requestFocus();
                        edtNew.setError(errorNew);
                        edtOld.setError(errorOld);
                    }
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void createDialogEdit(final Prefix prefix, final int pos) {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        dialog.setContentView(R.layout.dialog_add_prefix);
        dialog.setTitle("Sửa đầu số");

        final EditText edtOld = dialog.findViewById(R.id.edt_old);
        final EditText edtNew = dialog.findViewById(R.id.edt_new);

        edtOld.setText(prefix.getOldPRe());
        edtNew.setText(prefix.getNewPre());

        edtOld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String txtOld = edtOld.getText().toString();

                if (txtOld.isEmpty()) {
                    edtOld.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtOld.substring(0, 1).equals("0"))) {
                    edtOld.setError("Cần bắt đầu bằng số 0!");

                } else if (txtOld.length() > 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else if (txtOld.length() < 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getOldPRe().equals(txtOld)) {
                            if (i != pos) {
                                edtOld.setError("Đầu số đã tồn tại!");
                            }

                        }
                    }

                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txtOld = edtOld.getText().toString();

                if (txtOld.isEmpty()) {
                    edtOld.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtOld.substring(0, 1).equals("0"))) {
                    edtOld.setError("Cần bắt đầu bằng số 0!");

                } else if (txtOld.length() > 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else if (txtOld.length() < 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getOldPRe().equals(txtOld)) {
                            if (i != pos) {
                                edtOld.setError("Đầu số đã tồn tại!");
                            }


                        }
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String txtOld = edtOld.getText().toString();

                if (txtOld.isEmpty()) {
                    edtOld.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtOld.substring(0, 1).equals("0"))) {
                    edtOld.setError("Cần bắt đầu bằng số 0!");

                } else if (txtOld.length() > 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else if (txtOld.length() < 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getOldPRe().equals(txtOld)) {
                            if (i != pos) {
                                edtOld.setError("Đầu số đã tồn tại!");
                            }
                        }
                    }

                }


            }
        });

        edtOld.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String txtOld = edtOld.getText().toString();

                if (txtOld.isEmpty()) {
                    edtOld.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtOld.substring(0, 1).equals("0"))) {
                    edtOld.setError("Cần bắt đầu bằng số 0!");

                } else if (txtOld.length() > 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else if (txtOld.length() < 4) {
                    edtOld.setError("Vui lòng nhập đủ 4 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getOldPRe().equals(txtOld)) {
                            if (i != pos) {
                                edtOld.setError("Đầu số đã tồn tại!");
                            }
                        }
                    }

                }
            }
        });
        edtNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                String txtNew = edtNew.getText().toString();
                if (txtNew.isEmpty()) {
                    edtNew.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtNew.substring(0, 1).equals("0"))) {
                    edtNew.setError("Cần bắt đầu bằng số 0!");

                } else if (txtNew.length() > 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else if (txtNew.length() < 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getNewPre().equals(txtNew)) {
                            if (i != pos) {
                                edtNew.setError("Đầu số đã tồn tại!");
                            }
                        }
                    }

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String txtNew = edtNew.getText().toString();
                if (txtNew.isEmpty()) {
                    edtNew.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtNew.substring(0, 1).equals("0"))) {
                    edtNew.setError("Cần bắt đầu bằng số 0!");

                } else if (txtNew.length() > 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else if (txtNew.length() < 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getNewPre().equals(txtNew)) {
                            if (i != pos) {
                                edtNew.setError("Đầu số đã tồn tại!");
                            }
                        }
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                String txtNew = edtNew.getText().toString();
                if (txtNew.isEmpty()) {
                    edtNew.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtNew.substring(0, 1).equals("0"))) {
                    edtNew.setError("Cần bắt đầu bằng số 0!");

                } else if (txtNew.length() > 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else if (txtNew.length() < 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getNewPre().equals(txtNew)) {
                            if (i != pos) {
                                edtNew.setError("Đầu số đã tồn tại!");
                            }

                        }
                    }

                }


            }
        });
        edtNew.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String txtNew = edtNew.getText().toString();
                if (txtNew.isEmpty()) {
                    edtNew.setError("Vui lòng nhập dữ liệu!");

                } else if ((!txtNew.substring(0, 1).equals("0"))) {
                    edtNew.setError("Cần bắt đầu bằng số 0!");

                } else if (txtNew.length() > 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else if (txtNew.length() < 3) {
                    edtNew.setError("Vui lòng nhập đủ 3 số!");

                } else {
                    for (int i = 0; i < prefixList.size(); i++) {
                        if (prefixList.get(i).getNewPre().equals(txtNew)) {
                            if (i != pos) {
                                edtNew.setError("Đầu số đã tồn tại!");
                            }

                        }
                    }

                }
            }
        });
        dialog.show();

        final LinearLayout btnOk = dialog.findViewById(R.id.btn_ok);
        final LinearLayout btnCancel = dialog.findViewById(R.id.btn_cancel);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText edtOld = dialog.findViewById(R.id.edt_old);
                EditText edtNew = dialog.findViewById(R.id.edt_new);
                String oldPrefix = edtOld.getText().toString();
                String newPrefix = edtNew.getText().toString();
                if (oldPrefix.equals(prefixList.get(pos).getOldPRe()) && newPrefix.equals(prefixList.get(pos).getNewPre())) {
                    dialog.dismiss();
                } else if ((edtNew.getError() == null && edtOld.getError() == null) && !newPrefix.isEmpty() && !oldPrefix.isEmpty()) {
                    boolean check = true;
                    if ((!oldPrefix.substring(0, 1).equals("0")) && (!newPrefix.substring(0, 1).equals("0"))) {
                        check = false;
                        edtOld.requestFocus();
                        edtOld.setError("Cần bắt đầu bằng số 0!");
                        edtNew.setError("Cần bắt đầu bằng số 0!");
                    } else if ((oldPrefix.substring(0, 1).equals("0")) && (!newPrefix.substring(0, 1).equals("0"))) {
                        check = false;
                        edtNew.requestFocus();
                        edtNew.setError("Cần bắt đầu bằng số 0!");

                    } else if ((!oldPrefix.substring(0, 1).equals("0")) && (newPrefix.substring(0, 1).equals("0"))) {
                        check = false;
                        edtOld.requestFocus();
                        edtOld.setError("Cần bắt đầu bằng số 0!");
                    } else {
                        for (int i = 0; i < prefixList.size(); i++) {
                            if (i != pos) {
                                continue;
                            } else if (prefixList.get(i).getOldPRe().equals(oldPrefix) && prefixList.get(i).getNewPre().equals(newPrefix) && pos!=i) {
                                check = false;
                                edtOld.requestFocus();
                                edtOld.setError("Đầu số đã tồn tại!");
                                edtNew.setError("Đầu số đã tồn tại!");
                            } else if (!prefixList.get(i).getOldPRe().equals(oldPrefix) && prefixList.get(i).getNewPre().equals(newPrefix) && pos!=i) {
                                check = false;
                                edtNew.requestFocus();
                                edtNew.setError("Đầu số đã tồn tại!");
                            } else if (prefixList.get(i).getOldPRe().equals(oldPrefix) && !prefixList.get(i).getNewPre().equals(newPrefix) && pos!=i) {
                                check = false;
                                edtOld.requestFocus();
                                edtOld.setError("Đầu số đã tồn tại!");
                            }
                        }

                    }
                    if (check) {
                        dialog.dismiss();
                        Prefix prefix = new Prefix(prefixList.get(pos).getId(), edtOld.getText().toString(), edtNew.getText().toString());
                        db.updatePrefix(prefix);
                        prefixList.remove(pos);
                        prefixList.add(pos, prefix);
                        initializeRCV();
                        Toast.makeText(MainActivity.this, "Sửa đầu số thành công!", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    String errorOld = "";
                    String errorNew = "";
                    if (edtOld.getError() != null) {
                        errorOld = edtOld.getError().toString();
                    }
                    if (edtNew.getError() != null) {
                        errorNew = edtNew.getError().toString();
                    }
                    if (newPrefix.isEmpty() && oldPrefix.isEmpty()) {
                        edtOld.requestFocus();
                        edtOld.setError("Vui lòng nhập dữ liệu!");
                        edtNew.setError("Vui lòng nhập dữ liệu!");
                    }
                    if (errorNew == "" && errorOld != "" && newPrefix.isEmpty()) {
                        edtOld.requestFocus();
                        edtOld.setError(errorOld);
                        edtNew.setError("Vui lòng nhập dữ liệu!");
                    }
                    if (errorNew == "" && errorOld != "" && !newPrefix.isEmpty()) {
                        edtOld.requestFocus();
                        edtOld.setError(errorOld);
                    }
                    if (errorNew != "" && errorOld == "" && !oldPrefix.isEmpty()) {
                        edtNew.requestFocus();
                        edtNew.setError(errorNew);
                    }
                    if (errorNew != "" && errorOld == "" && oldPrefix.isEmpty()) {
                        edtOld.requestFocus();
                        edtNew.setError(errorNew);
                        edtOld.setError("Vui lòng nhập dữ liệu!");
                    }
                    if (errorNew != "" && errorOld != "") {
                        edtOld.requestFocus();
                        edtNew.setError(errorNew);
                        edtOld.setError(errorOld);
                    }
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void createDialogConfirm() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        dialog.setContentView(R.layout.fragment_confirm);
        dialog.setTitle("Xác nhận");
        dialog.show();

        final LinearLayout btnOk = dialog.findViewById(R.id.btn_ok);
        final LinearLayout btnCancel = dialog.findViewById(R.id.btn_cancel);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                contactsNew = new ArrayList<>();
                contactsOld = new ArrayList<>();
                contactsOld = readPhoneContacts(MainActivity.this);
                handler();
                new Convert().execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void createDialogDelete(final int position) {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        dialog.setContentView(R.layout.fragment_confirm);
        dialog.setTitle("Xác nhận");
        dialog.show();
        final TextView txtTitle = dialog.findViewById(R.id.txt_title);
        final TextView txtContent = dialog.findViewById(R.id.txt_content);

        txtTitle.setText("Xác nhận xóa đầu số");
        txtContent.setText("Bạn có chắc chắn muốn xóa đầu số này không?");
        final LinearLayout btnOk = dialog.findViewById(R.id.btn_ok);
        final LinearLayout btnCancel = dialog.findViewById(R.id.btn_cancel);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                db.deletePrefix(prefixList.get(position));
                prefixList.remove(position);
                initializeRCV();
                Toast.makeText(MainActivity.this, "Xóa đầu số thành công!", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public Group geGroup(int position) {
        return groupList.get(position);
    }


    @SuppressLint("StaticFieldLeak")
    public class Convert extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Cập nhật danh bạ...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            handler();
            return null;
        }

        void centerText(View view) {
            if (view instanceof TextView) {
                ((TextView) view).setGravity(Gravity.CENTER_HORIZONTAL);
            } else if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                int n = group.getChildCount();
                for (int i = 0; i < n; i++) {
                    centerText(group.getChildAt(i));
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            dialog.dismiss();
            if (contactsNew.size() > 0) {
                Toast toast = Toast.makeText(MainActivity.this, "Chuyển đổi đầu số thành công cho " + contactsNew.size() + " thuê bao trong danh bạ!", Toast.LENGTH_LONG);
                centerText(toast.getView());
                toast.show();
            } else {
                Toast toast = Toast.makeText(MainActivity.this, "Không có số nào cần chuyển đổi đầu số!", Toast.LENGTH_LONG);
                centerText(toast.getView());
                toast.show();
            }
            contactsNew = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return prefixList.size();
    }

    @Override
    public Prefix gePrefix(int position) {
        return prefixList.get(position);
    }


    public void enableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this,
                Manifest.permission.READ_CONTACTS)) {

        } else {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);


        }
        ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this,
                Manifest.permission.WRITE_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int RC, @NonNull String per[], @NonNull int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                break;
        }
    }


    public static String stripNonDigits(
            final CharSequence input) {
        final StringBuilder sb = new StringBuilder(
                input.length());
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c > 47 && c < 58) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public void update(Contact contact) {

        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < contact.getNumber().size(); i++) {
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getNumber().get(i).getNumberContact());

            StringBuffer whereClauseBuf = new StringBuffer();
            whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
            whereClauseBuf.append("=");
            whereClauseBuf.append(contact.getId());

            whereClauseBuf.append(" and ");
            whereClauseBuf.append(ContactsContract.Data.MIMETYPE);
            whereClauseBuf.append(" = '");
            String mimetype = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
            whereClauseBuf.append(mimetype);
            whereClauseBuf.append("'");

               whereClauseBuf.append(" and ");
                whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.NUMBER);
                whereClauseBuf.append(" = '");
                whereClauseBuf.append(contact.getNumber().get(i).getNumberContactOld().trim());
                whereClauseBuf.append("'");

                whereClauseBuf.append(" and ");
                whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.TYPE);
                whereClauseBuf.append(" = ");
                whereClauseBuf.append(contact.getNumber().get(i).getTypeContact());

                Uri dataUri = ContactsContract.Data.CONTENT_URI;

                contentResolver.update(dataUri, contentValues, whereClauseBuf.toString(), null);

        }
    }


    public static List<Contact> readPhoneContacts(Context context) {
        List<Contact> contacts = new ArrayList<>();
        try {
            Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");

            Integer contactsCount = cursor.getCount();
            if (contactsCount > 0) {
                while (cursor.moveToNext()) {

                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Contact contact = new Contact();
                        contact.setId(id);
                        contact.setName(contactName);
                        Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        List<NumberFeild> numberFeildList = new ArrayList<>();
                        while (pCursor.moveToNext()) {
                            int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            System.out.println(phoneType);
                            String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            NumberFeild numberFeild = new NumberFeild();
                            numberFeild.setTypeContact(phoneType);
                            numberFeild.setNumberContact(phoneNo);

                            numberFeildList.add(numberFeild);
                        }
                        pCursor.close();
                        contact.setNumber(numberFeildList);
                        contacts.add(contact);
                    }

                }
                cursor.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return contacts;
    }
}
