package kou.testwear;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 *モバイル側のメインアクティビティです
 */
public class MainActivity extends AppCompatActivity
        implements OnClickListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<DataItemResult>,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    /** ログ用のタグ */
    final static String TAG = "MainActivity";

    /** 送信メッセージ用エディタテキスト */
    EditText messageEditText;
    /** 受信テキスビュー */
    TextView recieveTextView;
    /** 送信用ボタン */
    Button sendButton;

    /** Google Play Service(これに接続しないとDataAPI使えない) */
    private GoogleApiClient mGoogleApiClient;

    /**
     * このメソッドからアプリケーションが始まります
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Viewの初期化
        findViews();

        //mGoogleApiClientのインスタンス化
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Wearable.API)
                .build();
        //Google Play Serviceに接続
        mGoogleApiClient.connect();
    }

    /** idを探すメソッド */
    protected void findViews(){
        messageEditText = (EditText)findViewById(R.id.message_edit_text);
        recieveTextView = (TextView)findViewById(R.id.recieve_textview);
        sendButton = (Button)findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
    }

    /**
     * 接続成功時
     */
    @Override
    public void onConnected(Bundle connectionHint){
        Log.d(TAG, "Google Play Serviceに接続成功");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    /**
     * サスペンド時
     */
    @Override
    public void onConnectionSuspended(int i) {

        Log.d(TAG, "Google Play Serviceにサスペンド");
    }

    /**
     * 接続失敗時
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

        Log.d(TAG, "Google Play Serviceに接続失敗");
    }

    /**
     * アプリケーション再開時
     */
    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     * アプリケーション一時停止時
     */
    @Override
    public  void  onPause(){
        super.onPause();
    }

    /**
     * Viewがクリックされたときの処理
     */
    static String a = "a";
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.send_button:
                Log.d(TAG, "送信します。");
                syncData(WearConstants.WEAR_ACTION_SEND_PATH, WearConstants.WEAR_ACTION_PARAM_KEY, a);
                a += "a";
                break;
        }
    }

    /**
     * Wearにデータを送信
     *
     * @param path パス名
     * @param key キー
     * @param value バリュー
     */
    public void syncData(String path, String key, String value){
        Log.d(TAG, "データを送信します.");

        // DataMapインスタンスを生成する
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
        DataMap dataMap = dataMapRequest.getDataMap();

        // データをセットする
        dataMap.putString(key, value);


        // データを更新する
        PutDataRequest request = dataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
        pendingResult.setResultCallback(this);
    }

    /**
     * 送信結果表示
     */
    @Override
    public void onResult(@NonNull DataItemResult result) {
        Log.d(TAG, "onResult:" + result.getStatus().isSuccess());
    }

    /**
     * データが変更されたときに呼び出されます。
     * 逆にデータが変更されないと呼び出されません
     * ex)同じ文字列を保存したなど
     * データの削除時と変更時に分けて処理しています
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "データが変更されました");
        for (DataEvent event : dataEvents) {
            // TYPE_DELETEDがデータ削除時、TYPE_CHANGEDがデータ登録・変更時
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());

            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());

                // 更新されたデータを取得する
                DataMap dataMap = DataMap.fromByteArray(event.getDataItem().getData());
                String[] data = new String[4];
                data[0] = dataMap.getString(WearConstants.WEAR_ACTION_PARAM_KEY);
                data[1] = dataMap.getString(WearConstants.WEAR_ACTION_PLAY_KEY);
                data[2] = dataMap.getString(WearConstants.WEAR_ACTION_SPEED_KEY);
                data[3] = dataMap.getString(WearConstants.WEAR_ACTION_PLACE_KEY);

                for(int i = 0; i < 4; i++){
                    if(data[i] != null) {
                        Log.d(TAG, i + ":" + data[i]);
                        recieveTextView.setText(data[i]);
                    }
                }
            }
        }
    }
}