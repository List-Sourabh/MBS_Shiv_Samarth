package list.shivsamarth_mbs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import mbLib.CryptoClass;
import mbLib.DatabaseManagement;
import mbLib.DialogBox;
import mbLib.MBSUtils;
import mbLib.MyThread;

@RequiresApi(api = Build.VERSION_CODES.M)
public class OtherBankTranIFSC extends Activity implements OnClickListener {
    Button btn_submit, btn_confirm, btn_con_back, otherIfscbtn_submit;
    Spinner spi_debit_account, spi_sel_beneficiery, spi_payment_option;
    ImageButton spinner_btn, spinner_btn2, btn_home, spinner_btn3;
    TextView cust_nm, txt_heading, txt_remark, txt_from, txt_to, txt_amount, txt_charges, txtTranId, tvIfsc, txt_trantype;
    EditText txtAccNo, txtAmt, txtRemk, txtBank, txtBranch, txtIfsc, txtBalance;
    DialogBox dbs;
    ProgressBar pb_wait;
    Editor e;
    LinearLayout confirm_layout, other_bnk_layout;
    private String benInfo = "";
    private static final String MY_SESSION = "my_session";
    DatabaseManagement dbms;
    String respcode = "", reTval = "", getBeneficiariesrespdesc = "", saveTransferTranrespdesc = "", getTransferChargesrespdesc = "", getBnkBrnrespdesc = "", generateMMIDrespdesc = "";
    int frmno = 0, tono = 0, flag = 0, cnt = 0;
    String stringValue, str = "", retMess = "", cust_name = "", custId = "", str2 = "", ifsCD = "", benSrno = null, tranPin = "";
    String mobPin = "", acnt_inf, all_acnts, bnCD, brCD, benAccountNumber = "", chrgCrAccNo = "", tranId = "", tranType = "", servChrg = "", cess = "";
    String otherIfsctxtIFSCCode = "", drBrnCD = "", drSchmCD = "", drAcNo = "", strFromAccNo = "", strToAccNo = "", strAmount = "", strRemark = "";
    String req_id = "", postingStatus = "", errorCode = "", mmid = "";
    public String encrptdTranMpin;
    public String encrptdUTranMpin;
    ArrayList<String> arrListTemp = new ArrayList<String>();
    private String userId;
    Accounts acArray[];
    ImageView img_heading, btn_home1;
    PrivateKey var1 = null;
    String var5 = "", var3 = "";
    SecretKeySpec var2 = null;
    public int frgIndex = -1;
    ImageButton btn_back;
    Cursor c1 = null;
    OtherBankTranRTGS otherBankTranRTGS;
    private MyThread t1;
    int timeOutInSecs = 300;

    public OtherBankTranIFSC() {
    }

    @Override
    public void onBackPressed() {
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_bank_tranf_ifsc);

        //dbms = new DatabaseManagement("sbk.mbank", "listMobileBanking");
        dbms = new DatabaseManagement("list.shivsamarth_mbs", "shivsamMBS");

        var1 = (PrivateKey) getIntent().getSerializableExtra("var1");
        var3 = (String) getIntent().getSerializableExtra("var3");

