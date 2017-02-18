package kou.testwear;

/**
 * データを通信する際のパスとキー
 * 下記のパスにデータは保存され、キーを使ってデータを取り出します。(DataMap)
 *
 * ！注！
 * 絶対にモバイル側と表記を合わせてください！！
 *
 * Created by kousuke nezu on 2017/02/11.
 */

public class WearConstants {
    /**
     * データ送信用パス
     * このパスにデータは保存される。
     */
    public static final String WEAR_ACTION_SEND_PATH = "/com/cyder/activa/data";
// お試し用に使った
// public static final String WEAR_ACTION_SEND_PATH = "/com/gclue/send/data";
    /** データ送信用キー(お試し用) */
    public static final String WEAR_ACTION_PARAM_KEY = "sampleData";
    /* データ送信用キー */
    /** 再生の時 */
    public static final String WEAR_ACTION_PLAY_KEY = "play";
    /** 速度調節の時 */
    public static final String WEAR_ACTION_SPEED_KEY = "speed";
    /** 位置指定の時 */
    public static final String WEAR_ACTION_PLACE_KEY = "place";
}