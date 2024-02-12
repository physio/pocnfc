package com.latlngpoc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NFCSuccessActivity extends AppCompatActivity {
    String CardNumber = "", CardType = "", CardExpire = "", CardDetails = "";
    TextView txtCardNumber, txtCardType, txtCardExpire, txtcarddetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcsuccess);
        // initView();
    }

    private void initView() {
        txtcarddetails = findViewById(R.id.txtcarddetails);
        CardNumber = getIntent().getStringExtra("CardNumber");
        CardExpire = getIntent().getStringExtra("CardExpire");
        CardType = getIntent().getStringExtra("CardType");
        CardDetails = getIntent().getStringExtra("CardDetails");
        txtCardType = findViewById(R.id.txtCardType);
        txtCardExpire = findViewById(R.id.txtCardExpire);
        txtCardNumber = findViewById(R.id.txtCardNumber);
        txtcarddetails.setText(CardDetails);
        txtCardNumber.setText(CardNumber);
        txtCardType.setText(CardType);
        txtCardExpire.setText(CardExpire);

    }

}