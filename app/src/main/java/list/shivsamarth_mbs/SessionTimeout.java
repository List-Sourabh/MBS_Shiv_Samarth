package list.shivsamarth_mbs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SessionTimeout extends Fragment {
    Button continueBtn;
    TextView txt_version_no;
    TextView txt_welcome, dyn_msg;
    Activity act;

    public SessionTimeout() {

    }

    @SuppressLint("ValidFragment")
    public SessionTimeout(Activity a) {
        act = a;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        Log.e("SessionTimeout===", "onCreateView===");

        return rootView;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Intent intent = new Intent(getActivity(), SessionOut.class);
        startActivity(intent);
        getActivity().finish();
    }

}
