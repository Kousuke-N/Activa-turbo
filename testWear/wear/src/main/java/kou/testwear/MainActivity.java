package kou.testwear;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * wear側のMainActivityです。
 */

public class MainActivity extends Activity
        implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<DataApi.DataItemResult>,
        GoogleApiClient.OnConnectionFailedListener,  DataApi.DataListener {
    /** Log用のタグ */
    final static String TAG = "MainActivity";

    /** Google Play Serviceインスタント */
    GoogleApiClient mGoogleApiClient;

    //View
    /** 送信用ボタン(削除してください) */
    Button sendButton;
    /** 再生ボタン　*/
    Button playButton;
    /** 速度設定ボタン */
    Button speedButton;
    /** 再生位置指定ボタン */
    Button placeButton;
    /** 設定ボタン */
    Button configButton;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_main);
        //Viewの初期化
        findViews();

        //ウインドウスリープ禁止
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //mGoogleApiClientのインスタンス化
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Wearable.API)
                .build();

        //Google Play Serviceに接続
        //アプリが立ち上がっているかどうかにかかわらず同期させたいのでonCreate()で接続
        mGoogleApiClient.connect();
    }

    /**
     * Viewのidを探し初期化するメソッド
     */
    protected void findViews(){
        //送信用ボタン
        sendButton = (Button)findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
        //再生ボタン
        playButton = (Button)findViewById(R.id.play_button);
        playButton.setOnClickListener(this);
        //速度設定ボタン
        speedButton = (Button)findViewById(R.id.speed_button);
        speedButton.setOnClickListener(this);
        //再生位置指定ボタン
        placeButton = (Button)findViewById(R.id.place_button);
        placeButton.setOnClickListener(this);
        //設定ボタン
        configButton = (Button)findViewById(R.id.config_button);
        configButton.setOnClickListener(this);
    }

    /**
     *  Google Play Serviceに接続成功したときに呼び出し
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google Play Service に接続成功");
        //データ変更を受け取れるようにする
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    /**
     *  Google Play Serviceにサスペンドしたときに呼び出し
     */
    @Override
    public void onConnectionSuspended(int i) {

        Log.d(TAG, "Google Play Service にサスペンド");
    }

    /**
     * Google Play Serviceに接続失敗したときに呼び出し
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

        Log.d(TAG, "Google Play Serviceに接続失敗");
    }

    /**
     * アプリ再開時に呼び出し
     */
    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     *   アプリ一時停止時に呼び出し
     */
    @Override
    public void onPause(){
        super.onPause();
    }

    static String a = "a";
    /**
     * Viewがクリックされたときに呼び出される
     * @param view クリックされたView
     */
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.send_button:
                syncData(WearConstants.WEAR_ACTION_SEND_PATH, WearConstants.WEAR_ACTION_PARAM_KEY, a);
                a += "s";
                break;
            case R.id.play_button:
                syncData(WearConstants.WEAR_ACTION_SEND_PATH, WearConstants.WEAR_ACTION_PLAY_KEY, a);
                a += "p";
                break;
            case R.id.speed_button:
                syncData(WearConstants.WEAR_ACTION_SEND_PATH, WearConstants.WEAR_ACTION_SPEED_KEY, a);
                a += "S";
                break;
            case R.id.place_button:
                syncData(WearConstants.WEAR_ACTION_SEND_PATH, WearConstants.WEAR_ACTION_PLACE_KEY, a);
                a += "P";
                break;
            case R.id.config_button:
                break;
            default:
        }
    }

    /**
     * Wearにデータを送信
     * Google Play Serviceにデータを保管し共有します。
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
    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
        Log.d("TAG", "onResult: " + dataItemResult.getStatus());
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
                Log.d("TAG", "データアイテムが削除されました: " + event.getDataItem().getUri());

            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d("TAG", "データアイテムが変更されました: " + event.getDataItem().getUri());

                // 更新されたデータを取得する
                DataMap dataMap = DataMap.fromByteArray(event.getDataItem().getData());
                String[] data = new String[4];
                data[0] = dataMap.getString(WearConstants.WEAR_ACTION_PARAM_KEY);
                data[1] = dataMap.getString(WearConstants.WEAR_ACTION_PLAY_KEY);
                data[2] = dataMap.getString(WearConstants.WEAR_ACTION_SPEED_KEY);
                data[3] = dataMap.getString(WearConstants.WEAR_ACTION_PLACE_KEY);

                for(int i = 0; i < 4; i++){
                    if(data[i] != null)
                        Log.d(TAG, i + ":" + data[i]);
                }
            }
        }
    }
}