        img_heading = (ImageView) findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.fund_trnsfer2);
        dbs = new DialogBox(this);

        try {
            c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
            if (c1 != null) {
                while (c1.moveToNext()) {
                    //Log.e("SHubham", "accounts123 cursor:- " + c1.getString(0));
                    stringValue = c1.getString(0);

                    //Log.e("SHubham", "accounts123 from sqlite:- " + stringValue);
                    custId = c1.getString(2);
                    // Log.e("custId", "......" + custId);
                    userId = c1.getString(3);
                    // Log.e("userId", "......" + userId);
                }
            }
        } catch (Exception e) {
            Log.e("Shubham", "accounts123 Error while fetching" + e.getMessage());
        } finally {
            if (c1 != null) {
                c1.close();
            }
        }


        spi_debit_account = (Spinner) findViewById(R.id.otherIfsc_spi_debit_account);
        spi_sel_beneficiery = (Spinner) findViewById(R.id.otherIfscspi_sel_beneficiery);
        spi_payment_option = (Spinner) findViewById(R.id.payment_options);

        btn_submit = (Button) findViewById(R.id.otherIfscbtn_submit);
        //    otherIfscbtn_submit = (Button) findViewById(R.id.otherIfscbtn_submit);
        txtAccNo = (EditText) findViewById(R.id.otherIfsctxtAccNo);
        txtAmt = (EditText) findViewById(R.id.otherIfsctxtAmt);
        txtRemk = (EditText) findViewById(R.id.otherIfsctxtRemk);
        pb_wait = (ProgressBar) findViewById(R.id.otherIfscpro_bar);
        txtBank = (EditText) findViewById(R.id.otherIfsctxtBank);
        txtBranch = (EditText) findViewById(R.id.otherIfsctxtBranch);
        txtIfsc = (EditText) findViewById(R.id.otherIfsctxtIFSCCode);
        all_acnts = stringValue;
        btn_submit.setOnClickListener(this);

        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        // btn_con_back = (Button) findViewById(R.id.btn_confirm_back);
        txt_trantype = (TextView) findViewById(R.id.txt_trantype);
        txt_remark = (TextView) findViewById(R.id.txt_remark);
        txt_from = (TextView) findViewById(R.id.txt_from);
        txt_to = (TextView) findViewById(R.id.txt_to);
        txt_amount = (TextView) findViewById(R.id.txt_amount);
        txt_charges = (TextView) findViewById(R.id.txt_charges);
        //txtTranId=(TextView)findViewById(R.id.txt_tranid);
        tvIfsc = (TextView) findViewById(R.id.tv_ifsc);
        btn_confirm.setOnClickListener(this);
        // btn_con_back.setOnClickListener(this);
        // btn_submit.setTypeface(tf_calibri);
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        //btn_back = (ImageButton) findViewById(R.id.btn_back);
        spinner_btn = (ImageButton) findViewById(R.id.spinner_btn);
        spinner_btn2 = (ImageButton) findViewById(R.id.spinner_btn2);
        spinner_btn3 = (ImageButton) findViewById(R.id.spinner_btn3);
        txt_heading = (TextView) findViewById(R.id.txt_heading);
        txt_heading.setText(getString(R.string.tabtitle_other_bank_fund_trans_ifsc));
        //btn_home.setImageResource(R.drawable.ic_home_d);
        //btn_back.setImageResource(R.drawable.backover);
        confirm_layout = (LinearLayout) findViewById(R.id.othr_confirm_layout);
        other_bnk_layout = (LinearLayout) findViewById(R.id.other_bnk_ifsc_layout);
        btn_home.setOnClickListener(this);
        btn_home1 = (ImageView) findViewById(R.id.btn_home1);

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //btn_home.setImageResource(R.mipmap.ic_home_d);
        btn_back.setImageResource(R.mipmap.backover);
        btn_back.setOnClickListener(this);


        spinner_btn.setOnClickListener(this);
        spinner_btn2.setOnClickListener(this);
        txtAmt.setText("");
        txtAccNo.setText("");
        txtIfsc.setText("");
        String[] arrList = {"IMPS"};//{"NEFT","RTGS"};
        ArrayAdapter<String> paymentOption = new ArrayAdapter<>(this, R.layout.spinner_item, arrList);
        //CustomeSpinnerAdapter paymentOption = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, arrList);
        //paymentOption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentOption.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spi_payment_option.setAdapter(paymentOption);

        if (spi_debit_account == null)
            System.out.println("spi_debit_account is null");
        else
            System.out.println("spi_debit_account is not null");

        if (spi_sel_beneficiery == null)
            System.out.println("spi_sel_beneficiery is null");
        else
            System.out.println("spi_sel_beneficiery is not null");

        if (btn_submit == null)
            System.out.println("btn_submit is null");
        else
            System.out.println("btn_submit is not null");

        if (txtAccNo == null)
            System.out.println("txtAccNo is null");
        else
            System.out.println("txtAccNo is not null");

        if (txtAmt == null)
            System.out.println("txtAmt is null");
        else
            System.out.println("txtAmt is not null");

        if (txtRemk == null)
            System.out.println("txtRemk is null");
        else
            System.out.println("txtRemk is not null");

        if (pb_wait == null)
            System.out.println("pb_wait is null");
        else
            System.out.println("pb_wait is not null");

        txtBalance = (EditText) findViewById(R.id.sameBnkTrantxtBal);
        if (txtBalance == null)
            System.out.println("txtBalance is null");
        else
            System.out.println("txtBalance is not null");
        addAccounts(all_acnts);

        flag = chkConnectivity();
        if (flag == 0) {
            new CallWebService_fetch_all_beneficiaries().execute();
        }
        pb_wait.setMax(10);
        pb_wait.setProgress(1);
        pb_wait.setVisibility(ProgressBar.INVISIBLE);

        spi_sel_beneficiery.setOnItemSelectedListener(new OnItemSelectedListener() {
            String otherIfsctxtIFSCCode = "";
            String otherIfsctxtBank = "";
            String otherIfsctxtBranch = "";
            String niknm = "";

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String str = spi_sel_beneficiery.getItemAtPosition(spi_sel_beneficiery.getSelectedItemPosition()).toString();

                System.out.println("ben info :===========>" + benInfo);
                if (str.indexOf("Select Beneficiary") > -1) {
                    txtAccNo.setText("");
                    txtIfsc.setText("");
                    txtAmt.setText("");
                    txtRemk.setText("");
                    //spi_payment_option.setAdapter(null);
                } else if (arg2 != 0) {

                    String[] allStr = benInfo.split("~");

                    for (int i = 1; i < allStr.length; i++) {
                        String[] str1 = allStr[i].split("#");
                        niknm = str1[2] + "(" + str1[1] + ")";
                        System.out.println("==== str :" + str);
                        System.out.println("Beneficiary serial number:=====>" + str1[0]);
                        System.out.println("(" + str1[1] + ")");

                        //if (str.indexOf("(" + str1[1] + ")") > -1) {
                        if (str.equalsIgnoreCase(niknm))//indexOf(str1[2])>-1)
                        {
                            System.out.println("========== inside if ============");
                            benSrno = str1[0];
                            benAccountNumber = str1[3];
                            otherIfsctxtIFSCCode = str1[4];
                            ifsCD = otherIfsctxtIFSCCode;
                            Log.e("OTHERBANK", "ifsCD=====" + ifsCD);

                            flag = chkConnectivity();
                            if (flag == 0) {
                                //new CallWebServiceFetBnkBrn().execute();
                            }
                            /*
                             * else { retMess=
                             * "Internet Connection problem,Please try later.."
                             * ; showAlert(retMess); }//end
                             */
                        }
                    }// end for

                    txtAccNo.setText(benAccountNumber.trim());
                    txtIfsc.setText(ifsCD.trim());
                }
            }// end onItemSelected

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spi_debit_account
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        // ////String
                        // str=spi_debit_account.getItemAtPosition(spi_debit_account.getSelectedItemPosition()).toString();
                        str = spi_debit_account.getSelectedItem().toString();
                        Log.e("arg2= ", "arg2=" + arg2);
                        Log.e("arg2= ", "arg2=" + arg2);
                        Log.e("arg2= ", "arg2=" + arg2);
                        if (arg2 == 0) {
                            txtBalance.setText("");
                        } else if (arg2 != 0) {
                            Log.e("str= ", "str=" + str);
                            Log.e("str= ", "str=" + str);
                            Log.e("str= ", "str=" + str);
                            if (str.equalsIgnoreCase("Select Debit Account")) {
                                txtBalance.setText("");
                            } else {
                                String str = arrListTemp.get(spi_debit_account.getSelectedItemPosition() - 1);

                                //retMess = "Selected Account number" + str;
                                // setAlert();

                                String[] debitAc = str.split("-");
                                System.out.println("account 1:" + debitAc[0]);// 5
                                System.out.println("account 2:" + debitAc[1]);// 101
                                // System.out.println("account 3:"+debitAc[2]);//SB
                                System.out.println("account 4:" + debitAc[3]);// 7

                                drBrnCD = debitAc[0];
                                drSchmCD = debitAc[1];
                                drAcNo = debitAc[3];
                                String mmid = debitAc[8];
                                Log.e("MMID", "MMID  " + mmid);
                                if (mmid.equals("NA")) {
                                    showAlert1(getString(R.string.lbl_mmid_msg));
                                }

                                Accounts selectedDrAccount = acArray[spi_debit_account.getSelectedItemPosition() - 1];
                                String balStr = selectedDrAccount.getBalace();
                                String drOrCr = "";
                                float amt = Float.parseFloat(balStr);
                                if (amt > 0)
                                    drOrCr = " Cr";
                                else if (amt < 0)
                                    drOrCr = " Dr";
                                if (balStr.indexOf(".") == -1)
                                    balStr = balStr + ".00";
                                balStr = balStr + drOrCr;
                                txtBalance.setText(balStr);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }

                });

        spi_debit_account.requestFocus();
        System.out.println("========== 8 ============");
        txtAmt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});

        t1 = new MyThread(timeOutInSecs, OtherBankTranIFSC.this, var1, var3);
        t1.start();
    }

    /*public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbms = new DatabaseManagement("sbk.mbank", "listMobileBanking");
        System.out.println("onCreateView() OtherBankTranIFSC");
        View rootView = inflater.inflate(R.layout.other_bank_tranf_ifsc,
                container, false);
        var1 = act.var1;
        var3 = act.var3;
        img_heading = (ImageView) rootView.findViewById(R.id.img_heading);
        img_heading.setBackgroundResource(R.mipmap.fund_trnsfer2);
        dbs = new DialogBox(act);
        Cursor c1 = dbms.selectFromTable("SHAREDPREFERENCE", "", null);//("select * from ", null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                stringValue = c1.getString(0);
                Log.e("retvalstr", "...." + stringValue);
                custId = c1.getString(2);
                Log.e("custId", "......" + custId);
                userId = c1.getString(3);
                Log.e("userId", "......" + userId);
            }
        }

        spi_debit_account = (Spinner) rootView.findViewById(R.id.otherIfsc_spi_debit_account);
        spi_sel_beneficiery = (Spinner) rootView.findViewById(R.id.otherIfscspi_sel_beneficiery);
        spi_payment_option = (Spinner) rootView.findViewById(R.id.payment_options);

        btn_submit = (Button) rootView.findViewById(R.id.otherIfscbtn_submit);
        txtAccNo = (EditText) rootView.findViewById(R.id.otherIfsctxtAccNo);
        txtAmt = (EditText) rootView.findViewById(R.id.otherIfsctxtAmt);
        txtRemk = (EditText) rootView.findViewById(R.id.otherIfsctxtRemk);
        pb_wait = (ProgressBar) rootView.findViewById(R.id.otherIfscpro_bar);
        txtBank = (EditText) rootView.findViewById(R.id.otherIfsctxtBank);
        txtBranch = (EditText) rootView.findViewById(R.id.otherIfsctxtBranch);
        txtIfsc = (EditText) rootView.findViewById(R.id.otherIfsctxtIFSCCode);
        all_acnts = stringValue;
        btn_submit.setOnClickListener(this);

        btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm);
        btn_con_back = (Button) rootView.findViewById(R.id.btn_confirm_back);
        txt_trantype = (TextView) rootView.findViewById(R.id.txt_trantype);
        txt_remark = (TextView) rootView.findViewById(R.id.txt_remark);
        txt_from = (TextView) rootView.findViewById(R.id.txt_from);
        txt_to = (TextView) rootView.findViewById(R.id.txt_to);
        txt_amount = (TextView) rootView.findViewById(R.id.txt_amount);
        txt_charges = (TextView) rootView.findViewById(R.id.txt_charges);
        //txtTranId=(TextView)rootView.findViewById(R.id.txt_tranid);
        tvIfsc = (TextView) rootView.findViewById(R.id.tv_ifsc);
        btn_confirm.setOnClickListener(this);
        btn_con_back.setOnClickListener(this);
        // btn_submit.setTypeface(tf_calibri);
        btn_home = (ImageButton) rootView.findViewById(R.id.btn_home);
        //btn_back = (ImageButton) rootView.findViewById(R.id.btn_back);
        spinner_btn = (ImageButton) rootView.findViewById(R.id.spinner_btn);
        spinner_btn2 = (ImageButton) rootView.findViewById(R.id.spinner_btn2);
        spinner_btn3 = (ImageButton) rootView.findViewById(R.id.spinner_btn3);
        txt_heading = (TextView) rootView.findViewById(R.id.txt_heading);
        txt_heading.setText(getString(R.string.tabtitle_other_bank_fund_trans_ifsc));
        //btn_home.setImageResource(R.drawable.ic_home_d);
        //btn_back.setImageResource(R.drawable.backover);
        confirm_layout = (LinearLayout) rootView.findViewById(R.id.othr_confirm_layout);
        other_bnk_layout = (LinearLayout) rootView.findViewById(R.id.other_bnk_ifsc_layout);
        btn_home.setOnClickListener(this);
        btn_home1 = (ImageView) rootView.findViewById(R.id.btn_home1);

        btn_home1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(act, DashboardActivity.class);
                in.putExtra("var1", var1);
                in.putExtra("var3", var3);
                startActivity(in);
                act.finish();
            }
        });
        //btn_back.setOnClickListener(this);
        spinner_btn.setOnClickListener(this);
        spinner_btn2.setOnClickListener(this);
        txtAmt.setText("");
        txtAccNo.setText("");
        txtIfsc.setText("");
        String[] arrList = {"IMPS"};//{"NEFT","RTGS"};
        ArrayAdapter<String> paymentOption = new ArrayAdapter<String>(act, R.layout.spinner_item, arrList);
        //CustomeSpinnerAdapter paymentOption = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, arrList);
        //paymentOption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentOption.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spi_payment_option.setAdapter(paymentOption);

        if (spi_debit_account == null)
            System.out.println("spi_debit_account is null");
        else
            System.out.println("spi_debit_account is not null");

        if (spi_sel_beneficiery == null)
            System.out.println("spi_sel_beneficiery is null");
        else
            System.out.println("spi_sel_beneficiery is not null");

        if (btn_submit == null)
            System.out.println("btn_submit is null");
        else
            System.out.println("btn_submit is not null");

        if (txtAccNo == null)
            System.out.println("txtAccNo is null");
        else
            System.out.println("txtAccNo is not null");

        if (txtAmt == null)
            System.out.println("txtAmt is null");
        else
            System.out.println("txtAmt is not null");

        if (txtRemk == null)
            System.out.println("txtRemk is null");
        else
            System.out.println("txtRemk is not null");

        if (pb_wait == null)
            System.out.println("pb_wait is null");
        else
            System.out.println("pb_wait is not null");

        txtBalance = (EditText) rootView.findViewById(R.id.sameBnkTrantxtBal);
        if (txtBalance == null)
            System.out.println("txtBalance is null");
        else
            System.out.println("txtBalance is not null");
        addAccounts(all_acnts);

        flag = chkConnectivity();
        if (flag == 0) {
            new CallWebService_fetch_all_beneficiaries().execute();
        }

        pb_wait.setMax(10);
        pb_wait.setProgress(1);
        pb_wait.setVisibility(ProgressBar.INVISIBLE);

        spi_sel_beneficiery
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    String otherIfsctxtIFSCCode = "";
                    String otherIfsctxtBank = "";
                    String otherIfsctxtBranch = "";
                    String niknm = "";

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        String str = spi_sel_beneficiery.getItemAtPosition(
                                spi_sel_beneficiery.getSelectedItemPosition())
                                .toString();

                        System.out.println("ben info :===========>" + benInfo);
                        if (str.indexOf("Select Beneficiary") > -1) {
                            txtAccNo.setText("");
                            txtIfsc.setText("");
                            txtAmt.setText("");
                            txtRemk.setText("");
                            //spi_payment_option.setAdapter(null);
                        } else if (arg2 != 0) {

                            String allStr[] = benInfo.split("~");

                            for (int i = 1; i < allStr.length; i++) {
                                String str1[] = allStr[i].split("#");
                                niknm = str1[2] + "(" + str1[1] + ")";
                                System.out.println("==== str :" + str);
                                System.out
                                        .println("Beneficiary serial number:=====>"
                                                + str1[0]);
                                System.out.println("(" + str1[1] + ")");

                                //if (str.indexOf("(" + str1[1] + ")") > -1) {
                                if (str.equalsIgnoreCase(niknm))//indexOf(str1[2])>-1)
                                {
                                    System.out
                                            .println("========== inside if ============");
                                    benSrno = str1[0];
                                    benAccountNumber = str1[3];
                                    otherIfsctxtIFSCCode = str1[4];
                                    ifsCD = otherIfsctxtIFSCCode;
                                    Log.e("OTHERBANK", "ifsCD=====" + ifsCD);

                                    flag = chkConnectivity();
                                    if (flag == 0) {
                                        //new CallWebServiceFetBnkBrn().execute();
                                    }
//                                     * else { retMess=
//                                     * "Internet Connection problem,Please try later.."
//                                     * ; showAlert(retMess); }//end
                                }
                            }// end for

                            txtAccNo.setText(benAccountNumber.trim());
                            txtIfsc.setText(ifsCD.trim());
                        }
                    }// end onItemSelected

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

        spi_debit_account
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // ////String
                        // str=spi_debit_account.getItemAtPosition(spi_debit_account.getSelectedItemPosition()).toString();
                        str = spi_debit_account.getSelectedItem().toString();
                        Log.e("arg2= ", "arg2=" + arg2);
                        Log.e("arg2= ", "arg2=" + arg2);
                        Log.e("arg2= ", "arg2=" + arg2);
                        if (arg2 == 0) {
                            txtBalance.setText("");
                        } else if (arg2 != 0) {
                            Log.e("str= ", "str=" + str);
                            Log.e("str= ", "str=" + str);
                            Log.e("str= ", "str=" + str);
                            if (str.equalsIgnoreCase("Select Debit Account")) {
                                txtBalance.setText("");
                            } else {
                                String str = arrListTemp.get(spi_debit_account.getSelectedItemPosition() - 1);

                                //retMess = "Selected Account number" + str;
                                // setAlert();

                                String debitAc[] = str.split("-");
                                System.out.println("account 1:" + debitAc[0]);// 5
                                System.out.println("account 2:" + debitAc[1]);// 101
                                // System.out.println("account 3:"+debitAc[2]);//SB
                                System.out.println("account 4:" + debitAc[3]);// 7

                                drBrnCD = debitAc[0];
                                drSchmCD = debitAc[1];
                                drAcNo = debitAc[3];
                                String mmid = debitAc[8];
                                Log.e("MMID", "MMID  " + mmid);
                                if (mmid.equals("NA")) {
                                    showAlert1(getString(R.string.lbl_mmid_msg));
                                }

                                Accounts selectedDrAccount = acArray[spi_debit_account.getSelectedItemPosition() - 1];
                                String balStr = selectedDrAccount.getBalace();
                                String drOrCr = "";
                                float amt = Float.parseFloat(balStr);
                                if (amt > 0)
                                    drOrCr = " Cr";
                                else if (amt < 0)
                                    drOrCr = " Dr";
                                if (balStr.indexOf(".") == -1)
                                    balStr = balStr + ".00";
                                balStr = balStr + drOrCr;
                                txtBalance.setText(balStr);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }

                });

        spi_debit_account.requestFocus();
        System.out.println("========== 8 ============");
        txtAmt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        return rootView;
    }*/

    public void addAccounts(String str) {
        //System.out.println("OtherBankTranIFSC IN addAccounts()" + str);

        try {
            ArrayList<String> arrList = new ArrayList<String>();
            String allstr[] = str.split("~");

            int noOfAccounts = allstr.length;
            arrList.add("Select Debit Account");
            acArray = new Accounts[noOfAccounts];
            int j = 0;
            for (int i = 0; i < noOfAccounts; i++) {
                str2 = allstr[i];
                String tempStr = str2;
                acArray[i] = new Accounts(str2);
                str2 = str2.replaceAll("#", "-");
                String accType = str2.split("-")[2];
                String oprcd = str2.split("-")[7];
                String str2Temp = str2;
                str2 = MBSUtils.get16digitsAccNo(str2);
                String withdrawalAllowed = allstr[i].split("#")[10];
                if (((accType.equals("SB")) || (accType.equals("CA"))
                        || (accType.equals("LO"))) && oprcd.equalsIgnoreCase("O") && withdrawalAllowed.equalsIgnoreCase("Y")) {
                    acArray[j++] = new Accounts(tempStr);
                    arrList.add(str2 + " (" + MBSUtils.getAccTypeDesc(accType) + ")");
                    arrListTemp.add(str2Temp);
                }
            }

            String[] debAccArr = new String[arrList.size()];

            debAccArr = arrList.toArray(debAccArr);
            //CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,R.layout.spinner_layout, debAccArr);
            //CustomeSpinnerAdapter debAccs = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, debAccArr);
            ArrayAdapter<String> debAccs = new ArrayAdapter<String>(OtherBankTranIFSC.this, R.layout.spinner_item, debAccArr);
            //debAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            debAccs.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spi_debit_account.setAdapter(debAccs);
            acnt_inf = spi_debit_account.getItemAtPosition(
                    spi_debit_account.getSelectedItemPosition()).toString();
            Log.i("OtherBankTranIFSC", acnt_inf);
        } catch (Exception e) {
            System.out.println("" + e);
        }

    }// end addAccount

    private void addBeneficiaries(String retval) {
        System.out
                .println("================ IN addBeneficiaries() of OtherBankTranIFSC ======================");
        System.out.println("OtherBankTranIFSC IN addBeneficiaries()" + retval);
        Log.e("OtherBankTranIMPS======", "addBeneficiaries======" + retval);
        try {
            ArrayList<String> arrList = new ArrayList<String>();
            String allstr[] = retval.split("~");

            int noOfben = allstr.length;
            Log.e("OTHERBNK", "noOfben==" + noOfben);
            Log.e("OTHERBNK", "noOfben==" + noOfben);
            Log.e("OTHERBNK", "noOfben==" + noOfben);
            String benName = "";
            arrList.add("Select Beneficiary");

            for (int i = 1; i < noOfben; i++) {
                System.out.println(i + "----STR1-----------" + allstr[i - 1]);
                String[] str2 = allstr[i].split("#");
                if (!(str2[4].equals("NA") || (str2[3].equals("-9999")))) {
                    benName = str2[2] + "(" + str2[1] + ")";
                    arrList.add(benName);
                    System.out.println("=============== benificiary Name is:======" + benName);
                }
            }

            String[] benfArr = new String[arrList.size()];
            benfArr = arrList.toArray(benfArr);
            //CustomeSpinnerAdapter accs = new CustomeSpinnerAdapter(act,R.layout.spinner_layout, benfArr);
            ArrayAdapter<String> benfAccs = new ArrayAdapter<String>(OtherBankTranIFSC.this, R.layout.spinner_item, benfArr);
            //CustomeSpinnerAdapter benfAccs = new CustomeSpinnerAdapter(act,android.R.layout.simple_spinner_item, benfArr);
            //benfAccs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            benfAccs.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spi_sel_beneficiery.setAdapter(benfAccs);

        } catch (Exception e) {
            System.out.println("" + e);
        }
    }// end addBeneficiaries

    public int chkConnectivity() {
        // pb_wait.setVisibility(ProgressBar.VISIBLE);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        try {
            State state = ni.getState();
            boolean state1 = ni.isAvailable();
            System.out
                    .println("OtherBankTranIFSC	in chkConnectivity () state1 ---------"
                            + state1);
            if (state1) {
                switch (state) {
                    case CONNECTED:
                        if (ni.getType() == ConnectivityManager.TYPE_MOBILE
                                || ni.getType() == ConnectivityManager.TYPE_WIFI) {
                        }
                        break;
                    case DISCONNECTED:
                        flag = 1;
                        // retMess =
                        // "Network Disconnected. Please Check Network Settings.";
                        retMess = getString(R.string.alert_014);
                        showAlert(retMess);
                        /*
                         * dbs = new DialogBox(this);
                         * dbs.get_adb().setMessage(retMess);
                         * dbs.get_adb().setPositiveButton("Ok", new
                         * DialogInterface.OnClickListener() { public void
                         * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
                         * } }); dbs.get_adb().show();
                         */
                        break;
                    default:
                        flag = 1;
                        // retMess = "Network Unavailable. Please Try Again.";
                        retMess = getString(R.string.alert_000);
                        showAlert(retMess);

                        /*
                         * dbs = new DialogBox(this);
                         * dbs.get_adb().setMessage(retMess);
                         * dbs.get_adb().setPositiveButton("Ok", new
                         * DialogInterface.OnClickListener() { public void
                         * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
                         * Intent in = null; in = new
                         * Intent(getApplicationContext(), LoginActivity.class);
                         * startActivity(in); finish(); } }); dbs.get_adb().show();
                         */
                        break;
                }
            } else {
                flag = 1;
                retMess = getString(R.string.alert_000);
                showAlert(retMess);

                /*
                 * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
                 * dbs.get_adb().setPositiveButton("Ok", new
                 * DialogInterface.OnClickListener() { public void
                 * onClick(DialogInterface arg0, int arg1) { arg0.cancel();
                 * Intent in = null; in = new Intent(getApplicationContext(),
                 * LoginActivity.class); startActivity(in); finish(); } });
                 * dbs.get_adb().show();
                 */
            }
        } catch (NullPointerException ne) {

            Log.i("OtherBankTranIFSC",
                    "NullPointerException Exception" + ne);
            flag = 1;
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);

            /*
             * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
             * dbs.get_adb().setPositiveButton("Ok", new
             * DialogInterface.OnClickListener() { public void
             * onClick(DialogInterface arg0, int arg1) { arg0.cancel(); Intent
             * in = null; in = new Intent(getApplicationContext(),
             * LoginActivity.class); startActivity(in); finish(); } });
             * dbs.get_adb().show();
             */

        } catch (Exception e) {
            Log.i("OtherBankTranIFSC", "Exception" + e);
            flag = 1;
            // retMess = "Network Unavailable. Please Try Again.";
            retMess = getString(R.string.alert_000);
            showAlert(retMess);

            /*
             * dbs = new DialogBox(this); dbs.get_adb().setMessage(retMess);
             * dbs.get_adb().setPositiveButton("Ok", new
             * DialogInterface.OnClickListener() { public void
             * onClick(DialogInterface arg0, int arg1) { arg0.cancel(); Intent
             * in = null; in = new Intent(getApplicationContext(),
             * LoginActivity.class); startActivity(in); finish(); } });
             * dbs.get_adb().show();
             */
        }
        return flag;
    }// end chkConnectivity


    public void post_successfetch_all_beneficiaries(String reTval) {
        respcode = "";
        getBeneficiariesrespdesc = "";
        System.out.println("decryptedBeneficiaries:" + reTval);
        Log.e("Get Beneficiary", "onPostExecute" + benInfo);
        benInfo = reTval;
        addBeneficiaries(benInfo);
    }

    class CallWebServiceSaveTransfer extends AsyncTask<Void, Void, Void> {
        String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(OtherBankTranIFSC.this);

        JSONObject jsonObj = new JSONObject();

        String accNo, debitAccno, benAcNo, amtStr, reMark;


        protected void onPreExecute() {
            try {
                respcode = "";
                reTval = "";
                saveTransferTranrespdesc = "";
                loadProBarObj.show();
                String location = MBSUtils.getLocation(OtherBankTranIFSC.this);
                accNo = txtAccNo.getText().toString().trim();
                debitAccno = spi_debit_account.getItemAtPosition(spi_debit_account.getSelectedItemPosition()).toString();
                benAcNo = spi_sel_beneficiery.getItemAtPosition(spi_sel_beneficiery.getSelectedItemPosition()).toString();
                amtStr = txtAmt.getText().toString().trim();
                reMark = txt_remark.getText().toString().trim();
                tranType = spi_payment_option.getItemAtPosition(spi_payment_option.getSelectedItemPosition()).toString();
                String crAccNo = txt_to.getText().toString().trim();
                String charges = txt_charges.getText().toString().split(" ")[1];
                String drAccNo = strFromAccNo.trim();
                drAccNo = drAccNo.substring(0, 16);

                if (tranType.equalsIgnoreCase("RTGS"))
                    tranType = "RT";
                else if (tranType.equalsIgnoreCase("NEFT"))
                    tranType = "NT";
                else if (tranType.equalsIgnoreCase("IMPS"))
                    tranType = "P2A";

                jsonObj.put("BENFSRNO", benSrno);
                jsonObj.put("CRACCNO", crAccNo);
                jsonObj.put("DRACCNO", drAccNo);
                int pos = amtStr.indexOf(".");
                jsonObj.put("AMOUNT", pos > -1 ? amtStr.substring(0, (amtStr.length() - pos) <= 2 ? amtStr.length() : pos + 2) : amtStr);
                jsonObj.put("REMARK", reMark);
                jsonObj.put("TRANSFERTYPE", tranType);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OtherBankTranIFSC.this));
                jsonObj.put("CUSTID", custId);
                jsonObj.put("CHARGES", charges);
                jsonObj.put("CHRGACCNO", chrgCrAccNo);
                jsonObj.put("TRANID", tranId);
                jsonObj.put("SERVCHRG", servChrg);
                jsonObj.put("CESS", cess);
                jsonObj.put("TRANPIN", encrptdTranMpin);
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(OtherBankTranIFSC.this));
                jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(OtherBankTranIFSC.this));
                jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
                jsonObj.put("LATITUDE", location.split("~")[0]);
                jsonObj.put("LONGITUDE", location.split("~")[1]);
                jsonObj.put("METHODCODE", "16");

            } catch (JSONException je) {
                je.printStackTrace();
            }


        }

        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";

            try {
                String keyStr = CryptoClass.Function2();
                var2 = CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);
                request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }// end dodoInBackground2

        protected void onPostExecute(Void paramVoid) {


            loadProBarObj.dismiss();

            try {
                String str = CryptoClass.Function6(var5, var2);
                JSONObject jsonObj;
                jsonObj = new JSONObject(str.trim());
                if (!(str.trim().indexOf("FAILED#") > -1)) {


                    try {


                        if (jsonObj.has("RESPCODE")) {
                            respcode = jsonObj.getString("RESPCODE");
                        } else {
                            respcode = "-1";
                        }
                        if (jsonObj.has("RETVAL")) {
                            reTval = jsonObj.getString("RETVAL");
                        } else {
                            reTval = "";
                        }
                        if (jsonObj.has("RESPDESC")) {
                            saveTransferTranrespdesc = jsonObj.getString("RESPDESC");
                        } else {
                            saveTransferTranrespdesc = "";
                        }


                        if (saveTransferTranrespdesc.length() > 0 && saveTransferTranrespdesc.indexOf("Success") == -1) {
                            showAlertPost(saveTransferTranrespdesc);
                        } else {
                            if (reTval.indexOf("SUCCESS") > -1) {
                                Log.e("onPostExecute==", "SUCCESS==" + reTval);
                                post_successsaveTransferTran(reTval);
                            } else if (reTval.indexOf("DUPLICATE") > -1) {
                                retMess = getString(R.string.alert_119) + tranId + "\n" + getString(R.string.alert_120);
                                showAlertPost(retMess);
                            } else if (reTval.indexOf("BENFACCINVALID") > -1) {
                                retMess = getString(R.string.alert_benfivalid);
                                showAlertPost(retMess);
                            } else if (reTval.indexOf("WRONGTRANPIN") > -1) {
                                String msg[] = reTval.split("~");
                                String first = msg[1];
                                String second = msg[2];
                                Log.e("first", "-------" + first);
                                Log.e("second", "-------" + second);

                                int count = Integer.parseInt(second);
                                count = 5 - count;
                                retMess = getString(R.string.alert_125_1) + " " + count + " " + getString(R.string.alert_125_2);
                                showAlert(retMess);
                            } else if (reTval.indexOf("BLOCKEDFORDAY") > -1) {
                                retMess = getString(R.string.login_alert_005);
                                showAlertPost(retMess);
                            } else if (reTval.indexOf("FAILED~") > -1) {
                                String msg[] = reTval.split("~");
                                if (msg.length > 3) {
                                    postingStatus = msg[1];
                                    req_id = msg[2];
                                    String errorMsg = msg[3];
                                    if (req_id.length() > 0) {
                                        if (req_id != null || req_id.length() > 0)
                                            retMess = getString(R.string.alert_162) + " " + req_id;
                                    } else if (errorMsg.length() > 0) {
                                        retMess = getString(R.string.alert_032) + errorMsg;
                                    }
                                } else {
                                    retMess = getString(R.string.alert_032);
                                }
                                showAlertPost(retMess);
                            } else if (reTval.indexOf("FAILED") > -1) {
                                if (reTval.split("~")[1] != "null" || reTval.split("~")[1] != "") {
                                    errorCode = reTval.split("~")[1];
                                } else {
                                    errorCode = "NA";
                                }

                                if (errorCode.equalsIgnoreCase("999")) {
                                    retMess = getString(R.string.alert_179);
                                } else if (errorCode.equalsIgnoreCase("001")) {
                                    retMess = getString(R.string.alert_180);
                                } else if (errorCode.equalsIgnoreCase("002")) {
                                    retMess = getString(R.string.alert_181);
                                } else if (errorCode.equalsIgnoreCase("003")) {
                                    retMess = getString(R.string.alert_182);
                                } else if (errorCode.equalsIgnoreCase("004")) {
                                    retMess = getString(R.string.alert_179);
                                } else if (errorCode.equalsIgnoreCase("005")) {
                                    retMess = getString(R.string.alert_183);
                                } else if (errorCode.equalsIgnoreCase("006")) {
                                    retMess = getString(R.string.alert_184);
                                } else if (errorCode.equalsIgnoreCase("007")) {
                                    retMess = getString(R.string.alert_179);
                                } else if (errorCode.equalsIgnoreCase("008")) {
                                    retMess = getString(R.string.alert_176);
                                } else
                                    retMess = getString(R.string.trnsfr_alert_001);

                                showAlertPost(retMess);
                            }
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    retMess = getString(R.string.alert_imps);
                    showAlertPost(retMess);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }// end onPostExecute
    }// end CallWebServiceSaveTransfer

    public void post_successsaveTransferTran(String reTval) {
        respcode = "";
        saveTransferTranrespdesc = "";
        String msg[] = reTval.split("~");
        if (msg.length > 2) {
            if (msg[2] != null || msg[2].length() > 0) {
                postingStatus = msg[0];
                req_id = msg[1];
                retMess = getString(R.string.alert_030) + " " + getString(R.string.alert_121) + " " + req_id;
            }
        } else {
            retMess = getString(R.string.alert_030);
        }

        showAlertPost(retMess);
    }

    class CallWebServiceFetBnkBrn extends AsyncTask<Void, Void, Void> {
        String retVal = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(OtherBankTranIFSC.this);

        JSONObject jsonObj = new JSONObject();


        protected void onPreExecute() {
            try {
                respcode = "";
                reTval = "";
                getBnkBrnrespdesc = "";
                loadProBarObj.show();

                jsonObj.put("IFSC", ifsCD);
                jsonObj.put("CUSTID", custId);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OtherBankTranIFSC.this));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(OtherBankTranIFSC.this));

                jsonObj.put("METHODCODE", "17");
            } catch (JSONException je) {
                je.printStackTrace();
            }


        }

        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";

            try {
                String keyStr = CryptoClass.Function2();
                var2 = CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);
                request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }// end dodoInBackground2

        protected void onPostExecute(Void paramVoid) {
            txtBank.setText(bnCD);
            txtBranch.setText(brCD);
            String str = CryptoClass.Function6(var5, var2);
            String decryptedBeneficiaries = str.trim();
            decryptedBeneficiaries = "SUCCESS";
            loadProBarObj.dismiss();
            if (decryptedBeneficiaries.indexOf("SUCCESS") > -1) {
				/*String allStr[] = decryptedBeneficiaries.split("~");
				String bankBranch[] = allStr[1].split("#");
				 
				bnCD =bankBranch[0];
				brCD = bankBranch[1];
				txtBank.setText(bnCD);
				txtBranch.setText(brCD);*/
                txtBank.setText("BOI");
                txtBranch.setText("Sangli");

            } else {
                retMess = getString(R.string.alert_069);
                //loadProBarObj.dismiss();
                showAlert(retMess);
            }
        }// end doPost
    }// end CallWebServiceFetBnkBrn


    public void post_successGetSrvcCharg(String reTval) {
        respcode = "";
        getTransferChargesrespdesc = "";
        //loadProBarObj.dismiss();
        other_bnk_layout.setVisibility(other_bnk_layout.INVISIBLE);
        confirm_layout.setVisibility(confirm_layout.VISIBLE);

        String retStr = reTval.split("~")[1];
        String trantype = "P2A";
        Log.e("HELL", retStr);
        String[] val = retStr.split("#");
        txt_heading.setText("Confirmation");
        txt_trantype.setText(trantype);
        txt_remark.setText(strRemark);
        txt_from.setText(strFromAccNo);
        tvIfsc.setText(txtIfsc.getText().toString().trim());
        txt_to.setText(strToAccNo);
        txt_amount.setText("INR " + strAmount);
        txt_charges.setText("INR " + val[0]);

        chrgCrAccNo = val[1];
        tranId = val[2];
        servChrg = val[3];
        cess = val[4];
        Log.e("OTHERBNKTRAN", "servChrg===" + servChrg + "==cess==" + cess);
        if (chrgCrAccNo.length() == 0 || chrgCrAccNo.equalsIgnoreCase("null"))
            chrgCrAccNo = "";

        if (servChrg.equalsIgnoreCase("null"))
            servChrg = "0";

        if (cess.equalsIgnoreCase("null"))
            cess = "0";

        Log.e("OTHERBNKTRAN", "2222servChrg===" + servChrg + "==cess==" + cess);
        txt_charges.setText("INR " + (Float.parseFloat(val[0]) + Float.parseFloat(servChrg) + Float.parseFloat(cess)));

    }

    @Override
    public void onClick(View v) {
        Intent in;
        switch (v.getId()) {
            case R.id.btn_home:
                in = new Intent(OtherBankTranIFSC.this, DashboardActivity.class);
                in.putExtra("var1", var1);
                in.putExtra("var3", var3);
                startActivity(in);
                finish();
                break;

            case R.id.btn_back:
                in = new Intent(OtherBankTranIFSC.this, FundTransferMenuActivity.class);
                in.putExtra("var1", var1);
                in.putExtra("var3", var3);
                startActivity(in);
                finish();
                break;

            case R.id.spinner_btn:
                spi_debit_account.performClick();
                break;

            case R.id.spinner_btn2:
                spi_sel_beneficiery.performClick();
                break;
            case R.id.spinner_btn3:
                spi_payment_option.performClick();
                break;

            case R.id.otherIfscbtn_submit:
                strFromAccNo = spi_debit_account.getSelectedItem().toString();
                Log.e("Shubham", "strFromAccNo: " + strFromAccNo);
                if (strFromAccNo == null) {
                    strFromAccNo = "Select Debit Account";
                }
                strToAccNo = txtAccNo.getText().toString().trim();
                strAmount = txtAmt.getText().toString().trim();
                strRemark = txtRemk.getText().toString().trim();
                //String transferType = spi_payment_option.getSelectedItem().toString();
                double balance = 0.0;

                String balString = txtBalance.getText().toString().trim();
                if (balString.length() > 0) {
                    balString = balString.substring(0, balString.length() - 2);
                    Log.e("balance=", "balString==" + balString);
                    Log.e("balance=", "balString==" + balString);
                    balance = Double.parseDouble(balString);
                    balance = Math.abs(balance);
                    Log.e("balance=", "balance==" + balance);
                    Log.e("balance=", "balance==" + balance);
                }
                Log.e("strAmount=", "strAmount==" + strAmount);
                Log.e("strAmount=", "strAmount==" + strAmount);
                if (strFromAccNo.equalsIgnoreCase("Select Debit Account")) {
                    showAlert(getString(R.string.alert_174));
                } else if (strToAccNo.length() == 0) {
                    showAlert(getString(R.string.alert_098));
                }
			/*else if(!MBSUtils.isNumeric(strToAccNo))
			{
				showAlert(getString(R.string.alert_187));
			}*/
                else if (strToAccNo.equalsIgnoreCase(strFromAccNo)) {
                    showAlert(getString(R.string.alert_107));
                } else if (strAmount.matches("")) {
                    showAlert(getString(R.string.alert_033));
                } else if (strAmount.length() == 1 && strAmount.equalsIgnoreCase(".")) {
                    showAlert(getString(R.string.alert_195));
                } else if (Double.parseDouble(strAmount) == 0) {
                    showAlert(getString(R.string.alert_034));
                } else if (strRemark.length() == 0) {
                    showAlert(getString(R.string.alert_165));
                } else if (Double.parseDouble(strAmount) > balance) {
                    showAlert(getString(R.string.alert_176));
                }
			/*else if(transferType.equalsIgnoreCase("NEFT") && Double.parseDouble(strAmount)>=200000)
			{
				showAlert(getString(R.string.alert_147));
			}
			else if(transferType.equalsIgnoreCase("RTGS") && Double.parseDouble(strAmount)<200000)
			{
				showAlert(getString(R.string.alert_148));
			}*/
                else {
                    try {
                        this.flag = chkConnectivity();
                        // Log.e("chkConnectivity", "chkConnectivity" + flag);
                        if (this.flag == 0) {
                            new CallWebServiceGetSrvcCharg().execute();
                        }
                    } catch (Exception e) {
                        Log.e("Shubham", "Exception in CallWebServiceGetSrvcCharg" + e.getMessage());
                    }
                }
                break;

            case R.id.btn_confirm:
                String str = txtAmt.getText().toString().trim();
                String str2 = txtRemk.getText().toString().trim();
                String accNo = txtAccNo.getText().toString().trim();
                String bnk = txtBank.getText().toString().trim();
                String brnch = txtBranch.getText().toString().trim();
                String ifscCd = txtIfsc.getText().toString().trim();
                if (accNo.length() == 0 || ifscCd.length() == 0) {
                    if (accNo.length() == 0)
                        retMess = "Account number " + getString(R.string.alert_068);
                    if (ifscCd.length() == 0)
                        retMess = "IFSC code " + getString(R.string.alert_068);
                    if (retMess != null && retMess.length() > 0)
                        showAlert(retMess);
                } else if (str.length() == 0) {

                    System.out.println("Cuttent thread name:==>"
                            + Thread.currentThread().getName());
                    System.out.println("--------------- 22 ------------");
                    retMess = getString(R.string.alert_033);
                    System.out.println("--------------- 22.1 ------------");
                    showAlert(retMess);
                    System.out.println("--------------- 22.2 ------------");
                    txtAmt.requestFocus();
                    System.out.println("--------------- 22.3 ------------");
                } else {
                    //int amt = Integer.parseInt(str); Gives error for fraction so
                    double amt = Double.parseDouble(str);
                    if (amt <= 0) {
                        System.out.println("--------------- 44 ------------");
                        retMess = getString(R.string.alert_034);
                        showAlert(retMess);
                        txtAmt.requestFocus();
                    } else {
                        {
                            InputDialogBox inputBox = new InputDialogBox(OtherBankTranIFSC.this);
                            inputBox.show();
                        } // end else
                    }
                }// end if
                break;
           /* case R.id.btn_confirm_back:
                in = new Intent(OtherBankTranIFSC.this, OtherBankTranIFSC.class);
                in.putExtra("var1", var1);
                in.putExtra("var3", var3);
                startActivity(in);
                finish();
                break;*/
            default:
                break;
        }

    }// end onClick

    public void showAlert(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(OtherBankTranIFSC.this, "" + str) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                if ((str.equalsIgnoreCase(getBeneficiariesrespdesc)) && (respcode.equalsIgnoreCase("0"))) {
                    post_successfetch_all_beneficiaries(reTval);
                } else if ((str.equalsIgnoreCase(getBeneficiariesrespdesc)) && (respcode.equalsIgnoreCase("1"))) {
                    this.dismiss();
                }
                if ((str.equalsIgnoreCase(saveTransferTranrespdesc)) && (respcode.equalsIgnoreCase("0"))) {
                    post_successsaveTransferTran(reTval);
                } else if ((str.equalsIgnoreCase(saveTransferTranrespdesc)) && (respcode.equalsIgnoreCase("1"))) {
					/*this.dismiss();
					FragmentManager fragmentManager;
					Fragment fragment = new FundTransferMenuActivity(act);
					act.setTitle(getString(R.string.lbl_same_bnk_trans));
					fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
                    Intent in = new Intent(OtherBankTranIFSC.this, FundTransferMenuActivity.class);
                    startActivity(in);
                }
                if ((str.equalsIgnoreCase(getTransferChargesrespdesc)) && (respcode.equalsIgnoreCase("0"))) {
                    post_successGetSrvcCharg(reTval);
                } else if ((str.equalsIgnoreCase(getTransferChargesrespdesc)) && (respcode.equalsIgnoreCase("1"))) {
                    this.dismiss();
                }
                if ((str.equalsIgnoreCase(generateMMIDrespdesc)) && (respcode.equalsIgnoreCase("0"))) {
                    post_successGetMMID(reTval);
                } else if ((str.equalsIgnoreCase(generateMMIDrespdesc)) && (respcode.equalsIgnoreCase("1"))) {
                    this.dismiss();
                } else if (this.textMessage.equalsIgnoreCase(OtherBankTranIFSC.this.getString(R.string.alert_125_1))) {
                    InputDialogBox inputBox = new InputDialogBox(OtherBankTranIFSC.this);
                    inputBox.show();
                }
            }
        };
        alert.show();
    }

    public void showAlertPost(final String str) {
        ErrorDialogClass alert = new ErrorDialogClass(OtherBankTranIFSC.this, "" + str) {
            @Override
            public void onClick(View v) {
				/*this.dismiss();
				FragmentManager fragmentManager;
				Fragment fragment = new FundTransferMenuActivity(act);
				act.setTitle(getString(R.string.lbl_same_bnk_trans));
				fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
                Intent in = new Intent(OtherBankTranIFSC.this, FundTransferMenuActivity.class);
                startActivity(in);
            }
        };
        alert.show();
    }

    public class InputDialogBox extends Dialog implements OnClickListener {
        Activity activity;
        String msg, title;
        Context appAcontext;
        EditText mpin;
        Button btnOk;
        String strmpin = "";
        TextView txtLbl;
        boolean flg;

        public InputDialogBox(Activity activity) {
            super(activity);
        }// end InputDialogBox

        protected void onCreate(Bundle bdn) {
            super.onCreate(bdn);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.transfer_dialog);
            mpin = (EditText) findViewById(R.id.txtMpin);
            btnOk = (Button) findViewById(R.id.btnOK);
            mpin.setVisibility(EditText.VISIBLE);
            btnOk.setVisibility(Button.VISIBLE);
            btnOk.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {

                // System.out.println("========= inside onClick ============***********");
                String str = mpin.getText().toString().trim();
                encrptdTranMpin = str;//ListEncryption.encryptData(custId+str);
                encrptdUTranMpin = str;//ListEncryption.encryptData(userId+str);

                if (str.length() == 0) {
                    retMess = getString(R.string.alert_116);
                    showAlert(retMess);//setAlert();
                    this.show();
                }
//				  else if(str.length()!=6)
//				  {
//					retMess=getString(R.string.alert_037);
//					showAlert(retMess);//setAlert();
//				  	this.show();
//				  }
                else {
                    //System.out.println("======== strmpin:=="+str);
                    //System.out.println("======== mobPin:=="+mobPin);
                    //	if(encrptdTranMpin.equals(tranPin)||encrptdUTranMpin.equals(tranPin))
                    {
                        //saveData();
                        callValidateTranpinService validateTran = new callValidateTranpinService();
                        validateTran.execute();

                        this.hide();
                    }
				  /*	else 
				  	{
				  		//System.out.println("=========== inside else ==============");
				  		retMess=getString(R.string.alert_118); 
				  		showAlert(retMess);//setAlert();
				  		this.show(); 
				  	} */
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out
                        .println("Exception in InputDialogBox of onClick:=====>"
                                + e);
            }
        }// end onClick
    }// end InputDialogBox

	/*public void saveData() {
		try {
			System.out.println("--------------- 44 ------------");
			flag = chkConnectivity();
			if (flag == 0) {
				new CallWebServiceSaveTransfer().execute();

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in onClick:==" + e);

		}
	}// end saveData
*/

    public void saveData() {
        JSONObject jsonObj = new JSONObject();
        try {
            String location = MBSUtils.getLocation(OtherBankTranIFSC.this);
            String accNo = txtAccNo.getText().toString().trim();
            String debitAccno = spi_debit_account.getItemAtPosition(spi_debit_account.getSelectedItemPosition()).toString();
            String benAcNo = spi_sel_beneficiery.getItemAtPosition(spi_sel_beneficiery.getSelectedItemPosition()).toString();
            String amtStr = txtAmt.getText().toString().trim();
            String reMark = txt_remark.getText().toString().trim();
            tranType = spi_payment_option.getItemAtPosition(
                    spi_payment_option.getSelectedItemPosition()).toString();
            String crAccNo = txt_to.getText().toString().trim();
            String charges = txt_charges.getText().toString().split(" ")[1];
            String drAccNo = strFromAccNo.toString().trim();
            drAccNo = drAccNo.substring(0, 16);

            if (tranType.equalsIgnoreCase("RTGS"))
                tranType = "RT";
            else if (tranType.equalsIgnoreCase("NEFT"))
                tranType = "NT";
            else if (tranType.equalsIgnoreCase("IMPS"))
                tranType = "P2A";

            jsonObj.put("BENFSRNO", benSrno);
            jsonObj.put("CRACCNO", crAccNo);
            jsonObj.put("DRACCNO", drAccNo);
            int pos = amtStr.indexOf(".");
            jsonObj.put("AMOUNT", pos > -1 ? amtStr.substring(0, (amtStr.length() - pos) <= 2 ? amtStr.length() : pos + 2) : amtStr);
            jsonObj.put("REMARK", reMark);
            jsonObj.put("TRANSFERTYPE", tranType);
            jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OtherBankTranIFSC.this));
            jsonObj.put("CUSTID", custId);
            jsonObj.put("CHARGES", charges);
            jsonObj.put("CHRGACCNO", chrgCrAccNo);
            jsonObj.put("TRANID", tranId);
            jsonObj.put("SERVCHRG", servChrg);
            jsonObj.put("CESS", cess);
            jsonObj.put("TRANPIN", encrptdTranMpin);
            jsonObj.put("SIMNO", MBSUtils.getSimNumber(OtherBankTranIFSC.this));
            jsonObj.put("MOBILENO", MBSUtils.getMyPhoneNO(OtherBankTranIFSC.this));
            jsonObj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
            jsonObj.put("OSVERSION", Build.VERSION.RELEASE);
            jsonObj.put("LATITUDE", location.split("~")[0]);
            jsonObj.put("LONGITUDE", location.split("~")[1]);

        } catch (JSONException je) {
            je.printStackTrace();
        }
        Log.e("OTHERBANKTRAN", "obj.toString()==" + jsonObj.toString());
       /* Bundle bundle = new Bundle();
        bundle.putString("CUSTID", custId);
        bundle.putString("FROMACT", "IMPSBANK");
        bundle.putString("JSONOBJ", jsonObj.toString());
        Fragment fragment = new TransferOTP(act);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
        Intent in = new Intent(OtherBankTranIFSC.this, TransferOTP.class);
        in.putExtra("CUSTID", custId);
        in.putExtra("FROMACT", "IMPSBANK");
        in.putExtra("JSONOBJ", jsonObj.toString());
        in.putExtra("var1", var1);
        in.putExtra("var3", var3);
        startActivity(in);

		/*try {
			//System.out.println("--------------- 44 ------------");
			flag = chkConnectivity();
			if (flag == 0) {
				new CallWebServiceSaveTransfer().execute();

			}
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("Exception in onClick:==" + e);

		}*/
    }// end saveData

    public void showAlert1(String str) {
        ErrorDialogClass1 alert = new ErrorDialogClass1(OtherBankTranIFSC.this, "" + str);
        alert.show();
    }

    public class ErrorDialogClass1 extends Dialog implements OnClickListener {

        private Context activity;
        private Dialog d;
        private Button ok, no;
        private TextView txt_message;
        public String textMessage;

        public ErrorDialogClass1(Context activity, String textMessage) {
            super(activity);
            this.textMessage = textMessage;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setCanceledOnTouchOutside(false);
            setContentView(R.layout.custom_alert_dialog);
            ok = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            txt_message = (TextView) findViewById(R.id.txt_dia);
            txt_message.setText(textMessage);
            ok.setOnClickListener(this);
            no.setOnClickListener(this);
        }//end onCreate

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
					/*FragmentManager fragmentManager;
					Fragment fragment = new GenerateMMID(act);
					act.setTitle(getString(R.string.lbl_mmid));
					fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();*/
                    Log.e("onClick", "btn_yes");
                    Log.e("onClick", "btn_yes");
                    Log.e("onClick", "btn_yes");
                    getMMID();

                    // break;
                case R.id.btn_no:
                    this.dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }//end class

    public void getMMID() {
        Log.e("In getMMID ", "In getMMID");

        try {
            System.out.println("--------------- 44 ------------");
            flag = chkConnectivity();
            if (flag == 0) {
                new CallWebServiceGetMMID().execute();

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in onClick:==" + e);

        }
    }


    public void post_successGetMMID(String reTval) {
        respcode = "";
        generateMMIDrespdesc = "";
        String msg[] = reTval.split("~");
        if (msg[1].equals("NA")) {
            if (msg[2] != null || msg[2].length() > 0) {
                req_id = msg[2];
                Log.e("Ganesh ", " Failed NA req_id=" + req_id);
                Log.e("Ganesh ", "Failed NA req_id=" + req_id);
                retMess = getString(R.string.alert_159) + " " + req_id;
            }
            showAlert2(retMess);
        } else {
            mmid = msg[1];
           /* FragmentManager fragmentManager;
            Fragment fragment = new FundTransferMenuActivity(act);
            Bundle b = new Bundle();
            act.setTitle(getString(R.string.lbl_fund_transfer));
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();*/
            Intent in = new Intent(OtherBankTranIFSC.this, FundTransferMenuActivity.class);
            startActivity(in);
        }
    }

    public void showAlert2(String str) {
        // Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        ErrorDialogClass2 alert = new ErrorDialogClass2(OtherBankTranIFSC.this, "" + str);
        alert.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        t1.sec = timeOutInSecs;
        Log.e("sec11= ", "sec11==" + t1.sec);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        t1.sec = -1;
        System.gc();
    }

    public class ErrorDialogClass2 extends Dialog implements OnClickListener {
        private Context activity;
        private Dialog d;
        private Button ok, no;
        private TextView txt_message;
        public String textMessage;

        public ErrorDialogClass2(Context activity, String textMessage) {
            super(activity);
            this.textMessage = textMessage;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setCanceledOnTouchOutside(false);
            setContentView(R.layout.custom_dialog);
            ok = (Button) findViewById(R.id.btn_ok);
            //no = (Button)findViewById(R.id.btn_no);
            txt_message = (TextView) findViewById(R.id.txt_dia);
            txt_message.setText(textMessage);
            ok.setOnClickListener(this);
            //no.setOnClickListener(this);
        }//end onCreate

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_ok:
				/*	FragmentManager fragmentManager;
					Fragment fragment = new FundTransferMenuActivity(act);
					act.setTitle(getString(R.string.lbl_fund_transfer));
					fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();*/
                    Intent myIntent = new Intent(OtherBankTranIFSC.this, DashboardActivity.class);
                    myIntent.putExtra("var1", var1);
                    myIntent.putExtra("var3", var3);
                    startActivity(myIntent);
                    System.exit(0);
                    break;


                default:
                    break;
            }
            dismiss();
        }
    }//end class

    class callValidateTranpinService extends AsyncTask<Void, Void, Void> {
        String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(OtherBankTranIFSC.this);


        JSONObject obj = new JSONObject();

        protected void onPreExecute() {
            loadProBarObj.show();


            try {
                String location = MBSUtils.getLocation(OtherBankTranIFSC.this);
                obj.put("SIMNO", MBSUtils.getSimNumber(OtherBankTranIFSC.this));
                obj.put("IMEINO", MBSUtils.getImeiNumber(OtherBankTranIFSC.this));
                obj.put("TRANPIN", encrptdTranMpin);
                obj.put("CUSTID", custId);
                obj.put("MOBILENO", MBSUtils.getMyPhoneNO(OtherBankTranIFSC.this));
                obj.put("IPADDRESS", MBSUtils.getLocalIpAddress());
                obj.put("OSVERSION", Build.VERSION.RELEASE);
                obj.put("LATITUDE", location.split("~")[0]);
                obj.put("LONGITUDE", location.split("~")[1]);

                obj.put("METHODCODE", "73");
                Log.e("SAM===", "SIMNO:" + MBSUtils.getSimNumber(OtherBankTranIFSC.this));
                Log.e("SAM===", "IMEINO:" + MBSUtils.getImeiNumber(OtherBankTranIFSC.this));
                Log.e("SAM===", "TRANPIN:" + encrptdTranMpin);
                Log.e("SAM===", "CUSTID:" + custId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";

            try {
                String keyStr = CryptoClass.Function2();
                var2 = CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);
                request.addProperty("value1", CryptoClass.Function5(obj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }// end dodoInBackground2

        protected void onPostExecute(Void paramVoid) {


            JSONObject jsonObj;
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());


                String decryptedAccounts = str.trim();
                Log.e("SAM===", "xml_data[0]=decryptedAccounts:" + decryptedAccounts);
                loadProBarObj.dismiss();
			/*if (retJson.has("VALIDATIONDATA") && ValidationData.equals(retJson.getString("VALIDATIONDATA")))
			{
			}	
			else
			{
				MBSUtils.showInvalidResponseAlert(act);
			}*/
                if (decryptedAccounts.indexOf("SUCCESS") > -1) {
                    saveData();
                } else if (decryptedAccounts.indexOf("FAILED#") > -1) {
                    retMess = getString(R.string.alert_032);
                    showAlert(retMess);// setAlert();
                } else if (decryptedAccounts.indexOf("BLOCKEDFORDAY") > -1) {
                    retMess = getString(R.string.login_alert_005);
                    showAlert(retMess);// setAlert();
                } else if (decryptedAccounts.indexOf("WRONGTRANPIN") > -1) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(decryptedAccounts);
                        String msg[] = obj.getString("RETVAL").split("~");
                        String first = msg[1];
                        String second = msg[2];
                        Log.e("OMKAR", "---" + second + "----");
                        int count = Integer.parseInt(second);
                        count = 5 - count;
                        loadProBarObj.dismiss();
                        retMess = getString(R.string.alert_125_1) + " " + count + " "
                                + getString(R.string.alert_125_2);
                        showAlert(retMess);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }// end onPostExecute
    }// end callValidateTranpinService       obj.put("METHODCODE", "73");

    class CallWebService_fetch_all_beneficiaries extends AsyncTask<Void, Void, Void> {

        String retval = "";
        LoadProgressBar loadProBarObj = new LoadProgressBar(OtherBankTranIFSC.this);
        //String[] xmlTags = {"CUSTID","SAMEBNK","IMEINO"};

        JSONObject jsonObj = new JSONObject();


        protected void onPreExecute() {
            try {
                respcode = "";
                reTval = "";
                getBeneficiariesrespdesc = "";
                // pb_wait.setVisibility(ProgressBar.VISIBLE);
                loadProBarObj.show();
                jsonObj.put("CUSTID", custId);
                jsonObj.put("SAMEBNK", "N");
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OtherBankTranIFSC.this));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(OtherBankTranIFSC.this));

                jsonObj.put("METHODCODE", "13");
            } catch (JSONException je) {
                je.printStackTrace();
            }


            //System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";

            try {
                String keyStr = CryptoClass.Function2();
                var2 = CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);
                request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }// end dodoInBackground

        protected void onPostExecute(Void paramVoid) {

            /*
             * retval = "SUCCESS~" +
             * "2#omkarkushte#omkar#0020001010000052#SBI00010023#1234567#8657888773#omkarkushte@gmail.com~"
             * +
             * "3#mohit#mohit#0020001030000099#SBI00010222#1234567#7854894545#mohitsharma@gmail.com"
             * ;
             */
            // *******************************
            //	String[] xml_data = CryptoUtil.readXML(retval,	new String[] { "BENEFICIARIES" });
            //String decryptedBeneficiaries = xml_data[0];
            loadProBarObj.dismiss();
            JSONObject jsonObj;
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());


                Log.e("IN return", "data :" + jsonObj.toString());
                if (jsonObj.has("RESPCODE")) {
                    respcode = jsonObj.getString("RESPCODE");
                } else {
                    respcode = "-1";
                }
                if (jsonObj.has("RETVAL")) {
                    reTval = jsonObj.getString("RETVAL");
                } else {
                    reTval = "";
                }
                if (jsonObj.has("RESPDESC")) {
                    getBeneficiariesrespdesc = jsonObj.getString("RESPDESC");
                } else {
                    getBeneficiariesrespdesc = "";
                }

                if (getBeneficiariesrespdesc.length() > 0) {
                    showAlert(getBeneficiariesrespdesc);
                } else {
                    //decryptedBeneficiaries="SUCCESS";
                    if (reTval.indexOf("SUCCESS") > -1) {


                        post_successfetch_all_beneficiaries(reTval);

                        //	loadProBarObj.dismiss();
                    } else {
                        //loadProBarObj.dismiss();
                        if (reTval.indexOf("NODATA") > -1) {
                            Toast.makeText(OtherBankTranIFSC.this, getString(R.string.alert_041), Toast.LENGTH_LONG).show();
					/*Fragment fragment = new FundTransferMenuActivity(act);
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();*/
                            Intent in = new Intent(OtherBankTranIFSC.this, FundTransferMenuActivity.class);
                            startActivity(in);
                        } else {
                            retMess = getString(R.string.alert_069);
                            showAlert(retMess);
                        }
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }// end onPostExecute

    }// end callWbService  jsonObj.put("METHODCODE", "13");

    class CallWebServiceGetMMID extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(OtherBankTranIFSC.this);
        String retval = "", accNumber = "";
        //String[] xmlTags = { "CUSTID", "ACCOUNTNO","IMEINO"};


        JSONObject jsonObj = new JSONObject();

        @Override
        protected void onPreExecute() {
            try {
                respcode = "";
                reTval = "";
                generateMMIDrespdesc = "";
                // p_wait.setVisibility(ProgressBar.VISIBLE);
                Log.e("CallWebServiceGetMMID ", "onPreExecute");
                loadProBarObj.show();
                //accNumber = arrListTemp.get(spi_debit_account.getSelectedItemPosition());
                //System.out.println("custId:"+custid);
                //System.out.println("accountNo:"+accountNo);
                accNumber = spi_debit_account.getSelectedItem().toString();
                //spinner.getSelectedItem().toString();

                accNumber = accNumber.substring(0, 16);
                jsonObj.put("CUSTID", custId);
                jsonObj.put("ACCOUNTNO", accNumber);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OtherBankTranIFSC.this));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(OtherBankTranIFSC.this));

                jsonObj.put("METHODCODE", "45");
                //valuesToEncrypt[0] = custId;
                //valuesToEncrypt[1] = accNumber;
                //valuesToEncrypt[2] = MBSUtils.getImeiNumber(act);
            } catch (JSONException je) {
                je.printStackTrace();
            }


            //System.out.println("&&&&&&&&&& generatedXML " + generatedXML);
            //Log.e("Ganesh ","custid="+custId);
            //Log.e("Ganesh ","accountNo="+accNumber);
            //Log.e("Ganesh ","valuesToEncrypt[2]="+valuesToEncrypt[2]);
        }

        ;

        @Override
        protected Void doInBackground(Void... arg0) {
            ////System.out.println("============= inside doInBackground =================");
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";

            try {
                String keyStr = CryptoClass.Function2();
                var2 = CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);
                request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }// end doInBackground

        protected void onPostExecute(final Void result) {
            Log.e("CallWebServiceGetMMID ", "onPostExecute=");
            //LOANDETAILS

            loadProBarObj.dismiss();


            //Log.e("Ganesh ","accNumber="+accNumber);
            Log.e("Ganesh ", "mmid=" + mmid);
            JSONObject jsonObj;
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());


                if (jsonObj.has("RESPCODE")) {
                    respcode = jsonObj.getString("RESPCODE");
                } else {
                    respcode = "-1";
                }
                if (jsonObj.has("RETVAL")) {
                    reTval = jsonObj.getString("RETVAL");
                } else {
                    reTval = "";
                }
                if (jsonObj.has("RESPDESC")) {
                    generateMMIDrespdesc = jsonObj.getString("RESPDESC");
                } else {
                    generateMMIDrespdesc = "";
                }


                if (getTransferChargesrespdesc.length() > 0) {
                    showAlert(getTransferChargesrespdesc);
                } else {
                    if (reTval.indexOf("FAILED") > -1) {
                        String msg[] = reTval.split("~");
                        if (msg[1].equalsIgnoreCase("NA")) {
                            Log.e("Ganesh ", "msg[2].length= " + msg[2].length());
                            if (msg[2].length() > 0) {
                                req_id = msg[2];
                                Log.e("Ganesh ", " Failed NA req_id=" + req_id);
                                Log.e("Ganesh ", "Failed NA req_id=" + req_id);
                                retMess = getString(R.string.alert_160) + " " + req_id;
                            } else {
                                retMess = getString(R.string.alert_158);
                            }
                            showAlert2(retMess);
                        }

                    } else if (reTval.indexOf("SUCCESS") > -1) {
                        post_successGetMMID(reTval);
                    }


                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }// end onPostExecute
    }// end CallWebServiceGetMMID   jsonObj.put("METHODCODE", "45");

    class CallWebServiceGetSrvcCharg extends AsyncTask<Void, Void, Void> {
        LoadProgressBar loadProBarObj = new LoadProgressBar(OtherBankTranIFSC.this);
        JSONObject jsonObj = new JSONObject();
        String accNo, debitAccno, benAcNo, amt, reMark;

        protected void onPreExecute() {
            try {
                respcode = "";
                reTval = "";
                getTransferChargesrespdesc = "";

                loadProBarObj.show();
                accNo = txtAccNo.getText().toString().trim();
                debitAccno = spi_debit_account.getSelectedItem().toString();//arrListTemp.get(spi_debit_account.getSelectedItemPosition());
                benAcNo = spi_sel_beneficiery.getSelectedItem().toString();//ItemAtPosition(
                //	spi_sel_beneficiery.getSelectedItemPosition()).toString();
                tranType = spi_payment_option.getItemAtPosition(spi_payment_option.getSelectedItemPosition()).toString();
                amt = txtAmt.getText().toString().trim();
                reMark = txtRemk.getText().toString().trim();

                if (tranType.equalsIgnoreCase("RTGS"))
                    tranType = "RT";
                else if (tranType.equalsIgnoreCase("NEFT"))
                    tranType = "NT";
                else if (tranType.equalsIgnoreCase("IMPS"))
                    tranType = "P2A";
                debitAccno = debitAccno.substring(0, 16);
                jsonObj.put("CUSTID", custId);
                jsonObj.put("TRANTYPE", tranType);
                jsonObj.put("DRACCNO", debitAccno);
                jsonObj.put("AMOUNT", amt);
                jsonObj.put("CRACCNO", accNo);
                jsonObj.put("IMEINO", MBSUtils.getImeiNumber(OtherBankTranIFSC.this));
                jsonObj.put("SIMNO", MBSUtils.getSimNumber(OtherBankTranIFSC.this));
                jsonObj.put("BENFSRNO", benSrno);

                jsonObj.put("METHODCODE", "28");

            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        protected Void doInBackground(Void... arg0) {
            String value4 = getString(R.string.namespace);
            String value5 = getString(R.string.soap_action);
            String value6 = getString(R.string.url);
            final String value7 = "callWebservice";

            try {
                String keyStr = CryptoClass.Function2();
                var2 = CryptoClass.getKey(keyStr);
                SoapObject request = new SoapObject(value4, value7);
                request.addProperty("value1", CryptoClass.Function5(jsonObj.toString(), var2));
                request.addProperty("value2", CryptoClass.Function3(keyStr, var1));
                request.addProperty("value3", var3);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(value6, 45000);

                androidHttpTransport.call(value5, envelope);
                var5 = envelope.bodyIn.toString().trim();
                var5 = var5.substring(var5.indexOf("=") + 1, var5.length() - 3);
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }// end dodoInBackground2

        protected void onPostExecute(Void paramVoid) {

            loadProBarObj.dismiss();
            JSONObject jsonObj;
            try {
                String str = CryptoClass.Function6(var5, var2);
                jsonObj = new JSONObject(str.trim());
                Log.e("Shubham", "Returned json values: " + jsonObj.toString());

                if (jsonObj.has("RESPCODE")) {
                    respcode = jsonObj.getString("RESPCODE");
                } else {
                    respcode = "-1";
                }
                if (jsonObj.has("RETVAL")) {
                    reTval = jsonObj.getString("RETVAL");
                } else {
                    reTval = "";
                }
                if (jsonObj.has("RESPDESC")) {
                    getTransferChargesrespdesc = jsonObj.getString("RESPDESC");
                } else {
                    getTransferChargesrespdesc = "";
                }


                if (getTransferChargesrespdesc.length() > 0) {
                    showAlert(getTransferChargesrespdesc);
                } else {
                    if (reTval.indexOf("SUCCESS") > -1) {
                        post_successGetSrvcCharg(reTval);
                    } else {
                        if (reTval.indexOf("TRANAMTLIMIT") > -1) {
                            String errCd = reTval.split("~")[2];
                            if (errCd.equalsIgnoreCase("01"))
                                retMess = getString(R.string.alert_148);
                            else
                                retMess = getString(R.string.alert_149);
                            showAlert(retMess);
                        } else if (reTval.indexOf("LOWBALANCE") > -1) {
                            retMess = getString(R.string.alert_176);
                            showAlert(retMess);
                        } else if (reTval.indexOf("BENFACCINVALID") > -1) {
                            retMess = getString(R.string.alert_benfivalid);
                            //loadProBarObj.dismiss();
                            showAlert(retMess);
                        } else if (reTval.indexOf("SingleLimitExceeded") > -1) {
                            retMess = getString(R.string.alert_193);
                            showAlert(retMess);
                        } else if (reTval.indexOf("TotalLimitExceeded") > -1) {
                            retMess = getString(R.string.alert_194);
                            //loadProBarObj.dismiss();
                            showAlert(retMess);
                        }
                        else if (reTval.indexOf("STOPTRAN") > -1) {
                            retMess = getString(R.string.stop_transation);
                            //loadProBarObj.dismiss();
                            showAlert(retMess);
                        }else {
                            // this case consider when in retval string contains only  "FAILED"
                            retMess = getString(R.string.alert_032);
                            //loadProBarObj.dismiss();
                            showAlert(retMess);//setAlert();
                            //System.out
                            //		.println("================== in onPostExecute 2 ============================");
                        }
                    }// end else


                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }// end onPostExecute
    }// end CallWebServiceGetSrvcCharg jsonObj.put("METHODCODE", "28");


}// end OtherBankTranIFSC
