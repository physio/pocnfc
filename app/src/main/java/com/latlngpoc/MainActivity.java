package com.latlngpoc;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.latlngpoc.NFCReader.CardUtils;
import com.latlngpoc.NFCReader.NFCCardReader;
import java.io.IOException;
import io.github.tapcard.emvnfccard.model.EmvCard;

public class MainActivity extends AppCompatActivity {
    private NFCCardReader nfcCardReader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (nfcCardReader.isSuitableIntent(intent)) {
            // textView.setText(R.string.reading);
            // utility.showProgressDialog("Reading Card Details..");
            readCardDataAsync(intent);
        } else {
            showToastMessage("Fail to read card details..");
        }
    }

    @Override
    protected void onResume() {
        nfcCardReader.enableDispatch();
        // Check if NFC is available
        if (!isNfcAvailable(getApplicationContext())) {
            //   backToHomeScreen();
            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setTitle("POC");
            alertbox.setMessage("NFC Scanner is not available in your device");
            alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dialog.dismiss();
                }
            });
            alertbox.setCancelable(false);
            alertbox.show();
        } else {
            if (!isNfcEnabled(getApplicationContext())) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
                alertbox.setTitle("POC");
                alertbox.setMessage("Turn on NFC reader to start taking payments.");
                alertbox.setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        Intent intent = null;
                        intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        dialog.dismiss();
                        startActivity(intent);
                    }
                });
                alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert11 = alertbox.create();
                alert11.show();
//                alertbox.setCancelable(false);
//                alertbox.show();
                Button buttonbackground = alert11.getButton(DialogInterface.BUTTON_NEGATIVE);
                buttonbackground.setTextColor(getResources().getColor(R.color.colorPrimary));
                Button buttonbackground1 = alert11.getButton(DialogInterface.BUTTON_POSITIVE);
                buttonbackground1.setTextColor(getResources().getColor(R.color.colorPrimary));

            }
        }
        super.onResume();

    }


    public static boolean isNfcAvailable(final Context pContext) {
        boolean nfcAvailable = true;
        try {
            NfcAdapter adapter = NfcAdapter.getDefaultAdapter(pContext);
            if (adapter == null) {
                nfcAvailable = false;
            }

        } catch (UnsupportedOperationException e) {
            nfcAvailable = false;
        }
        return nfcAvailable;
    }

    public static boolean isNfcEnabled(final Context pContext) {
        boolean nfcEnabled = true;
        try {
            NfcAdapter adapter = NfcAdapter.getDefaultAdapter(pContext);
            nfcEnabled = adapter.isEnabled();
        } catch (UnsupportedOperationException e) {
            nfcEnabled = false;
        }
        return nfcEnabled;
    }

    @Override
    protected void onPause() {
        nfcCardReader.disableDispatch();
        super.onPause();
    }

    private void initView() {
        nfcCardReader = new NFCCardReader(this);
    }

    private void showCardInfo(EmvCard card) {
        String text = "";
        if (card != null) {
            text = TextUtils.join("\n", new Object[]{
                    CardUtils.formatCardNumber(card.getCardNumber(), card.getType()),
                    DateFormat.format("M/y", card.getExpireDate()),
                    "---",
                    "Bank info (probably): ",
                    card.getAtrDescription(),
                    "---",
                    card.toString().replace(", ", ",\n")
            });
            Log.e("POC: ", "Card Details: " + text);
            Intent intent = new Intent(MainActivity.this, NFCSuccessActivity.class);
            intent.putExtra("CardNumber", CardUtils.formatCardNumber(card.getCardNumber(), card.getType()));
            intent.putExtra("CardType", card.getType().toString());
            intent.putExtra("CardExpire", DateFormat.format("MM/y", card.getExpireDate()));
            intent.putExtra("CardDetails", text);
            startActivity(intent);
        } else {
            showToastMessage(getResources().getString(R.string.error_reding_data));
        }

    }

    public void showToastMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void readCardDataAsync(Intent intent) {
        new AsyncTask<Intent, Object, EmvCard>() {

            @Override
            protected EmvCard doInBackground(Intent... intents) {
                // utility.dismissProgressDialog();
                try {
                    return nfcCardReader.readCardBlocking(intents[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("POC: ", "IOException: " + e.toString());
                } catch (NFCCardReader.WrongIntentException e) {
                    e.printStackTrace();
                    Log.e("POC: ", "WrongIntentException: " + e.toString());
                } catch (NFCCardReader.WrongTagTech e) {
                    e.printStackTrace();
                    Log.e("POC: ", "WrongTagTech: " + e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(EmvCard emvCard) {
                // utility.dismissProgressDialog();
                showCardInfo(emvCard);
            }
        }.execute(intent);
    }

}