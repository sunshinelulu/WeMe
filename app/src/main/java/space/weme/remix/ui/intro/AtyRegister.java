package space.weme.remix.ui.intro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.regex.Pattern;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.CountDownButton;

/**
 * Created by Liujilong on 16/2/3.
 * liujilong.me@gmail.com
 */
public class AtyRegister extends BaseActivity {
    private static final String TAG = "AtyRegister";

    Pattern phone = Pattern.compile(StrUtils.PHONE_PATTERN);

    EditText etName;
    EditText etPass;
    EditText etPass2;
    EditText etCode;
    Button btnCode;
    TextView tvContract;
    TextView tvRegister;
    TextView tvError;

    CountDownButton mCountDown;
    ProgressDialog progressDialog;


    TextWatcher mTextWatcher;
    View.OnClickListener mListener;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_register);

        etName = (EditText) findViewById(R.id.phone);
        etPass = (EditText) findViewById(R.id.login_password);
        etPass2 = (EditText) findViewById(R.id.login_copy_password);
        tvContract = (TextView) findViewById(R.id.aty_register_contract);
        tvRegister = (TextView) findViewById(R.id.register);
        tvError = (TextView) findViewById(R.id.aty_register_error);
        etCode = (EditText) findViewById(R.id.verification_code);
        btnCode = (Button) findViewById(R.id.gain_verification_code);

        mCountDown = new CountDownButton(btnCode,btnCode.getText().toString(),60,1);

        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
                mCountDown.start();
            }
        });

        tvContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AtyRegister.this, AtyContract.class);
                startActivity(i);
            }
        });

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                checkText();
            }
        };

        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        };

        etName.addTextChangedListener(mTextWatcher);
        etPass.addTextChangedListener(mTextWatcher);
        etPass2.addTextChangedListener(mTextWatcher);
        etCode.addTextChangedListener(mTextWatcher);
        tvRegister.setOnClickListener(mListener);

    }

    private void sendCode(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("phone",etName.getText().toString());
        param.put("type","1");
        OkHttpUtils.post(StrUtils.SEND_CODE,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.d(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(AtyRegister.this,s);
                if(j != null){
                    Toast.makeText(AtyRegister.this,R.string.send_code_complete,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkText(){
        if(!phone.matcher(etName.getText()).matches()){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.please_input_phone);
            btnCode.setEnabled(false);
            return;
        }else{
            btnCode.setEnabled(true);
        }
        if(etCode.getText().length()==0){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.code_length);
            return;
        }
        if(etPass.getText().length()<6){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.password_long_6);
            return;
        }
        if(!etPass.getText().toString().equals(etPass2.getText().toString())){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.password_not_equal);
            return;
        }
        tvRegister.setEnabled(true);
        tvError.setText("");
    }


    private void register(){
        String name = etName.getText().toString();
        String passMD5 = StrUtils.md5(etPass.getText().toString());
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("phone",name);
        param.put("password", passMD5);
        param.put("code",etCode.getText().toString());
        progressDialog = ProgressDialog.show(AtyRegister.this,null,"正在注册");
        OkHttpUtils.post(StrUtils.REGISTER_PHONE,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                final JSONObject j = OkHttpUtils.parseJSON(AtyRegister.this, s);
                if(j == null){
                    progressDialog.dismiss();
                    return;
                }
                final String id = j.optString("id");
                final String token = j.optString("token");
                SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER, MODE_PRIVATE);
                sp.edit().putString(StrUtils.SP_USER_ID, id)
                        .putString(StrUtils.SP_USER_TOKEN, token).apply();
                progressDialog.setMessage("注册成功，请编辑个人信息");
                Handler handler = new Handler(getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(AtyRegister.this, AtyEditInfo.class);
                        startActivity(i);
                        finish();
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
