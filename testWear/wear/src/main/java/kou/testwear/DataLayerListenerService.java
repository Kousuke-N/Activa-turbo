package kou.testwear;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * データを受信するためのサービスのクラス
 * Created by kousuke nezu on 2017/01/30.
 */

public class DataLayerListenerService extends WearableListenerService{
    /** ロゴのタグ */
    private static final String TAG = "WEAR";


    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "DataLayerListenerService 起動");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents){
        Log.d(TAG, "データアイテムの変更を検出しました。");
        for(DataEvent event: dataEvents){
            //データアイテムが削除されたとき
            if(event.getType() == DataEvent.TYPE_DELETED){
                Log.d(TAG, "データアイテムが削除されました。");
            }
            //データアイテムが変更されたとき
            else if(event.getType() == DataEvent.TYPE_CHANGED){
                Log.d(TAG, "データアイテムが変更されました。");

                //Action名が同じ場合は値を取得
                if(WearConstants.WEAR_ACTION_SEND_PATH.equals(event.getDataItem().getUri().getPath())){
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    String data = dataMapItem.getDataMap().getString(WearConstants.WEAR_ACTION_PARAM_KEY);

                    //今は仮にトースト使用　変更してください。
                    Toast.makeText(this, "data:" + data, Toast.LENGTH_LONG).show();
                    Log.d(TAG, data);
                }
            }
        }
    }
}
